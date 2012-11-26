/**************************************************************
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 *************************************************************/

package org.openoffice.extensions.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.util.Vector;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.project.classpath.ProjectClassPathExtender;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.Lookup;

/**
 * Class for adding the build/classes directory to the classpath to integrate
 * compiled idl files into the Java part of the extension:
 * adds ${build.classes.dir} to the project.properties entry 
 */
public class ClasspathUpdater {
    
    final static String LINE_ENDING = "\\";
    
    /** Do not instantiate */
    private ClasspathUpdater() {
    }
    
    public static void extendJavaClasspathWithIdlTypes(Project project) {
        try {
            if (project == null) return;
            final FileObject projectDir = project.getProjectDirectory();
            final FileObject distDir = projectDir.getFileObject("dist");
            final FileObject sourceDir = projectDir.getFileObject("src");
            final String IDL_JAR_NAME = ProjectTypeHelper.getObjectFromUnoProperties(projectDir, 
                    "idl_types.jar").toString(); // NOI18N
            
            if (sourceDir != null && distDir != null) {
                FileObject jarFile = distDir.getFileObject(
                        IDL_JAR_NAME);
                if (jarFile != null && jarFile.isData() && jarFile.canRead()) {
                    Lookup lookup = project.getLookup();
                    if (lookup == null) return;
                    // TODO: check if ProjectClassPathModifier works.
                    ProjectClassPathExtender extender = lookup.lookup(ProjectClassPathExtender.class);
                    extender.addArchiveFile(jarFile);
//                    URL jarURL1 = jarFile.getURL();
//                    File f = FileUtil.toFile(jarFile);
//                    URL jarURL2 = f.toURL();
//                    try {
//                        ProjectClassPathModifier.addRoots(
//                            new URL[]{jarFile.getURL()}, sourceDir, ClassPath.COMPILE);
//                    } catch (Throwable t) {
//                        t.printStackTrace();
//                    }
//
//                    try {
//                        ProjectClassPathModifier.addRoots(
//                            new URL[]{jarURL2}, projectDir, ClassPath.COMPILE);
//                    } catch (Throwable t) {
//                        t.printStackTrace();
//                    }
                    
//                    ProjectClassPathModifierImplementation modImpl = project.getLookup().lookup(ProjectClassPathModifierImplementation.class);
//                    
//                    Class clazz = ProjectClassPathModifierImplementation.class;
//                    Class[] cArray = clazz.getDeclaredClasses();
//                    for (int i = 0; i < cArray.length; i++) {
//                        Class class1 = cArray[i];
//                        System.out.println("Clas: " + class1.toString());
//                    }

//                    File f = FileUtil.toFile(jarFile);
//                    URL jarUrl = f.toURL();
//                    URL jarUrl = jarFile.getURL();
//                    Enumeration<? extends FileObject> files = sourceDir.getData(true);
//                    while (files.hasMoreElements()) {
//                        FileObject object = files.nextElement();
//                        if (object.getExt().equals("java")) {
//                            try {
//                                ProjectClassPathModifier.addRoots(
//                                    new URL[]{jarUrl}, object, ClassPath.COMPILE);
//                            } catch (Throwable t) {
//                                LogWriter.getLogWriter().printStackTrace(t);
//                            }
//                        }
//                    }   
                }
            }
//        } catch (FileStateInvalidException ex) {
//            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (MalformedURLException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (FileStateInvalidException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnsupportedOperationException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } 
    }

    public static void removeIdlTypesFromJavaClasspath(Project project) {
        final FileObject projectDir = project.getProjectDirectory();
        final String IDL_JAR_NAME = ProjectTypeHelper.getObjectFromUnoProperties(projectDir, 
                "idl_types.jar").toString(); // NOI18N
        final String propName = "file.reference.".concat(IDL_JAR_NAME);
        final String javacPropName = "javac.classpath";
        Vector<String> allLines = new Vector<String>();
        FileObject nbProject = projectDir.getFileObject("nbproject");
        if (nbProject == null) return; // may happen when project is in process of deleting
        FileObject projectProps = nbProject.getFileObject("project.properties");
        if (projectProps == null) return; // may happen when project is in process of deleting
        boolean writeOut = false;
        // read the stuff
        BufferedReader buf = null;
        InputStream in = null;
        try {
            in = projectProps.getInputStream();
            if (in == null) {
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "cannot get inputstream from project properties"); // NOI18N
                return;
            }
            buf = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while(buf.ready() && (line = buf.readLine()) != null) {
                if (line.startsWith(javacPropName)) {
                    writeOut = true;
                    removeEntryFromClassPath(line, propName, allLines);
                    // handle javac.classpath extending over several lines
                    while (buf.ready() && line.endsWith(LINE_ENDING) && 
                                (line = buf.readLine()) != null) {
                        removeEntryFromClassPath(line, propName, allLines);
                    }
                }
                else if (line.startsWith(propName)) {
                    writeOut = true;
                    // handle file.reference.IDL_types.jar extending over several lines
                    while (buf.ready() && line.endsWith(LINE_ENDING) && (line = buf.readLine()) != null) {
                        // all done in while statement...
                    }
                }
                else {
                    allLines.add(line);
                }
            }
        } catch (IOException ex) {
            writeOut = false;
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally { // all other exceptions produce a message box in ide: nothing will be written...
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
            else if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        }
        // write out again, but only when something was changed
        if (writeOut) {
            OutputStream out = null;
            BufferedWriter write = null;
            try {
                out = projectProps.getOutputStream();
                write = new BufferedWriter(new OutputStreamWriter(out));
                for (int i=0; i<allLines.size(); i++) {
                    write.write(allLines.get(i));
                    write.newLine();
                }
                write.flush();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
            finally {
                if (write != null) {
                    try {
                        write.close();
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
                else if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
            }
        }
    }
    
    private static void removeEntryFromClassPath(String classPath, String entry, Vector<String> allLines) {
        final String colon = ":";
        boolean removeEndingFromLineBefore = false;
        int index = classPath.indexOf(entry);
        if (index > 1) {
            if (!classPath.endsWith(LINE_ENDING)) { 
                // last line: line before may need editing
                removeEndingFromLineBefore = true;
            }
            // remove ${<entry>}
            String firstClassPath = classPath.substring(0, index - 2);
            String secondClassPath = classPath.substring(index + entry.length() + 1);
            // handle ":" as separator
            if (firstClassPath.endsWith(colon)) {
                firstClassPath = firstClassPath.substring(0, firstClassPath.length() - 1);
            }
            else if (secondClassPath.startsWith(colon)) {
                secondClassPath = secondClassPath.substring(1);
            }
            classPath = firstClassPath.concat(secondClassPath);
        }
        if (removeEndingFromLineBefore) {
            if (classPath.trim().length() > 0) { // some classpath element left over
                allLines.add(classPath);
            }
            else {  // entry was last entry in classpath
                String lastLine = allLines.remove(allLines.size() - 1);
                index = lastLine.indexOf(":\\");
                if (index > 0) {
                    lastLine = lastLine.substring(0, index);
                }
                else {
                    lastLine = lastLine.substring(0, lastLine.length() - 1);
                }
                allLines.add(lastLine);
            }
        }
        else {
            if (classPath.trim().length() > 1) { // classpath is at least \ but maybe more...
                allLines.add(classPath);
            }
        }
    }
    
    /* removeIdlTypesFromJavaClasspath tested with:
        String testValues = new String(
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n" +
        "javac.classpath=\\\n" +
        "${build.classes.dir}:\\\n" +
        "${libs.StarOffice8.classpath}:\\\n" +
        "${file.reference.IDL_types.jar}");

        testValues = new String(
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n" +
        "javac.classpath=\\\n" +
        "${file.reference.IDL_types.jar}:\\\n" +
        "${build.classes.dir}:\\\n" +
        "${libs.StarOffice8.classpath}\n"
        );

        testValues = new String(
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n" +
        "javac.classpath=${file.reference.IDL_types.jar}:\\\n" +
        "${build.classes.dir}:\\\n" +
        "${libs.StarOffice8.classpath}\n"
        );

        testValues = new String(
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n" +
        "javac.classpath=\\\n" +
        "${file.reference.IDL_types.jar}:${build.classes.dir}:\\\n" +
        "${libs.StarOffice8.classpath}\n"
        );
        removeIdlTypesFromJavaClasspath(testValues);
        testValues = new String(
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n" +
        "javac.classpath=\\\n" +
        "${build.classes.dir}:${file.reference.IDL_types.jar}:\\\n" +
        "${libs.StarOffice8.classpath}\n"
        );

        testValues = new String(
        "javac.classpath=\\\n" +
        "${file.reference.IDL_types.jar}:${build.classes.dir}:\\\n" +
        "${libs.StarOffice8.classpath}\n" +
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n"
        );

        testValues = new String(
        "javac.classpath=${file.reference.IDL_types.jar}\n" +
        "file.reference.IDL_types.jar=../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n"
        );

        testValues = new String(
        "file.reference.IDL_types.jar=\\\n" +
        "../../../../../home/sg128468/NetBeansProjects/AddIn/dist/IDL_types.jar\n" +
        "javac.classpath=${file.reference.IDL_types.jar}\n"
        );
     */ 
}
