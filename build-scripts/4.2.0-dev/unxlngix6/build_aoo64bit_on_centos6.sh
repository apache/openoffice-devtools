#!/bin/bash

#
# Parse options
#
AOO_SKIP_CONFIG=
AOO_VERBOSE_BUILD=
AOO_BUILD_TYPE="Community Build"
AOO_BUILD_VERSION=

while true; do
  case "$1" in
    "--verbose" ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
    "--skip-config" ) AOO_SKIP_CONFIG="yes"; shift ;;
    "--dev" ) AOO_BUILD_TYPE="Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; shift ;;
    "--" ) shift; break ;;
    "" ) break ;;
    * ) echo "unknown option: $1"; shift ;;
  esac
done

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi

if [ ! -e external/unowinreg/unowinreg.dll ] ; then
	echo "Downloading unowinreg.dll..."
    wget -O external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll
fi

LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"

if [ -e configure.in ]; then
    AOO_CONF_T="configure.in"
else
    AOO_CONF_T="configure.ac"
fi
if [ ! -e configure -o $AOO_CONF_T -nt configure ] ; then
	echo "Running autoconf..."
	autoconf || exit 1
fi

if [ "$AOO_SKIP_CONFIG" != "yes" ]; then
    ( ./configure   \
	--with-build-version="$(date +"%Y-%m-%d %H:%M:%S (%a, %d %b %Y)") - `uname -sm`${AOO_BUILD_VERSION}" \
	${AOO_VERBOSE_BUILD} \
	--with-vendor="Apache OpenOffice ${AOO_BUILD_TYPE}" \
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
	--with-ant-home=$ANT_HOME \
	--with-package-format="rpm deb" \
	--with-lang="${LANGS}" \
	--with-dmake-url=http://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2 \
	--with-epm-url=http://sourceforge.net/projects/oooextras.mirror/files/epm-3.7.tar.gz \
	| tee config.out ) || exit 1
fi

source ./LinuxX86-64Env.Set.sh || exit 1 
./bootstrap || exit 1
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P5 || exit 1
cd util
dmake -P2 ooolanguagepack || exit 1
dmake -P2 sdkoo_en-US || exit 1 

date "+Build ended at %H:%M:%S"

