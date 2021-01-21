This is the configuration used for AOO 4.2.0-Dev3:

Step by step building guide can be found here:
https://wiki.openoffice.org/wiki/Documentation/Building_Guide_AOO/Step_by_step#Windows_7.2C_Windows_8.1.2C_Windows_10

Build Environment:

 - Windows 10 Pro 20H2 (64-bit)
 - Cygwin 3.1.7 (64-bit)
 - Apache Ant 1.10.9
 - MozillaBuild 3.3
 - MS Visual C++ Compiler 2008 Standard Edition (32-bit)
 - MS Visual C++ Compiler 2008 Standard Edition (64-bit)
 - MS Windows Driver Kit 7.1.0.7600
 - MS Windows SDK for Windows 7.0.7600
 - MS DirectX SDK (June 2010)
 - NASM 2.15.05 (64-bit)
 - Oracle Java Development Kit 8 Update 281 (32-bit)
 - JUnit 4.13
 - Hamcrest-Core 1.3
 - NSIS 3.06.1

Bundled Runtime:

 - MS Visual C++ 2008 Redistributable (32-bit) 9.0.30729.6161
 - MS Visual C++ 2008 Redistributable (64-bit) 9.0.30729.6161
 - MS Visual C Runtime / msvcr100.dll (32-bit) 10.0.40219.325

Configure:

SDK_PATH="/cygdrive/c/Microsoft_SDKs/Windows/v7.0"
WDK_HOME="/cygdrive/c/WinDDK/7600.16385.1"
LANGS_DEV="ast bg ca ca-XR ca-XV cs da de el en-GB en-US es et eu fi fr gd gl he hi hu hy it ja kab km ko lt nb nl om pl pt pt-BR ru sk sl sr sv ta th tr uk vi zh-CN zh-TW"
./configure \
    --with-build-version="$(date +"%Y-%m-%d %H:%M") - `uname -sm`  -  Developer Build 3" \
    --with-frame-home="$SDK_PATH" \
    --with-psdk-home="$SDK_PATH" \
    --with-midl-path="$SDK_PATH/bin" \
    --with-ant-home="/cygdrive/c/apache-ant-1.10.9" \
    --with-jdk-home="/cygdrive/c/Program Files (x86)/Java/jdk1.8.0_281" \
    --with-dmake-url="https://github.com/jimjag/dmake/archive/v4.13.1/dmake-4.13.1.tar.gz" \
    --with-directx-home="/cygdrive/c/Microsoft_DirectX_SDK_June_2010" \
    --with-csc-path="/cygdrive/c/Windows/Microsoft.NET/Framework/v3.5" \
    --with-atl-include-dir="$WDK_HOME/inc/atl71" \
    --with-atl-lib-dir="$WDK_HOME/lib/atl/i386" \
    --with-mfc-include-dir="$WDK_HOME/inc/mfc42" \
    --with-mfc-lib-dir="$WDK_HOME/lib/mfc/i386" \
    --with-nasm-home="/cygdrive/c/Program Files/NASM" \
    --enable-win-x64-shellext \
    --enable-wiki-publisher \
    --with-junit="/cygdrive/c/junit/junit-4.13.jar" \
    --with-hamcrest-core="/cygdrive/c/hamcrest/hamcrest-core-1.3.jar" \
    --without-stlport \
    --with-mozilla-build="/cygdrive/c/mozilla-build-3.3" \
    --enable-category-b \
    --enable-beanshell \
    --with-lang="${LANGS_DEV}" \
    --enable-bundled-dictionaries \
    --with-packager-list=/cygdrive/c/Source/Pack-dev.lst \
    --with-nsis-path="/cygdrive/c/NSIS"

For further details see "config.log"

