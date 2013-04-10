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
package org.openoffice.extensions.util.lookup;

import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.openide.filesystems.FileObject;
import org.openoffice.extensions.projecttemplates.actions.ProjectVersion;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ProjectTypeHelper;
import org.openoffice.extensions.util.listeners.DirectoryListener;

/**
 *
 * @author sg128468
 */
public class OOoProjectHook extends ProjectOpenedHook {
    
    private Project m_prj;
    private DirectoryListener m_listener;
    
    public OOoProjectHook(Project prj) {
        this.m_prj = prj;
    }


    @Override
    protected void projectOpened() {
        // should listener be static to avoid adding more than one: no, remove when closing
        if (m_prj != null && ProjectTypeHelper.isExtensionProject(m_prj.getProjectDirectory())) {
            // not sure if good idea, but let listener do all the work recursively
            final String IDL_JAR_NAME = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_prj, 
                    "idl_types.jar"); // NOI18N
            m_listener = new DirectoryListener(m_prj,
                    new String[]{"dist"}, 
                    IDL_JAR_NAME);
            // update project when necessary
            try {
                ProjectVersion.updateProjectFiles(m_prj.getProjectDirectory());
            }
            catch (Exception ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }

    @Override
    protected void projectClosed() {
        // remove listeners...
        if (m_prj != null && m_listener != null) {
            if (ProjectTypeHelper.isExtensionProject(m_prj.getProjectDirectory())) {
                m_listener.removeListenerRecursively();
                m_listener = null;
            }
        }
    }
}
