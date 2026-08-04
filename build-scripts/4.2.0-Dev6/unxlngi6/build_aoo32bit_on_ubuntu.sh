#!/usr/bin/env bash
#

set -eo pipefail

# Build-script for AOO 4.2.x (and later) on Ubuntu
#
# ENV:
#    LC_CTYPE=en_US.UTF-8
#    LANG=en_US.UTF-8
# 
# 
# Installed in /usr/local:
# 
#   o Apache ant 1.10.9
#   o dmake 4.13.1 (https://github.com/jimjag/dmake/archive/v4.13.1/dmake-4.13.1.tar.gz)
#   o epm 5.0.1 (https://github.com/jimjag/epm/archive/v5.0.1/epm-5.0.1.tar.gz)
#   o autoconf 2.72

#
# Build options
#
AOO_JAVA_VERSION=1.8.0
AOO_ANT_VERSION=1.10

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
AOO_BUILD_ALL="yes"
AOO_BUILD_DEBUG=

AOPTS=`getopt -o vsjtdbqa:j:m:k: --long verbose,debug,skip-config,just-config,build-src,dev,beta,quick,ant-version:,java-version:,macos-target:,macos-sdk: -n 'parse-options' -- "$@"`
if [ $? != 0 ] ; then echo "Failed parsing options." >&2 ; exit 1 ; fi
#echo "$AOPTS"
eval set -- "$AOPTS"

while true; do
	case "$1" in
    -v | --verbose ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
    -s | --skip-config ) AOO_SKIP_CONFIG="yes"; shift ;;
    -j | --just-config ) AOO_JUST_CONFIG="yes"; shift ;;
    -t | --build-src ) AOO_BUILD_SRC="yes"; shift ;;
    -q | --quick ) AOO_BUILD_ALL="no"; shift ;;
    -a | --ant-version ) AOO_ANT_VERSION=$2; shift 2 ;;
    -j | --java-version ) AOO_JAVA_VERSION=$2; shift 2 ;;
    -m | --macos-target ) AOO_MACOS_TARGET=$2; shift 2 ;;
    -k | --macos-sdk ) AOO_MACOS_SDK=$2; shift 2 ;;
    -d | --dev ) AOO_BUILD_TYPE="Apache OpenOffice Test Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_DEV="yes"; AOO_BUILD_BETA=""; shift ;;
    -b | --beta ) AOO_BUILD_TYPE="Apache OpenOffice Beta Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_BETA="yes"; AOO_BUILD_DEV=""; shift ;;
    --debug ) AOO_BUILD_DEBUG="--enable-symbols"; shift ;;
    -- ) shift; break ;;
		* ) echo "unknown option: $1"; shift ;;
	esac
done

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi

JAVA_HOME=/usr/lib/jvm/java-${AOO_JAVA_VERSION})-openjdk-i386
if [ ! -d "$JAVA_HOME" ] ; then
    echo "JAVA_HOME not found: $JAVA_HOME"
    exit 1
fi
export JAVA_HOME
echo "JAVA_HOME is: $JAVA_HOME..."

ANT_HOME=/usr/local/share/java/apache-ant-${AOO_ANT_VERSION}
if [ ! -d "$ANT_HOME" ] ; then
    echo "ANT_HOME not found: $ANT_HOME"
    exit 1
fi
export ANT_HOME
ANT_CLASSPATH=${ANT_HOME}/lib
export ANT_CLASSPATH
echo "ANT_HOME is: $ANT_HOME..."
echo "ANT_CLASSPATH is: $ANT_CLASSPATH..."

echo "Building for Linux 32, Java $(echo ${AOO_JAVA_VERSION} | sed -e s/..//) : Ant ${AOO_ANT_VERSION}"
echo "---"
echo "Starting build:"
echo ""
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
	--with-jdk-home="$JAVA_HOME" \
	--with-ant-home="$ANT_HOME" \
	--with-package-format="rpm deb" \
	--with-lang="${LANGS}" \
	--with-epm=/usr/local/bin/epm \
	--with-dmake-path=/usr/local/bin/dmake \
	| tee config.out ) || exit 1
fi

source ./LinuxX86Env.Set.sh || exit 1 
./bootstrap || exit 1
if [ -e solenv/inc/reporevision.lst ]; then
	\rm solenv/inc/reporevision.lst
fi
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -P4 MAXPROCESS=2 EXTMAXPROCESS=2 -- -P2 || exit 1

cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
    dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 openofficebeta  || exit 1
	dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 sdkoobeta_en-US || exit 1
	dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 ooobetalanguagepack || exit 1
elif [ "$AOO_BUILD_DEV" = "yes" ]; then
    dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 openofficedev  || exit 1
	dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 sdkoodev_en-US || exit 1
	dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 ooodevlanguagepack || exit 1
elif [ "$AOO_BUILD_ALL" = "yes" ]; then
	dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 ooolanguagepack || exit 1
	dmake -P8 MAXPROCESS=1 EXTMAXPROCESS=1 sdkoo_en-US || exit 1
fi
if [ "$AOO_BUILD_SRC" = "yes" ]; then
	dmake aoo_srcrelease || exit 1
fi

date "+Build ended at %H:%M:%S"
