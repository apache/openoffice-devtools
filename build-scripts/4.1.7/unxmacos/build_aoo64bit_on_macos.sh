#!/usr/bin/env bash
#
# Build-script for AOO 4.1.x on OSX 10.13
#
# System Setup:
#  XCode 7.3.1
#
# Local Changes:
#   MacPorts:
#     o apache-ant (1.9.9)
#     o gnutar (1.29) (symlink gnutar to gtar)
#     o perl5 (perl5.22)
#     o p5-archive-*
#     o p5-lwp-*
#     o p5-xml-parser
#     o autoconf (symlinked to ~/bin)
#     o getopt (symlinked to ~/bin)
#     o subversion
#     o git
#
#   Oracle JAVA 1.7 JDK
#
#   /usr/local:
#   o Apache ant 1.9.13 / 1.10.4
#   o dmake 4.12.3 (https://github.com/jimjag/dmake/archive/dmake-4.12.3.tar.gz)
#   o epm 4.4.2 (https://github.com/jimjag/epm/archive/epm-4.4.2.tar.gz)
#   o openssl (1.0.2p)
#         https://www.openssl.org/source/openssl-1.0.2p.tar.gz
#         $ export MACOSX_DEPLOYMENT_TARGET=10.7
#         $ ./Configure darwin64-x86_64-cc no-shared --prefix=/usr/local --openssldir=/usr/local ; make install
#   o libxml2-2.9.8 (--prefix=/usr/local --enable-shared=no --without-iconv)
#   o libxslt-1.1.32 (--prefix=/usr/local --enable-shared=no)
#   o pkg-config 0.29.2 (--prefix=/usr/local)
#   o GNU patch 2.7.6 (--prefix=/usr/local)
#
#   Env:
#     LIBRARY_PATH=/usr/local/lib
#     C_INCLUDE_PATH=/usr/local/include
#     CPLUS_INCLUDE_PATH=/usr/local/include
#     MACOSX_DEPLOYMENT_TARGET=10.7
#     PATH=/opt/local/bin:/opt/local/sbin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:.
#
#   Notes:
#     o openssl required for serf; we build non-shared so all
#       linkings are to the static libs
#
#     o JDK 1.7 seems to work better; and it's always best to
#       have just *one* version of te JDK installed.
#

#
# Build options
#
AOO_MACOS_TARGET=10.7
AOO_JAVA_VERSION=1.7
AOO_ANT_VERSION=1.9

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

AOPTS=`getopt -o vsjdba:j:m: --long verbose,skip-config,just-config,dev,beta,ant-version:,java-version:,macos-target: -n 'parse-options' -- "$@"`
if [ $? != 0 ] ; then echo "Failed parsing options." >&2 ; exit 1 ; fi
#echo "$AOPTS"
eval set -- "$AOPTS"

while true; do
  case "$1" in
    -v | --verbose ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
    -s | --skip-config ) AOO_SKIP_CONFIG="yes"; shift ;;
    -j | --just-config ) AOO_JUST_CONFIG="yes"; shift ;;
    -a | --ant-version ) AOO_ANT_VERSION=$2; shift 2 ;;
    -j | --java-version ) AOO_JAVA_VERSION=$2; shift 2 ;;
    -m | --macos-target ) AOO_MACOS_TARGET=$2; shift 2 ;;
    -d | --dev ) AOO_BUILD_TYPE="Apache OpenOffice Test Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_DEV="yes"; AOO_BUILD_BETA=""; shift ;;
    -b | --beta ) AOO_BUILD_TYPE="Apache OpenOffice Beta Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_BETA="yes"; AOO_BUILD_DEV=""; shift ;;
    -- ) shift; break ;;
    * ) echo "unknown option: $1"; shift ;;
  esac
done


if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi


JAVA_HOME=$(/usr/libexec/java_home -v ${AOO_JAVA_VERSION})
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

echo "Building for ${AOO_BUILD_TYPE}: min macOS ${AOO_MACOS_TARGET}, Java $(echo ${AOO_JAVA_VERSION} | sed -e s/..//) : Ant ${AOO_ANT_VERSION}"
echo "---"
echo "Starting build:"
echo ""
sleep 5
#Setup build Env
export MACOSX_DEPLOYMENT_TARGET=${AOO_MACOS_TARGET}
export LIBRARY_PATH=/usr/local/lib
export C_INCLUDE_PATH=/usr/local/include
export CPLUS_INCLUDE_PATH=/usr/local/include

if [ ! -e external/unowinreg/unowinreg.dll ] ; then
	echo "Downloading unowinreg.dll..."
	curl -o external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll
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
	--with-openldap \
	--enable-category-b \
	--enable-bundled-dictionaries \
	--enable-wiki-publisher \
	--with-jdk-home="$JAVA_HOME" \
	--with-ant-home="$ANT_HOME" \
	--without-junit \
	--with-epm=/usr/local/bin/epm \
	--with-dmake-path=/usr/local/bin/dmake \
	--without-stlport \
	--with-package-format="dmg" \
	--disable-systray \
	--with-macosx-target=${AOO_MACOS_TARGET} \
	--with-alloc=system \
	--with-lang="${LANGS}" \
	| tee config.out ) || exit 1
fi

if [ "$AOO_JUST_CONFIG" = "yes" ]; then
    exit
fi
./bootstrap || exit 1
source ./MacOSXX64Env.Set.sh || exit 1
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P6 || exit 1

cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
    dmake -P5 openofficebeta  || exit 1
	dmake -P5 sdkoobeta_en-US || exit 1
	dmake -P5 ooobetalanguagepack || exit 1
elif [ "$AOO_BUILD_DEV" = "yes" ]; then
    dmake -P5 openofficedev  || exit 1
	dmake -P5 sdkoodev_en-US || exit 1
	dmake -P5 ooodevlanguagepack || exit 1
else
	dmake -P5 ooolanguagepack || exit 1
	dmake -P5 sdkoo_en-US || exit 1 
fi
date "+Build ended at %H:%M:%S"
