#!/usr/bin/env python3
################################################################
#
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing,
#  software distributed under the License is distributed on an
#  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#  KIND, either express or implied.  See the License for the
#  specific language governing permissions and limitations
#  under the License.
#
################################################################
"""Update extras/l10n/source/*/localize.sdf from the Pootle PO files, without Pootle.

For each language this does what the Pootle server and the integration
round do together: re-key the tree file to the current en-US.sdf, make
templates, re-key and pomerge the PO files from apache/openoffice-translation,
convert them with po2oo, and merge the result into the tree file under a
gsicheck gate.  Nothing is committed; --apply copies the results into the tree.

See README.md for the steps it runs and how to check the result.
"""

import argparse
import collections
import concurrent.futures
import datetime
import filecmp
import glob
import json
import os
import platform
import shutil
import subprocess
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import sdf_integrate as si  # noqa: E402

DEFAULT_TEMPLATE = os.path.join(HERE, os.pardir, "scripts", "en-US.sdf")
PO_REPO_URL = "https://github.com/apache/openoffice-translation.git"
TOOLKIT = "translate-toolkit==2.2.5"  # what the Pootle server runs


def log_run(cmd, log, **kw):
    log.write("$ " + " ".join(cmd) + "\n")
    log.flush()
    return subprocess.run(cmd, stdout=log, stderr=subprocess.STDOUT, **kw).returncode


def toolkit_python(args, workdir):
    if args.python:
        return args.python
    if subprocess.run([sys.executable, "-c", "import translate"], capture_output=True).returncode == 0:
        return sys.executable
    env = os.path.join(workdir, "venv")
    py = os.path.join(env, "bin", "python")
    if not os.path.exists(py):
        print("installing %s into %s" % (TOOLKIT, env))
        subprocess.run([sys.executable, "-m", "venv", env], check=True)
        subprocess.run([py, "-m", "pip", "install", "-q", "--disable-pip-version-check", TOOLKIT], check=True)
    return py


def extract_template(aoo, out):
    if platform.system() == "Darwin":
        sys.exit("localize -e does not work on macOS; extract on a Linux build or pass --template")
    envs = glob.glob(os.path.join(aoo, "main", "*Env.Set.sh"))
    if len(envs) != 1:
        sys.exit("need exactly one main/*Env.Set.sh in a configured and built tree, found %d" % len(envs))
    cmd = 'cd "%s" && . "./%s" >/dev/null && localize -e -l en-US -f "%s"' % (
        os.path.join(aoo, "main"), os.path.basename(envs[0]), out)
    with open(out + ".log", "w") as log:
        rc = log_run(["bash", "-c", cmd], log)
    if rc or not os.path.getsize(out):
        sys.exit("localize -e failed, see %s.log" % out)


def old_templates(template, po_repo, extra, outdir):
    """en-US.sdf files the tree may still be keyed from: every committed version
    of scripts/en-US.sdf, the legacy copy in openoffice-translation, and extra."""
    found = list(extra)
    committed = os.path.realpath(DEFAULT_TEMPLATE)
    top = subprocess.run(["git", "-C", os.path.dirname(committed), "rev-parse", "--show-toplevel"],
                         capture_output=True, text=True)
    if top.returncode == 0:
        rel = os.path.relpath(committed, top.stdout.strip())
        revs = subprocess.run(["git", "-C", top.stdout.strip(), "log", "--format=%h", "--", rel],
                              capture_output=True, text=True).stdout.split()
        for rev in revs:
            path = os.path.join(outdir, "en-US.%s.sdf" % rev)
            with open(path, "wb") as f:
                subprocess.run(["git", "-C", top.stdout.strip(), "show", "%s:%s" % (rev, rel)], stdout=f)
            if os.path.getsize(path) and not filecmp.cmp(path, template, shallow=False):
                found.append(path)
    legacy = os.path.join(po_repo, "en-US.sdf") if po_repo else None
    if legacy and os.path.exists(legacy) and not filecmp.cmp(legacy, template, shallow=False):
        found.append(legacy)
    return found


