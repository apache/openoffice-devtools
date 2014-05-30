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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;

/**
 *
 * @author js93811
 */
public class ProjectTypeHelper {
    
    public static boolean isExtensionProject(Project p) {
        if (p != null) {
            FileObject f  = p.getProjectDirectory();
            return isExtensionProject(f);
        }
        return false;
    }
    
    public static boolean isExtensionProject(FileObject projDir) {
        boolean bExtensionProject = false;
        Object o = getObjectFromUnoProperties(projDir, "uno.project.type"); // NOI18N
        // some resource is not there: not an extension project, then
        if (o != null) {
            String projectType = o.toString();
            // i've classified the project fine grained to be flexible
            if (projectType.equals("office.calc.addin.project")) { // NOI18N
                bExtensionProject = true;
            }
            else if (projectType.equals("office.addon.project")) { // NOI18N
                bExtensionProject = true;
            }
            else if (projectType.equals("office.component.project")) { // NOI18N
                bExtensionProject = true;
            }
        }
        return bExtensionProject;
    }
    
    public static Object getObjectFromUnoProperties(FileObject projectDir, String propName) {
        PropertyResourceBundle unoProjectProps = getProperties(projectDir);
        if (unoProjectProps == null) return null;
        return unoProjectProps.handleGetObject(propName);
    }
    
    public static Object getObjectFromUnoProperties(Project proj, String propName) {
        if (proj == null) return null;
        FileObject projectDir = proj.getProjectDirectory();
        return getObjectFromUnoProperties(projectDir, propName);
    }
    
    public static Properties getUnoProperties(FileObject projectRoot) {
        Properties props = new Properties();
        if (projectRoot != null) {
            FileObject f = projectRoot.getFileObject("nbproject"); // NOI18N
            if (f != null) {
                f = f.getFileObject("project-uno", "properties"); // NOI18N
                if (f != null) {
                    InputStream inStream = null;
                    try {
                        inStream = f.getInputStream();
                        props.load(inStream);
                    } catch (FileNotFoundException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    } finally {
                        try {
                            if (inStream != null)
                                inStream.close();
                        } catch (IOException ex) {
                            LogWriter.getLogWriter().printStackTrace(ex);
                        }
                    }
                }
            }
        }
        return props;
    }
    
    private static PropertyResourceBundle getProperties(FileObject projectRoot) {
        PropertyResourceBundle unoProjectProps = null;
        if (projectRoot != null) {
            FileObject f = projectRoot.getFileObject("nbproject"); // NOI18N
            if (f != null) {
                f = f.getFileObject("project-uno", "properties"); // NOI18N
                if (f != null && f.isValid()) {
                    InputStream inStream = null;
                    try {
                        inStream = f.getInputStream();
                        unoProjectProps = new PropertyResourceBundle(inStream);
                    } catch (FileNotFoundException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    } finally {
                        try {
                            if (inStream != null)
                                inStream.close();
                        } catch (IOException ex) {
                            LogWriter.getLogWriter().printStackTrace(ex);
                        }
                    }
                }
            }
        }
        return unoProjectProps;
    }
}
