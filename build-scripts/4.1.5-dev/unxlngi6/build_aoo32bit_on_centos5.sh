#! /bin/bash

if [ ! -d ../main -o ! -d sal ] ; then
	echo "CHDIR into AOO's main/ directory first!"
	exit 1
fi

wget -O external/unowinreg/unowinreg.dll http://www.openoffice.org/tools/unowinreg_prebuild/680/unowinreg.dll

LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"

if [ ! -e configure -o configure.in -nt configure ] ; then
	echo "Running autoconf..."
	autoconf || exit 1
fi
./configure   \
	--with-build-version="$(date +"%Y-%m-%d %H:%M") - `uname -sm`" \
	--enable-verbose \
	--with-system-stdlibs \
	--enable-crashdump=yes \
	--enable-category-b \
	--enable-wiki-publisher \
	--enable-bundled-dictionaries \
	--enable-opengl  \
	--enable-dbus  \
	--enable-gstreamer \
	--without-junit \
	--without-stlport \
	--with-ant-home=$HOME/ant \
	--with-package-format="rpm deb" \
	--with-lang="${LANGS}" \
	--with-dmake-url=http://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2 \
	--with-epm-url=http://sourceforge.net/projects/oooextras.mirror/files/epm-3.7.tar.gz \
	| tee config.out

source ./LinuxX86Env.Set.sh || exit 1
./bootstrap || exit 1
cd instsetoo_native
time perl "$SOLARENV/bin/build.pl" --all -- -P4 || exit 1
cd util
dmake ooolanguagepack || exit 1
dmake sdkoo_en-US || exit 1 

date "+Build ended at %H:%M:%S"

