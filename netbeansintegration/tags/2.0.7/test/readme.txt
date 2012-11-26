Stuff to do before tests can be build and executed

This is about qa-functional tests - unit tests normally work without problems.

- install xtest, junit, jemmy and jellytools as plugins
  to be on the safe side: all of those you can get, look in beta-update center;
  caution! In NetBeans 6.1 tests fail with some obscure problem when 
  plugins are installed in shared layer: install as normal user

- edit nbproject/project.properties
  uncomment everything needed for building and executing the tests:
  can be tricky, what is needed depends on the NetBeans version;
  consider restarting NetBeans to activate everything

- edit test/build.xml
  point to the right directory for xtest and netbeans user directory

- edit test/qa-functional/src/org/openoffice/extensions/test/Test.properties
  point to the OpenOffice.org and OpenOffice.org SDK installation directories

- happy testing  