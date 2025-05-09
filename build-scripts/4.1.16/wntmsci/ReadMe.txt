This is the configuration used for AOO 4.1.16-RC1:

Step by step building guide can be found here:
https://wiki.openoffice.org/wiki/Documentation/Building_Guide_AOO/Step_by_step_Windows

Build Environment:

 - Windows 10 Pro 22H2 (64-bit)
 - Cygwin 3.6.1 (64-bit)
 - Apache Ant 1.9.16
 - MozillaBuild 3.4
 - MS Visual C++ Compiler 2008 Standard Edition (32-bit)
 - MS Visual C++ Compiler 2008 Standard Edition (64-bit)
 - MS Windows Driver Kit 7.1.0.7600
 - MS Windows SDK for Windows 7.0.7600
 - MS DirectX SDK (June 2010)
 - NASM 2.16.03 (64-bit)
 - Oracle Java Development Kit 7 Update 80 (32-bit)
 - NSIS 3.11

Bundled Runtime:

 - MS Visual C++ 2008 Redistributable (32-bit) 9.0.30729.6161
 - MS Visual C++ 2008 Redistributable (64-bit) 9.0.30729.6161
 - MS Visual C Runtime / msvcr100.dll (32-bit) 10.0.40219.325

Configure:

SDK_PATH="/cygdrive/c/Microsoft_SDKs/Windows/v7.0"
WDK_HOME="/cygdrive/c/WinDDK/7600.16385.1"
LANGS="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es eu fi fr gd gl he hi hu it ja km ko lt nb nl pl pt pt-BR ru sk sl sr sv ta th tr vi zh-CN zh-TW"
./configure \
    --with-build-version="$(date +"%Y-%m-%d %H:%M")" \
    --with-frame-home="$SDK_PATH" \
    --with-psdk-home="$SDK_PATH" \
    --with-midl-path="$SDK_PATH/bin" \
    --with-ant-home="/cygdrive/c/apache-ant-1.9.16" \
    --with-jdk-home="/cygdrive/c/Program Files (x86)/Java/jdk1.7.0_80" \
    --with-csc-path="/cygdrive/c/Windows/Microsoft.NET/Framework/v3.5" \
    --with-cl-home="/cygdrive/c/Program Files (x86)/Microsoft Visual Studio 9.0/VC" \
    --with-asm-home="/cygdrive/c/Program Files (x86)/Microsoft Visual Studio 9.0/VC/bin" \
    --with-atl-include-dir="$WDK_HOME/inc/atl71" \
    --with-atl-lib-dir="$WDK_HOME/lib/atl/i386" \
    --with-mfc-include-dir="$WDK_HOME/inc/mfc42" \
    --with-mfc-lib-dir="$WDK_HOME/lib/mfc/i386" \
    --with-dmake-url="https://sourceforge.net/projects/oooextras.mirror/files/dmake-4.12.tar.bz2" \
    --with-directx-home="/cygdrive/c/Microsoft_DirectX_SDK_June_2010" \
    --with-nasm-home="/cygdrive/c/Program Files/NASM" \
    --enable-win-x64-shellext \
    --enable-wiki-publisher \
    --without-junit \
    --without-stlport \
    --with-mozilla-build="/cygdrive/c/mozilla-build" \
    --enable-category-b \
    --with-lang="${LANGS}" \
    --enable-bundled-dictionaries \
    --with-packager-list=/cygdrive/c/Source/Pack.lst \
    --with-nsis-path="/cygdrive/c/NSIS"

For further details see "config.log"

