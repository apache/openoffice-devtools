#!/usr/bin/env bash
#
# Build-script for AOO 4.2.x on OSX 10.12
#
# System Setup:
#  XCode 7.2.1
#
#
# ENV:
#    JAVA_HOME=`/usr/libexec/java_home -v 1.7`
#    LC_CTYPE=en_US.UTF-8
#    LANG=en_US.UTF-8
#    MACOSX_DEPLOYMENT_TARGET=10.7
#    ANT_HOME=/usr/local/share/java/apache-ant
#    ANT_CLASSPATH=/usr/local/share/java/apache-ant/lib
#    PATH=~/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/opt/X11/bin:.
# 
# 
# Installed in /usr/local:
# 
#   o Apache ant 1.9.9
#   o dmake 4.12
#   o epm 4.4
#   o openssl 1.0.2l (no-shared)
#   o libxml2-2.9.7 (--prefix=/usr/local --enable-shared=no --without-iconv)
#   o libxslt-1.1.32 (--prefix=/usr/local --enable-shared=no)
#   o pkg-config 0.29.2 (--prefix=/usr/local)
# 
# Macports (/opt/local):
# 
#   o autoconf (symlinked to ~/bin)
#   o gnutar (symlinked to ~/bin)
#   o perl5 (symlinked to ~/bin)
#   o subversion
#   o git
# 
# OS:
# 
#   o OSX 10.12.6 (Sierra)
#   o Xcode 7.3.1
#   o jdk-7u80-macosx-x64
# 

#
# Parse options
#
AOO_SKIP_CONFIG=
AOO_JUST_CONFIG=
AOO_VERBOSE_BUILD=
AOO_BUILD_TYPE="Community Build"
AOO_BUILD_VERSION=
AOO_BUILD_BETA=

while true; do
  case "$1" in
    "--verbose" ) AOO_VERBOSE_BUILD="--enable-verbose"; shift ;;
    "--skip-config" ) AOO_SKIP_CONFIG="yes"; shift ;;
    "--just-config" ) AOO_JUST_CONFIG="yes"; shift ;;
    "--dev" ) AOO_BUILD_TYPE="Development Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; shift ;;
    "--beta" ) AOO_BUILD_TYPE="Beta Build"; AOO_BUILD_VERSION=" [${AOO_BUILD_TYPE}]"; AOO_BUILD_BETA="yes"; shift ;;
    "--" ) shift; break ;;
    "" ) break ;;
    * ) echo "unknown option: $1"; shift ;;
  esac
done

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
	JAVA_HOME=$(/usr/libexec/java_home -v 1.7)
fi
if [ ! -d "$JAVA_HOME" ] ; then
    echo "JAVA_HOME not found: $JAVA_HOME"
    exit 1
fi
export JAVA_HOME
echo "JAVA_HOME is: $JAVA_HOME..."

if [ -z "$ANT_HOME" ] ; then
	ANT_HOME=/usr/local/share/java/apache-ant
fi
if [ ! -d "$ANT_HOME" ] ; then
    echo "ANT_HOME not found: $ANT_HOME"
    exit 1
fi
export ANT_HOME
echo "ANT_HOME is: $ANT_HOME..."

if [ -z "$JUNIT_PATH" ] ; then
	JUNIT_PATH=/usr/local/share/java/junit.jar
fi
if [ ! -e "$JUNIT_PATH" ] ; then
    echo "JUNIT_PATH not found: $JUNIT_PATH"
    exit 1
fi
export JUNIT_PATH
echo "JUNIT_PATH is: $JUNIT_PATH..."

#Setup build Env
export MACOSX_DEPLOYMENT_TARGET=10.7
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
	--with-vendor="Apache OpenOffice ${AOO_BUILD_TYPE}" \
	--with-openldap \
	--enable-category-b \
	--enable-bundled-dictionaries \
	--enable-wiki-publisher \
	--with-junit="$JUNIT_PATH" \
	--with-jdk-home="$JAVA_HOME" \
	--with-ant-home="$ANT_HOME" \
	--with-epm=/usr/local/bin/epm \
	--with-dmake-path=/usr/local/bin/dmake \
	--without-stlport \
	--with-package-format="dmg" \
	--disable-systray \
	--with-macosx-target=10.7 \
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
time perl "$SOLARENV/bin/build.pl" --all -- -P5 || exit 1

cd util
if [ "$AOO_BUILD_BETA" = "yes" ]; then
    dmake openofficebeta -P5 || exit 1
fi
dmake ooolanguagepack -P2 || exit 1
dmake sdkoo_en-US -P2 || exit 1

date "+Build ended at %H:%M:%S"
