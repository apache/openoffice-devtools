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
"""Merge po2oo output into an existing localize.sdf.

  combine  build one po2oo SDF from a base and a newer snapshot, ignoring
           newer values that are blank or revert to text from older snapshots
  merge    update only the entries whose translation changed
  gate     merge, then run gsicheck and keep the tree text for every entry
           that adds a gsicheck error the tree file does not already have

See README.md for the full workflow.
"""

import argparse
import collections
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile

TEXT_COLUMNS = range(10, 14)  # text, helptext, quickhelptext, title


def key(cols):
    # project, file, type, gid, lid, helpid, platform
    return (cols[0], cols[1], cols[3], cols[4], cols[5], cols[6], cols[7])


def read_lines(path):
    """Lines with their endings, split at \n only.

    str.splitlines() also splits at U+2028, U+2029 and other separators, and
    some translations contain them."""
    with open(path, encoding="utf-8", newline="") as f:
        parts = f.read().split("\n")
    return [p + "\n" for p in parts[:-1]] + ([parts[-1]] if parts[-1] else [])


def load(path, lang):
    entries = {}
    for line in read_lines(path):
        cols = line.rstrip("\r\n").split("\t")
        if len(cols) == 15 and cols[9] == lang:
            entries[key(cols)] = cols
    return entries


def is_translation(value, english):
    return bool(value) and value != english


def combine(lang, template, base_path, newer_path, history_paths):
    en = load(template, "en-US")
    base, newer = load(base_path, lang), load(newer_path, lang)
    history = collections.defaultdict(set)
    for path in history_paths:
        for k, cols in load(path, lang).items():
            for i in TEXT_COLUMNS:
                history[(k, i)].add(cols[i])

    stats = collections.Counter()
    out = []
    for k, a in base.items():
        row = list(a)
        b = newer.get(k)
        if b:
            for i in TEXT_COLUMNS:
                if b[i] == a[i]:
                    continue
                english = en[k][i] if k in en else None
                if not is_translation(b[i], english):
                    stats["newer blank or English, kept base"] += 1
                elif b[i] in history[(k, i)]:
                    stats["newer reverts to older text, kept base"] += 1
                else:
                    row[i] = b[i]
                    stats["newer value taken"] += 1
        out.append("\t".join(row) + "\n")
    for k, b in newer.items():
        if k not in base:
            out.append("\t".join(b) + "\n")
            stats["key only in newer"] += 1
    return "".join(out), stats


def history_values(lang, paths):
    values = collections.defaultdict(set)
    for path in paths:
        for k, cols in load(path, lang).items():
            for i in TEXT_COLUMNS:
                values[(k, i)].add(cols[i])
    return values


def merge(lang, tree_path, new_path, template, skip=(), add_only=False, history=()):
    """Return (text, changed keys, added keys).

    A translation in the tree is not replaced by text found in a history SDF
    for the same key, which is how a Pootle rollback shows up."""
    new = load(new_path, lang)
    if not new:
        sys.exit("no %s lines in %s; po2oo --language must use the tree's code (pt-BR, not pt_BR)"
                 % (lang, new_path))
    en = load(template, "en-US")
    skip = set(skip)
    old = history_values(lang, history)
    lines = read_lines(tree_path)

    seen, changed, last_of_file = set(), [], {}
    timestamp, eol = None, "\n"
    out = []
    for line in lines:
        body = line.rstrip("\r\n")
        cols = body.split("\t")
        if len(cols) == 15 and cols[9] == lang:
            k = key(cols)
            seen.add(k)
            timestamp = timestamp or cols[14]
            eol = line[len(body):] or eol
            n, e = new.get(k), en.get(k)
            if n and k not in skip:
                updated = list(cols)
                for i in TEXT_COLUMNS:
                    if is_translation(cols[i], e[i] if e else None) and (add_only or n[i] in old[(k, i)]):
                        continue
                    if n[i] != cols[i] and is_translation(n[i], e[i] if e else None):
                        updated[i] = n[i]
                if updated != cols:
                    changed.append(k)
                    line = "\t".join(updated) + line[len(body):]
            last_of_file[(cols[0], cols[1])] = len(out)
        out.append(line)

    # New keys go after the last line of the same source file, so the file stays grouped.
    added, inserts = [], collections.defaultdict(list)
    for k, n in new.items():
        if k in seen or k in skip:
            continue
        e = en.get(k)
        if not any(is_translation(n[i], e[i] if e else None) for i in TEXT_COLUMNS):
            continue
        row = list(n)
        row[14] = timestamp or row[14]
        inserts[last_of_file.get((n[0], n[1]), len(out) - 1)].append("\t".join(row) + eol)
        added.append(k)
    for pos in sorted(inserts, reverse=True):
        out[pos + 1:pos + 1] = inserts[pos]
    return "".join(out), changed, added


def english(cols):
    return tuple(cols[i] for i in TEXT_COLUMNS)


