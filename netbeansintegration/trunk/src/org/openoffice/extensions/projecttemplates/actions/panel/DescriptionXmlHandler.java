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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.xml.XMLUtil;
import org.openoffice.extensions.projecttemplates.actions.ProjectVersion;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ProjectTypeHelper;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Handle the description.xml file: rwead and write it back after changes are 
 * made.
 * @author sg128468
 */
public class DescriptionXmlHandler {

    // initial version
    private static final String INITIAL_VERSION = "0.0.1"; // NOI18N

    // tag names from the description xml
    private static final String DESCRIPTION_TAG_NAME = "description";  // NOI18N
    private static final String VERSION_TAG_NAME = "version";  // NOI18N
    private static final String IDENTIFIER_TAG_NAME = "identifier"; // NOI18N
    private static final String DEPENDENCY_TAG_NAME = "dependencies"; // NOI18N
    private static final String OOO_VERSION_TAG_NAME = "OpenOffice.org-minimal-version"; // NOI18N
    private static final String URL_TAG_NAME = "update-information"; // NOI18N
    private static final String URL_SRC_TAG_NAME = "src"; // NOI18N
    private static final String REGISTRATION_TAG_NAME = "registration"; // NOI18N
    private static final String LICENSE_TAG_NAME = "simple-license"; // NOI18N
    private static final String LICENSE_TEXT_TAG_NAME = "license-text"; // NOI18N
    private static final String DISPLAY_NAME_TAG_NAME = "display-name"; // NOI18N
    private static final String NAME_TAG_NAME = "name"; // NOI18N
    private static final String PUBLISHER_TAG_NAME = "publisher"; // NOI18N
    private static final String ICON_TAG_NAME = "icon"; // NOI18N
    private static final String DEFAULT_ICON_TAG_NAME = "default"; // NOI18N
    private static final String HIGH_CONTRAST_ICON_TAG_NAME = "high-contrast"; // NOI18N
    private static final String EXTENSION_DESCRIPTION_TAG_NAME = "extension-description"; // NOI18N
       
    // attribute names from tags
    private static final String VALUE_ATTRIBUTE_NAME = "value"; // NOI18N
    private static final String OOO_VERSION_ATTRIBUTE_NAME = "d:name"; // NOI18N
    private static final String URL_HREF_ATTRIBUTE_NAME = "xlink:href"; // NOI18N
    private static final String ACCEPT_BY_ATTRIBUTE_NAME = "accept-by"; // NOI18N
    private static final String DEFAULT_LICENSE_ATTRIBUTE_NAME = "default-license-id"; // NOI18N
    private static final String LANGUAGE_ATTRIBUTE_NAME = "lang"; // NOI18N
    private static final String LICENSE_ID_ATTRIBUTE_NAME = "license-id"; // NOI18N
    
    // namespace for attribute nodes
    private static final String NODE_NAMESPACE = "http://openoffice.org/extensions/description/2006"; // NOI18N
    
    private static final String NB_PLUGIN_STAMP = "Created with Apache OpenOffice API plug-in for NetBeans Version "; // NOI18N

    // project root dir
    private final FileObject m_ProjectDir;
    
    // DOM Document of the description.xml
    private Document m_DescriptionDocument;
    
    // icon identification:
    private static final int DEFAULT_ICON = 0;
    private static final int HIGH_DEFINITION_ICON = 1; 
    
    // properties from the description.xml
    private String m_DefaultLocale;
    private String m_Version;
    private String m_Identifier;
    private String m_DependencyValue;
    private String m_DependencyName;
    private String[] m_IconNames;
    private String[] m_UpdateURLs;
    private GenericDescriptionProperty<String> m_Licenses;
    private String m_acceptBy;
    private GenericDescriptionProperty<String[]>m_PublisherData;
    private GenericDescriptionProperty<String>m_DisplayName;
    private GenericDescriptionProperty<String> m_DescriptionData;    
    
    
    /** Creates a new instance of DescriptionXmlHandler 
     * Only used when initial version is written out without localized data.
     * NOTE: LOCALE IS NOT INITIALIZED!
     * @param projectDir the root directory of the project
     */
    public DescriptionXmlHandler(FileObject projectDir) {
        this.m_ProjectDir = projectDir;
        
        // set defaults
        m_Version = INITIAL_VERSION;
        m_Identifier = getDefaultExtensionIdentifier();
        m_DependencyName = ""; // NOI18N
        m_DependencyValue = ""; // NOI18N
        m_UpdateURLs = new String[0];
        m_IconNames = new String[2];
    }
    