def get_po_repo(args, workdir, langs):
    if args.no_po:
        return None
    repo = args.po_repo or os.path.join(workdir, "openoffice-translation")
    dirs = ["PO-Files/" + x.replace("-", "_") for x in langs] if args.langs else ["PO-Files"]
    if args.po_repo:
        if not os.path.isdir(os.path.join(repo, "PO-Files")):
            sys.exit("%s has no PO-Files directory" % repo)
    elif not os.path.isdir(os.path.join(repo, ".git")):
        print("cloning %s (%s) into %s" % (PO_REPO_URL, " ".join(dirs) if args.langs else "PO-Files", repo))
        depth = [] if args.history else ["--depth", "1"]
        subprocess.run(["git", "clone", "--filter=blob:none", "--sparse"] + depth + [PO_REPO_URL, repo], check=True)
        subprocess.run(["git", "-C", repo, "sparse-checkout", "set"] + dirs, check=True)
    elif subprocess.run(["git", "-C", repo, "sparse-checkout", "list"], capture_output=True).returncode == 0:
        subprocess.run(["git", "-C", repo, "sparse-checkout", "add"] + dirs, check=True)
    if args.history and subprocess.run(["git", "-C", repo, "rev-parse", "--is-shallow-repository"],
                                       capture_output=True, text=True).stdout.strip() == "true":
        sys.exit("--history needs the full history of %s (git fetch --unshallow)" % repo)
    return repo


def po_to_sdf(py, lang, podir, pot, template, out, log):
    """Pootle's template update and mksdf.sh for one language: re-key, pomerge, po2oo."""
    fixed, merged = out + ".fixed", out + ".merged"
    steps = [
        [py, os.path.join(HERE, "fix_msgctxt.py"), "--template", pot, "--input", podir, "--output", fixed],
        [py, "-m", "translate.tools.pomerge", "--template=" + pot + "/", "--input=" + fixed + "/",
         "--output=" + merged + "/"],
        [py, "-m", "translate.convert.po2oo", "--template=" + template, "--skipsource", "--keeptimestamp",
         "--language=" + lang, merged + "/", out],
    ]
    for cmd in steps:
        if log_run(cmd, log):
            raise RuntimeError("%s failed" % os.path.basename(cmd[1] if cmd[1] != "-m" else cmd[2]))
    return merged


def check_rekey(tree, rekeyed, old, template):
    """Every changed line keeps its text, timestamp and line ending, and its English."""
    en_new, en_old = si.load(template, "en-US"), si.load(old, "en-US")
    a, b = si.read_lines(tree), si.read_lines(rekeyed)
    if len(a) != len(b):
        return "line count changed"
    for x, y in zip(a, b):
        if x == y:
            continue
        p, q = x.rstrip("\r\n").split("\t"), y.rstrip("\r\n").split("\t")
        same = [p[i] for i in (2, 8, 9, 10, 11, 12, 13, 14)] == [q[i] for i in (2, 8, 9, 10, 11, 12, 13, 14)]
        if not same or x[len(x.rstrip("\r\n")):] != y[len(y.rstrip("\r\n")):]:
            return "a moved line changed outside its key: %s" % p[4]
        if si.english(en_old.get(si.key(p), [""] * 15)) != si.english(en_new.get(si.key(q), ["-"] * 15)):
            return "a line moved to a key with different English: %s -> %s" % (p[4], q[4])
    return None


def usable(path, lang, en):
    return sum(1 for k, c in si.load(path, lang).items()
               if k in en and any(si.is_translation(c[i], en[k][i]) for i in si.TEXT_COLUMNS))