def with_key(cols, k):
    row = list(cols)
    row[0], row[1], row[3], row[4], row[5], row[6], row[7] = k
    return row


def rekey(lang, tree_path, old_templates, template):
    """Move tree entries whose key changed to the key their English has now.

    The tree is taken to be keyed from whichever of the old templates, or the
    current one, holds most of its keys; if that is the current template,
    nothing moves.  A move needs the English to be the same under both keys:
    first the same key without the help lid (oldref), else the only key in the
    same file with that English.  Return (text, moves, stats)."""
    en_new = load(template, "en-US")
    own = set(key(c) for c in (l.rstrip("\r\n").split("\t") for l in read_lines(tree_path))
              if len(c) == 15 and c[9] == lang)
    best, en_old = None, en_new
    hits = len(own & en_new.keys())
    for path in old_templates:
        cand = load(path, "en-US")
        n = len(own & cand.keys())
        if n > hits:
            best, en_old, hits = path, cand, n
    if best is None:
        with open(tree_path, encoding="utf-8", newline="") as f:
            return f.read(), {}, collections.Counter({"keyed to the current template": len(own)})
    by_file = collections.defaultdict(lambda: collections.defaultdict(list))
    old_by_file = collections.defaultdict(collections.Counter)
    for k, cols in en_new.items():
        by_file[k[:2]][english(cols)].append(k)
    for k, cols in en_old.items():
        old_by_file[k[:2]][english(cols)] += 1

    lines = read_lines(tree_path)
    # Some tree files carry an en-US line paired with the translation; gsicheck
    # needs the pair to keep one key, so both lines move together.
    tree_keys = []
    for line in lines:
        cols = line.rstrip("\r\n").split("\t")
        ok = len(cols) == 15 and cols[9] in (lang, "en-US")
        tree_keys.append(key(cols) if ok else None)

    stats = collections.Counter({"keyed from " + best: hits})
    plan = {}
    for k in dict.fromkeys(filter(None, tree_keys)):
        old, new = en_old.get(k), en_new.get(k)
        if new is not None and (old is None or english(old) == english(new)):
            stats["key unchanged"] += 1
            continue
        if old is None:
            stats["key in neither template, left"] += 1
            continue
        e = english(old)
        target = None
        if k[4]:
            lidless = k[:4] + ("",) + k[5:]
            if lidless in en_new and english(en_new[lidless]) == e:
                target = lidless
        if target is None:
            same = by_file[k[:2]].get(e, [])
            if (len(same) == 1 and old_by_file[k[:2]][e] == 1
                    and english(en_old.get(same[0], [None] * 15)) != e):
                target = same[0]
        if target is None:
            stats["English changed or gone, left" if new is None else
                  "English changed under the same key, left"] += 1
            continue
        plan[k] = target

    # A target must end up with one line: not a tree line that stays, and not
    # the target of another move.
    staying = set(k for k in tree_keys if k and k not in plan)
    hits = collections.Counter(plan.values())
    for k, t in list(plan.items()):
        if t in staying or hits[t] > 1:
            del plan[k]
            stats["target already has a line, left"] += 1
    stats["moved"] = len(plan)

    out = []
    for line, k in zip(lines, tree_keys):
        if k in plan:
            body = line.rstrip("\r\n")
            line = "\t".join(with_key(body.split("\t"), plan[k])) + line[len(body):]
        out.append(line)
    return "".join(out), plan, stats


def run_gsicheck(gsicheck, libdir, path, lang):
    """Return a Counter of (message, UniqueID) for every error except header lines."""
    if not os.access(gsicheck, os.X_OK):
        sys.exit("gsicheck not found or not executable: %s" % gsicheck)
    env = dict(os.environ)
    if libdir:
        for var in ("DYLD_LIBRARY_PATH", "LD_LIBRARY_PATH"):
            env[var] = libdir + (os.pathsep + env[var] if env.get(var) else "")
    with tempfile.TemporaryDirectory() as tmp:
        shutil.copy(path, os.path.join(tmp, "localize.sdf"))
        p = subprocess.run([gsicheck, "-c", "-t", "-l", lang, "localize.sdf"], cwd=tmp, env=env,
                           capture_output=True, text=True, errors="replace")
    if p.returncode not in (0, 55) and "Error" not in p.stdout + p.stderr:
        sys.exit("gsicheck failed (%d): %s" % (p.returncode, (p.stdout + p.stderr).strip()[:500]))
    errors = collections.Counter()
    for line in (p.stdout + p.stderr).splitlines():
        if not line.startswith("Error") or "Format of line is unknown" in line:
            continue
        m = re.search(r"UniqueID (.*?): ", line)
        message = re.sub(r"Line \d+[,:] ?", "", line.split('  "')[0]).strip()
        errors[(message, m.group(1) if m else None)] += 1
    return errors


