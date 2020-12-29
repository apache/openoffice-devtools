#!/usr/bin/env bash

#
# Parse options
#
AOO_SKIP_CONFIG=
AOO_JUST_CONFIG=
AOO_VERBOSE_BUILD=
AOO_BUILD_TYPE=
AOO_BUILD_VERSION=
AOO_BUILD_BETA=
AOO_BUILD_DEV=
AOO_BUILD_SRC=

while true; do
	case "$1" in
		"--verbose" ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
		"--skip-config" ) AOO_SKIP_CONFIG="yes"; shift ;;
		"--just-config" ) AOO_JUST_CONFIG="yes"; shift ;;
		"--build-src" ) AOO_BUILD_SRC="yes"; shift ;;
		"--dev" ) AOO_BUILD_TYPE="Apache OpenOffice Test Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_DEV="yes"; AOO_BUILD_BETA=""; shift ;;
		"--beta" ) AOO_BUILD_TYPE="Apache OpenOffice Beta Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_BETA="yes"; AOO_BUILD_DEV=""; shift ;;
		"--" ) shift; break ;;
		"" ) break ;;
		* ) echo "unknown option: $1"; shift ;;
	esac
done

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi

wget -O external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll

LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"

if [ ! -e configure -o configure.in -nt configure ] ; then
	echo "Running autoconf..."
	autoconf || exit 1
fi
./configure   \
	--with-build-version="$(date +"%Y-%m-%d %H:%M") - `uname -sm`${AOO_BUILD_VERSION}" \
	${AOO_VERBOSE_BUILD} \
	--with-system-stdlibs \
	--enable-crashdump=yes \
	--enable-category-b \
	--enable-wiki-publisher \
	--enable-bundled-dictionaries \
	--enable-opengl  \
	--enable-dbus  \
	--enable-gstreamer \
	--without-junit \
	--without-stlport \
	--with-ant-home=$HOME/ant \
	--with-package-format="rpm deb" \
	--with-lang="${LANGS}" \
	--with-epm=/usr/local/bin/epm \
	--with-dmake-path=/usr/local/bin/dmake \
	| tee config.out || exit 1

source ./LinuxX86-64Env.Set.sh || exit 1 
./bootstrap || exit 1
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P6 || exit 1
cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
	dmake -P6 openofficebeta  || exit 1
	dmake -P6 sdkoobeta_en-US || exit 1
	dmake -P6 ooobetalanguagepack || exit 1
elif [ "$AOO_BUILD_DEV" = "yes" ]; then
	dmake -P6 openofficedev  || exit 1
	dmake -P6 sdkoodev_en-US || exit 1
	dmake -P6 ooodevlanguagepack || exit 1
else
	dmake -P6 ooolanguagepack || exit 1
	dmake -P6 sdkoo_en-US || exit 1 
fi
if [ "$AOO_BUILD_SRC" = "yes" ]; then
	dmake aoo_srcrelease || exit 1
fi

date "+Build ended at %H:%M:%S"

