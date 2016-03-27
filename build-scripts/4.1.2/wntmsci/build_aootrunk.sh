#!/bin/bash

SOURCE_HOME=`pwd`

TEST=`basename $SOURCE_HOME`   

if [ $TEST != "main" ] ; then
    SOURCE_MAIN=$SOURCE_HOME/main
    EXT_SOURCES=$TEST/ext-sources
else
    TEST=`dirname $SOURCE_HOME`
    EXT_SOURCES=`basename $TEST`/ext-sources
    SOURCE_MAIN=$SOURCE_HOME
    DIR=`pwd`
    SOURCE_HOME=`cd ..; pwd`
fi

cd $SOURCE_MAIN

if [ ! -f ./external/dbghelp/DbgHelp.Dll ]
then
    echo "### copy DbgHelp.dll"
    cp /cygdrive/c/AOO/build_requirements/external/dbghelp/DbgHelp.Dll ./external/dbghelp
fi
if [ ! -f ./external/gdiplus/gdiplus.dll ]
then
    echo "### copy gdiplus.dll"
    cp /cygdrive/c/AOO/build_requirements/external/gdiplus/gdiplus.dll ./external/gdiplus
fi
#if [ ! -f ./external/unowinreg/unowinreg.dll ]
#then
#    echo "### copy unowinreg.dll"
#    cp /cygdrive/c/AOO/build_requirements/external/unowinreg/unowinreg.dll ./external/unowinreg
#fi
if [ ! -f ./external/msvcp100 ]
then
    echo "### copy msvcr100.dll"
    cp /cygdrive/c/AOO/build_requirements/external/msvcp100/msvcr100.dll ./external/msvcp100
fi
if [ ! -f ./external/msvcp71 ]
then
    echo "### copy msvcr71.dll"
    cp /cygdrive/c/AOO/build_requirements/external/msvcp71/msvcr* ./external/msvcp71
fi
if [ ! -f ./external/msvcp80 ]
then
    echo "### copy msvcr80.dll"
    cp /cygdrive/c/AOO/build_requirements/external/msvcp80/msvc*l ./external/msvcp80
fi
if [ ! -f ./external/vcredist/vcredist_x64.exe ]
then
    echo "### copy vcredist*.exe"
    cp /cygdrive/c/AOO/build_requirements/external/vcredist/vcredist*.exe ./external/vcredist
fi

#echo "### checking for moz prebuild libs ... "
#if [ ! -e  ./moz/zipped//WNTMSCIinc.zip ]; then
#    cp /cygdrive/c/AOO/build_requirements/moz/zipped/WNTMSCIinc.zip ./moz/zipped//WNTMSCIinc.zip
#    cp /cygdrive/c/AOO/build_requirements/moz/zipped/WNTMSCIlib.zip ./moz/zipped//WNTMSCIlib.zip
#    cp /cygdrive/c/AOO/build_requirements/moz/zipped/WNTMSCIruntime.zip ./moz/zipped//WNTMSCIruntime.zip
#else
#    echo "  moz prebuild libs found" 
#fi

echo "current working dir: $SOURCE_MAIN"

if [ ! -e  ./configure ]; then
    echo "### autoconf ..."
    autoconf
fi

echo "### configure ..."
source /cygdrive/c/AOO/sources/aoo_config_cmd



#./configure \ 
#--with-cl-home="/cygdrive/c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio\ 9.0/VC\" \
#--with-mspdb-path="/cygdrive/c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio\ 9.0/Common7/IDE" \
#--with-frame-home="/cygdrive/c/Program\ Files/Microsoft\ SDKs/Windows/v6.1" \
#--with-psdk-home="/cygdrive/c/Program\ Files/Microsoft\ SDKs/Windows/v6.1" \
#--with-midl-path="/cygdrive/c/Program\ Files/Microsoft\ SDKs/Windows/v6.1" \
#--with-asm-home="/cygdrive/c/Program\ Files\ \(x86\)/Microsoft\ Visual\ Studio\ 9.0/Bin" \
#--with-csc-path="/cygdrive/c/Program\ \(x86\)/Microsoft\ Visual\ Studio\ 9.0/SDK/v3.5" \
#--with-jdk-home="/cygdrive/c/Program\ \(x86\)/Java/jdk1.6.0_27" \
#--with-ant-home="c:/apache-ant-1.8.3" \
#--without-junit \
#--enable-win-x64-shellext \
#--enable-pch \
#--enable-verbose \
#--enable-category-b \
#--enable-bundled-dictionaries \
#--enable-minimizer \
#--enable-presenter-screen \
#--enable-wiki-publisher \
#--disable-build-mozilla \
#--with-mozilla-build=c:/mozilla-build \
#--with-dmake-url=http://dmake.apache-extras.org.codespot.com/files/dmake-4.12.2.tar.bz2


#--with-atl-include-dir="/cygdrive/c/WinDDK/7600.16385.1/inc/atl71" \
#--with-atl-lib-dir="/cygdrive/c/WinDDK/7600.16385.1/lib/atl/i386" \
#--with-mfc-include-dir="/cygdrive/c/WinDDK/7600.16385.1/inc/mfc42" \
#--with-mfc-lib-dir="/cygdrive/c/WinDDK/7600.16385.1/lib/mfc/i386"


#--with-lang="en-US ar ast cs de da en-GB es eu fi fr gd gl hu it ja km ko nb nl pl pt-BR ru sk sl sv zh-CN zh-TW" \

#--with-dmake-path=/cygdrive/c/dev/tools/bin/dmake.exe \
#--with-dmake-url=http://dmake.apache-extras.org.codespot.com/files/dmake-4.12.2.tar.bz2


#echo "### bootastrap ..."
#./bootstrap

