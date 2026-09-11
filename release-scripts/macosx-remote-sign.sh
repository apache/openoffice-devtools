#!/bin/bash
#
# macosx-remote-sign.sh : sign an unsigned Apache OpenOffice macOS .dmg on a
# machine separate from the one that built it, so only this one machine ever
# needs the Developer ID Application private key (and, if used, the notary
# credentials) - build servers never touch that key material.
#
#   ./macosx-remote-sign.sh [options] <unsigned.dmg> <signed-output.dmg>
#
# This is release engineering tooling, not product source, so it lives here
# in openoffice-devtools rather than in the openoffice/main tree. See
# TODO-MACOS-SIGNING.md ("Remote/split signing workflow") in the openoffice
# checkout for the design this implements: "Option B" - a build server
# configures instsetoo_native's normal "dmg" package format without
# --with-macosx-codesigning-identity, which already produces an ordinary
# unsigned .dmg with no build-side changes, and hands it to this script.
#
# Deploy this script to the signing host alongside copies of three files from
# openoffice/main/solenv/bin/: macosx-codesign.sh,
# macosx-check-load-commands.sh, macosx-codesign-entitlements.plist. All the
# actual signing/notarizing/verifying is done by macosx-codesign.sh, resolved
# here as a sibling of wherever this script itself was invoked from - no
# OpenOffice source checkout is needed on the signing host.
#
# Not to be confused with this directory's hash-sign.sh, which GPG-signs
# release artifacts for Apache distribution integrity (checksums + .asc
# files). This does Apple code-signing/notarization of the application
# itself.
#
# Options:
#   -i, --identity ID    Developer ID Application identity (required; "-"
#                        (ad-hoc) is refused - pointless on a signing host)
#   -k, --keychain PATH  keychain holding the identity (default: search list)
#   -e, --entitlements   entitlements plist (default: macosx-codesign.sh's own)
#       --notarize PROFILE
#                        passed through to macosx-codesign.sh for both the
#                        extracted app and the rebuilt dmg (see its --help)
#       --release        passed through: fail if spctl rejects the result
#       --sha256 HASH    verify <unsigned.dmg> against this checksum before
#                        doing anything else
#   -h, --help
#
# Known limitation: the dmg is rebuilt with a plain "hdiutil create", without
# the Rez license-resource step instsetoo_native's own dmg packaging does
# when HIDELICENSEDIALOG is unset (see simplepackage.pm:590-594 in the
# openoffice tree). A signing host has no access to that sla.r resource or
# the build tree's include path, so a license-enabled build's attached
# resource would be silently dropped by a rebuild here. Not an issue while
# HIDELICENSEDIALOG stays set, as it is today.

set -euo pipefail

SRCDIR=$(cd "$(dirname "$0")" && pwd)
CODESIGN="$SRCDIR/macosx-codesign.sh"

IDENTITY=""
KEYCHAIN=""
ENTITLEMENTS=""
NOTARY_PROFILE=""
RELEASE=no
EXPECT_SHA256=""
SRC_DMG=""
OUT_DMG=""

while [ $# -gt 0 ]; do
	case "$1" in
		-i|--identity)
			[ $# -ge 2 ] || { echo "$1 requires an argument" >&2; exit 2; }
			IDENTITY="$2"; shift 2 ;;
		-k|--keychain)
			[ $# -ge 2 ] || { echo "$1 requires an argument" >&2; exit 2; }
			KEYCHAIN="$2"; shift 2 ;;
		-e|--entitlements)
			[ $# -ge 2 ] || { echo "$1 requires an argument" >&2; exit 2; }
			ENTITLEMENTS="$2"; shift 2 ;;
		--notarize)
			[ $# -ge 2 ] || { echo "$1 requires an argument" >&2; exit 2; }
			NOTARY_PROFILE="$2"; shift 2 ;;
		--release)         RELEASE=yes; shift ;;
		--sha256)
			[ $# -ge 2 ] || { echo "$1 requires an argument" >&2; exit 2; }
			EXPECT_SHA256="$2"; shift 2 ;;
		-h|--help)         sed -n '2,49p' "$0"; exit 0 ;;
		-*)                echo "unknown option: $1" >&2; exit 2 ;;
		*)
			if [ -z "$SRC_DMG" ]; then SRC_DMG="$1"
			elif [ -z "$OUT_DMG" ]; then OUT_DMG="$1"
			else echo "unexpected argument: $1" >&2; exit 2
			fi
			shift ;;
	esac
