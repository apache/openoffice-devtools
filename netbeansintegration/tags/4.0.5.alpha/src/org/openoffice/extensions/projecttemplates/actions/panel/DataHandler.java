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
package org.openoffice.extensions.projecttemplates.actions.panel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ProjectTypeHelper;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;

/**
 * This class consolidates handling of data that goes to decsription.xml. 
 * The class is mostly just a wrapper around datat structures and the
 * handler classes for the description.xml file. 
 */
public class DataHandler {
    private FileObject m_ProjectDir;
    private DescriptionXmlHandler m_DXmlHandler;
    private HashMap<String, GenericDescriptionProperty>table;

    
    public DataHandler(FileObject projectDir) {
        m_ProjectDir = projectDir;
        // clear resident data
        table = new HashMap<String, GenericDescriptionProperty>();
        // description handler last
        m_DXmlHandler = new DescriptionXmlHandler(m_ProjectDir, this);
        
        // initialize locale if nothing can be obtained from description.xml
        if (m_DXmlHandler.getDefaultLocale() == null) {
            m_DXmlHandler.setDefaultLocale(
                    LanguageDefinition.getLanguageShortNameForId(
                    LanguageDefinition.LANGUAGE_ID_en));
        }
    }
    
    // TODO: think of method to remove unchecked warning
    @SuppressWarnings("unchecked")
    public void clearAll() {
        Collection<GenericDescriptionProperty> c = table.values();
        for (Iterator<GenericDescriptionProperty> it = c.iterator(); it.hasNext();) {
            GenericDescriptionProperty<String> genericDescriptionProperty = it.next();
            synchronized(genericDescriptionProperty) { 
                genericDescriptionProperty.clear();
            }
        }
        table.clear();
    }

    // TODO: think of method to remove unchecked warning
    @SuppressWarnings("unchecked")
    public GenericDescriptionProperty<String> getGenericStringProperty(String name) {
        GenericDescriptionProperty<String> item = table.get(name);
        if (item == null) {
            item = new GenericDescriptionProperty<String>(name);
            table.put(name, item);
        }
        return item;
    }

    // TODO: think of method to remove unchecked warning
    @SuppressWarnings("unchecked")
    public GenericDescriptionProperty<String[]> getGenericStringArrayProperty(String name) {
        GenericDescriptionProperty<String[]> item = table.get(name);
        if (item == null) {
            item = new GenericDescriptionProperty<String[]>(name);
            table.put(name, item);
        }
        return item;
    }

