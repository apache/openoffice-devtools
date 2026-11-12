# Integrating Pootle translations into the source tree

`sdf_integrate.py` brings translations from the PO files in
[apache/openoffice-translation](https://github.com/apache/openoffice-translation)
into `extras/l10n/source/<lang>/localize.sdf`.

It merges instead of replacing the file. `po2oo` writes English for every entry
that is fuzzy or empty in Pootle, and many of those are translated in the tree,
so a whole-file replacement loses them (15,441 entries for `am` in 2026).

Needs Python 3.7+, translate-toolkit (`po2oo`) and `gsicheck` from a built
OpenOffice tree. No other dependencies.

## All languages in one command

`update_translations.py` does what the Pootle server's template update and an
integration round do together, for every tree language or for `--langs de,fr`:

    update_translations.py --aoo ~/src/openoffice            # report only
    update_translations.py --aoo ~/src/openoffice --apply    # write the tree files

- English template: `../scripts/en-US.sdf`, or `--extract` to make a fresh one
  from `--aoo` (Linux, configured and built; `localize -e` fails on macOS).
- PO files: cloned from apache/openoffice-translation into the work directory,
  only the languages asked for, or `--po-repo` for an existing checkout.
- translate-toolkit: 2.2.5, the server's version, installed into a venv in the
  work directory unless this Python already has it.
- `gsicheck`: from `main/solver` in `--aoo`, or `--solver`.

Per language it re-keys the tree file (the steps below), makes templates, runs
`fix_msgctxt.py`, `pomerge` and `po2oo`, and gates the merge on `gsicheck`. The
tree is matched against every committed version of `../scripts/en-US.sdf` and
the 2022 one in openoffice-translation; if it is already keyed to the current
template, nothing moves.

`--mode add-only` (the default) fills only entries the tree has no translation
for. `--mode update` also replaces tree text with newer PO text. Use it with
`--history` and a few older openoffice-translation revisions, so that text from
a Pootle rollback is not taken; this needs the repository's full history. In
2026 the revisions `ebbd469a1c a5e2ff9195 a962caea70 4b986fdc1b 3a07d1c27e
60e3e58b9e c1358ae8ce` held back 1,086 `uk` and 789 `pl` entries.

Each run gets its own directory under `--workdir` with `report.md` (entries
moved, updated, added and left out, and translated entries before and after),
`langs/<lang>/` with the intermediate files and a log, `pot/`, and with
`--export-po` the updated PO files that Pootle would offer translators. Nothing
is committed. Languages that have PO files but no tree file are listed, not
added. A second run on the result reports no changes.

## Steps, for one language

1. Convert the PO files with the English SDF they were made from:

       po2oo --template=en-US.sdf --skipsource --keeptimestamp \
             --language=de PO-Files/de de.po2oo.sdf

   `po2oo` writes the `--language` value into every line, so use the tree's
   code (`pt-BR`) here and for `--lang` below. Only the PO directory keeps
   Pootle's form (`PO-Files/pt_BR`).

2. Optional: if a newer snapshot may have rolled translations back, convert
   the trusted snapshot, the newer one and some older ones, then combine.
   A newer value is taken only if it is not blank and does not match text
   from any of the `--history` files.

       sdf_integrate.py combine --lang de --template en-US.sdf \
           --base de.2022.sdf --newer de.head.sdf \
           --history de.2019.sdf de.2021.sdf --out de.po2oo.sdf

3. Merge and gate:

       sdf_integrate.py gate --lang de \
           --tree extras/l10n/source/de/localize.sdf \
           --new de.po2oo.sdf --template en-US.sdf --out de.merged.sdf \
           --gsicheck main/solver/<ver>/<platform>/bin/gsicheck \
           --libdir main/solver/<ver>/<platform>/lib \
           --skip-out de.left-out.json

   Only entries whose translation changed are updated. Every other line, its
   line ending and its timestamp stay as they are. Keys that are new and
   translated are added after the last line of the same source file.

   Entries whose new text adds a `gsicheck` error that the tree file does not
   already have keep their tree text, and are listed as "left out". Report
   those to the language team. The command exits 1 if any new error is left
   that it cannot tie to an entry.

4. Copy `de.merged.sdf` over the tree file and commit it, one commit per
   language. `merge --skip de.left-out.json` gives the same result without
   running `gsicheck` again.

## When the English SDF is newer than the PO files

The Help sources lost their `oldref` attributes in 2024-25. `oldref` was the
help key's lid, so tree entries keyed from the 2022 `en-US.sdf` no longer match
the source and the build shows English for them. Extract a current `en-US.sdf`
(`localize -e -l en-US -f en-US.sdf` on Linux), then per language:

1. Move tree entries to the current keys. An entry moves only if its English
   is the same under the old and the new key; everything else is left alone.

       sdf_integrate.py rekey --lang de \
           --tree extras/l10n/source/de/localize.sdf \
           --old-template en-US.2022.sdf --template en-US.sdf \
           --out de.rekeyed.sdf

2. Make templates from the new `en-US.sdf` (`oo2po --pot en-US.sdf pot/`) and
   re-key the PO files to them before `pomerge`. Without this, units whose
   msgctxt holds the old lid come out fuzzy or empty, and `po2oo` writes
   translations to locations whose English has changed.

       fix_msgctxt.py --template pot --input PO-Files/de --output de.fixed
       pomerge --template=pot/ --input=de.fixed/ --output=de.merged/
       po2oo --template=en-US.sdf --skipsource --keeptimestamp \
             --language=de de.merged/ de.po2oo.sdf

   `fix_msgctxt.py` runs under the same Python as translate-toolkit.

3. Gate as above, against the re-keyed file. `--add-only` takes PO text only
   where the tree has no translation, for when the tree already holds an
   earlier, checked integration of the same PO files.

       sdf_integrate.py gate --add-only --lang de --tree de.rekeyed.sdf \
           --new de.po2oo.sdf --template en-US.sdf --out de.merged.sdf ...
