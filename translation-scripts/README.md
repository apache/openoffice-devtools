# OpenOffice translation scripts

Two sets of tools live here:

- `integrate/`: what an OpenOffice developer runs to bring the translations in
  `extras/l10n/source/<lang>/localize.sdf` up to date. It needs no Pootle server.
- `scripts/`: the scripts the Pootle server at translate.apache.org is run with,
  and `scripts/en-US.sdf`, the English strings of the current source.

The process as a whole, and its history, is on the wiki:
[Translation Process](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=103092505).

## Updating the translations in the source tree

`integrate/update_translations.py` does in one command what the Pootle server
and an integration round do together, for every language in the tree.

You need:

- an OpenOffice checkout (the directory with `main/` and `extras/`) in which
  `l10ntools` is built, for `gsicheck`. A full build is only needed for
  `--extract`, below.
- Python 3.7 or later and git. The script installs translate-toolkit 2.2.5, the
  version the Pootle server runs, into a venv in its work directory, and clones
  the PO files from [apache/openoffice-translation](https://github.com/apache/openoffice-translation),
  only for the languages you ask for, unless you pass `--po-repo`.

Then:

    cd ~/src      # the directory holding openoffice/ and openoffice-devtools/
    python3 openoffice-devtools/translation-scripts/integrate/update_translations.py --aoo openoffice

This only reports, and keeps its work in `./aoo-l10n-work` (`--workdir` to
change it), outside the checkout. Read `aoo-l10n-work/<run>/report.md`: per language, the
entries re-keyed, updated, added and left out, and the translated entries
before and after. When it looks right, run again with `--apply`, check
`git diff --stat extras/l10n`, and commit the changed `localize.sdf` files. A
second run on the result reports no changes; if it does not, something is wrong.

Common options:

| Option | Use |
|---|---|
| `--langs de,fr` | only these languages (tree codes, `pt-BR` not `pt_BR`) |
| `--mode update --history REV...` | also replace tree text with newer PO text, except text found in the given older openoffice-translation revisions (a Pootle rollback); needs the full history |
| `--extract` | make a fresh `en-US.sdf` from `--aoo` first; Linux, fully built tree |
| `--save-template` | with `--extract`, write it over `scripts/en-US.sdf`; commit that too |
| `--export-po` | also write the updated PO files, as Pootle would offer them to translators |
| `--po-repo DIR`, `--solver DIR`, `--python PY` | use an existing checkout, gsicheck build or toolkit |

When strings change in the source, refresh the template first: on a Linux
build, run with `--extract --save-template --apply`, then commit
`scripts/en-US.sdf` here and the tree files in OpenOffice. The tree files are
re-keyed against the template, so the two must stay in step.

To check a result in a real build, configure a Linux tree with
`--with-lang="de ja"` and compare the help in `helpcontent2/<platform>/bin/xhp_*_<lang>.zip`
with a build of the unchanged files.

The individual tools (`sdf_integrate.py rekey|combine|merge|gate` and
`fix_msgctxt.py`) and the reasons behind each step are described in
`integrate/README.md`.

## The Pootle server scripts

These run on the server, from a Python 2.7 virtualenv with Pootle 2.8.2, and
use paths in the maintainer's home directory. They are kept here as the record
of how the server works. In order:

| Script | Step |
|---|---|
| `10_Download.sh` | fetch `en-US.sdf` from the AOO42X nightly build |
| `20_sdf2pot.sh` | `oo2po --pot` into `POT-UI/` and `POT-HELP/`; skips an empty file |
| `25_sync-pot-files2db.sh` | load the templates into Pootle (`update_stores`) |
| `30_sync-db2files.sh` | write each language from Pootle to PO files (`sync_stores --overwrite`) |
| `40_pomerge.sh` | `pomerge` each language against the new templates |
| `50_sync-po-files2db.sh` | load the merged PO files back (`update_stores`) |
| `60_cp2home.sh` | copy the PO files into the git work directory, published as openoffice-translation |
| `mksdf.sh` | `po2oo` per language, for integration |

`LanguageLists.sh` holds the language lists the others read, `mklangdirs.sh`
and `reset-pootle.sh` set up language directories and restore PO files after a
bad template import, `mnrun.sh` is the nightly cron entry (all commented out
here), and `UpdateTranslation.sh` and `UpdateTranslation2.sh` hold the same steps
in one script (in the first most of them are commented out; the second asks
whether the `en-US.sdf` it finds is a recent one). The unnumbered `Download.sh`,
`sdf2pot.sh`, `sync-*.sh`, `pomerge.sh` and `cp2home.sh` are identical copies of
the numbered ones. `TranslationAOO.pdf` is the maintainer's description of the
server, in German.

`scripts/en-US.sdf` is not fetched by these scripts. It is a trunk extract kept
for `integrate/`. The nightly copy is empty: it is dated 2026-11-06, the day of
the last AOO42X nightly build, which failed.
