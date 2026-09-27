# updateVersion

Version bookkeeping for an Apache OpenOffice source tree. Both scripts read the
tree's current state and derive everything else, so the same commands are
correct on any release branch (AOO41X, AOO50X, trunk, ...).

## updateVersion.sh

    updateVersion.sh [options] <version> [oo_path]

Sets the product version everywhere it is recorded:

| file | what it gets |
| --- | --- |
| `main/solenv/inc/minor.mk` | `RSCVERSION`, `RSCREVISION`, `BUILD`, `LAST_MINOR`, `SOURCEVERSION` |
| `main/solenv/inc/version.lst` | major/minor/micro, and the release date with `--date` |
| `main/sysui/desktop/productversion.mk` | `PRODUCTVERSION`, `PRODUCTVERSIONSHORT` |
| `main/odk/util/makefile.pmk` | `PRODUCT_RELEASE` |
| `main/solenv/bin/srcrelease.xml` | `aoo.ver` |
| `main/setup_native/source/win32/nsis/downloadtemplate.nsi` | `VIProductVersion` |
| `main/instsetoo_native/util/openoffice.lst` | the version, package, brand, service-tag, database-name and update-URL entries |

Options: `-b/--build <n|keep>` (default: current `BUILD` + 1), `-m/--milestone
<n>` (default 1), `-p/--previous <ver|none>`, `-d/--date <yyyy-mm-dd>`,
`-c/--config <file>`, `-n/--dry-run`. Run the dry run first; it prints every
line it would touch. Progress is also appended to `/tmp/updateVersion_<date>.log`.

    ./updateVersion.sh -n 4.1.18 ../openoffice      # AOO41X
    ./updateVersion.sh 5.0.1 ../openoffice          # AOO50X

Values that differ per branch are handled by reading, not by assuming:

* Component count is preserved, so `PRODUCTVERSION` in `productversion.mk`
  stays `4.1.18` on AOO41X and `5.0` on AOO50X.
* `RSCVERSION`/`SOURCEVERSION` are the version's digits joined: 4.1.18 ->
  `4118`, 5.0.1 -> `501`.
* Values that merely contain the version (`UPDATEURL`, `DATABASENAME`,
  `SERVICETAG_*NAME`) are rewritten only where the version being replaced
  actually appears, so AOO41X's `aoo4117` becomes `aoo4118` while AOO50X's
  version-independent `aoonext` URLs are left alone.
* `PREVIOUS_VERSION` is set to the version being replaced only when the bump
  stays inside one release line; use `--previous` to set it across lines.

`updateVersion.config` maps files to keys and rules; its header documents the
format. Files a branch does not have are skipped.

## bump.pl

    bump.pl [oo_path]

For the rebuilds between two version bumps: increments the build number and the
milestone in `minor.mk` and the build field of `VIProductVersion` in
`downloadtemplate.nsi`, leaving the product version alone.

## bumpYear.pl

    bumpYear.pl [-n|--dry-run] [-y|--year <yyyy>] [oo_path]

The yearly copyright-year update. The current year is read from the ASF line
of `main/NOTICE`; the default target is that year + 1. Updates `LICENSE`,
`LICENSE_ALv2`, `helpauthoring/license/LICENSE`, `NOTICE`, the About box
(`cui/source/dialogs/about.cxx`), `OOOBASEVERSIONYEAR` in `version.lst` (which
also sets the Windows `.rc` copyright), the NSIS `LegalCopyright`, the SDK HTML
footers and the `packinfo_*.txt` files. Afterwards it lists any other
`2011-`/`2012-<year> ... Apache Software Foundation` line still at the old year.

    ./bumpYear.pl -n ../openoffice      # AOO41X: 2025 -> 2026

## Not covered

Moving to a new major line (4.x -> 5.x) additionally changes install paths,
SDK examples and extension min/max versions in a few dozen files (`openoffice4`
-> `openoffice5` and friends). That is a one-off, done by hand.
