#!/bin/sh
#
# Build-script for AOO 4.1.x on OSX 10.12
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
#
#   Oracle JAVA 1.7 JDK
#
#   /usr/local:
#     o dmake
#         http://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2
#         ./configure --prefix=/usr/local ; make install
#     o epm
#         http://www.msweet.org/files/project2/epm-4.3-source.tar.gz
#         $ ./configure --prefix=/usr/local ; make install
#
#     o openssl (1.0.2p)
#         https://www.openssl.org/source/openssl-1.0.2p.tar.gz
#         $ export MACOSX_DEPLOYMENT_TARGET=10.7
#         $ ./Configure darwin64-x86_64-cc no-shared --prefix=/usr/local --openssldir=/usr/local ; make install
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

if [ ! -e external/unowinreg/unowinreg.dll ] ; then
	echo "Downloading unowinreg.dll..."
	curl -o external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll
fi

LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"

if [ ! -e configure -o configure.in -nt configure ] ; then
	echo "Running autoconf..."
	autoconf || exit 1
fi
./configure   \
    --with-build-version="$(date +"%Y-%m-%d %H:%M:%S (%a, %d %b %Y)") - `uname -sm`" \
	--enable-verbose \
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
	--with-alloc=internal \
	--with-lang="${LANGS}" \
	| tee config.out || exit 1

./bootstrap || exit 1
source ./MacOSXX64Env.Set.sh || exit 1
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P5 || exit 1
cd util
dmake -P2 ooolanguagepack || exit 1
dmake -P2 sdkoo_en-US || exit 1

date "+Build ended at %H:%M:%S"
