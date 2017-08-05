This is the latest configuration used for building AOO 4.1.4 RC1:

Step by step building guide can be found here:
https://wiki.openoffice.org/wiki/Documentation/Building_Guide_AOO/Step_by_step#Windows_7.2C_Windows_8.1.2C_Windows_10

Build Environment:

 - Windows 10 Pro (64bit)
 - Cygwin 2.8.2 (32bit)
 - Apache Ant 1.9.9
 - Mozilla Build 2.2.0
 - MS Visual C++ Compilers 2008 Standard Edition (32bit)
 - MS Visual C++ Compilers 2008 Standard Edition (64bit)
 - MS Windows Driver Kit 7.1.0.7600
 - MS Windows SDK for Windows 7.0.7600
 - MS DirectX SDK (June 2010)
 - Java SE Development Kit 7 Update 80 (32bit)
 - NSIS 3.02.1

Bundled Runtime:

 - MS Visual C++ 2008 Redistributable (32bit) 9.0.30729.6161
 - MS Visual C++ 2008 Redistributable (64bit) 9.0.30729.6161
 - MS Visual C Runtime / msvcr100.dll (32bit) 10.0.40219.325

Configure:

SDK_PATH="/cygdrive/c/Microsoft_SDKs/Windows/v7.0"
WDK_HOME="/cygdrive/c/WinDDK/7600.16385.1"
./configure \
    --with-build-version="$(date +"%Y-%m-%d %H:%M")" \
    --with-frame-home="$SDK_PATH" \
    --with-psdk-home="$SDK_PATH" \
    --with-midl-path="$SDK_PATH/bin" \
    --with-ant-home="/cygdrive/c/apache-ant-1.9.9" \
    --with-jdk-home="C:/PROGRA~2/Java/JDK17~1.0_8" \
    --with-csc-path="C:/Windows/Microsoft.NET/Framework/v3.5" \
    --with-cl-home="C:/PROGRA~2/MICROS~1.0/VC" \
    --with-asm-home="C:/PROGRA~2/MICROS~1.0/VC/bin" \
    --with-atl-include-dir="$WDK_HOME/inc/atl71" \
    --with-atl-lib-dir="$WDK_HOME/lib/atl/i386" \
    --with-mfc-include-dir="$WDK_HOME/inc/mfc42" \
    --with-mfc-lib-dir="$WDK_HOME/lib/mfc/i386" \
    --with-dmake-url="https://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2" \
    --with-directx-home="C:\Microsoft_DirectX_SDK_June_2010" \
    --enable-win-x64-shellext \
    --without-junit \
    --without-stlport \
    --with-mozilla-build="/cygdrive/c/mozilla-build" \
    --enable-category-b \
    --with-lang="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW" \
    --enable-bundled-dictionaries \
    --with-packager-list=/cygdrive/c/Source/aoo-build-pack.lst \
    --with-nsis-path="C:/NSIS"

For further details see "config.log"
