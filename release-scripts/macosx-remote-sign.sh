#!/bin/bash
#
# macosx-remote-sign.sh : sign an Apache OpenOffice macOS .dmg on a
# machine separate from the one that built it, so only this one machine ever
# needs the Developer ID Application private key (and, if used, the notary
# credentials) - build servers never touch that key material.
#
#   ./macosx-remote-sign.sh [options] <input.dmg> <signed-output.dmg>
#
# This is release engineering tooling, not product source, so it lives here
# in openoffice-devtools rather than in the openoffice/main tree. See
# TODO-MACOS-SIGNING.md ("Remote/split signing workflow") in the openoffice
# checkout for the design this implements: "Option B" - a build server
# configures instsetoo_native's normal "dmg" package format without
# --with-macosx-codesigning-identity, which already produces an ordinary
# unsigned .dmg with no build-side changes, and hands it to this script.
#
# Deploy this script together with its siblings macosx-codesign.sh,
# macosx-check-load-commands.sh and macosx-codesign-entitlements.plist, which
# do the actual signing/notarizing/verifying. They are verbatim copies of
# openoffice trunk's main/solenv/bin/ files as of ed9fccbc30; keep them in
# sync from there rather than editing them here. No OpenOffice source
# checkout is needed on the signing host.
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
#       --release        fail if spctl rejects the result (required for release)
#       --non-release    permit signing without notarization or Gatekeeper
#                        acceptance; intended only for diagnostics
#       --sha256 HASH    verify <input.dmg> against this checksum before
#                        doing anything else (a bare digest or a full
#                        "shasum -a 256" checksum line both work)
#       --legacy-layout  first move non-code out of Contents/MacOS and loose
#                        entries out of Contents/ into Contents/Resources,
#                        leaving symlinks, and drop dangling symlinks: 4.1.x
#                        bundles cannot be sealed otherwise
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
LEGACY_LAYOUT=no
NON_RELEASE=no
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
		--non-release)     NON_RELEASE=yes; shift ;;
		--legacy-layout)   LEGACY_LAYOUT=yes; shift ;;
		--sha256)
			[ $# -ge 2 ] || { echo "$1 requires an argument" >&2; exit 2; }
			EXPECT_SHA256="$2"; shift 2 ;;
		-h|--help)         sed -n '2,56p' "$0"; exit 0 ;;
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
	echo "usage: $(basename "$0") [options] <input.dmg> <signed-output.dmg>" >&2
	exit 2
}
[ -n "$IDENTITY" ] && [ "$IDENTITY" != "-" ] || {
	echo "-i/--identity is required and must be a real Developer ID, not \"-\"" >&2
	exit 2
}
if [ "$NON_RELEASE" = yes ]; then
	[ "$RELEASE" = no ] || {
		echo "--release and --non-release cannot be used together" >&2
		exit 2
	}
elif [ -z "$NOTARY_PROFILE" ] || [ "$RELEASE" = no ]; then
	echo "release signing requires --notarize PROFILE and --release; use --non-release only for diagnostics" >&2
	exit 2
fi
[ -x "$CODESIGN" ] || {
	echo "macosx-codesign.sh not found beside this script ($CODESIGN) - see this script's header for the required deployment file list" >&2
	exit 2
}
[ -f "$SRC_DMG" ] || { echo "no such file: $SRC_DMG" >&2; exit 1; }
[ -d "$OUT_DMG" ] && { echo "output path is a directory: $OUT_DMG" >&2; exit 2; }
case "$OUT_DMG" in
	*.dmg) ;;
	*) OUT_DMG="$OUT_DMG.dmg" ;;
esac
[ -d "$OUT_DMG" ] && { echo "output path is a directory: $OUT_DMG" >&2; exit 2; }
if [ "$SRC_DMG" -ef "$OUT_DMG" ]; then
	echo "output path is the input dmg: $OUT_DMG (write to a different file)" >&2
	exit 2
fi

MOUNT_POINT=""
STAGING_DIR=""
TEMP_DIR=""
cleanup() {
	if [ -n "$MOUNT_POINT" ]; then
		hdiutil detach "$MOUNT_POINT" -quiet 2>/dev/null || true
		rmdir "$MOUNT_POINT" 2>/dev/null || true
	fi
	[ -z "$STAGING_DIR" ] || rm -rf "$STAGING_DIR"
	[ -z "$TEMP_DIR" ] || rm -rf "$TEMP_DIR"
}
trap cleanup EXIT

