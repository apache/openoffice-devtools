#!/usr/bin/env bash
#
# Installed in /usr/local:
#   o dmake 4.13.1 (https://github.com/jimjag/dmake/archive/v4.13.1/dmake-4.13.1.tar.gz)
#   o epm 5.0.0 (https://github.com/jimjag/epm/archive/v5.0.0/epm-5.0.0.tar.gz)

set -eo pipefail

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
\rm -f solenv/inc/reporevision.lst
if [ ! -e external/unowinreg/unowinreg.dll ] ; then
	echo "Downloading unowinreg.dll..."
	wget -O external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll
fi

LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es et eu fi fr gd gl he hi hu hy it ja kab km ko lt nb nl om pl pt pt-BR ru sk sl sr sv ta th tr uk vi zh-CN zh-TW"

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
	--with-system-stdlibs \
	--enable-crashdump=yes \
	--enable-category-b \
	--enable-beanshell \
	--enable-wiki-publisher \
	--enable-bundled-dictionaries \
	--enable-opengl  \
	--enable-dbus  \
	--enable-gstreamer \
	--without-junit \
	--without-stlport \
	--with-ant-home=$ANT_HOME \
	--with-jdk-home=/usr/lib/jvm/java-1.7.0-openjdk \
	--with-package-format="rpm deb" \
	--with-lang="${LANGS}" \
	--with-epm=/usr/local/bin/epm \
	--with-dmake-path=/usr/local/bin/dmake \
	| tee config.out ) || exit 1
fi

source ./LinuxX86-64Env.Set.sh || exit 1 
./bootstrap || exit 1
if [ -e solenv/inc/reporevision.lst ]; then
	\rm solenv/inc/reporevision.lst
fi
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P7 || exit 1
cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
	dmake -P7 openofficebeta  || exit 1
	dmake -P7 sdkoobeta_en-US || exit 1
	dmake -P7 ooobetalanguagepack || exit 1
elif [ "$AOO_BUILD_DEV" = "yes" ]; then
	dmake -P7 openofficedev  || exit 1
	dmake -P7 sdkoodev_en-US || exit 1
	dmake -P7 ooodevlanguagepack || exit 1
else
	dmake -P7 ooolanguagepack || exit 1
	dmake -P7 sdkoo_en-US || exit 1 
fi
if [ "$AOO_BUILD_SRC" = "yes" ]; then
	dmake aoo_srcrelease || exit 1
fi

date "+Build ended at %H:%M:%S"

