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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.api.project.Project;
import org.openide.awt.DynamicMenuContent;
import org.openide.awt.Mnemonics;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openoffice.extensions.util.ProjectTypeHelper;


/**
 *
 */
public class UnoPropertiesAction extends AbstractAction implements ContextAwareAction {

    private static Process officeProcess;
    
    private ContextAction contextAction;
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
        
        return NbBundle.getMessage(DebugAction.class, "CTL_UnoPropertiesAction");
    }
    
    private void perform(Project p) {
        assert p != null;
//        UnoPropertiesPanel.start(p);
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