    /** Creates a new instance of DescriptionXmlHandler 
     * @param projectDir the root directory of the project
     * @param handler the handler for existing description data
     */
    public DescriptionXmlHandler(FileObject projectDir, DataHandler handler) {
        this(projectDir);
        
        // set defaults for localized stuff
        m_Licenses = handler.getGenericStringProperty("license");
        m_PublisherData = handler.getGenericStringArrayProperty("publisher");
        m_DisplayName = handler.getGenericStringProperty("display");
        m_DescriptionData = handler.getGenericStringProperty("description");
        

        FileObject fo = m_ProjectDir.getFileObject(
            "src").getFileObject("description", "xml"); // NOI18N
        if (fo != null) {
            File f = FileUtil.toFile(fo);
            readXmlFile(f);  // overwrite defaults
        }
    }
 
    public String getExtensionIdentifier() {
        return m_Identifier;
    }
    
    public String getExtensionVersion() {
        return m_Version;
    }
    
    public GenericDescriptionProperty<String> getDisplayData() {
        return m_DisplayName;
    }
    
    public String[] getUpdateURLs() {
        return m_UpdateURLs;
    }
    
    public String getDependencyNumber() {
        return m_DependencyValue;
    }
    
    public String getDependencyName() {
        return m_DependencyName;
    }
    
    public String getDefaultIconName() {
        return m_IconNames[DEFAULT_ICON];
    }
    
    public String getHighContrastIconName() {
        return m_IconNames[HIGH_DEFINITION_ICON];
    }
    
    public String getDefaultLocale() {
        return m_DefaultLocale;
    }
    
    public void setDefaultLocale(String language) {
        m_DefaultLocale = language;
    }
    
    public void setExtensionIdentifier(String identifier) {
        m_Identifier = identifier;
    }
    
    public void setExtensionVersion(String version) {
        m_Version = version;
    }
    
    public void setDisplayData(GenericDescriptionProperty<String> data) {
        m_DisplayName = data;
    }
    
    public void setUpdateURLs(String[] urls) {
        m_UpdateURLs = urls;
    }
    
    public void setDependencyNumber(String dependencyNumber) {
        m_DependencyValue = dependencyNumber;
    }
    
    public void setDependencyName(String dependencyName) {
        m_DependencyName = dependencyName;
    }
    
    public void setDefaultIconName(String iconName) {
        m_IconNames[DEFAULT_ICON] = iconName;
    }
    
    public void setHighContrastIconName(String iconName) {
        m_IconNames[HIGH_DEFINITION_ICON] = iconName;
    }
    
    public GenericDescriptionProperty<String> getLicenseFiles() {
        return m_Licenses;
    }
    
    public void setLicenseFiles(GenericDescriptionProperty<String> lFiles) {
        m_Licenses = lFiles;
    }
    
    public void setAcceptBy(String acceptBy) {
        m_acceptBy = acceptBy;
    }
    
    public String getAcceptBy() {
        return m_acceptBy;
    }
    

    public GenericDescriptionProperty<String[]> getPublisherData() {
        return m_PublisherData;
    }

    public void setPublisherData(GenericDescriptionProperty<String[]> m_PublisherData) {
        this.m_PublisherData = m_PublisherData;
    }
    
    public GenericDescriptionProperty<String> getDescriptionData() {
        return m_DescriptionData;
    }

    public void setDescriptionData(GenericDescriptionProperty<String> descriptionData) {
        this.m_DescriptionData = descriptionData;
    }
    