def uid_to_key(uid):
    parts = uid.split("/")
    gid, lid, helpid, platform = "/".join(parts[3:]).rsplit("/", 3)
    return (parts[0], parts[1], parts[2], gid, lid, helpid, platform)


def write(path, text):
    with open(path, "w", encoding="utf-8", newline="") as f:
        f.write(text)


def cmd_combine(a):
    text, stats = combine(a.lang, a.template, a.base, a.newer, a.history)
    write(a.out, text)
    print(a.lang, dict(stats))


def cmd_rekey(a):
    text, plan, stats = rekey(a.lang, a.tree, a.old_template, a.template)
    write(a.out, text)
    print(a.lang, dict(stats))


def cmd_merge(a):
    skip = [tuple(k) for k in json.load(open(a.skip))] if a.skip else []
    text, changed, added = merge(a.lang, a.tree, a.new, a.template, skip, a.add_only, a.history)
    write(a.out, text)
    print("%s: %d entries updated, %d keys added" % (a.lang, len(changed), len(added)))


def gate(lang, tree, new, template, out, gsicheck, libdir, rounds=4, add_only=False, history=()):
    """Merge, leaving out every entry that adds a gsicheck error.

    Return (changed, added, left-out keys, new errors not tied to an entry)."""
    baseline = run_gsicheck(gsicheck, libdir, tree, lang)
    skip = []
    for _ in range(rounds):
        text, changed, added = merge(lang, tree, new, template, skip, add_only, history)
        write(out, text)
        new_errors = run_gsicheck(gsicheck, libdir, out, lang) - baseline
        keyed = {uid_to_key(uid) for _, uid in new_errors if uid} - set(skip)
        if not keyed:
            break
        skip += sorted(keyed)
    return changed, added, skip, new_errors


def cmd_gate(a):
    changed, added, skip, new_errors = gate(a.lang, a.tree, a.new, a.template, a.out, a.gsicheck, a.libdir,
                                            a.rounds, a.add_only, a.history)
    if a.skip_out:
        with open(a.skip_out, "w", encoding="utf-8") as f:
            json.dump(skip, f, ensure_ascii=False, indent=1)
    print("%s: %d entries updated, %d keys added, %d left out" % (a.lang, len(changed), len(added), len(skip)))
    for k in skip:
        print("  left out: %s %s %s" % (k[1], k[3], k[4]))
    if new_errors:
        for msg, uid in sorted(new_errors):
            print("  new gsicheck error: %s %s" % (msg, uid or ""))
        sys.exit(1)


def main():
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    sub = p.add_subparsers(dest="command", required=True)

    c = sub.add_parser("combine", help="combine a base and a newer po2oo SDF")
    c.add_argument("--lang", required=True)
    c.add_argument("--template", required=True, help="en-US.sdf used for po2oo")
    c.add_argument("--base", required=True, help="po2oo output from the trusted snapshot")
    c.add_argument("--newer", required=True, help="po2oo output from the newer snapshot")
    c.add_argument("--history", nargs="*", default=[], help="po2oo output from older snapshots")
    c.add_argument("--out", required=True)
    c.set_defaults(func=cmd_combine)

    r = sub.add_parser("rekey", help="move tree entries to the keys of a newer en-US.sdf")
    r.add_argument("--lang", required=True)
    r.add_argument("--tree", required=True, help="extras/l10n/source/<lang>/localize.sdf")
    r.add_argument("--old-template", required=True, nargs="+",
                   help="en-US.sdf files the tree keys may come from; the best match is used")
    r.add_argument("--template", required=True, help="en-US.sdf extracted from the current source")
    r.add_argument("--out", required=True)
    r.set_defaults(func=cmd_rekey)

    for name, func, helptext in (("merge", cmd_merge, "merge without checking"),
                                 ("gate", cmd_gate, "merge and gate on gsicheck")):
        m = sub.add_parser(name, help=helptext)
        m.add_argument("--lang", required=True)
        m.add_argument("--tree", required=True, help="extras/l10n/source/<lang>/localize.sdf")
        m.add_argument("--new", required=True, help="po2oo (or combine) output")
        m.add_argument("--template", required=True, help="en-US.sdf used for po2oo")
        m.add_argument("--out", required=True)
        m.add_argument("--add-only", action="store_true",
                       help="fill only entries the tree has no translation for")
        m.add_argument("--history", nargs="*", default=[],
                       help="po2oo output from older snapshots; their text never replaces a tree translation")
        if name == "merge":
            m.add_argument("--skip", help="JSON list of keys to leave as they are")
        else:
            m.add_argument("--gsicheck", required=True, help="path to the gsicheck binary")
            m.add_argument("--libdir", help="directory with libtl and friends, e.g. solver/<ver>/<platform>/lib")
            m.add_argument("--rounds", type=int, default=4)
            m.add_argument("--skip-out", help="write the left-out keys here as JSON")
        m.set_defaults(func=func)

    a = p.parse_args()
    a.func(a)


if __name__ == "__main__":
    main()
