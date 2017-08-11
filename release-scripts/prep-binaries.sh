#!/bin/sh
# Prepare the OpenOffice download tree: after build and move files.
# Use hash-sign.sh to sign the binaries (and source files).
## NOTE: This is macOS and Linux specific... 

if [ ! -d 'main' ] ; then
  echo "Run from the root of an OpenOffice build tree (the one containing the 'main' directory)."
  exit 1
fi

# TODO: Extend to cover other platforms; this is for Linux and macOS only.
PLATFORMS="unxlngx6.pro unxlngi6.pro unxmaccx.pro"
PRODUCTS="Apache_OpenOffice Apache_OpenOffice_languagepack Apache_OpenOffice_SDK"
DESTINATION="../binaries"
# For future use.
# FORMATS=`ls -1 main/instsetoo_native/$PLATFORM/$PRODUCT`

# Move all packages to the appropriate location in ../binaries
for PLATFORM in $PLATFORMS; do
  echo "Looking for main/instsetoo_native/$PLATFORM"
  if [ -d "main/instsetoo_native/$PLATFORM" ]; then
    echo "Working on main/instsetoo_native/$PLATFORM"
    for PRODUCT in $PRODUCTS; do
      if [ "$PLATFORM" = "unxmaccx.pro" ]; then
        FTYPE="dmg"
      else
        FTYPE="tar.gz"
      fi
      PACKAGES=`ls -1 main/instsetoo_native/$PLATFORM/$PRODUCT/*/install/*/*.$FTYPE` || echo "Warning: no packages found."
      for PACKAGE in $PACKAGES; do
        LANGUAGE=`echo "$PACKAGE" | sed -e "s:main/instsetoo_native/$PLATFORM/$PRODUCT/[^/]*/install/::" | sed -e "s:/.*::"`
        FILENAME=`basename $PACKAGE`
        if [ "$PRODUCT" = "Apache_OpenOffice_SDK" ]; then
          OUTPUT_DIR="$DESTINATION/SDK/"
        else
          OUTPUT_DIR="$DESTINATION/$LANGUAGE/"
        fi
        # For future use.
        # FORMAT=`echo $PACKAGE | sed s!main/instsetoo_native/$PLATFORM/$PRODUCT/!! | sed s!/.*!!`
        mkdir -p "$OUTPUT_DIR"
        echo " -> $OUTPUT_DIR$FILENAME"
        cp "$PACKAGE" "$OUTPUT_DIR"
      done
    done
  fi
done

