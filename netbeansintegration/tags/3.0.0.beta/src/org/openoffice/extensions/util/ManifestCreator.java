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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author ab106281
 */


public class ManifestCreator {
    
    public static String uno_typelibrary_rdb_MediaType = "application/vnd.sun.star.uno-typelibrary;type=RDB"; // NOI18N
    public static String uno_typelibrary_java_MediaType = "application/vnd.sun.star.uno-typelibrary;type=Java"; // NOI18N
    public static String uno_component_java_MediaType = "application/vnd.sun.star.uno-component;type=Java"; // NOI18N
//    public static String uno_component_python_MediaType = "application/vnd.sun.star.uno-component;type=Python"; // NOI18N
    // TODO native requires a further atribute platform 
    public static String uno_component_native_MediaType = "application/vnd.sun.star.uno-component;type=native"; // NOI18N
    public static String configuration_data_MediaType = "application/vnd.sun.star.configuration-data"; // NOI18N
    public static String configuration_schema_MediaType = "application/vnd.sun.star.configuration-schema"; // NOI18N
//    public static String package_bundle_description_MediaType = "application/vnd.sun.star.package-bundle-description"; // NOI18N
    public static String uno_component_basic_library_MediaType = "application/vnd.sun.star.basic-library"; // NOI18N
    public static String uno_component_dialog_library_MediaType = "application/vnd.sun.star.dialog-library"; // NOI18N
    public static String uno_component_help_file_MediaType = "application/vnd.sun.star.help"; // NOI18N
            
    // TODO add further media types.
    private static String[][] media_type_mapping = {
        {".xcu", configuration_data_MediaType},
        {".xcs", configuration_schema_MediaType},
//        {".xdl", uno_component_dialog_library_MediaType},
        {".xlb", uno_component_basic_library_MediaType},
//        {".xhp", uno_component_help_file_MediaType},
    }; // NOI18N
    // with these extensions, not the file, but the directory of the file should be referenced
    private static String[] reference_Directory = { 
//        ".xdl", 
//        ".xba", 
        ".xlb",
    }; // NOI18N
    
    private FileObject m_projectRoot;
    private StringBuffer m_manifestLines;
    
    
    public ManifestCreator(FileObject projectRoot) {
        this.m_projectRoot = projectRoot;
        this.m_manifestLines = new StringBuffer();
        m_manifestLines.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N
        //m_manifestLines.append("<!DOCTYPE manifest:manifest PUBLIC \"-//OpenOffice.org//DTD Manifest 1.0//EN\" \"Manifest.dtd\">\n"); // NOI18N
        m_manifestLines.append("<manifest:manifest xmlns:manifest=\"http://openoffice.org/2001/manifest\">\n"); // NOI18N
    }
    
    public void add(String mimeType, String fullPath) {
        m_manifestLines.append("  <manifest:file-entry manifest:media-type=\"" ); // NOI18N
        m_manifestLines.append(mimeType);
        m_manifestLines.append("\"\n                       manifest:full-path=\""); // NOI18N
        m_manifestLines.append(fullPath);
        m_manifestLines.append("\"/>\n"); // NOI18N
    }
    
    public void remove(String mimeType, String fullPath) {
        final String PATH_MANIFEST_ENTRY = "manifest:full-path=";
        int fromIndex = 0;
        int start = 0;
        // idea: look for the mime type, then for the full path, if they match: delete!
        // TODO: better algorithm taking user interaction into account
        while ((start = m_manifestLines.indexOf("  <manifest:file-entry manifest:media-type=\"".concat(mimeType), fromIndex)) != -1) {
            int middleIndex = m_manifestLines.indexOf(PATH_MANIFEST_ENTRY, start);
            int tempStart = middleIndex + PATH_MANIFEST_ENTRY.length() + 1;
            int end = tempStart + fullPath.length();
            String pathCandidate = m_manifestLines.substring(tempStart, end);
            if (pathCandidate.equals(fullPath)) {
                end = end + 4;  // include \"/>\n
                m_manifestLines.delete(start, end);
            }
            fromIndex = start;
        }
    }
    