mount_readonly() {
	local dmg="$1"
	MOUNT_POINT=$(mktemp -d "${TMPDIR:-/tmp}/macosx-remote-sign.mount.XXXXXX")
	if ! hdiutil attach -readonly -nobrowse -mountpoint "$MOUNT_POINT" "$dmg" >/dev/null; then
		echo "could not mount $dmg" >&2
		return 1
	fi
}

detach_mounted() {
	local mount_point="$MOUNT_POINT"
	hdiutil detach "$mount_point" -quiet
	rmdir "$mount_point" 2>/dev/null || true
	MOUNT_POINT=""
}

require_developer_id_signature() {
	local target="$1" details
	details=$(codesign -dv --verbose=4 "$target" 2>&1) || {
		echo "could not inspect signature: $target" >&2
		return 1
	}
	printf '%s\n' "$details" | grep -q '^[[:space:]]*Authority=Developer ID Application:' || {
		echo "not signed with a Developer ID Application certificate: $target" >&2
		return 1
	}
}

is_macho() {
	[ -f "$1" ] && [ ! -L "$1" ] && file -b "$1" | grep '^Mach-O' >/dev/null
}

# NUL-separated: what codesign refuses to seal in Contents/MacOS, i.e. every
# entry other than Mach-O files, symlinks and the bundle's main executable.
non_code_in_macos() {
	local app="$1" exe e
	[ -d "$app/Contents/MacOS" ] || return 0
	exe=$(/usr/libexec/PlistBuddy -c 'Print CFBundleExecutable' "$app/Contents/Info.plist" 2>/dev/null) || exe=""
	while IFS= read -r -d '' e; do
		[ ! -L "$e" ] && [ "${e##*/}" != "$exe" ] && ! is_macho "$e" || continue
		printf '%s\0' "$e"
	done < <(find "$app/Contents/MacOS" -mindepth 1 -maxdepth 1 -print0)
}

# Relative symlinks keep every path the 4.1.x runtime uses resolving to the
# same file, while codesign only sees code in Contents/MacOS.
relayout_legacy_app() {
	local app="$1" contents="$1/Contents" e n
	/usr/libexec/PlistBuddy -c 'Print CFBundleExecutable' "$contents/Info.plist" >/dev/null || {
		echo "no CFBundleExecutable in $contents/Info.plist" >&2
		return 1
	}
	# codesign --strict rejects them, and they resolve to nothing anyway.
	find "$app" -type l ! -exec test -e {} \; -print -delete | sed 's/^/    removed dangling symlink: /'
	chmod u+w "$contents" "$contents/MacOS" "$contents/Resources"
	mkdir -p "$contents/Resources/ooo-program" "$contents/Resources/ooo-contents"
	while IFS= read -r -d '' e; do
		n="${e##*/}"
		if [ -d "$e" ] && find "$e" -type f -print0 | xargs -0 file --no-pad -- 2>/dev/null | grep ': Mach-O' >/dev/null; then
			echo "legacy layout: $e holds code; refusing to move it into Resources" >&2
			return 1
		fi
		[ ! -e "$contents/Resources/ooo-program/$n" ] || { echo "legacy layout: $n already in Resources/ooo-program" >&2; return 1; }
		mv "$e" "$contents/Resources/ooo-program/$n"
		ln -s "../Resources/ooo-program/$n" "$e"
	done < <(non_code_in_macos "$app")
	while IFS= read -r -d '' e; do
		n="${e##*/}"
		[ ! -L "$e" ] || continue
		case "$n" in
			Info.plist|PkgInfo|MacOS|Resources|Frameworks|PlugIns|Library|SharedSupport|_CodeSignature) continue ;;
		esac
		[ ! -e "$contents/Resources/ooo-contents/$n" ] || { echo "legacy layout: $n already in Resources/ooo-contents" >&2; return 1; }
		mv "$e" "$contents/Resources/ooo-contents/$n"
		ln -s "Resources/ooo-contents/$n" "$e"
	done < <(find "$contents" -mindepth 1 -maxdepth 1 -print0)
}

if [ -n "$EXPECT_SHA256" ]; then
	echo "==> verifying checksum of $SRC_DMG"
	# Accept either a bare digest or a full checksum line as written by
	# shasum/hash-sign.sh ("<hash>  <file>" / "<hash> *<file>"): compare
	# against the first field.
	EXPECT_SHA256="${EXPECT_SHA256%%[[:space:]]*}"
	actual=$(shasum -a 256 "$SRC_DMG" | awk '{print $1}')
	if [ "$actual" != "$EXPECT_SHA256" ]; then
		echo "checksum mismatch: expected $EXPECT_SHA256, got $actual" >&2
		exit 1
	fi
	echo "    OK"
fi

