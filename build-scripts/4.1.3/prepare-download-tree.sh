#!/bin/bash
# Prepare the OpenOffice download tree: after build, move and sign files.
# Options are passed on to gpg:
# - You can run with '--batch --passphrase MY_PASSPHRASE' to avoid old versions of gpg prompting for password - Don't use on a multi-user system.
# - Use gpg-agent on newer systems.

if [ ! -d 'main' ] ; then
  echo "Run from the root of an OpenOffice build tree (the one containing the 'main' directory)."
  exit 1
fi
# TODO: Extend to cover other platforms; this is for Linux-64 only.
PLATFORM="unxlngx6.pro"
PRODUCT="Apache_OpenOffice"
DESTINATION="../binaries"
# For future use.
# FORMATS=`ls -1 main/instsetoo_native/$PLATFORM/$PRODUCT`

# Move all packages to the appropriate location in ../binaries
PACKAGES=`ls -1 main/instsetoo_native/unxlngx6.pro/Apache_OpenOffice/*/install/*/*.tar.gz` || echo "Warning: no packages found."
for PACKAGE in $PACKAGES; do
  LANGUAGE=`echo "$PACKAGE" | sed s!main/instsetoo_native/$PLATFORM/$PRODUCT/[^/]*/install/!! | sed s!_download.*!!`
  FILENAME=`basename $PACKAGE`
  OUTPUT_DIR="$DESTINATION/$LANGUAGE/"
  # For future use.
  # FORMAT=`echo $PACKAGE | sed s!main/instsetoo_native/$PLATFORM/$PRODUCT/!! | sed s!/.*!!`
  mkdir -p "$OUTPUT_DIR"
  mv "$PACKAGE" "$OUTPUT_DIR"
done

# Add hashes/signatures.
cd "$DESTINATION"
FILES=`find . -type f -name '*.tar.gz'`
for FILE in $FILES; do
  FILENAME=`basename "$FILE"`
  pushd `dirname "$FILE"`
  md5sum "$FILENAME" > "$FILENAME.md5"
  sha256sum --binary "$FILENAME" > "$FILENAME.sha256"
  # The gpg execution will fail if the file already exists.
  rm -f "$FILENAME.asc"
  # Any command-line options are passed on to gpg.
  gpg $* --armor --output "$FILENAME.asc" --detach-sig "$FILENAME"
  popd
done