    /**
     * writes the manifest.xml to m_projectRoot/dist/META-INF/manifest.xml
     */
    public void flush() throws IOException {
        m_manifestLines.append("</manifest:manifest>"); // NOI18N

        FileUtil.createFolder(m_projectRoot, "src"); // NOI18N
        
        FileObject fo = FileUtil.createData(m_projectRoot, "src/uno-extension-manifest.xml"); // NOI18N
        FileLock lock = fo.lock();
        try {
            OutputStream out = fo.getOutputStream(lock);
            try {
                FileUtil.copy(new ByteArrayInputStream(this.m_manifestLines.toString().getBytes()), out);
            } finally {
                out.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    public void updateManifest(Properties props) {
        String[] extensions = getExtensions(props);
        try {
            FileObject fo = FileUtil.createData(m_projectRoot, "src/uno-extension-manifest.xml"); // NOI18N

            XMLReader parser = XMLReaderFactory.createXMLReader();
            ManifestHandler mHandler = new ManifestHandler(extensions);
            parser.setContentHandler(mHandler);
            InputSource source = new InputSource(fo.getInputStream());
            parser.parse(source);

        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (SAXException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }

        updateFileTypes(extensions);
        // after handling xcu/xcs files in source directory (older projects)
        // handle registry file
        updateRegistryEntry();
        updateRdbEntry();
        updateHelpEntry();

        try {
            flush();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
            
    }

    private void updateHelpEntry() {
        // do not update help entry when it is already there
        if (m_manifestLines.indexOf(
            uno_component_help_file_MediaType) == -1) {

            String helpDirName = null;
            Object help = ProjectTypeHelper.getObjectFromUnoProperties(m_projectRoot, "help.dir");
            if (help == null) { // just as fallback
                helpDirName = "help";
            }
            helpDirName = help.toString();
            FileObject helpDir = m_projectRoot.getFileObject(helpDirName);
            if (helpDir != null && helpDir.isFolder()) { // no help found otherwise
                Enumeration<? extends FileObject> fileObjectEnumeration = helpDir.getData(true);
                while (fileObjectEnumeration.hasMoreElements()) {
                    FileObject file = fileObjectEnumeration.nextElement();
                    if (file.getExt().equals("xhp")) {
                        // one entry is enough: do it when help file is found
                        add(uno_component_help_file_MediaType, helpDirName.replaceAll("\\\\", "/")); // NOI18N
                        return;
                    }
                }
            }
        }
    }

    private void updateRdbEntry()  {
        final String IDL_JAR_NAME = (String)ProjectTypeHelper.getObjectFromUnoProperties(
            m_projectRoot, "idl_types.jar"); // NOI18N

        try {
            FileObject sourceFolder = FileUtil.createFolder(m_projectRoot, "src"); // NOI18N
            Enumeration allFiles = sourceFolder.getData(true); // get all files recursively
            boolean notFound = true;
            while (allFiles.hasMoreElements() && notFound) {
                FileObject nextFile = (FileObject)allFiles.nextElement();
                if (nextFile.getExt().equals("idl")) { // NOI18N
                    notFound = false;   // we will hav an rdb
                }
            }
            if (!notFound) { // do rdb entry.
                if (m_manifestLines.indexOf(
                    uno_typelibrary_rdb_MediaType) == -1) {
                    // entry is not there, so do it.
                    add(uno_typelibrary_rdb_MediaType, "types.rdb"); // NOI18N
                }
                if (m_manifestLines.indexOf(uno_typelibrary_java_MediaType) == -1
                        && m_manifestLines.indexOf(IDL_JAR_NAME) == -1) {
                    // maybe double media type, so look for type AND jar
                    add(uno_typelibrary_java_MediaType, IDL_JAR_NAME);
                }
            }
            else {
                if (m_manifestLines.indexOf(
                    uno_typelibrary_rdb_MediaType) != -1) {
                    remove(uno_typelibrary_rdb_MediaType, "types.rdb"); // NOI18N
                }
                if (m_manifestLines.indexOf(uno_typelibrary_java_MediaType) != -1) {
                    // remove handles double entries
                    remove(uno_typelibrary_java_MediaType, IDL_JAR_NAME);
                }
            }
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private void updateFileTypes(String[] extensions) {
        // for all extensions, get all files in the source path
        for (int i = 0; i < extensions.length; i++) {
            String[] entries = createEntries(extensions[i]);
            String mimeType = getMediaTypeForExtension(extensions[i]);
            if (entries.length > 0) {
                if (mimeType != null) {
                    for (int j = 0; j < entries.length; j++) {
                        add(mimeType, entries[j]);
                    }
                }
                else {  
                    // file type without mime type: will be copied in the .oxt file, 
                    // but not referenced in the manifest.
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, 
                        "Extension " + extensions[i] + " has an unknown mime type. Files with that extension will not be referenced in the Manifest.");
                }
            }
        }
    }
    
    private String getMediaTypeForExtension(String extension) {
        for (int i = 0; i < media_type_mapping.length; i++) {
            if (extension.equals(media_type_mapping[i][0])) {
                return media_type_mapping[i][1];
            }
        }
        return null;
    }
    
    private String[] getExtensions(Properties props) {
//        String[] result = new String[0];
//        String extensions = props.getProperty("manifest.package.extensions"); // NOI18N
//        if (extensions != null && extensions.length() > 0) {
//            Vector<String> results = new Vector<String>();
//            StringTokenizer ext = new StringTokenizer(extensions, ","); // NOI18N
//            while(ext.hasMoreElements()) {
//                String candidate = ext.nextToken();
//                if (candidate.startsWith("**/*.")) {
//                    results.add(candidate.substring(4));
//                }
//                else if(candidate.startsWith("*.")) {
//                    results.add(candidate.substring(1));
//                }
//            }
//            result = results.toArray(new String[results.size()]);
//        }
//        return result;
        return new String[]{".xcu", ".xcs", ".xlb"};  // only return types where we have a type mapping
    }
    
    /** 
     * Add entries in the manifest file from existing files in the 
     * source path for recognized extensions.
     * @param _extension the extension to update
     * @return the entries for this extension
     */
    private String[] createEntries(String _extension) {
        boolean referenceDirectory = false;
        for (int i = 0; i < reference_Directory.length; i++) {
            if (_extension.equals(reference_Directory[i])) {
                referenceDirectory = true;
            }
        }
        Vector<String> pathEntries = new Vector<String>();
        Enumeration dataChildren = null;
        int fileNamePrefixLength = -1;
        try {
            FileObject fo = FileUtil.createData(m_projectRoot, "src"); // NOI18N
            fileNamePrefixLength = FileUtil.toFile(fo).getCanonicalPath().length();
            dataChildren = fo.getData(true);
        }
        catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        while (dataChildren != null && dataChildren.hasMoreElements()) {
            try {
                FileObject aDataFile = (FileObject)dataChildren.nextElement();
                if (aDataFile.getNameExt().endsWith(_extension)) {
                    // we have a winner
                    if (referenceDirectory) { // enter the directory, not the file
                        String name = FileUtil.toFile(aDataFile).getCanonicalPath();
                        name = name.substring(fileNamePrefixLength + 1);
                        int index = name.lastIndexOf(File.separatorChar);
                        if (index > 0)
                            name = name.substring(0, index);
                        String entryName = name.replaceAll("\\\\", "/");
                        if (!pathEntries.contains(entryName)) {
                            pathEntries.add(entryName);
                        }
                    }
                    else {
                        String name = FileUtil.toFile(aDataFile).getCanonicalPath();
                        name = name.substring(fileNamePrefixLength + 1);
                        pathEntries.add(name.replaceAll("\\\\", "/"));
                    }
                }
            }
            catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        return pathEntries.toArray(new String[pathEntries.size()]);
    }

    /**
     * Hanlde xcu/xcs files that are located in the regitry subdirectory,
     * i.e. ther regitry.dir referenced in the project properties.
     */
    private void updateRegistryEntry() {
        String registryDirName = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_projectRoot, "registry.dir");
        if (registryDirName == null) {
            registryDirName = "registry";
        }
        FileObject registryDir = m_projectRoot.getFileObject(registryDirName);
        if (registryDir != null) {
            try {
                int fileNamePrefixLength = FileUtil.toFile(m_projectRoot).getCanonicalPath().length();
                Enumeration<? extends FileObject> registryCandidate = registryDir.getChildren(true); // get all recursively
                while (registryCandidate.hasMoreElements()) {
                    FileObject cand = registryCandidate.nextElement();
                    String manifestMediaType = null;
                    if (cand.getExt().endsWith("xcu")) {
                        manifestMediaType = configuration_data_MediaType;
                    }
                    else if (cand.getExt().endsWith("xcs")) {
                        manifestMediaType = configuration_schema_MediaType;
                    }
                    if (manifestMediaType != null) {
                        String path = FileUtil.toFile(cand).getCanonicalPath();
                        path = path.substring(fileNamePrefixLength + 1);
                        add(manifestMediaType, path.replaceAll("\\\\", "/"));
                    }
                }
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    /**
     * Class for handling manifest entries. There are two kinds:
     * entries that are left alone and entries for files that must be
     * handled. The files to be handled are recognized at their extension.
     * Files with a known extension are to be taken from the path they are
     * located in the file system.
     */
    private class ManifestHandler extends DefaultHandler {
        String[] extensions;
        public ManifestHandler(String[] _extensions) {
            extensions = _extensions;
        }
        @Override
        public void startElement(String uri, String localName, 
                String qName, Attributes attributes) throws SAXException {
            if (!qName.equals("manifest:manifest")) { // NOI18N
                String mimeType = attributes.getValue("manifest:media-type"); // NOI18N
                String fullPath = attributes.getValue("manifest:full-path"); // NOI18N
                String extension = fullPath.substring(fullPath.length() - 4);
                boolean found = false;
                // basic paths get excluded: will be added again later anyway
                if (mimeType == null || mimeType.equals(uno_component_basic_library_MediaType)) {
                    found = true;
                }
                for (int i = 0; i < extensions.length; i++) {
                    if (extension.equals(extensions[i])) {  // extension that must be handled
                        found = true;
                    }
                }
                if (!found) {  // this entry is kept as it is
                    ManifestCreator.this.add(mimeType, fullPath);
                }
            }
        }
    }
    
}
