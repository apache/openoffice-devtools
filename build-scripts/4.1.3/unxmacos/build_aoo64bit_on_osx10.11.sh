#!/bin/sh

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
	JAVA_HOME=$(/usr/libexec/java_home)
	export JAVA_HOME
	echo "Setting JAVA_HOME to $JAVA_HOME..."
fi

if [ ! -e external/unowinreg/unowinreg.dll ] ; then
	echo "Downloading unowinreg.dll..."
	curl -o external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll
fi

LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja kid km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"

if [ ! -e ./configure ] ; then
	echo "Running autoconf..."
	autoconf || exit 1
fi
./configure   \
	--with-build-version="$(date +"%Y-%m-%d %H:%M") - `uname -sm`" \
	--enable-verbose \
	--enable-crashdump=yes \
	--enable-category-b \
	--enable-wiki-publisher \
	--enable-bundled-dictionaries \
	--with-junit=/usr/local/share/java/junit.jar \
	--without-stlport \
	--with-package-format="installed" \
	--with-lang="${LANGS}" 

./bootstrap || exit 1
source ./MacOSXX64Env.Set.sh || exit 1 
cd instsetoo_native
perl "$SOLARENV/bin/build.pl" --all -P4 -- -P4 || exit 1
#cd util
#dmake ooolanguagepack || exit 1
#dmake sdkoo_en-US || exit 1 

date "+Build ended at %H:%M:%S"

