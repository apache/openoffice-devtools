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

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.util.LogWriter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Determine if an older created project is up to date and supports
 * all features of this version of the plugin.
 * If not, the build-uno.xml and project-uno.properties files have to
 * be updated.
 *
 */
public class ProjectVersion {

    // plugin version as fallback if dynamic reading fails
    public static final String PLUGIN_VERSION = "2.0.6.000205";
    
    public static final String PROPERTY_NAME = "property";  // NOI18N
    public static final String PROJECT_NAME = "project";  // NOI18N
    public static final String NAME_NAME = "name";  // NOI18N
    public static final String SPEC_VERSION_NAME = "specversion"; // NOI18N
    public static final String UNO_VERSION_NAME = "build.uno.version";  // NOI18N
    public static final String VALUE_NAME = "value"; // NOI18N
    
    private String m_sLeastVersionString;
    private String m_sProjectName;
    
    private static ProjectVersion s_projectVersion;
    
    public static void updateProjectFiles(FileObject projectRoot) {
        if (s_projectVersion == null) {
            s_projectVersion = new ProjectVersion();
        }
        if (!s_projectVersion.projectUpToDate(projectRoot)) {
            UpdateProject.updateProject(projectRoot, 
                s_projectVersion.m_sLeastVersionString, 
                s_projectVersion.m_sProjectName);
        }
    }
    
    public static String getProjectVersion() {
        if (s_projectVersion == null) {
            s_projectVersion = new ProjectVersion();
        }
        return s_projectVersion.m_sLeastVersionString;
    }
    
