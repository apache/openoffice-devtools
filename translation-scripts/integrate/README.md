# Integrating Pootle translations into the source tree

`sdf_integrate.py` brings translations from the PO files in
[apache/openoffice-translation](https://github.com/apache/openoffice-translation)
into `extras/l10n/source/<lang>/localize.sdf`.

It merges instead of replacing the file. `po2oo` writes English for every entry
that is fuzzy or empty in Pootle, and many of those are translated in the tree,
so a whole-file replacement loses them (15,441 entries for `am` in 2026).

Needs Python 3.7+, translate-toolkit (`po2oo`) and `gsicheck` from a built
OpenOffice tree. No other dependencies.

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
