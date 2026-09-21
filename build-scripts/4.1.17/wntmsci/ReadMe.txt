This is the configuration used for AOO 4.1.17:

Step by step building guide can be found here:
https://wiki.openoffice.org/wiki/Documentation/Building_Guide_AOO/Step_by_step_Windows

Build Environment:

 - Windows 11 Pro 25H2 (64-bit)
 - Cygwin 3.6.5-1 (64-bit)
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

The commands for configuring and building the release, the language packs
etc. are taken from script:
build_aoo32bit_on_cygwin.sh
launched without any parameters, and with file Pack.lst copied into
/cygdrive/c/Source

For further details see "config.log"