    /**
     * Creates a new instance of ProjectVersion
     * NOTE: debugging or deploying the plug-in in a new NB installation
     * may lead to failure here because of different structures regarding 
     * directories. Check this with properly installed versions and Hello World
     * debugging.
     */
    private ProjectVersion() {
        FileObject root = FileUtil.getConfigRoot();
        
        try {
            FileObject ext_xml = root.getFileObject("Modules/org-openoffice-extensions.xml"); // NOI18N

            XMLReader parser = XMLReaderFactory.createXMLReader();
            VersionHandler vHandler = new VersionHandler(false);
            parser.setContentHandler(vHandler);
            InputSource source = new InputSource(ext_xml.getInputStream());
            parser.parse(source);

            m_sLeastVersionString = vHandler.getVersion();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (SAXException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        
        // officeial version should be existent with a length and has to contain 
        // at least two '.' and if not, set to small version so no updates happen.
        if ((m_sLeastVersionString == null || m_sLeastVersionString.length() == 0) ||
            m_sLeastVersionString.indexOf('.') == -1 ||
            m_sLeastVersionString.indexOf('.') == m_sLeastVersionString.lastIndexOf('.')) {
            m_sLeastVersionString = PLUGIN_VERSION;
            LogWriter.getLogWriter().log(LogWriter.LEVEL_WARNING, 
                "Cannot determine OOo plugin version, assuming " + m_sLeastVersionString); // NOI18N
        }
    }

    /**
     * Method for checking of the version of the important files
     * for building a project: build-uno.xml and project-uno.properties.
     * build-uno.xml contains a property for this.
     * @param xmlFile the build-uno.xml file to check
     * @returns true if the version in build-uno.xml is new enough, false otherwise
     **/
     // This method may be called often: at the beginning of every compile,
     // deploy or debug action. It must be fast: use SAX parser
    private boolean projectUpToDate(FileObject projectDir) {
        boolean projectUpToDate = true; // if in doubt, don't change anything
        try {
            if (projectDir != null) {
                FileObject xmlFile = projectDir.getFileObject("nbproject/build-uno-impl.xml"); // NOI18N
                if (xmlFile != null) {
                    XMLReader parser = XMLReaderFactory.createXMLReader();
                    VersionHandler vHandler = new VersionHandler(true);
                    parser.setContentHandler(vHandler);
                    InputSource source = new InputSource(xmlFile.getInputStream());
                    parser.parse(source);

                    String version = vHandler.getVersion();
                    m_sProjectName = vHandler.getProjectName();
                    projectUpToDate = isProjectUpToDate(version);
                }
            }
            
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (SAXException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return projectUpToDate;
    }
    
    private boolean isProjectUpToDate(String projectVersion) {
        // fast exit for projects older than 1.1.0: no project version there
        if (projectVersion == null) return false; 
        boolean projectMatches = checkVersionFormat(projectVersion);
        if (!projectMatches) {
            // nothing more to do here: do not update, this is terra incognita 
            return true;
        }
        // there are either three or four dots in projectVersion: since there are four in m_sLeastVersionString,
        // the while() statement is safe
        StringTokenizer projectTokens = new StringTokenizer(projectVersion, "."); // NOI18N
        // chech for m_sLeastVersionString is in c'tor.
        StringTokenizer leastVersionTokens = new StringTokenizer(m_sLeastVersionString, "."); // NOI18N
        while (projectTokens.hasMoreTokens()) {
            String projectString = projectTokens.nextToken();
            String leastVersionString = leastVersionTokens.nextToken();
            int project = new Integer(projectString).intValue();
            int leastVersion = new Integer(leastVersionString).intValue();
            if (project < leastVersion)
                return false;
        }
        return true;
    }
    
    /**
     * To be relatively save in the future and to make sure we do not produce
     * an exception when someone tampers with the project version, check the format.
     * Right now, the versions are major.minor.micro.build_number, so the function
     * returns true, if that format is recognized.
     * Note that before version 2.0.5 of the plugin, the format was
     * major.minor.micro, so if that format is recognized, the return value is also true.
     * @param version the version to check.
     * @return true if a correct version number was recognized
     */
    private boolean checkVersionFormat(String version) {
        // first check current version format
        boolean match = Pattern.matches("\\d+\\.\\d+\\.\\d+\\.\\d+", version); // NOI18N
        // now check old format
        if (!match) {
            match = Pattern.matches("\\d+\\.\\d+\\.\\d+", version); // NOI18N
        }
        return match;
    }
    
    /**
     * Handler for getting the project version. This is used for getting
     * the version from the installed plugin and from the current project.
     * This is determined by the constructor: with true, the project version is
     * read, with false it's the plugin version.
     * Since this handler already parses the build-uno.xml, the name of the 
     * project is also taken - is needed to be inserted into the updated 
     * build-uno.xml version.
     */
    private class VersionHandler extends DefaultHandler {
        private String version;
        private String project;
        private int allFound = 2; // 2 elements are searched, when found, rush through the rest
        private boolean searchProjectVersion; // search version of local project or else version of installed plugin 
        private boolean readElement;
        private StringBuffer tmpVersion;
        public VersionHandler(boolean searchProjectVersion) {
            this.searchProjectVersion = searchProjectVersion;
        }
        public void startElement(String uri, String localName, 
                String qName, Attributes attributes) throws SAXException {
            if (searchProjectVersion) {
                if (allFound > 0) {
                    if (localName.equals(PROPERTY_NAME)) {
                        String name = attributes.getValue(NAME_NAME);
                        if (name != null && name.equals(UNO_VERSION_NAME)) {
                            version = attributes.getValue(VALUE_NAME);
                            allFound--;
                        }
                    }
                    else if (localName.equals(PROJECT_NAME)) { // it's only one project element
                        project = attributes.getValue(NAME_NAME);
                        allFound--;
                    }
                }
            }
            else {
                String name = attributes.getValue(NAME_NAME);
                if (name.equals(SPEC_VERSION_NAME)) {
                    tmpVersion = new StringBuffer();
                    readElement = true;
                }
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            if (readElement) {
                tmpVersion.append(ch, start, length);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (readElement) {
                version = tmpVersion.toString();
                readElement = false;
            }
        }


        public String getVersion() {
            return version;
        }
        public String getProjectName() {
            return project;
        }
    }
}