    /**
     * Write the current state of the description out to the description.xml
     * file.
     */
    public void writeDescriptionXml() {
        OutputStream outStream = null;
        try {
            // build tree
            buildDomTree();
            
            // if description already exists, get it
            FileObject fo = m_ProjectDir.getFileObject("src").getFileObject("description", "xml"); // NOI18N
            // if not, create it
            if (fo == null)
                fo = m_ProjectDir.getFileObject("src").createData("description", "xml"); // NOI18N

            File f = FileUtil.toFile(fo);

            // Transmit the request document
            outStream = new FileOutputStream(f);

            XMLUtil.write(m_DescriptionDocument, outStream, "UTF-8"); // NOI18N
            outStream.flush();
        }
        catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (ParserConfigurationException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally {
            try {
                if (outStream != null)
                    outStream.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }

    /**
     * Build the Dom tree from the settings of the properties received per UI
     */
    private void buildDomTree() throws ParserConfigurationException {
        // Build the request document
        if (m_DescriptionDocument == null) {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            m_DescriptionDocument = builder.newDocument();
        }
        
        // comment for identifying the extension
        setOrCreateComment(NB_PLUGIN_STAMP.concat(ProjectVersion.getProjectVersion()));

        // root node
        Element descriptionNode = getOrCreateElement(DESCRIPTION_TAG_NAME, m_DescriptionDocument);
        descriptionNode.setAttribute("xmlns", NODE_NAMESPACE); // NOI18N
        descriptionNode.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink"); // NOI18N

        // version
        Element versionNode = getOrCreateElement(VERSION_TAG_NAME, descriptionNode);
        versionNode.setAttribute(VALUE_ATTRIBUTE_NAME, m_Version);

        // identifier
        Element idNode = getOrCreateElement(IDENTIFIER_TAG_NAME, descriptionNode);
        idNode.setAttribute(VALUE_ATTRIBUTE_NAME, m_Identifier);

        // icon
        if (m_IconNames[DEFAULT_ICON] != null || m_IconNames[HIGH_DEFINITION_ICON] != null) { // at least one icon should be there
            Element iconNode = getOrCreateElement(ICON_TAG_NAME, descriptionNode);
            if (m_IconNames[DEFAULT_ICON] != null && m_IconNames[DEFAULT_ICON].length() > 0) {
                Element defaultNode = getOrCreateElement(DEFAULT_ICON_TAG_NAME, iconNode);
                defaultNode.setAttribute(URL_HREF_ATTRIBUTE_NAME, m_IconNames[DEFAULT_ICON]);   
            }
            else {
                deleteElements(DEFAULT_ICON_TAG_NAME);
            }
            if (m_IconNames[HIGH_DEFINITION_ICON] != null && m_IconNames[HIGH_DEFINITION_ICON].length() > 0) {
                Element highContrastNode = getOrCreateElement(HIGH_CONTRAST_ICON_TAG_NAME, iconNode);
                highContrastNode.setAttribute(URL_HREF_ATTRIBUTE_NAME, m_IconNames[HIGH_DEFINITION_ICON]);   
            }
            else {
                deleteElements(HIGH_CONTRAST_ICON_TAG_NAME);
            }
        }
        else {
            deleteElements(ICON_TAG_NAME);
        }
        
        // display name
        // do not write out if nothing's set
        if (m_DisplayName != null && m_DisplayName.getSize() > 0) {
            Element updateNode = getOrCreateElement(DISPLAY_NAME_TAG_NAME, descriptionNode);
            deleteElements(NAME_TAG_NAME, updateNode);
            for (int i = 0; i < m_DisplayName.getSize(); i++) {
                String locale = m_DisplayName.getShortLocale(i);
                Element srcNode = getOrCreateElementWithAttribute(NAME_TAG_NAME, LANGUAGE_ATTRIBUTE_NAME, locale, updateNode);
//                srcNode.setAttribute(LANGUAGE_ATTRIBUTE_NAME, locale);
                String object = m_DisplayName.getProperty(i);
                srcNode.setTextContent(object);
            }
        }
        else {
            deleteElements(DISPLAY_NAME_TAG_NAME);
        }
        
        // publisher name and link
        // do not write out if nothing's set
        if (m_PublisherData != null && m_PublisherData.getSize() > 0) {
            Element publisherNode = getOrCreateElement(PUBLISHER_TAG_NAME, descriptionNode);
            deleteElements(NAME_TAG_NAME, publisherNode);
            for (int i = 0; i < m_PublisherData.getSize(); i++) {
                String locale = m_PublisherData.getShortLocale(i);
                Element nameNode = getOrCreateElementWithAttribute(NAME_TAG_NAME, LANGUAGE_ATTRIBUTE_NAME, locale, publisherNode);
                String[] nameAndLink = m_PublisherData.getProperty(i);
                nameNode.setAttribute(URL_HREF_ATTRIBUTE_NAME, nameAndLink[1]);
                nameNode.setTextContent(nameAndLink[0]);
            }
        }
        else {
            deleteElements(PUBLISHER_TAG_NAME);
        }
        
        // update Urls
        // do not write out if nothing's set
        if (m_UpdateURLs != null && m_UpdateURLs.length != 0) {
            Element updateNode = getOrCreateElement(URL_TAG_NAME, descriptionNode);
            for (int i = 0; i < m_UpdateURLs.length; i++) {
                Element srcNode = getOrCreateElementWithAttribute(URL_SRC_TAG_NAME, URL_HREF_ATTRIBUTE_NAME, m_UpdateURLs[i], updateNode);
//                srcNode.setAttribute(URL_HREF_ATTRIBUTE_NAME, m_UpdateURLs[i]);   
            }
        }
        else {
            deleteElements(URL_TAG_NAME);
        }

        // extension description
        // do not write out if nothing's set
        if (m_DescriptionData != null && m_DescriptionData.getSize() != 0) {
            Element updateNode = getOrCreateElement(EXTENSION_DESCRIPTION_TAG_NAME, descriptionNode);
            deleteElements(URL_SRC_TAG_NAME, updateNode);
            for (int i = 0; i < m_DescriptionData.getSize(); i++) {
                String descriptionFileReference = m_DescriptionData.getProperty(i);
                String locale = m_DescriptionData.getShortLocale(i);
                Element srcNode = getOrCreateElementWithAttribute(URL_SRC_TAG_NAME, LANGUAGE_ATTRIBUTE_NAME, locale, updateNode);
//                srcNode.setAttribute(LANGUAGE_ATTRIBUTE_NAME, locale);
                srcNode.setAttribute(URL_HREF_ATTRIBUTE_NAME, descriptionFileReference);   
            }
        }
        else {
            // deleteElements(URL_TAG_NAME);
            deleteElements(EXTENSION_DESCRIPTION_TAG_NAME);
        }

        // License Files
        // do not write out if nothing's set
        if (m_Licenses != null && m_Licenses.getSize() > 0) {
//            String licenseDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_ProjectDir, "licenses.dir"); // NOI18N
//            if (licenseDir == null) {
//                licenseDir = "licenses"; // NOI18N
//            }
            Element registrationNode = getOrCreateElement(REGISTRATION_TAG_NAME, descriptionNode);
            Element depNode = getOrCreateElement(LICENSE_TAG_NAME, registrationNode);
            depNode.setAttribute(ACCEPT_BY_ATTRIBUTE_NAME, m_acceptBy);
            depNode.setAttribute(DEFAULT_LICENSE_ATTRIBUTE_NAME, m_Licenses.getDefaultShortLocale());
            
            // remove existing elements: are created new anyway
            deleteElements(LICENSE_TEXT_TAG_NAME);
            for (int i = 0; i < m_Licenses.getSize(); i++) {
                String locale = m_Licenses.getShortLocale(i);
                // avoid getting an lement we just created: locle must be unique
                Element srcNode = getOrCreateElementWithAttribute(LICENSE_TEXT_TAG_NAME, 
                    LANGUAGE_ATTRIBUTE_NAME, locale, depNode);
                String prop = m_Licenses.getProperty(i);
                srcNode.setAttribute(URL_HREF_ATTRIBUTE_NAME, prop); // NOI18N
//                srcNode.setAttribute(LANGUAGE_ATTRIBUTE_NAME, locale);
                if (i == 0) { // double effort for default: now the first one is it, not the one with the right id.
                    srcNode.setAttribute(LICENSE_ID_ATTRIBUTE_NAME, m_Licenses.getDefaultShortLocale());
                }
            }
        }
        else {
            deleteElements(REGISTRATION_TAG_NAME);
        }

        // dependencies
        // do not write out if nothing's set
        if ((m_DependencyValue != null && m_DependencyValue.length() != 0)
            && (m_DependencyName != null && m_DependencyName.length() != 0)) {
            Element depNode = getOrCreateElement(DEPENDENCY_TAG_NAME, descriptionNode);
            Element oooNode = getOrCreateElement(OOO_VERSION_TAG_NAME, depNode);
            oooNode.setAttribute(VALUE_ATTRIBUTE_NAME, m_DependencyValue);
            Attr attribute = oooNode.getAttributeNode(OOO_VERSION_ATTRIBUTE_NAME);
            if (attribute == null) {
                Attr newAttribute = m_DescriptionDocument.createAttributeNS(
                    NODE_NAMESPACE, 
                    OOO_VERSION_ATTRIBUTE_NAME);
                newAttribute.setValue(m_DependencyName);
                oooNode.setAttributeNodeNS(newAttribute);
            }
            else {
                attribute.setValue(m_DependencyName);
            }
        }
        else {
            deleteElements(DEPENDENCY_TAG_NAME);
        }
    }

    private void fillDisplayName() {
        try {   
            NodeList idList = m_DescriptionDocument.getElementsByTagName(DISPLAY_NAME_TAG_NAME);
            Node displayNameNode = idList.item(0);
            NodeList nameNodeList = displayNameNode.getChildNodes();
            
            boolean defaultLocaleFound = false;
            for (int i = 0; i < nameNodeList.getLength(); i++) {
                Node nameNode = nameNodeList.item(i);
                try {  
                    // NP inside here should not stop loop
                    // else it's not a name node
                    if (nameNode.getNodeName().equals(NAME_TAG_NAME)) {
                        String name = nameNode.getTextContent();
                        NamedNodeMap idAttr = nameNode.getAttributes();
                        Node urlValueNode = idAttr.getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
                        String locale = urlValueNode.getNodeValue();
                        if (name != null && locale != null) {
                            m_DisplayName.setPropertyAndLocale(locale, name);
                            if (!defaultLocaleFound && m_DefaultLocale == null) {
                                m_DefaultLocale = locale;
                                defaultLocaleFound = true;
                            }
                        }
                    }
                }
                catch (NullPointerException ex) { // so many ways this can cause NP
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                }
            }
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }

        
/*            NodeList licenseList = m_DescriptionDocument.getElementsByTagName(LICENSE_TEXT_TAG_NAME);
            int length = licenseList.getLength();
            for (int i = 0; i < length; i++) {
                Node srcNode = licenseList.item(i); 
                try {  // NP inside here should not stop loop
                    NamedNodeMap idAttr = srcNode.getAttributes();
                    Node urlValueNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
                    String name = urlValueNode.getNodeValue();
                    Node langValueNode = idAttr.getNamedItem(LICENSE_LANGUAGE_ATTRIBUTE_NAME);
                    String language = langValueNode.getNodeValue();
                    Node idValueNode = idAttr.getNamedItem(LICENSE_ID_ATTRIBUTE_NAME);
                    String licenseID = null;
                    if (idValueNode != null) {
                        licenseID = idValueNode.getNodeValue();
                    }
                    boolean defLicense = licenseID != null && licenseID.equals(defaultLicenseID);
                    LicenseFile lFile = new LicenseFile(name, 
                        LanguageDefinition.getLanguageNameForShortName(language), defLicense);
                    if (defLicense)
                        LicenseFile.setDefaultLicense(lFile);
                    licenses.add(lFile);
                }
                catch (NullPointerException ex) { // so many ways this can cause NP
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                }
            }
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } */
    }

    /**
     * Function for accessing an existing description.xml file.
     * Called from the c'tor, so it must not throw an exception, if avoidable.
     * In case of a missing description.xml, fall back to defaults.
     * @param xmlFile The descritpion.xml file.
     */
    private void readXmlFile(File xmlFile) {
        InputStream inStream = null;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder(); 
            inStream = new FileInputStream(xmlFile);
            m_DescriptionDocument = builder.parse(inStream);
            // get information for the panels
            fillVersionAndIdentifierAndIcons();
            fillDisplayName();
            fillDependencies();
            fillUpdateURLs();
            fillLicenseFiles();
            fillPublisher();
            fillDescriptionFile();
        } catch (ParserConfigurationException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (SAXException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally {
            try {
                if (inStream != null)
                    inStream.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    /** 
     * Set the license files. Default is, there are none.
     */
    private void fillLicenseFiles() {
//        String defaultLicenseID = null;
        
        try {   // accept by and default license first
            // get the top level node list for licenses
            NodeList idList = m_DescriptionDocument.getElementsByTagName(LICENSE_TAG_NAME);
            Node licenseNode = idList.item(0);
            NamedNodeMap idAttr = licenseNode.getAttributes();
            
            Node acceptValueNode = idAttr.getNamedItem(ACCEPT_BY_ATTRIBUTE_NAME);
            m_acceptBy = acceptValueNode.getNodeValue();

            // write this still out, but ignore when reading...
//            Node defaultValueNode = idAttr.getNamedItem(DEFAULT_LICENSE_ATTRIBUTE_NAME);
//            defaultLicenseID = defaultValueNode.getNodeValue();
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        
        try {  // license files next
            // get the top level node list for urls
            NodeList licenseList = m_DescriptionDocument.getElementsByTagName(LICENSE_TEXT_TAG_NAME);
            boolean defaultLocaleFound = false;
            int length = licenseList.getLength();
            for (int i = 0; i < length; i++) {
                Node srcNode = licenseList.item(i); 
                try {  // NP inside here should not stop loop
                    NamedNodeMap idAttr = srcNode.getAttributes();
                    Node urlValueNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
                    String name = urlValueNode.getNodeValue();
                    Node langValueNode = idAttr.getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
                    String language = langValueNode.getNodeValue();
                    // ignore license ids when reading
//                    Node idValueNode = idAttr.getNamedItem(LICENSE_ID_ATTRIBUTE_NAME);
//                    String licenseID = null;
//                    if (idValueNode != null) {
//                        licenseID = idValueNode.getNodeValue();
//                    }
                    m_Licenses.setPropertyAndLocale(language, name);
                    if (!defaultLocaleFound && m_DefaultLocale == null) {
                        defaultLocaleFound = true;
                        m_DefaultLocale = language;
                    }
                }
                catch (NullPointerException ex) { // so many ways this can cause NP
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                }
            }
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    private void fillPublisher() {

        try {  // publisher link
            // get the top level node list for urls
            NodeList publisherList = m_DescriptionDocument.getElementsByTagName(PUBLISHER_TAG_NAME);
            Node displayNameNode = publisherList.item(0);
            NodeList nameNodeList = displayNameNode.getChildNodes();
           
            boolean defaultLocaleFound = false;
            for (int i = 0; i < nameNodeList.getLength(); i++) {
                Node nameNode = nameNodeList.item(i);
                try {  // NP inside here should not stop loop
                    if (nameNode.getNodeName().equals(NAME_TAG_NAME)) {
                        String name = nameNode.getTextContent();
                        NamedNodeMap idAttr = nameNode.getAttributes();
                        Node urlValueNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
                        String url = urlValueNode.getNodeValue();
                        Node localeValueNode = idAttr.getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
                        String locale = localeValueNode.getNodeValue();
                        String[] publish = new String[]{
                            name, url,
                        };
                        m_PublisherData.setPropertyAndLocale(locale, publish);
                        if (!defaultLocaleFound && m_DefaultLocale == null) {
                            m_DefaultLocale = locale;
                            defaultLocaleFound = true;
                        }
                    }
                }
                catch (NullPointerException ex) { // so many ways this can cause NP
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                }
            }
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    /**
     * Set the data for the description
     */
    private void fillDescriptionFile() {

        try { 
            // get the top level node list for descriptions
            NodeList descriptionEntryList = m_DescriptionDocument.getElementsByTagName(EXTENSION_DESCRIPTION_TAG_NAME);
            Node srcNode = descriptionEntryList.item(0);
            NodeList srcNodeList = srcNode.getChildNodes();
            
            for (int i = 0; i < srcNodeList.getLength(); i++) {
                Node descriptionNode = srcNodeList.item(i);
                try {  // NP inside here should not stop loop
                    NamedNodeMap idAttr = descriptionNode.getAttributes();
                    Node descriptionRefTypeNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
                    String descriptionFileReference = descriptionRefTypeNode.getNodeValue();
                    Node localeTypeNode = idAttr.getNamedItem(LANGUAGE_ATTRIBUTE_NAME);
                    String locale = localeTypeNode.getNodeValue();
                    m_DescriptionData.setPropertyAndLocale(locale, descriptionFileReference);
                    
                }
                catch (NullPointerException ex) { // so many ways this can cause NP
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                }
            }
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    /** 
     * Set the update URLs. Default is, there are none.
     */
    private void fillUpdateURLs() {
        Vector<String>urls = new Vector<String>();
        try {
            // get the top level node list for urls
            NodeList idList = m_DescriptionDocument.getElementsByTagName(URL_TAG_NAME);
            Node urlNode = idList.item(0);
            NodeList childNodes = urlNode.getChildNodes();
            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                Node srcNode = childNodes.item(i); 
                if (srcNode.getNodeName().equals(URL_SRC_TAG_NAME)) {
                    try {  // NP inside here should not stop loop
                        NamedNodeMap idAttr = srcNode.getAttributes();
                        Node idValueNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
                        urls.add(idValueNode.getNodeValue());
                    }
                    catch (NullPointerException ex) { // so many ways this can cause NP
                        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                    }
                }
            }
        }
        catch (NullPointerException ex) { // so many ways this can cause NP
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
        }
        m_UpdateURLs = urls.toArray(new String[urls.size()]);
    }
    
    /** 
     * Set the dependency of the extension. Default is, there is none.
     */
    private void fillDependencies() {
        // get the top level node list for dependency
        try {
            NodeList idList = m_DescriptionDocument.getElementsByTagName(DEPENDENCY_TAG_NAME);
            Node dependencyNode = idList.item(0);
            NodeList childNodes = dependencyNode.getChildNodes();
            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                Node identifierNode = childNodes.item(i);
                if (identifierNode.getNodeName().equals(OOO_VERSION_TAG_NAME)) {
                    NamedNodeMap idAttr = identifierNode.getAttributes();
                    Node idValueNode = idAttr.getNamedItem(VALUE_ATTRIBUTE_NAME);
                    m_DependencyValue = idValueNode.getNodeValue();
                    Node nameValueNode = idAttr.getNamedItem(OOO_VERSION_ATTRIBUTE_NAME);
                    m_DependencyName = nameValueNode.getNodeValue();
                }
            }
        }
        catch (NullPointerException ex) { // so many ways this can cause NP
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
        }
    }
    
    /** 
     * Set the version name and the unique identifier for the extension.
     * The identifier is set to a sensible default when it cannot be read.
     * @param response the Dom Document of the description.xml
     */
    private void fillVersionAndIdentifierAndIcons() {
        // get the top level node list for version
        try {
            NodeList versionList = m_DescriptionDocument.getElementsByTagName(VERSION_TAG_NAME);
            Node versionNode = versionList.item(0);  // must be only one version
            NamedNodeMap versionAttr = versionNode.getAttributes();
            Node versionValueNode = versionAttr.getNamedItem(VALUE_ATTRIBUTE_NAME);
            m_Version = versionValueNode.getNodeValue();
        }
        catch (NullPointerException ex) { // so many ways this can cause NP
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
        }
        
        // get the top level node list for identifier
        try {
            NodeList idList = m_DescriptionDocument.getElementsByTagName(IDENTIFIER_TAG_NAME);
            Node identifierNode = idList.item(0);  // must be only one identifier
            NamedNodeMap idAttr = identifierNode.getAttributes();
            Node idValueNode = idAttr.getNamedItem(VALUE_ATTRIBUTE_NAME);
            m_Identifier = idValueNode.getNodeValue();
        }
        catch (NullPointerException ex) { // so many ways this can cause NP
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
        }
        
        // get default icon
        try {
            NodeList iconList = m_DescriptionDocument.getElementsByTagName(DEFAULT_ICON_TAG_NAME);
            for (int i=0; i<iconList.getLength(); i++) {
                // right now there's only one node named "default", but who knows... 
                Node defaultNode = iconList.item(i);
                try { // for an unknown node named "default" many things may be null.
                    NamedNodeMap idAttr = defaultNode.getAttributes();
                    Node idValueNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
                    m_IconNames[0] = idValueNode.getNodeValue();
                }
                catch (NullPointerException ex) { // so many ways this can cause NP
                    // exception here means nothing tragic, just record
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        }
        catch (NullPointerException ex) { // so many ways this can cause NP
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
        }

        // get high definition icon
        try {
            NodeList iconList = m_DescriptionDocument.getElementsByTagName(HIGH_CONTRAST_ICON_TAG_NAME);
            // high-contrast is hardly there two times
            Node defaultNode = iconList.item(0);
            NamedNodeMap idAttr = defaultNode.getAttributes();
            Node idValueNode = idAttr.getNamedItem(URL_HREF_ATTRIBUTE_NAME);
            m_IconNames[1] = idValueNode.getNodeValue();
        }
        catch (NullPointerException ex) { // so many ways this can cause NP
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
        }
    }
    
    /**
     * Create a unique identifier for the extension. According to the DevGuide,
     * it should be <path>.<OXT name>
     * @return the unique identifier
     */
    private String getDefaultExtensionIdentifier() {
        StringBuffer id = new StringBuffer();
        String projectName = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_ProjectDir, "project.name");
        String regClassName = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_ProjectDir, "registration.classname");
        int index = regClassName!=null?regClassName.lastIndexOf('.'):-1;
        if (index > 0)
            id.append(regClassName.substring(0, index + 1));
        id.append(projectName);
        return id.toString();
    }
 
    private void setOrCreateComment(String data) {
        NodeList list = m_DescriptionDocument.getChildNodes();
        int length = list.getLength();
        boolean createNewComment = true;
        for (int i = 0; i < length; i++) {
            Node node = list.item(i);
            int type = node.getNodeType();
            if (type == Node.COMMENT_NODE) {
                Comment c = (Comment)node;
                String text = c.getData();
                if (text.startsWith(NB_PLUGIN_STAMP)) {
                    createNewComment = false;
                    c.setData(data);
                }
            }
        }
        if (createNewComment) {
            Comment comment = m_DescriptionDocument.createComment(data);
            m_DescriptionDocument.appendChild(comment);
        }
    }
    
    private Element getOrCreateElement(String tagName, Node parent) {
        NodeList list = parent.getChildNodes();
        
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element)node;
                    if (e.getTagName().equals(tagName)) {
                        return e;
                    }
                }
            }
        }

        Element node = m_DescriptionDocument.createElement(tagName);
        parent.appendChild(node);
        
        return node;
    }

    private Element getOrCreateElementWithAttribute(String tagName, String attrName, 
                        String attrValue, Node parent) {
        NodeList list = parent.getChildNodes();
        
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element)node;    
                    if (e.getTagName().equals(tagName)) {
                        String value = e.getAttribute(attrName);
                        if (value != null && value.equals(attrValue)) {
                            return e;
                        }
                    }
                }
            }
        }

        Element node = m_DescriptionDocument.createElement(tagName);
        node.setAttribute(attrName, attrValue);
        parent.appendChild(node);
        
        return node;
    }
    
    private void deleteElements(String elementName, Node parent) {
        NodeList list = parent.getChildNodes();
        if (list != null) {
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals(elementName)) {
                        parent.removeChild(node);
                    }
                }
            }
        }
        
    }
    
    private void deleteElements(String elementName) {
        NodeList list = m_DescriptionDocument.getElementsByTagName(elementName);
        if (list != null && list.getLength() > 0) {
            Node parentNode = list.item(0).getParentNode();
            while (list.getLength() > 0) {
                Node node = list.item(0);
                parentNode.removeChild(node);
            }
        }
        
    }
}
