#!/usr/bin/env bash
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
AOO_LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"
AOO_ANT_HOME="/cygdrive/c/apache-ant-1.9.15"
AOO_JDK_HOME="/cygdrive/c/Program Files (x86)/Java/jdk1.7.0_80"
AOO_CSC_PATH="/cygdrive/c/Windows/Microsoft.NET/Framework/v3.5"
AOO_CL_HOME="/cygdrive/c/Program Files (x86)/Microsoft Visual Studio 9.0/VC"
AOO_ASM_HOME="/cygdrive/c/Program Files (x86)/Microsoft Visual Studio 9.0/VC/bin"
AOO_SDK_PATH="/cygdrive/c/Microsoft_SDKs/Windows/v7.0"
AOO_WDK_HOME="/cygdrive/c/WinDDK/7600.16385.1"
AOO_DIRECTX_HOME="/cygdrive/c/Microsoft_DirectX_SDK_June_2010"
AOO_NASM_HOME="/cygdrive/c/Program Files/NASM"
AOO_MOZBUILD="/cygdrive/c/mozilla-build-3.3"
AOO_NSIS_HOME="/cygdrive/c/NSIS"

while true; do
	case "$1" in
		"--verbose" ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
		"--skip-config" ) AOO_SKIP_CONFIG="yes"; shift ;;
		"--just-config" ) AOO_JUST_CONFIG="yes"; shift ;;
		"--build-src" ) AOO_BUILD_SRC="yes"; shift ;;
		"--dev" ) AOO_BUILD_TYPE="Apache OpenOffice Test Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_DEV="yes"; AOO_BUILD_BETA=""; shift ;;
		"--beta" ) AOO_BUILD_TYPE="Apache OpenOffice Beta Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_BETA="yes"; AOO_BUILD_DEV=""; shift ;;
                "--langs" ) shift ; AOO_LANGS="$1"; shift ;;
		"--" ) shift; break ;;
		"" ) break ;;
		* ) echo "unknown option: $1"; shift ;;
	esac
done

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi
AOO_ATL_INCLUDE="$AOO_WDK_HOME/inc/atl71"
AOO_ATL_LIB="$AOO_WDK_HOME/lib/atl/i386"
AOO_MFC_INCLUDE="$AOO_WDK_HOME/inc/mfc42"
AOO_MFC_LIB="$AOO_WDK_HOME/lib/mfc/i386"

# It is possible that missing directories are detected after hours of
# compilation. We try to avoid problems by checking them in advance.
for d in "$AOO_ANT_HOME" "$AOO_JDK_HOME" "$AOO_CSC_PATH" "$AOO_CL_HOME" \
          "$AOO_ASM_HOME" "$AOO_SDK_PATH" "$AOO_ATL_INCLUDE" "$AOO_ATL_LIB" \
          "$AOO_MFC_INCLUDE" "$AOO_MFC_LIB" "$AOO_DIRECTX_HOME" \
          "$AOO_MOZBUILD" "$AOO_NASM_HOME" "$AOO_NSIS_HOME" ; do
    if [ ! -d "$d" ]; then
        echo "Directory $d does not exist!"
        exit 1
    fi
done

rm -f solenv/inc/reporevision.lst
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
    (./configure \
         --with-build-version="$(date +"%Y-%m-%d %H:%M")" \
         --with-frame-home="$AOO_SDK_PATH" \
         --with-psdk-home="$AOO_SDK_PATH" \
         --with-midl-path="$AOO_SDK_PATH/bin" \
         --with-ant-home="$AOO_ANT_HOME" \
         --with-jdk-home="$AOO_JDK_HOME" \
         --with-csc-path="$AOO_CSC_PATH" \
         --with-cl-home="$AOO_CL_HOME" \
         --with-asm-home="$AOO_ASM_HOME" \
         --with-atl-include-dir="$AOO_ATL_INCLUDE" \
         --with-atl-lib-dir="$AOO_ATL_LIB" \
         --with-mfc-include-dir="$AOO_MFC_INCLUDE" \
         --with-mfc-lib-dir="$AOO_MFC_LIB" \
         --with-dmake-url="https://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2" \
         --with-directx-home="$AOO_DIRECTX_HOME" \
         --with-nasm-home="$AOO_NASM_HOME" \
         --enable-win-x64-shellext \
         --enable-wiki-publisher \
         --without-junit \
         --without-stlport \
         --with-mozilla-build="$AOO_MOZBUILD" \
         --enable-category-b \
         --with-lang="${AOO_LANGS}" \
         --enable-bundled-dictionaries \
         --with-packager-list=/cygdrive/c/Source/Pack.lst \
         --with-nsis-path="$AOO_NSIS_HOME" \
         | tee config.out ) || exit 1
fi

source ./winenv.set.sh || exit 1 
./bootstrap || exit 1
rm -f solenv/inc/reporevision.lst
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -P2 -- -P4 || exit 1
cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
	dmake -P4 openofficebeta  || exit 1
	dmake -P4 sdkoobeta_en-US || exit 1
	dmake -P4 ooobetalanguagepack || exit 1
elif [ "$AOO_BUILD_DEV" = "yes" ]; then
	dmake -P4 openofficedev  || exit 1
	dmake -P4 sdkoodev_en-US || exit 1
	dmake -P4 ooodevlanguagepack || exit 1
else
	dmake -P4 ooolanguagepack || exit 1
	dmake -P4 sdkoo_en-US || exit 1 
fi
if [ "$AOO_BUILD_SRC" = "yes" ]; then
	dmake aoo_srcrelease || exit 1
fi

date "+Build ended at %H:%M:%S"

