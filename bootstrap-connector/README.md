BootstrapConnector
==================

Introduction
------------
This software can be used by Java applications to bootstrap Apache OpenOffice when the Java UNO jars are downloaded from a Maven repository or otherwise in a different location than the soffice executable.

How-To
------
The original How-to can be found here [OpenOffice.org Forum][1].

* Add the jar file from building this software to the classpath.
* Determine the folder of your OpenOffice.org executable "soffice.exe" (on Windows systems) or "soffice" (on *nix systems). On Windows it might be something like "C:\Program Files (x86)\OpenOffice 4\program\", and on *nix for example something like "/opt/openoffice4/program".
* Edit your Java source code file, that tries to get the connection by calling "Bootstrap.bootstrap()". This is mostly done with a Java source code line looking like this:
> XComponentContext xContext = Bootstrap.bootstrap();
* Add the following line to your import statements:
> import ooo.connector.BootstrapSocketConnector;
* Add a line above "Bootstrap.bootstrap()" to assign the name of the folder of your OpenOffice.org executable to a String variable (note slash direction for Windows):
> String oooExeFolder = "C:/Program Files (x86)/OpenOffice 4/program/";
* Change the call of "Bootstrap.bootstrap()" to "BootstrapSocketConnector.bootstrap(oooExeFolder)":
> XComponentContext xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);

Building
--------
This software uses the Gradle build system and can be built with:
> gradle clean jar
Javadoc can be built with:
> gradle javadoc
Or all at once:
> gradle clean javadoc jar

Credits
-------
This software is based on source code found in bootstrapconnector.jar authored 
by OpenOffice.org forum user hol.sten in a OpenOffice.org forum post [OpenOffice.org Forum][1].

License
-------
This software and the application of the Apache License 2.0 is by permission granted by hol.sten and documented with the Apache OpenOffice PMC.

See src/main/resources/META-INF/CREDIT, LICENSE, and NOTICE files for more information.

[1]: https://forum.openoffice.org/en/forum/viewtopic.php?t=2520 "OpenOffice.org Forum"


