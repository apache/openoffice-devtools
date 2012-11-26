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

package org.openoffice.extensions.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem.AtomicAction;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.config.office.OpenOfficeLocation;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author js93811
 */
public class LibraryManager {
    
    public static final String LIBRARY_HOME = "org-netbeans-api-project-libraries/Libraries"; // NOI18N
    public static final String LIBRARY_BASE = "org-netbeans-api-project-libraries"; // NOI18N
    public static final String LIBRARY_DIR = "Libraries"; // NOI18N
    public static final String LIBRARY_EXTENSION = "xml"; // NOI18N

    // internal without officebean: may not be there
    public static final String[] JAR_LIBRARIES_ESSENTIAL = new String[] {
       "juh.jar", "jurt.jar", "ridl.jar", "unoil.jar",  
    }; // NOI18N

    // jar libraries for external use including officebean: if it's missing, 
    // externals have to handle that
    public static final String[] JAR_LIBRARIES = new String[] {
       JAR_LIBRARIES_ESSENTIAL[0],
       JAR_LIBRARIES_ESSENTIAL[1],
       JAR_LIBRARIES_ESSENTIAL[2],
       JAR_LIBRARIES_ESSENTIAL[3],
       "officebean.jar"
    }; // NOI18N

    public static final String JAR_LIBRARY_CONTENT_LINE = 
            "\t\t<resource>jar:file:%LibrarySrcClassDir%!/</resource>\n"; // NOI18N
            
    public static final String LIBRARY_CONTENT = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE library PUBLIC \"-//NetBeans//DTD Library Declaration 1.0//EN\" \"http://www.netbeans.org/dtds/library-declaration-1_0.dtd\">\n" +
            "<library version=\"1.0\">\n" +
            "\t<name>%LibraryName%</name>\n" +
            "\t<type>j2se</type>\n" +
            "\t<volume>\n" +
            "\t\t<type>classpath</type>\n" +
            "%AllLibraries%" +
            "\n\t</volume>\n" +
            "\t<volume>\n" +
            "\t\t<type>src</type>\n" +
            "\t</volume>\n" +
            "\t<volume>\n" +
            "\t\t<type>javadoc</type>\n" +
            "\t\t<resource>file:%SdkPath%/docs/common/ref/</resource>\n" +
            "\t\t<resource>file:%SdkPath%/docs/java/ref/</resource>\n" +
            "\t</volume>\n" +
            "</library>\n"; // NOI18N
    
    /** Creates a new instance of LibraryManager */
    public LibraryManager() {

    }
    
    public String createLibrary(OpenOfficeLocation loc) {
        String libraryName = ""; // NOI18N
        String allLibraries = ""; // NOI18N
        try
        {
            FileObject libraryBaseFolder= FileUtil.getConfigRoot().getFileObject(LIBRARY_BASE);
            if (libraryBaseFolder==null)
            {
                libraryBaseFolder = FileUtil.getConfigRoot().createFolder(LIBRARY_BASE);
            }
                     
            FileObject libraryFolder= libraryBaseFolder.getFileObject(LIBRARY_DIR);
            if (libraryFolder==null)
            {
                libraryFolder = libraryBaseFolder.createFolder(LIBRARY_DIR);
            }           
            
            libraryName = getLibraryName(loc);

            // officebean may not be there
            String officeBeanEntry = correctPathForAllPlatforms(loc.getFullPathForJar("officebean.jar")); // NOI18N
            if (officeBeanExists(officeBeanEntry)) {
                officeBeanEntry = JAR_LIBRARY_CONTENT_LINE.replaceAll("%LibrarySrcClassDir%", officeBeanEntry); // NOI18N
                allLibraries = officeBeanEntry;
            }

            for (int i = 0; i < JAR_LIBRARIES_ESSENTIAL.length; i++) {
                String jarLib = JAR_LIBRARIES_ESSENTIAL[i];
                String path = correctPathForAllPlatforms(loc.getFullPathForJar(jarLib));
                allLibraries = allLibraries.concat(
                        JAR_LIBRARY_CONTENT_LINE.replaceAll("%LibrarySrcClassDir%", path)); // NOI18N
            }
            
            String content = LIBRARY_CONTENT.replaceAll("%SdkPath%", correctPathForAllPlatforms(loc.getSdkPath())); // NOI18N
            content = content.replaceAll("%LibraryName%", libraryName); // NOI18N
            content = content.replaceAll("%AllLibraries%", allLibraries); // NOI18N
            
            LibraryCreateAction createAction = new LibraryCreateAction(libraryFolder, libraryName, content);
            
            libraryFolder.getFileSystem().runAtomicAction(createAction);                        
        }
        catch (IOException ex)
        {
            // TODO file can not be created , do something about it
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        
        return libraryName;
    }
    
    /**
     * Make a NB name out of platform dependent paths: start with
     * slash, have slash as path separator and %20 instead of space.
     * @param line
     * @return
     */
    private String correctPathForAllPlatforms(String line) {
        // really two backslashes needed + masking, so it's four
        line = line.replaceAll("\\\\", "/").replaceAll(" ", "%20");
        return line.startsWith("/")?line:"/".concat(line);
    }
    
    private boolean officeBeanExists(String officebeanpath) {
        if (officebeanpath == null) return false;
        File officebeanjar = new File(officebeanpath); // NOI18N
        return officebeanjar.exists();
    }
 
    static String getLibraryName(OpenOfficeLocation loc) {
        String libraryName = ""; // NOI18N
        FileInputStream f = null;
        try
        {
            f = new FileInputStream(loc.getBootstrapPath()); 
            java.util.PropertyResourceBundle props = new java.util.PropertyResourceBundle(f);
            libraryName = (String)props.handleGetObject("ProductKey"); // NOI18N
        }
        catch (IOException ex)
        {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally {
            if (f != null) {
                try {
                    f.close();
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        }
        return libraryName;
    }
    
    class LibraryCreateAction implements AtomicAction {
        private FileObject libraryFolder;
        private String libraryContent;
        private String libraryName;
        
        public LibraryCreateAction(FileObject libraryfolder, String libraryname, String librarycontent) {
            libraryFolder = libraryfolder;
            libraryContent = librarycontent;
            libraryName = libraryname;
        }
    
        
        public void run() {
            try
            {
                FileObject libraryFile = libraryFolder.getFileObject(libraryName,LIBRARY_EXTENSION);
                if (libraryFile==null)
                {
                    libraryFile = libraryFolder.createData(libraryName,LIBRARY_EXTENSION);
                }

                FileLock lock = libraryFile.lock();
                OutputStream out = libraryFile.getOutputStream(lock);            
                out.write(libraryContent.getBytes());
                out.close();
                lock.releaseLock();
            }
            catch (IOException ex)
            {
                // TODO file can not be created , do something about it
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
}