def do_language(lang, ctx):
    a = ctx
    d = os.path.join(a.run, "langs", lang)
    os.makedirs(d)
    tree = os.path.join(a.l10n, lang, "localize.sdf")
    result = {"lang": lang, "status": "ok"}
    with open(os.path.join(d, "log.txt"), "w") as log:
        try:
            rekeyed = os.path.join(d, "rekeyed.sdf")
            text, plan, stats = si.rekey(lang, tree, a.olds, a.template)
            si.write(rekeyed, text)
            log.write("rekey: %s\n" % dict(stats))
            chosen = next((k[len("keyed from "):] for k in stats if k.startswith("keyed from ")), None)
            result["keyed from"] = os.path.basename(chosen) if chosen else "current"
            result["moved"] = len(plan)
            if plan:
                problem = check_rekey(tree, rekeyed, chosen, a.template)
                if problem:
                    raise RuntimeError("re-key check: " + problem)
            final = rekeyed
            podir = os.path.join(a.po_repo, "PO-Files", lang.replace("-", "_")) if a.po_repo else None
            if podir and os.path.isdir(podir):
                po2oo = os.path.join(d, "po2oo.sdf")
                merged = po_to_sdf(a.py, lang, podir, a.pot, a.template, po2oo, log)
                history = []
                for rev in a.history:
                    hdir = os.path.join(d, "history", rev)
                    os.makedirs(hdir)
                    arch = subprocess.run(["git", "-C", a.po_repo, "archive", rev,
                                           "PO-Files/" + lang.replace("-", "_")], capture_output=True)
                    if arch.returncode:
                        log.write("history %s: no PO files for %s\n" % (rev, lang))
                        continue
                    subprocess.run(["tar", "-x", "-C", hdir], input=arch.stdout, check=True)
                    hsdf = os.path.join(d, "history-%s.sdf" % rev)
                    po_to_sdf(a.py, lang, os.path.join(hdir, "PO-Files", lang.replace("-", "_")),
                              a.pot, a.template, hsdf, log)
                    history.append(hsdf)
                if si.load(po2oo, lang):
                    final = os.path.join(d, "final.sdf")
                    if a.gsicheck:
                        changed, added, skip, new_errors = si.gate(
                            lang, rekeyed, po2oo, a.template, final, a.gsicheck, a.libdir,
                            add_only=a.mode == "add-only", history=history)
                        result["left out"] = len(skip)
                        for k in skip:
                            log.write("left out (adds a gsicheck error): %s %s %s\n" % (k[1], k[3], k[4]))
                        if new_errors:
                            result["status"] = "new gsicheck errors not tied to an entry"
                            for msg, uid in sorted(new_errors):
                                log.write("new gsicheck error: %s %s\n" % (msg, uid or ""))
                        si.write(os.path.join(d, "left-out.json"), json.dumps(skip, ensure_ascii=False, indent=1))
                    else:
                        text, changed, added = si.merge(lang, rekeyed, po2oo, a.template, (),
                                                        a.mode == "add-only", history)
                        si.write(final, text)
                    result["updated"], result["added"] = len(changed), len(added)
                if a.export_po:
                    shutil.copytree(merged, os.path.join(a.run, "po", lang.replace("-", "_")))
            else:
                result["note"] = "no PO files"
            result["before"] = usable(tree, lang, a.en)
            result["after"] = usable(final, lang, a.en)
            result["final"] = final
            result["changed"] = not filecmp.cmp(final, tree, shallow=False)
        except Exception as e:  # report and go on with the other languages
            result["status"] = "failed: %s" % e
            log.write("FAILED: %s\n" % e)
    return result


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("--aoo", required=True, help="AOO checkout (the directory with main/ and extras/)")
    p.add_argument("--template", default=DEFAULT_TEMPLATE,
                   help="en-US.sdf of the current source (default: %(default)s)")
    p.add_argument("--extract", action="store_true",
                   help="extract en-US.sdf from --aoo with localize -e first (Linux, built tree)")
    p.add_argument("--old-template", action="append", default=[],
                   help="more en-US.sdf files the tree may be keyed from (earlier versions of --template "
                        "and openoffice-translation/en-US.sdf are added automatically)")
    p.add_argument("--po-repo", help="checkout of apache/openoffice-translation (default: clone one into the work dir)")
    p.add_argument("--no-po", action="store_true", help="only re-key the tree files")
    p.add_argument("--langs", help="comma-separated tree language codes (default: every tree language)")
    p.add_argument("--mode", choices=("add-only", "update"), default="add-only",
                   help="add-only: fill entries the tree has no translation for (default); "
                        "update: also replace tree text with newer PO text")
    p.add_argument("--history", nargs="*", default=[],
                   help="openoffice-translation revisions whose text must not replace a tree translation "
                        "(update mode; guards against Pootle rollbacks)")
    p.add_argument("--solver", help="solver platform directory with bin/gsicheck and lib/ (default: from --aoo)")
    p.add_argument("--no-gsicheck", action="store_true", help="merge without the gsicheck gate")
    p.add_argument("--workdir", default="aoo-l10n-work", help="where runs are kept (default: %(default)s)")
    p.add_argument("--jobs", type=int, default=os.cpu_count() or 4)
    p.add_argument("--python", help="Python with translate-toolkit (default: this one, else a venv in the work dir)")
    p.add_argument("--export-po", action="store_true", help="also write the updated PO files per language")
    p.add_argument("--apply", action="store_true", help="copy the results into the tree")
    a = p.parse_args()
    sys.stdout.reconfigure(line_buffering=True)

    a.aoo = os.path.abspath(a.aoo)
    a.l10n = os.path.join(a.aoo, "extras", "l10n", "source")
    if not os.path.isdir(a.l10n):
        sys.exit("no extras/l10n/source in %s" % a.aoo)
    if a.history and a.mode != "update":
        sys.exit("--history only applies to --mode update")
    a.history = list(dict.fromkeys(a.history))
    workdir = os.path.abspath(a.workdir)
    a.run = os.path.join(workdir, datetime.datetime.now().strftime("%Y%m%d-%H%M%S"))
    os.makedirs(a.run)
    print("work directory:", a.run)

    if a.extract:
        a.template = os.path.join(a.run, "en-US.sdf")
        extract_template(a.aoo, a.template)
    a.template = os.path.abspath(a.template)
    a.en = si.load(a.template, "en-US")
    if not a.en:
        sys.exit("no en-US lines in %s" % a.template)
    print("template: %s (%d entries)" % (a.template, len(a.en)))

    langs = sorted(d for d in os.listdir(a.l10n) if os.path.isfile(os.path.join(a.l10n, d, "localize.sdf")))
    if a.langs:
        wanted = a.langs.split(",")
        missing = [x for x in wanted if x not in langs]
        if missing:
            sys.exit("no tree file for: %s" % " ".join(missing))
        langs = wanted

    a.po_repo = get_po_repo(a, workdir, langs)
    if a.po_repo:
        rev = subprocess.run(["git", "-C", a.po_repo, "log", "-1", "--format=%h %cs"],
                             capture_output=True, text=True).stdout.strip()
        print("PO files: %s at %s" % (a.po_repo, rev))
    os.makedirs(os.path.join(a.run, "old"))
    a.olds = old_templates(a.template, a.po_repo, [os.path.abspath(x) for x in a.old_template],
                           os.path.join(a.run, "old"))

    a.gsicheck = a.libdir = None
    if not a.no_gsicheck:
        solver = a.solver or next(iter(sorted(glob.glob(os.path.join(a.aoo, "main", "solver", "*", "*", "bin",
                                                                      "gsicheck")))), None)
        if solver and solver.endswith("gsicheck"):
            solver = os.path.dirname(os.path.dirname(solver))
        if not solver or not os.access(os.path.join(solver, "bin", "gsicheck"), os.X_OK):
            sys.exit("no gsicheck found; build l10ntools in --aoo, pass --solver, or use --no-gsicheck")
        a.gsicheck, a.libdir = os.path.join(solver, "bin", "gsicheck"), os.path.join(solver, "lib")

    a.py = None
    if a.po_repo:
        a.py = toolkit_python(a, workdir)
        a.pot = os.path.join(a.run, "pot")
        with open(os.path.join(a.run, "oo2po.log"), "w") as log:
            if log_run([a.py, "-m", "translate.convert.oo2po", "--pot", a.template, a.pot], log):
                sys.exit("oo2po failed, see %s/oo2po.log" % a.run)

    if a.po_repo:
        tree_po = {x.replace("-", "_") for x in langs}
        extra = sorted(set(os.listdir(os.path.join(a.po_repo, "PO-Files"))) - tree_po) if not a.langs else []
        if extra:
            print("PO files without a tree file, not integrated: %s" % " ".join(extra))

    with concurrent.futures.ThreadPoolExecutor(a.jobs) as pool:
        results = list(pool.map(lambda x: do_language(x, a), langs))

    lines = ["| language | keyed from | moved | updated | added | left out | translated before | after | status |",
             "|---|---|---|---|---|---|---|---|---|"]
    for r in results:
        lines.append("| %s | %s | %s | %s | %s | %s | %s | %s | %s |" % (
            r["lang"], r.get("keyed from", ""), r.get("moved", ""), r.get("updated", ""), r.get("added", ""),
            r.get("left out", ""), r.get("before", ""), r.get("after", ""), r.get("note", r["status"])))
    total = collections.Counter()
    for r in results:
        total["before"] += r.get("before", 0)
        total["after"] += r.get("after", 0)
    lines.append("")
    lines.append("Translated entries whose key is in the template, all languages: %d before, %d after "
                 "(%d template entries per language)." % (total["before"], total["after"], len(a.en)))
    report = "\n".join(lines) + "\n"
    si.write(os.path.join(a.run, "report.md"), report)
    print(report)

    failed = [r["lang"] for r in results if r["status"] != "ok"]
    changed = [r for r in results if r["status"] == "ok" and r.get("changed")]
    if a.apply:
        for r in changed:
            shutil.copyfile(r["final"], os.path.join(a.l10n, r["lang"], "localize.sdf"))
        print("copied %d files into %s" % (len(changed), a.l10n))
    else:
        print("%d languages changed; rerun with --apply, or copy langs/<lang>/final.sdf (rekeyed.sdf if there "
              "is no final.sdf) over the tree file" % len(changed))
    if failed:
        print("FAILED: %s (see langs/<lang>/log.txt)" % " ".join(failed))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
