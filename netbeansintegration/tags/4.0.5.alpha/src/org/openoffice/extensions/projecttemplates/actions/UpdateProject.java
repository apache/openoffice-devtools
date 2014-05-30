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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.xml.XMLUtil;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.util.LogWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Update all relevant file in a OOo project to the newest version,
 * if this is necessary to support new features.
 */
public class UpdateProject {
    
    private FileObject m_projectRoot;
    private String m_sVersionString;
    private String m_sProjectName;
    
    public static void updateProject(FileObject projectRoot, String versionString, String projectName) {
        UpdateProject up = new UpdateProject(projectRoot, versionString, projectName);
        up.updateProject();
        
    }
    
    /** Creates a new instance of UpdateProject
     * Do not instantiate, therefore private
     */
    private UpdateProject(FileObject projectRoot, String versionString, String projectName) {
        m_projectRoot = projectRoot;
        m_sVersionString = versionString;
        m_sProjectName = projectName;
    }
    
    private void updateProject() {
        FileObject propsFile = m_projectRoot.getFileObject("nbproject/project-uno.properties"); // NOI18N
        Properties props = new Properties();
        try {
            File f = FileUtil.toFile(propsFile);
            FileInputStream fileIn = new FileInputStream(f);
            props.load(fileIn);
            fileIn.close();
            String projTypes = props.getProperty("uno.project.type"); // NOI18N
            String zipFileName = NbBundle.getMessage(UpdateProject.class, projTypes); // NOI18N
            if (zipFileName != null) {  // try to get the file...
                java.net.URL url = this.getClass().getResource("/".concat(zipFileName)); // NOI18N
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                changeProjectFiles(in);
            }
        } catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (MissingResourceException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (NullPointerException ex)  {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private void changeProjectFiles(InputStream source) {
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            try {
                while ((entry = str.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        // in this template we don't have to change any paths
                        continue;
                    } else {
                        if (entry.getName().endsWith("build-uno-impl.xml")) { // NOI18N
                            // stream must be copied, or str is destroyed
                            ByteArrayOutputStream temp = new ByteArrayOutputStream ((int) entry.getSize());
                            FileUtil.copy (str, temp);
                            byte[] buf = temp.toByteArray();
                            ByteArrayInputStream stream = new ByteArrayInputStream(buf);
                            handleBuildUnoXmlFile(stream);
                        }
                        else if (entry.getName().endsWith("project-uno.properties")) { // NOI18N
                            handleUnoPropertiesFile(str);
                        }
                        else if (entry.getName().endsWith("CentralRegistrationClass.java")) { // NOI18N
                            handleCentralregistrationClass(str);
                        }
                    }
                }
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        } finally {
            try {
                source.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    private void handleBuildUnoXmlFile(InputStream inStream) {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        OutputStream outStream = null;
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document newBuildUnoXmlDoc = builder.parse(inStream);
            
            // update the version string
            NodeList propList = newBuildUnoXmlDoc.getElementsByTagName(ProjectVersion.PROPERTY_NAME);
            for (int i = 0; i < propList.getLength(); i++) {
                Node licenseNode = propList.item(i);
                NamedNodeMap idAttr = licenseNode.getAttributes();
                Node acceptValueNode = idAttr.getNamedItem(ProjectVersion.NAME_NAME);
                if (acceptValueNode == null) continue;
                String propName = acceptValueNode.getNodeValue();
                if (ProjectVersion.UNO_VERSION_NAME.equals(propName)) {
                    Node valueNode = idAttr.getNamedItem(ProjectVersion.VALUE_NAME);
                    valueNode.setNodeValue(m_sVersionString);
                }
            }
            
            // update the project name; only one project element
            NodeList projectList = newBuildUnoXmlDoc.getElementsByTagName(ProjectVersion.PROJECT_NAME);
            Node projectNode = projectList.item(0);
            NamedNodeMap projectAttributes = projectNode.getAttributes();
            Node projectName = projectAttributes.getNamedItem(ProjectVersion.NAME_NAME);
            projectName.setNodeValue(m_sProjectName);
            
            // write the stuff back
            FileObject fo = m_projectRoot.getFileObject("nbproject").getFileObject("build-uno-impl", "xml"); // NOI18N

            FileLock lock = fo.lock();
            // Transmit the request document
            outStream = fo.getOutputStream(lock);

            XMLUtil.write(newBuildUnoXmlDoc, outStream, "UTF-8"); // NOI18N

            lock.releaseLock();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (SAXException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (ParserConfigurationException ex) {
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
    
    private void handleUnoPropertiesFile(InputStream inStream) {
        FileObject fo = m_projectRoot.getFileObject("nbproject").getFileObject("project-uno", "properties"); // NOI18N
        Properties newProps = new Properties();
        try {
            // load the new props from template
            newProps.load(inStream);
            
            // load the existing props
            InputStream in = fo.getInputStream();
            newProps.load(in);
            in.close();
            
            // update new variables that have __PlaceHolder__ settings as default
            String sIdlTypes = newProps.getProperty("idl_types.jar");
            if (sIdlTypes.equals("__ProjectName___IDL_types.jar")) {
                // replace __ProjectName__
                String projectName = newProps.getProperty("project.name");
                sIdlTypes = sIdlTypes.replace("__ProjectName__", projectName);
            }
            newProps.setProperty("idl_types.jar", sIdlTypes);
            String startupOptions = newProps.getProperty("office.startup.options");
            if (startupOptions.equals("__StartupOptions__")) {
                startupOptions = "";
            }
            newProps.setProperty("office.startup.options", startupOptions);
            FileLock lock = fo.lock();
            try {
                OutputStream out = fo.getOutputStream(lock);
                newProps.store(out, null);
                out.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
            finally {
                lock.releaseLock();
            }
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private void handleCentralregistrationClass(InputStream inStream) {
        FileObject projectUnoProps = m_projectRoot.getFileObject("nbproject").getFileObject("project-uno", "properties"); // NOI18N
        Properties props = new Properties();
        try {
            // load the existing props
            InputStream in = projectUnoProps.getInputStream();
            props.load(in);
            in.close();
            String centralRegClass = props.getProperty("central.registration.class"); // NOI18N
            // entry exists, do nothing
            if (centralRegClass == null || centralRegClass.length() == 0 || centralRegClass.equals("__CentralRegistrationClass__")) { // NOI18N
                String packageName = props.getProperty("registration.classname"); // NOI18N
                if (packageName == null) packageName = "";
                // get the first package structure here
                int index = packageName.indexOf(' ');
                if (index != - 1) {
                    packageName = packageName.substring(0, index);
                }
                index = packageName.lastIndexOf('.');
                if (index != -1) {
                    packageName = packageName.substring(0, index);
                }
                String fileName = packageName.concat(".CentralRegistrationClass"); // NOI18N
                
                // write correct properties back
                props.setProperty("central.registration.class", fileName); // NOI18N
                FileLock lock = projectUnoProps.lock();
                try {
                    OutputStream out = projectUnoProps.getOutputStream(lock);
                    props.store(out, null);
                    out.close();
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
                finally {
                    lock.releaseLock();
                }
                
                // write central registration class file object...
                FileObject targetFolderObject = FileUtil.createFolder(
                    m_projectRoot.getFileObject("src"), packageName.replace('.', '/')); // NOI18N
                
                FileObject targetFileObject = null;
                try {
                    targetFileObject = targetFolderObject.createData("CentralRegistrationClass", "java");
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
                
                if (targetFileObject != null) {  // do not overwrite something existing
                    BufferedReader buf = new BufferedReader(new InputStreamReader(inStream));
                    lock = targetFileObject.lock();
                    try {
                        OutputStream out = targetFileObject.getOutputStream(lock);
                        BufferedWriter write = new BufferedWriter(new OutputStreamWriter(out));
                        while (buf.ready()) {
                            String line = buf.readLine();
                            line = replaceLine(line, packageName);
                            write.write(line, 0, line.length());
                            write.newLine();
                        }
                        write.close();
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                    finally {
                        lock.releaseLock();
                    }
                }
                else {
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "File " + packageName.concat(".CentralRegistrationClass.java")
                        + " already exists in directory structure and is not overwritten. " + 
                        "Registration is supposed to fail."); // NOI18N
                }
                
            }
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private String replaceLine(String line, String packageName) {
        // replace the three things that can occur here
        if (line.indexOf("__UnoPackage__") != -1) {
            line = line.replaceAll("__UnoPackage__", packageName);
        }
        if (line.indexOf("__TimeStamp__") != -1) {
            line = line.replaceAll("__TimeStamp__", ConfigurationSettings.getTimeStamp());
        }
        if (line.indexOf("__UserName__") != -1) {
            line = line.replaceAll("__UserName__", ConfigurationSettings.getSettings().getUser());
        }
        return line;
    }
}
