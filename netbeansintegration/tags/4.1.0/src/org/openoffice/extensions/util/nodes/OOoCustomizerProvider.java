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
package org.openoffice.extensions.util.nodes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.actions.panel.DataHandler;
import org.openoffice.extensions.projecttemplates.actions.panel.PropsPanel;
import org.openoffice.extensions.projecttemplates.actions.panel.VersionPanel;
import org.openoffice.extensions.util.ProjectTypeHelper;

/**
 *
 * @author sg128468
 */
public class OOoCustomizerProvider implements ProjectCustomizer.CompositeCategoryProvider, ActionListener {

    private static final String DISPLAY = "Display"; // NOI18N
    private static final String VERSION = "Version"; // NOI18N
    private static final String OOO_PROPERTIES_NAME = "OOoPropsTop"; // NOI18N
    
    private DataHandler m_DataHandler;
    
    public ProjectCustomizer.Category createCategory(Lookup arg0) {
        Project prj = arg0.lookup(Project.class);
        if (ProjectTypeHelper.isExtensionProject(prj)) {
            ProjectCustomizer.Category[] subEntries = new ProjectCustomizer.Category[]{
                ProjectCustomizer.Category.create(DISPLAY, 
                        NbBundle.getMessage(OOoCustomizerProvider.class, "OOoCustomizerProvider.DisplayCategory.Name"), // NOI18N 
                        null, new ProjectCustomizer.Category[0]),
                ProjectCustomizer.Category.create(VERSION, 
                        NbBundle.getMessage(OOoCustomizerProvider.class, "OOoCustomizerProvider.VersionCategory.Name"), // NOI18N
                        null, new ProjectCustomizer.Category[0])
            };
            ProjectCustomizer.Category toReturn = 
                ProjectCustomizer.Category.create(OOO_PROPERTIES_NAME,
                NbBundle.getMessage(OOoCustomizerProvider.class, "OOoCustomizerProvider.TopCategory.Name"), // NOI18N
                null, subEntries);
            toReturn.setStoreListener(this);
            return toReturn;
        }
        return null;
    }

    public JComponent createComponent(Category arg0, Lookup arg1) {
        Project prj = arg1.lookup(Project.class);
        if (prj != null) {
            FileObject m_ProjectDir= prj.getProjectDirectory();
            if (m_ProjectDir != null) {
                if (m_DataHandler == null || !m_DataHandler.getProjectDir().equals(m_ProjectDir)) {
                    m_DataHandler = new DataHandler(m_ProjectDir);
                }
                if (arg0.getName().equals(DISPLAY))
                    return new PropsPanel(m_DataHandler); 
                if (arg0.getName().equals(VERSION))
                    return new VersionPanel(m_DataHandler); 
                
            }
        }
        return new JLabel();
    }

    public static OOoCustomizerProvider create() {
        return new OOoCustomizerProvider();
    }

    public void actionPerformed(ActionEvent e) {
        if (m_DataHandler != null) { // npe happened in Addin project
            m_DataHandler.store();
        }
    }
}
