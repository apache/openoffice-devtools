#!/bin/sh
### 4.1.2 Script: 
### 4.1.2 Script: echo "### Configure"
### 4.1.2 Script: ./configure \
### 4.1.2 Script:              --with-build-version="$(date +"%Y-%m-%d %H:%M:%S (%a, %d %b %Y)")" \
### 4.1.2 Script:              --enable-verbose \
### 4.1.2 Script:              --enable-category-b \
### 4.1.2 Script:              --enable-wiki-publisher \
### 4.1.2 Script:              --enable-bundled-dictionaries \
### 4.1.2 Script:              --without-stlport \
### 4.1.2 Script:              --with-dmake-path=/Users/jsc/dev/tools/bin/dmake \
### 4.1.2 Script:              --with-epm=/Users/jsc/dev/tools/bin/epm/epm \
### 4.1.2 Script:              --with-openldap \
### 4.1.2 Script:              --with-junit=/Users/jsc/dev/tools/junit/junit-4.11.jar \
### 4.1.2 Script:              --with-packager-list=/Users/jsc/dev/svn/aoo-build-pack.lst
### 4.1.2 Script:              --with-jdk-home=/Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home \
### 4.1.2 Script:              --with-ant-home=/Users/jsc/dev/tools/apache-ant-1.9.3/dist \
### 4.1.2 Script:              --with-lang="kid ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"
### 4.1.2 Script: 
### 4.1.2 Script: 
#
# Build-script for AOO 4.1.x on OSX 10.12
#
# System Setup:
#  XCode 8.3.3 (Updated w/ https://github.com/devernay/xcodelegacy.git)
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
#     o openssl (1.0.2j)
#         https://www.openssl.org/source/openssl-1.0.2j.tar.gz
#         $ export MACOSX_DEPLOYMENT_TARGET=10.7
#         $ ./Configure darwin64-x86_64-cc no-shared --prefix=/usr/local --openssldir=/usr/local ; make install
#
#   Env:
#     LIBRARY_PATH=/usr/local/lib
#     C_INCLUDE_PATH=/usr/local/include
#     CPLUS_INCLUDE_PATH=/usr/local/include
#     MACOSX_DEPLOYMENT_TARGET=10.7
#     CLANG_CXX_LIBRARY=libc++
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

if [ ! -e configure -o configure.ac -nt configure ] ; then
	echo "Running autoconf..."
	autoconf || exit 1
	sleep 2
fi
#CXXFLAGS="-stdlib=libc++ -std=c++11"
LIBRARY_PATH=/usr/local/lib
C_INCLUDE_PATH=/usr/local/include
CPLUS_INCLUDE_PATH=/usr/local/include
MACOSX_DEPLOYMENT_TARGET=10.9
#CLANG_CXX_LIBRARY=libc++
#CXX="clang++ -stdlib=libstdc++ -std=c++11"

#export CXXFLAGS
export LIBRARY_PATH
export C_INCLUDE_PATH
export CPLUS_INCLUDE_PATH
export MACOSX_DEPLOYMENT_TARGET
#export CLANG_CXX_LIBRARY
#export CXX

./configure   \
    --with-build-version="$(date +"%Y-%m-%d %H:%M:%S (%a, %d %b %Y)") - `uname -sm`" \
	--with-vendor="Apache OpenOffice Community Build" \
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
	--with-package-format="installed dmg" \
	--disable-systray \
	--with-alloc=system \
	--with-lang="${LANGS}" \
	| tee config.out || exit 1

./bootstrap || exit 1
source ./MacOSXX64Env.Set.sh || exit 1
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P4 || exit 1
cd util
dmake -P2 ooolanguagepack || exit 1
dmake -P2 sdkoo_en-US || exit 1

date "+Build ended at %H:%M:%S"