    public boolean hasDataForLocale(String locale) {
        Collection<GenericDescriptionProperty> c = table.values();
        for (Iterator<GenericDescriptionProperty> it = c.iterator(); it.hasNext();) {
            GenericDescriptionProperty genericDescriptionProperty = it.next();
            Object o = genericDescriptionProperty.getPropertyForLocale(locale);
            if (o != null) {
                // do not recognize publisher link!
                if (genericDescriptionProperty.getName().equals("publisher")) {
                    String[]object = (String[])genericDescriptionProperty.getPropertyForLocale(locale);
                    if (object[0] != null && object[0].length() > 0) {
                        return true;
                    }
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }
    
    public GenericDescriptionProperty<String> getDisplayData() {
         return m_DXmlHandler.getDisplayData();
    }

    public void setDisplayData(GenericDescriptionProperty<String> data) {
         m_DXmlHandler.setDisplayData(data);
    }

    public void setLicenseFiles(GenericDescriptionProperty<String> data) {
         m_DXmlHandler.setLicenseFiles(data);
    }

    public GenericDescriptionProperty<String> getLicenseFiles() {
         return m_DXmlHandler.getLicenseFiles();
    }

    public boolean getLicenseAcceptByUser() {
        String acceptby = m_DXmlHandler.getAcceptBy();
        return (acceptby != null && acceptby.equals("user")); // NOI18N
    }
    
    public boolean hasLicenseFiles() {
        GenericDescriptionProperty<String> licenses = m_DXmlHandler.getLicenseFiles();
        if (licenses.getSize() > 0) {
            return true;
        }
        return false;
    }
    
    public void setLicenseAcceptByUser(boolean acceptbyuser) {
        m_DXmlHandler.setAcceptBy(acceptbyuser?"user":"admin"); // NOI18N
    }
    
    public void setPublisherData(GenericDescriptionProperty<String[]> data) {
         m_DXmlHandler.setPublisherData(data);
    }
    
    public GenericDescriptionProperty<String[]> getPublisherData() {
         return m_DXmlHandler.getPublisherData();
    }
    
    public void setDescriptionData(GenericDescriptionProperty<String> data) {
        m_DXmlHandler.setDescriptionData(data);
    }
    
    public GenericDescriptionProperty<String> getDescriptionData() {
        return m_DXmlHandler.getDescriptionData();
    }
    
    public void setVersion(String version) {
        m_DXmlHandler.setExtensionVersion(version);
    }
    
    public String getVersion() {
        return m_DXmlHandler.getExtensionVersion();
    }
    
    public void setIdentifier(String identifier) {
        m_DXmlHandler.setExtensionIdentifier(identifier);
    }
    
    public String getIdentifier() {
        return m_DXmlHandler.getExtensionIdentifier();
    }
    
    public String getDefaultShortLocale() {
        return m_DXmlHandler.getDefaultLocale();
    }
    
    public void setDefaultShortLocale(String locale) {
        m_DXmlHandler.setDefaultLocale(locale);
    }
    
    public void setDependencyName(String dependencyName) {
        m_DXmlHandler.setDependencyName(dependencyName);
    }
    
    public String getDependencyName() {
        return m_DXmlHandler.getDependencyName();
    }
    
    public void setDependencyNumber(String value) {
        m_DXmlHandler.setDependencyNumber(value);
    }
    
    public String getDependencyNumber() {
        return m_DXmlHandler.getDependencyNumber();
    }
    
    public void setUpdateURLs(String[] urls) {
        m_DXmlHandler.setUpdateURLs(urls);
    }
    
    public String[] getUpdateURLs() {
        return m_DXmlHandler.getUpdateURLs();
    }
    
    public void setIconFile(String iconFile) {
        m_DXmlHandler.setDefaultIconName(iconFile);
    }
    
    public String getIconFile() {
        return m_DXmlHandler.getDefaultIconName();
    }
    
    public void setHighDefIconFile(String iconFile) {
        m_DXmlHandler.setHighContrastIconName(iconFile);
    }
    
    public String getHighDefIconFile() {
        return m_DXmlHandler.getHighContrastIconName();
    }
    
    public FileObject getProjectDir() {
        return m_ProjectDir;
    }
            
    /**
     * Trigger storing back the changes in properties panels: this is called by
     * OOoCustomizerProvider which has 
     */
    public void store() {
        copyFiles();
        // make sure default locale is first.
        String localeName = m_DXmlHandler.getDefaultLocale();
        getPublisherData().setDefaultLocale(localeName);
        getDisplayData().setDefaultLocale(localeName);
        getLicenseFiles().setDefaultLocale(localeName);
        getDescriptionData().setDefaultLocale(localeName);
        
        m_DXmlHandler.writeDescriptionXml();
    }
    
    private void copyFiles() {
        // copy description files
        String propKey = "description.dir"; // NOI18N
        String descriptionDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_ProjectDir, propKey);
        GenericDescriptionProperty<String> descriptionFiles = getDescriptionData();
        ArrayList<Integer> deleteDescriptionIndexes = new ArrayList<Integer>();
        for (int i = 0; i < descriptionFiles.getSize(); i++) {
            String object = descriptionFiles.getProperty(i);
            String name = getNameFromSource(object);
            // if file could not be copied, do not refer to it in xml file
            if (copyOneFile(object, descriptionDir, name)) {
                descriptionFiles.setProperty(i, descriptionDir.concat("/").concat(name));
            }
            else {
                // do not change size in loop which relies on it!
                deleteDescriptionIndexes.add(i);
            }
        }
        // delete from list now: traverse from top to bottom to prevent index out of bounds exception
        for (int i = deleteDescriptionIndexes.size() - 1; i >= 0; i--) {
            Integer integer = deleteDescriptionIndexes.get(i);
            descriptionFiles.deletePropertyAndLocale(integer);
        }

        // copy license files
        propKey = "licenses.dir"; // NOI18N
        String licenseDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_ProjectDir, propKey);
        GenericDescriptionProperty<String> licenseFiles = getLicenseFiles();
        ArrayList<Integer> deleteLicenseIndexes = new ArrayList<Integer>();
        // if file could not be copied, do not refer to it in xml file
        for (int i = 0; i < licenseFiles.getSize(); i++) {
            String object = licenseFiles.getProperty(i);
            String name = getNameFromSource(object);
            if (copyOneFile(object, licenseDir, name)) {
                licenseFiles.setProperty(i, licenseDir.concat("/").concat(name));
            }
            else {
                // do not change size in loop which relies on it!
                deleteLicenseIndexes.add(i);
            }
        }
        // delete from list now: traverse from top to bottom to prevent index out of bounds exception
        for (int i = deleteLicenseIndexes.size() - 1; i >= 0; i--) {
            Integer integer = deleteLicenseIndexes.get(i);
            licenseFiles.deletePropertyAndLocale(integer);
        }
        
        // copy icon files
        propKey = "images.dir"; // NOI18N
        String imagesDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_ProjectDir, propKey);
        File iconFile = getProjectOrSystemPath(getIconFile());
        if (iconFile != null) {
            try {
                String fileName = iconFile.getCanonicalPath();
                String name = getNameFromSource(fileName);
                if (copyOneFile(fileName, imagesDir, name)) {
                    // add to description handler
                    m_DXmlHandler.setDefaultIconName(imagesDir.concat("/").concat(name));
                }
                else {
                    m_DXmlHandler.setDefaultIconName(null);
                }
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        iconFile = getProjectOrSystemPath(getHighDefIconFile());
        if (iconFile != null) {
            try {
                String fileName = iconFile.getCanonicalPath();
                String name = getNameFromSource(fileName);
                if (copyOneFile(fileName, imagesDir, name)) {
                    // add to description handler
                    m_DXmlHandler.setHighContrastIconName(imagesDir.concat("/").concat(name));
                }
                else {
                    m_DXmlHandler.setHighContrastIconName(null);
                }
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    private boolean copyOneFile(String source, String target, String name) {
        // initial test
        if (source == null || target == null || name == null) {
            return false;
        }
        File sourceFile = getProjectOrSystemPath(source);
        File targetDir = getOrCreateProjectSubDir(target);
        // some cases where copying will not be done
        if (sourceFile == null || targetDir == null) {
            return false;
        }
        if (targetDir.equals(sourceFile.getParentFile())) {
            // target file already there: everything is ok
            return true;
        }
        if (!targetDir.exists() && !targetDir.canWrite()) {
            // target dir is not writeable: fail.
            return false;
        }
        FileObject sourceFileObject = FileUtil.toFileObject(sourceFile);
        FileObject targetDirObject = FileUtil.toFileObject(targetDir);
        if (sourceFileObject == null || targetDirObject == null) {
            // do not try copying with null objects
            return false;
        }
        FileObject targetFileObject = targetDirObject.getFileObject(name);
        if (targetFileObject != null && targetFileObject.isValid()) {  // file to copy is already there: do nothing
            return true;
        }
        
        // construct proper name
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            name = name.substring(0, index);
        }
        try {
            FileUtil.copyFile(sourceFileObject, targetDirObject, name);
            return true;
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return false;
    }

    /**
     * Get the name from a path give as string
     * @param source a system path like c:\temp\document.txt
     * @return the name of the file, e.g. document.txt
     */
    private String getNameFromSource(String source) {
        if (source != null && source.length() > 0) {
            // next line call is somewhat called twice in one copying process: improve algorithm?
            File f = getProjectOrSystemPath(source);
            if (f != null)
                return f.getName();
        }
        return null;
    }
    
    /**
     * Return the subdirectory (can also be a directory structure) in the
     * project folder. The directory is created if it not yet exists.
     * @param dirName The directory or a structure with "/" as path separator
     * @return the new directory as file, null if the directory does not 
     * exist and could not be created
     */
    private File getOrCreateProjectSubDir(String dirName) {
        if (dirName != null) {
            FileObject newObject = m_ProjectDir.getFileObject(dirName);
            if (newObject == null) {
                File projectDir = FileUtil.toFile(m_ProjectDir);
                File newFile = new File(projectDir, dirName);
                if (newFile.mkdir()) return newFile;
            }
            else{
                return FileUtil.toFile(newObject);
            }
        }
        return null;
    }

    /**
     * Get the complete path to a file in the project file system or in the
     * system.
     * @param localOrSystemPath local path in project or system path
     * @return the absolute path
     */
    public File getProjectOrSystemPath(String localOrSystemPath) {
        File file = null;
        if (localOrSystemPath != null && localOrSystemPath.length() > 0) {
            // try to find the file in project subdirectory
            FileObject localFolder = m_ProjectDir.getFileObject(localOrSystemPath);
            if (localFolder != null) {
                File f = FileUtil.toFile(localFolder);
                if (f != null && f.canRead()) {
                    file = f;
                }
            }
            if (file == null) { // first try did not work
                // read file directly
                File f = new File(localOrSystemPath);
                if (f != null && f.canRead()) {
                    file = f;
                }
            }
        }
        return file;
    }
    
}