echo "==> mounting $SRC_DMG"
mount_readonly "$SRC_DMG"
VOLUME_NAME=$(diskutil info "$MOUNT_POINT" | awk '/Volume Name/{sub(/^[^:]*: +/, ""); print; exit}')
[ -n "$VOLUME_NAME" ] || VOLUME_NAME=$(basename "$MOUNT_POINT")

STAGING_DIR=$(mktemp -d)
# ditto into a not-yet-existing directory so it takes the volume root's mode;
# the 0700 mktemp directory would otherwise become the rebuilt volume's root.
VOLUME_DIR="$STAGING_DIR/volume"
echo "==> copying volume contents to $VOLUME_DIR"
# Copy everything on the volume, not just the .app: an install DMG normally
# also carries an Applications symlink, license/readme folders, and a
# background image, all of which the rebuilt dmg below should keep too.
ditto "$MOUNT_POINT" "$VOLUME_DIR"
# Shipped dmgs put FinderInfo on read-only files, which macosx-codesign.sh's
# xattr -cr cannot strip and codesign rejects; recopy the app without it.
for app in "$VOLUME_DIR"/*.app; do
	[ -d "$app" ] || continue
	rm -rf "$app"
	ditto --norsrc --noextattr --noqtn "$MOUNT_POINT/${app##*/}" "$app"
done

detach_mounted

apps=("$VOLUME_DIR"/*.app)
[ -d "${apps[0]}" ] || { echo "no .app bundle found in $SRC_DMG" >&2; exit 1; }
[ ${#apps[@]} -eq 1 ] || {
	echo "expected exactly one .app in $SRC_DMG, found ${#apps[@]}:" >&2
	printf '  %s\n' "${apps[@]}" >&2
	exit 1
}
APP="${apps[0]}"

if [ "$LEGACY_LAYOUT" = yes ]; then
	echo "==> moving non-code out of $APP/Contents/MacOS"
	relayout_legacy_app "$APP"
else
	non_code=()
	while IFS= read -r -d '' e; do non_code+=("${e#"$APP"/}"); done < <(non_code_in_macos "$APP")
	[ ${#non_code[@]} -eq 0 ] || {
		echo "$APP/Contents/MacOS holds ${#non_code[@]} non-code entries, which codesign will not seal:" >&2
		printf '  %s\n' "${non_code[@]:0:10}" >&2
		echo "this is the layout of 4.1.x and 4.2.0 dev builds; rerun with --legacy-layout" >&2
		exit 1
	}
fi

sign_args=(-i "$IDENTITY")
[ -z "$KEYCHAIN" ] || sign_args+=(-k "$KEYCHAIN")
[ -z "$ENTITLEMENTS" ] || sign_args+=(-e "$ENTITLEMENTS")
[ -z "$NOTARY_PROFILE" ] || sign_args+=(--notarize "$NOTARY_PROFILE")
[ "$RELEASE" = no ] || sign_args+=(--release)

echo "==> signing $APP"
"$CODESIGN" "${sign_args[@]}" "$APP"
require_developer_id_signature "$APP"

echo "==> building $OUT_DMG  (volume: $VOLUME_NAME)"
mkdir -p "$(dirname "$OUT_DMG")"
# Build and sign the image in a temp directory beside the output (same
# filesystem, so the final mv is atomic) and only publish it once the signing
# succeeds: a failure here must not leave an unsigned image at $OUT_DMG. It
# keeps the final file name because codesign takes the dmg's identifier from it.
TEMP_DIR=$(mktemp -d "$(dirname "$OUT_DMG")/.macosx-remote-sign.XXXXXX")
TEMP_DMG="$TEMP_DIR/$(basename "$OUT_DMG")"
hdiutil create -srcfolder "$VOLUME_DIR" -volname "$VOLUME_NAME" -fs HFS+ -format UDZO -ov "$TEMP_DMG"

echo "==> signing $TEMP_DMG"
"$CODESIGN" "${sign_args[@]}" "$TEMP_DMG"
require_developer_id_signature "$TEMP_DMG"

if [ -n "$NOTARY_PROFILE" ]; then
	echo "==> validating enclosed app staple"
	mount_readonly "$TEMP_DMG"
	final_apps=("$MOUNT_POINT"/*.app)
	[ -d "${final_apps[0]}" ] && [ ${#final_apps[@]} -eq 1 ] || {
		echo "rebuilt dmg does not contain exactly one .app" >&2
		exit 1
	}
	xcrun stapler validate "${final_apps[0]}"
	detach_mounted
fi

mv -f "$TEMP_DMG" "$OUT_DMG"
rmdir "$TEMP_DIR"
TEMP_DIR=""

echo "==> done: $OUT_DMG"
