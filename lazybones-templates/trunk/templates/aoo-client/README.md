Apache OpenOffice Client Application project template
-----------------------------------------------------

You have just created a basic Apache OpenOffice Client application. 
There is a standard project structure for source code and tests.
Simply add your source files to `src/main/<groovy or java>`, your test cases 
to `src/test/<groovy or java>` and then you will be able to build your project 
with `gradle jar` and run with `gradle runJar`.

In this project you get:

* A Gradle build file
* A standard project structure:

   <proj>
      |
      +- src
           |
           +- main
           |     |
           |     +- groovy
           |     |     |
           |     |     +- <your-package>
           |     |     |    |
           |     |     |    +- <your-class>.groovy
           |     |     |          
           |     |     |          
           |     |     |
           |     |     +- org
           |     |          |
           |     |          +- openoffice
           |     |                |
           |     |                +- guno
           |     |                      |
           |     |                      +- SpreadsheetDocHelper.groovy
           |     |      
           |     +- java
           |     |     |
           |     |     +- com
           |     |          |
           |     |          +- sun
           |     |               |
           |     |               +- lib
           |     |                    |
           |     |                    +- loader
           |     |                          |
           |     |                          +- InstallationFinder.java
           |     |                          |
           |     |                          +- Loader.java
           |     |                          |
           |     |                          +- WinRegKey.java
           |     |                          |
           |     |                          +- WinRegKeyException.java
           |     |
           |     +- resources
           |           |                           
           |           +- META-INF
           |           |     |
           |           |     +- LICENSE
           |           |     |
           |           |     +- NOTICE
           |           |
           |           +- win
           |                |
           |                +- unowinreg.dll
           |
           +- test
                |
                +- groovy
 
You need to edit the build.gradle file for the location of the GroovyUnoExtension 
jar file and also add any additional dependencies if needed.


