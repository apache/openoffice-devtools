#!/usr/bin/env bash
#
# Installed in /usr/local:
#   o dmake 4.13.1 (https://github.com/jimjag/dmake/archive/v4.13.1/dmake-4.13.1.tar.gz)
#   o epm 5.0.0 (https://github.com/jimjag/epm/archive/v5.0.0/epm-5.0.0.tar.gz)

set -eo pipefail

date > config.out
echo "Invocation:" >> config.out
echo "$ $0 $@" >> config.out
echo >> config.out

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
AOO_DEBUG=
AOO_LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"
AOO_PACKAGE_FORMAT="deb rpm"

while true; do
	case "$1" in
		"--verbose" ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
		"--skip-config" ) AOO_SKIP_CONFIG="yes"; shift ;;
		"--just-config" ) AOO_JUST_CONFIG="yes"; shift ;;
		"--build-src" ) AOO_BUILD_SRC="yes"; shift ;;
		"--dev" ) AOO_BUILD_TYPE="Apache OpenOffice Test Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_DEV="yes"; AOO_BUILD_BETA=""; shift ;;
		"--beta" ) AOO_BUILD_TYPE="Apache OpenOffice Beta Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_BETA="yes"; AOO_BUILD_DEV=""; shift ;;
		"--langs" ) shift ; AOO_LANGS="$1"; shift ;;
                "--symbols" ) AOO_DEBUG="$AOO_DEBUG --enable-symbols=yes"; shift ;;
		"--debug" ) AOO_DEBUG="$AOO_DEBUG --enable-debug"; shift ;;
		"--package-format" ) shift; AOO_PACKAGE_FORMAT="$1"; shift ;;
		"--" ) shift; break ;;
		"" ) break ;;
		* ) echo "unknown option: $1"; exit 1 ;;
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
    if [ -e /usr/local/bin/epm ]; then
        epm_param="--with-epm=/usr/local/bin/epm"
    else
        epm_param="--with-epm-url=http://sourceforge.net/projects/oooextras.mirror/files/epm-3.7.tar.gz"
    fi
    if [ -e /usr/local/bin/dmake ]; then
        dmake_param="--with-dmake-path=/usr/local/bin/dmake"
    else
        dmake_param="--with-dmake-url=http://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2"
    fi
    (./configure   \
	--with-build-version="$(date +"%Y-%m-%d %H:%M") - `uname -sm`${AOO_BUILD_VERSION}" \
	${AOO_VERBOSE_BUILD} \
	--with-system-stdlibs \
	--enable-crashdump=yes \
	--enable-category-b \
	--enable-wiki-publisher \
	--enable-bundled-dictionaries \
	--enable-opengl  \
	--enable-dbus  \
	--without-junit \
	--without-stlport \
	--with-system-expat \
	--with-jdk-home=/usr/lib64/jvm/java-1.8.0-openjdk \
	--with-package-format="$AOO_PACKAGE_FORMAT" \
	--with-lang="${AOO_LANGS}" \
	$AOO_DEBUG \
	$epm_param \
	$dmake_param \
	| tee -a config.out ) || exit 1
fi

source ./LinuxX86-64Env.Set.sh || exit 1 
./bootstrap || exit 1
\rm -f solenv/inc/reporevision.lst
cd instsetoo_native
cpus=`nproc` || cpus=4
if [ $cpus -ge 8 ]; then
    p1=$(( $cpus / 4 ))
    p2=4
elif [ $cpus -ge 2 ]; then
    p1=$(( $cpus / 2 ))
    p2=2
else
    p1=1
    p2=2
fi
time perl "$SOLARENV/bin/build.pl" --all -P${p1} -- -P${p2} || exit 1
cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
	dmake -P${cpus} openofficebeta  || exit 1
	dmake -P${cpus} sdkoobeta_en-US || exit 1
	dmake -P${cpus} ooobetalanguagepack || exit 1
elif [ "$AOO_BUILD_DEV" = "yes" ]; then
	dmake -P${cpus} openofficedev  || exit 1
	dmake -P${cpus} sdkoodev_en-US || exit 1
	dmake -P${cpus} ooodevlanguagepack || exit 1
else
	dmake -P${cpus} ooolanguagepack || exit 1
	dmake -P${cpus} sdkoo_en-US || exit 1 
fi
if [ "$AOO_BUILD_SRC" = "yes" ]; then
	dmake aoo_srcrelease || exit 1
fi

date "+Build ended at %H:%M:%S"