done

[ -n "$SRC_DMG" ] && [ -n "$OUT_DMG" ] || {
	echo "usage: $(basename "$0") [options] <unsigned.dmg> <signed-output.dmg>" >&2
	exit 2
}
[ -n "$IDENTITY" ] && [ "$IDENTITY" != "-" ] || {
	echo "-i/--identity is required and must be a real Developer ID, not \"-\"" >&2
	exit 2
}
[ -x "$CODESIGN" ] || {
	echo "macosx-codesign.sh not found beside this script ($CODESIGN) - see this script's header for the required deployment file list" >&2
	exit 2
}
[ -f "$SRC_DMG" ] || { echo "no such file: $SRC_DMG" >&2; exit 1; }

MOUNT_POINT=""
STAGING_DIR=""
cleanup() {
	[ -z "$MOUNT_POINT" ] || hdiutil detach "$MOUNT_POINT" -quiet 2>/dev/null || true
	[ -z "$STAGING_DIR" ] || rm -rf "$STAGING_DIR"
}
trap cleanup EXIT

if [ -n "$EXPECT_SHA256" ]; then
	echo "==> verifying checksum of $SRC_DMG"
	actual=$(shasum -a 256 "$SRC_DMG" | awk '{print $1}')
	if [ "$actual" != "$EXPECT_SHA256" ]; then
		echo "checksum mismatch: expected $EXPECT_SHA256, got $actual" >&2
		exit 1
	fi
	echo "    OK"
fi

echo "==> mounting $SRC_DMG"
MOUNT_POINT=$(hdiutil attach -readonly -nobrowse "$SRC_DMG" | tail -1 | awk -F'\t' '{print $NF}')
[ -n "$MOUNT_POINT" ] && [ -d "$MOUNT_POINT" ] || {
	echo "could not mount $SRC_DMG" >&2
	exit 1
}
VOLUME_NAME=$(diskutil info "$MOUNT_POINT" | awk -F': +' '/Volume Name/{print $2; exit}')
[ -n "$VOLUME_NAME" ] || VOLUME_NAME=$(basename "$MOUNT_POINT")

STAGING_DIR=$(mktemp -d)
echo "==> copying volume contents to $STAGING_DIR"
# Copy everything on the volume, not just the .app: an install DMG normally
# also carries an Applications symlink, license/readme folders, and a
# background image, all of which the rebuilt dmg below should keep too.
ditto "$MOUNT_POINT" "$STAGING_DIR"

hdiutil detach "$MOUNT_POINT" -quiet
MOUNT_POINT=""

apps=("$STAGING_DIR"/*.app)
[ -d "${apps[0]}" ] || { echo "no .app bundle found in $SRC_DMG" >&2; exit 1; }
[ ${#apps[@]} -eq 1 ] || {
	echo "expected exactly one .app in $SRC_DMG, found ${#apps[@]}:" >&2
	printf '  %s\n' "${apps[@]}" >&2
	exit 1
}
APP="${apps[0]}"

sign_args=(-i "$IDENTITY")
[ -z "$KEYCHAIN" ] || sign_args+=(-k "$KEYCHAIN")
[ -z "$ENTITLEMENTS" ] || sign_args+=(-e "$ENTITLEMENTS")
[ -z "$NOTARY_PROFILE" ] || sign_args+=(--notarize "$NOTARY_PROFILE")
[ "$RELEASE" = no ] || sign_args+=(--release)

echo "==> signing $APP"
"$CODESIGN" "${sign_args[@]}" "$APP"

echo "==> building $OUT_DMG  (volume: $VOLUME_NAME)"
mkdir -p "$(dirname "$OUT_DMG")"
hdiutil create -srcfolder "$STAGING_DIR" -volname "$VOLUME_NAME" -fs HFS+ -format UDZO -ov "$OUT_DMG"

echo "==> signing $OUT_DMG"
"$CODESIGN" "${sign_args[@]}" "$OUT_DMG"

echo "==> done: $OUT_DMG"
