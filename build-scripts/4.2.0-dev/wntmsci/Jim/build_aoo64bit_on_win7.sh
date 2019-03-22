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

SDK_HOME="/cygdrive/c/MicrosoftSDKs/Windows/v7.0"
if [ ! -d "$SDK_HOME" ]; then
	echo "Can't find SDK_HOME: $SDK_HOME!"
	exit 1
fi
echo "Setting SDK_HOME to $SDK_HOME..."
export SDK_HOME

JDK_HOME="/cygdrive/c/Java/jdk1.8.0_144"
if [ ! -d "$JDK_HOME" ]; then
	echo "Can't find JDK_HOME: $JDK_HOME!"
	exit 1
fi
echo "Setting JDK_HOME to $JDK_HOME..."
export JDK_HOME
JAVA_HOME="$JDK_HOME"
export JAVA_HOME

ANT_HOME="/cygdrive/c/apache-ant-1.9.9-bin/apache-ant-1.9.9"
if [ ! -d "$ANT_HOME" ]; then
	echo "Can't find ANT_HOME: $ANT_HOME!"
	exit 1
fi
echo "Setting ANT_HOME to $ANT_HOME..."
export ANT_HOME

VSTUDIO_HOME=`cygpath -m -s 'C:\Program Files (x86)\Microsoft Visual Studio 9.0'`
if [ ! -d "$VSTUDIO_HOME" ]; then
	echo "Can't find VSTUDIO_HOME: $VSTUDIO_HOME!"
	exit 1
fi
echo "Setting VSTUDIO_HOME to $VSTUDIO_HOME..."
export VSTUDIO_HOME

WINDDK_HOME="/cygdrive/c/WinDDK/7600.16385.1"
if [ ! -d "$WINDDK_HOME" ]; then
	echo "Can't find WINDDK_HOME: $WINDDK_HOME!"
	exit 1
fi
echo "Setting WINDDK_HOME to $WINDDK_HOME..."

DIRECTX_HOME="/cygdrive/c/Microsoft_DirectX_SDK_June_2010"
if [ ! -d "$DIRECTX_HOME" ]; then
	echo "Can't find DIRECTX_HOME: $DIRECTX_HOME!"
	exit 1
fi
echo "Setting DIRECTX_HOME to $DIRECTX_HOME..."

NSIS_HOME="/cygdrive/c/NSIS"
if [ ! -d "$NSIS_HOME" ]; then
	echo "Can't find NSIS_HOME: $NSIS_HOME!"
	exit 1
fi
echo "Setting NSIS_HOME to $NSIS_HOME..."

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
	--with-vendor="Apache OpenOffice ${AOO_BUILD_TYPE}" \
	--enable-wiki-publisher \
	--enable-category-b \
	--enable-beanshell \
	--enable-bundled-dictionaries \
	--with-jdk-home="$JDK_HOME" \
	--with-ant-home="$ANT_HOME" \
	--with-lang="${LANGS}" \
	--with-frame-home="$SDK_HOME" \
	--with-psdk-home="$SDK_HOME" \
	--with-midl-path="$SDK_HOME/bin" \
	--with-cl-home="$VSTUDIO_HOME/VC" \
	--with-asm-home="$VSTUDIO_HOME" \
	--with-mspdb-path="$VSTUDIO/Common7/IDE" \
	--with-csc-path="$VSTUDIO_HOME/SDK/v3.5" \
	--with-dmake-path=/usr/local/bin/dmake \
	--without-stlport \
	--without-junit \
	--with-directx-home="$DIRECTX_HOME" \
	--enable-win-x64-shellext \
	--with-mozilla-build="/opt/mozilla-build" \
	--with-atl-include-dir="$WINDDK_HOME/inc/atl71" \
	--with-atl-lib-dir="$WINDDK_HOME/lib/atl/i386" \
	--with-mfc-include-dir="$WINDDK_HOME/inc/mfc42" \
	--with-mfc-lib-dir="$WINDDK_HOME/lib/mfc/i386" \
	--disable-odk  \
	| tee config.out ) || exit 1
fi
#exit 0
./bootstrap || exit 1
source ./winenv.set.sh || exit 1
cd instsetoo_native
perl "$SOLARENV/bin/build.pl" --all -- -P4 || exit 1
cd util
dmake -P2 ooolanguagepack || exit 1
dmake -P2 sdkoo_en-US || exit 1

date "+Build ended at %H:%M:%S"
