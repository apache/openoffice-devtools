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

package org.openoffice.extensions.projecttemplates.actions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Vector;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.config.LibraryManager;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ProjectTypeHelper;

/**
 * Update Java System properties for usage of external Jars in extensions
 * and copy the jars in the dist directory.
 * This class is used by every Action available for extensions.
 * At the time of writing: debug, deploy, package
 * 
 */
public class UpdateExternalJarProperties {
    
    /** 
     * Only static methods: do not create
     */
    private UpdateExternalJarProperties() {
    }

    /**
     * copy any additional referenced jars into the project for packaging 
     * with the extension
     * @param projectDir The project root directory
     */
    public static void copyAdditionalJars(FileObject projectDir) 
                                throws IOException, URISyntaxException {
        Vector<File> jars = new Vector<File>();
        FileObject sourceRoot = FileUtil.createFolder(projectDir, "src"); // NOI18N
        ClassPath cp = ClassPath.getClassPath(sourceRoot, ClassPath.COMPILE);

        final String IDL_JAR_NAME = (String)ProjectTypeHelper.getObjectFromUnoProperties(
                projectDir, "idl_types.jar"); // NOI18N
        List<ClassPath.Entry> cpEntries = cp.entries();
        // get the jars from classpath
        for (Iterator<ClassPath.Entry> it = cpEntries.iterator(); it.hasNext(); ) {
            ClassPath.Entry entry = it.next();
            URL url = entry.getURL();
            
            String urlString = url.toString();

            if (urlString.startsWith("jar:")) { // NOI18N
                /* ResourceManager.class resides in a jar file. Strip it down to the 'file:' part */
                urlString = urlString.substring(4, urlString.lastIndexOf('!'));
                File jarFile = new File(new URI(urlString));
                if (jarFile.exists()              // no broken references
                        && IDL_JAR_NAME != null && urlString.indexOf(IDL_JAR_NAME) == -1) { // no idl types, get packed anyway
                    boolean addFile = true;
                    // do not add office jars
                    for (int i = 0; i < LibraryManager.JAR_LIBRARIES.length; i++) {
                        addFile &= (urlString.indexOf(LibraryManager.JAR_LIBRARIES[i]) == -1);
                    }
                    if (addFile) {
                        jars.add(jarFile);
                    }
                }
            }
        }
        String externalJarDir = null;
        try {
            FileObject f = projectDir.getFileObject("nbproject").getFileObject("project-uno", "properties"); // NOI18N
            if (f != null) {
                InputStream in = null;
                try {
                    in = f.getInputStream();
                    PropertyResourceBundle unoProjectProps = new PropertyResourceBundle(in);
                    externalJarDir = (String)unoProjectProps.handleGetObject("external.jar.dir"); // NOI18N
                }
                catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        }
                        catch (IOException ex) {
                            LogWriter.getLogWriter().printStackTrace(ex);
                        }
                    }
                }
            }
        }
        finally {
            // fallback name if property file cannot be read
            if (externalJarDir == null || externalJarDir.length() == 0)
                externalJarDir = "lib"; // NOI18N
            System.setProperty("external.jar.dir", externalJarDir); // NOI18N
        }
        // copy the files
        if (jars.size() != 0) {
            FileObject dist = FileUtil.createFolder(projectDir, "dist"); // NOI18N
            FileObject jarDirectory = FileUtil.createFolder(dist, externalJarDir);
            FileObject[] entries = jarDirectory.getChildren();
            // delete the jar files from that directory
            for (int i = 0; i < entries.length; i++) {
                FileLock lock = entries[i].lock();
                try {
                    entries[i].delete(lock);
                }
                finally {
                    lock.releaseLock();
                }
            }
            // copy
            StringBuffer jarReference = new StringBuffer();
            for (int i = 0; i < jars.size(); i++) {
                File jarFile = jars.get(i);
                FileObject sourceJar = FileUtil.toFileObject(jarFile);
                FileUtil.copyFile(sourceJar, jarDirectory, sourceJar.getName());
                if (i > 0) {
                    jarReference.append(" "); // NOI18N
                }
                jarReference.append(externalJarDir).append("/").append(jarFile.getName()); // NOI18N
            }
            // write property for the classpath to the jars
            Properties props = new Properties();
            props.setProperty("external.jars", jarReference.toString()); // NOI18N
            FileObject propsOut = FileUtil.createData(jarDirectory, "jars.properties"); // NOI18N
            FileLock lock = propsOut.lock();
            try {
                OutputStream stream = propsOut.getOutputStream(lock);
                try {
                    props.store(stream, 
                            "PackageActionDescription"); // NOI18N
                }
                finally {
                    stream.close();
                }
            }
            finally {
                lock.releaseLock();
            }
        }
        else {
            // delete the external jars
            FileObject dist = projectDir.getFileObject("dist"); // NOI18N
            if (dist != null) {
                FileObject jarDir = dist.getFileObject(externalJarDir);
                if (jarDir != null) {
                    // remove dir if it exists: this happens, 
                    // when all external jars are dereferenced
                    FileLock lock = jarDir.lock();
                    try {
                        jarDir.delete(lock);
                    }
                    finally {
                        lock.releaseLock();
                    }
                }
            }
        }
    }
}
