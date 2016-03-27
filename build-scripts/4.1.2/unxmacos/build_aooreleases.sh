#! /bin/bash

export PATH="/Library/Frameworks/Python.framework/Versions/2.7/bin:/opt/subversion/bin:/Library/Frameworks/Python.framework/Versions/2.7/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/usr/X11/bin:/usr/local/git/bin:/usr/local/MacGPG2/bin"

echo "### checking for unowinreg.dll ... "
if [ ! -e  ./external/unowinreg/unowinreg.dll ]; then
    curl -o external/unowinreg/unowinreg.dll http://tools.openoffice.org/unowinreg_prebuild/680/unowinreg.dll
else
    echo "  unowinreg.dll found" 
fi

#echo "### checking for moz prebuild libs ... "
#if [ ! -e  ./moz/zipped/MACOSXGCCIinc.zip ]; then
#    cp ~/dev/backup/moz_prebuild/MACOSXGCCIinc.zip ./moz/zipped/MACOSXGCCIinc.zip
#    cp ~/dev/backup/moz_prebuild/MACOSXGCCIlib.zip ./moz/zipped/MACOSXGCCIlib.zip
#    cp ~/dev/backup/moz_prebuild/MACOSXGCCIruntime.zip ./moz/zipped/MACOSXGCCIruntime.zip
#else
#    echo "  moz prebuild libs found" 
#fi



if [ ! -e  ./configure ]; then
    echo "### autoconf ..."
    autoconf
fi

echo "### Configure"
./configure --with-build-version="$(date +"%Y-%m-%d %H:%M:%S (%a, %d %b %Y)")" --enable-verbose --enable-category-b --enable-wiki-publisher --enable-bundled-dictionaries --without-stlport --with-dmake-path=/Users/jsc/dev/tools/bin/dmake --with-epm=/Users/jsc/dev/tools/bin/epm/epm --with-openldap --with-junit=/Users/jsc/dev/tools/junit/junit-4.11.jar --with-packager-list=/Users/jsc/dev/svn/aoo-build-pack.lst --with-jdk-home=/Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home --with-ant-home=/Users/jsc/dev/tools/apache-ant-1.9.3/dist --with-lang="kid ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"


#--with-dmake-url=http://dmake.apache-extras.org.codespot.com/files/dmake-4.12.tar.bz
#--with-epm.url=http://ftp.easysw.com/pub/epm/3.7/epm-3.7-source.tar.gz

./bootstrap

#cd instsetoo_native 

#build --all -P4 --P4
