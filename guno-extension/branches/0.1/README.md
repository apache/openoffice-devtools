Groovy UNO Extension
====================

Introduction
------------
This is an Apache Groovy language extension of the Java UNO API's. The artifact of this extension is a Java jar file that when used in a Groovy script or class adds convenience methods to the regular Java UNO API's.

The goal of the Groovy UNO Extension is to allow UNO programming that is less verbose than using the Java UNO API's alone.

These methods are implemented using Groovy Extension Modules. An extension module allows you to add new methods to existing classes, including classes which are precompiled, like classes from the JDK or in this case Java UNO classes. These new methods, unlike those defined through a metaclass or using a category, are available globally.

Aside from a few general methods, initial efforts have been on enhancing the spreadsheet API's and future work will be on enhancing the other applications. 

How-To
------
More information can be found on the Apache OpenOffice wiki [Groovy UNO Extension] [1].

Building
--------
This software uses the Gradle build system and can be built with:
> gradle jar

The jar task will build the documentation and the jar archive.

License
-------
This software is licensed under the Apache License 2.0

See the LICENSE, and NOTICE files for more information.

[1]: https://wiki.openoffice.org/wiki/Groovy_UNO_Extension "Groovy UNO Extension"

