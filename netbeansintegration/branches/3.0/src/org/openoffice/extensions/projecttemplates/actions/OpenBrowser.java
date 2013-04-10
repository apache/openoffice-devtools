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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.apache.tools.ant.module.api.support.ActionUtils;
import org.netbeans.api.project.Project;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.awt.Mnemonics;
import org.openide.execution.ExecutorTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openoffice.extensions.config.ConfigurationValidator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ProjectTypeHelper;


/**
 *
 * @author js93811
 */
public class OpenBrowser extends AbstractAction implements ContextAwareAction {
    public void actionPerformed(ActionEvent e) {assert false;}
    
    public Action createContextAwareInstance(Lookup context) {
        return new ContextAction(context);
    }
    
    private boolean enable(Project p) {
        assert p != null;        
        // check if it is a special office extension project
        return ProjectTypeHelper.isExtensionProject(p);
    }
    
    private String labelFor(Project p) {
        assert p != null;    
        
        return NbBundle.getMessage(OpenBrowser.class, "CTL_PublishAction"); // NOI18N
    }
    
    private void perform(Project p) {
        assert p != null;

        boolean settingsValid = ConfigurationValidator.validateSettings();
        
        if (settingsValid) { // NP when settings not valid
            try {            
                FileObject projectDir = p.getProjectDirectory();
                String path = FileUtil.toFile(projectDir).getCanonicalPath();

                UpdateExternalJarProperties.copyAdditionalJars(projectDir);

                FileObject xmlFile = FileUtil.toFileObject(
                            new File(path + File.separator
                                           + "build.xml")); // NOI18N

                ExecutorTask task = ActionUtils.runTarget(xmlFile, new String[] { "uno-package" }, null); // NOI18N
                if (task.result() == 0) { // only publish something that sucessfully creates an oxt
                    URL oooUrl = new URL("http://extensions.services.openoffice.org/resources/user/submission");
                    URLDisplayer.getDefault().showURL(oooUrl);
                }
            }
            catch (IOException ex)
            {
                // TODO file can not be created , do something about it
                LogWriter.getLogWriter().printStackTrace(ex);
            }        
            catch (URISyntaxException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    private final class ContextAction extends AbstractAction implements Presenter.Popup {
        private final Project m_project;
        
        public ContextAction(Lookup context) {
            Project p = (Project) context.lookup(Project.class);
            m_project = (p != null && enable(p)) ? p : null;
        }
        
        public void actionPerformed(ActionEvent e) {
            perform(m_project);
        }
        
        public JMenuItem getPopupPresenter() {
            class Presenter extends JMenuItem implements DynamicMenuContent {
                public Presenter() {
                    super(ContextAction.this);
                }
                public JComponent[] getMenuPresenters() {
                    if (m_project != null) {
                        Mnemonics.setLocalizedText(this, labelFor(m_project));
                        return new JComponent[] {this};
                    } else {
                        return new JComponent[0];
                    }
                }
                public JComponent[] synchMenuPresenters(JComponent[] items) {
                    return getMenuPresenters();
                }
            }
            return new Presenter();
        }
    }
}