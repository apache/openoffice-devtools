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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class XcuFileCreator {
    
    private final String PROTOCOL_HANDLER_TEMPLATE_NAME = "ProtocolHandlerTemplate"; // NOI18N
    private final String ADDON_TEMPLATE_NAME = "AddonTemplate"; // NOI18N
    private final String TOOLBAR_TEMPLATE_NAME = "ToolBarTemplate"; // NOI18N
    private final String MENUBAR_TEMPLATE_NAME = "MenuBarTemplate"; // NOI18N
    private final String PROPS_FILE = "Bundle.properties"; // NOI18N

    private WizardDescriptor m_wizard;
    private Class m_templateBaseClass;
//    private String m_baseDir;
    
    public static final int XCU_ADDON_TYPE = 0;
    public static final int XCU_PROTOCOL_HANDLER_TYPE = 1;
    public static final int XCU_ADDIN_TYPE = 2; // 2do: use this

    /** Creates a new instance of XcuFileCreator */
    public XcuFileCreator(WizardDescriptor wiz, 
            Class templateBaseClass) {
        this.m_wizard = wiz;
        this.m_templateBaseClass = templateBaseClass;
//        this.m_baseDir = baseDir;
    }
    
    public void createXcuFile(int type) {
        switch(type) {
            case XCU_ADDON_TYPE:
                // add templates for menus and toolbars as wizard props
                m_wizard.putProperty("OfficeMenuBar",  // NOI18N
                    ProjectCreator.readFileLinesInArray(
                                m_templateBaseClass, PROPS_FILE, MENUBAR_TEMPLATE_NAME));
                m_wizard.putProperty("OfficeToolBar",  // NOI18N
                    ProjectCreator.readFileLinesInArray(
                                m_templateBaseClass, PROPS_FILE, TOOLBAR_TEMPLATE_NAME));
                // additional templates: better idea than this?
                createFile("org/openoffice/Office/Addons.xcu", new AdvancedReplace(m_wizard, (AddOn)m_wizard.getProperty("AddOn")), ADDON_TEMPLATE_NAME); // NOI18N
                break;
            case XCU_PROTOCOL_HANDLER_TYPE:
                createFile("org/openoffice/Office/ProtocolHandler.xcu", new AdvancedReplace(m_wizard, (AddOn)m_wizard.getProperty("AddOn")), PROTOCOL_HANDLER_TEMPLATE_NAME); // NOI18N
                break;
            case XCU_ADDIN_TYPE:
                // 2do
                break;
            default:
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Illegal xcu type."); // NOI18N
                return; // do nothing
        } 
    }

    private void createFile(String fileName, AdvancedReplace aReplace, String templateName) {
        // get the template
        String[] template_xcu = ProjectCreator.readFileLinesInArray(
                m_templateBaseClass, PROPS_FILE, templateName);
        try {
            FileObject projDir = FileUtil.toFileObject((File)m_wizard.getProperty("projdir")); // NOI18N
            String registryDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(projDir, "registry.dir"); // NOI18N
            if (registryDir == null) {
                registryDir = "registry"; // NOI18N
            }
            FileObject registryData = projDir.getFileObject(registryDir.concat("/data")); // NOI18N
            if (registryData == null) {
                registryData = FileUtil.createFolder(projDir, registryDir.concat("/data")); // NOI18N
            }
            
            FileObject ifcObject = FileUtil.createData(
                    registryData, fileName);
            fillFile(aReplace, ifcObject, template_xcu);
        }
        catch(IOException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private void fillFile(AdvancedReplace aReplace, FileObject fo, String[] data) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        FileLock lock = fo.lock();
        try {
            OutputStream out = fo.getOutputStream(lock);
            OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8"); // NOI18N
            try {
                writeStringArrayInFile(outWriter, aReplace, data);
            }
            finally {
                outWriter.close();
            }
        } finally {
            lock.releaseLock();
        }
    }

    private void writeStringArrayInFile(
            OutputStreamWriter outWriter, AdvancedReplace aReplace, String[]data) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<data.length; i++) {
            String[] nextData = aReplace.replaceVariables(data[i]);
            if (nextData.length == 1) {
                outWriter.write(nextData[0], 0, nextData[0].length());
                outWriter.write("\n", 0, 1); // NOI18N
            }
            else if (nextData.length > 0){ // just to avoid endless recursion
                outWriter.flush();
                writeStringArrayInFile(outWriter, aReplace, (String[])nextData);
            }
        }
        outWriter.flush();
    }
    
    public static String getXcuContext(String[] string) {
        if (string == null || string.length == 0)
            return "<value/>"; // NOI18N
        StringBuffer retValue = new StringBuffer("<value>"); // NOI18N
        for (int i = 0; i < string.length; i++) {
            if (i > 0) {
                retValue.append(","); // NOI18N
            }
            // replace short form of contexts with proper entries for the Office
            for (int j = 0; j < PropertyContainer.PROPERTY_CONTAINER_CONTEXTS.length; j++) {
                if (string[i].equals(PropertyContainer.PROPERTY_CONTAINER_CONTEXTS[j])) {
                    retValue.append(PropertyContainer.PROPERTY_CONTAINER_CONTEXT_REPRESENTATIONS[j]); // NOI18N
                }
            }
        }
        return retValue.append("</value>").toString(); // NOI18N
    }
}
