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
package org.openoffice.extensions.projecttemplates.addon;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openoffice.extensions.config.ConfigurationValidator;
import org.openoffice.extensions.projecttemplates.actions.panel.DescriptionXmlHandler;
import org.openoffice.extensions.util.ProjectCreator;

public class AddOnWizardIterator implements WizardDescriptor.InstantiatingIterator {
    
    public static final int ALL_PANELS = 0;
    public static final int MENU_PANEL = 2;
    public static final int TOOLBAR_PANEL = 3;
    
    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor.Panel[] panelCache;
    private WizardDescriptor wiz;
    private HashSet<ChangeListener>listeners;
    
    public AddOnWizardIterator() {
        listeners = new HashSet<ChangeListener>();
    }
    
    public static AddOnWizardIterator createIterator() {
        return new AddOnWizardIterator();
    }
    
    private void createPanelCache() {
        panelCache =  new WizardDescriptor.Panel[] {
            new AddOnWizardPanel1Project(this),
            new AddOnWizardPanel2Description(),
            new AddOnWizardPanel3Menubar(),
            new AddOnWizardPanel4Toolbar(),
        };
        String[] steps = createSteps();
        int stepIndex = 0;
        for (int i = 0; i < panelCache.length; i++) {
            Component c = panelCache[i].getComponent();
            JComponent jc = (JComponent) c;
            // Step #.
            jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(stepIndex)); // NOI18N
            if (stepIndex < panelCache.length - 2) {
                stepIndex++;
            }
            // Step name (actually the whole list for reference).
            jc.putClientProperty("WizardPanel_contentData", steps); // NOI18N
        }
    }
    
    private void createPanels(int panelID) {
        switch(panelID) {
            case MENU_PANEL:
                panels =  new WizardDescriptor.Panel[] {
                    panelCache[0], panelCache[1], panelCache[2]
                };
                break;
            case TOOLBAR_PANEL:
                panels =  new WizardDescriptor.Panel[] {
                    panelCache[0], panelCache[1], panelCache[3],
                };
                break;
            default:
                panels =  new WizardDescriptor.Panel[] {
                    panelCache[0], panelCache[1], panelCache[2], panelCache[3]
                };
        }
    }
    
    private String[] createSteps() {
        return new String[] {
                    NbBundle.getMessage(AddOnWizardIterator.class, "LBL_CreateProjectStep"),
                    NbBundle.getMessage(AddOnWizardIterator.class, "LBL_UIProjectStep"),
                    NbBundle.getMessage(AddOnWizardIterator.class, "LBL_UIProject"),
                };
    }
    
    void updatePanelSteps(int panelID) {
        // allow editing of the panel steps only in first panel!
        if (index == 0) {
            createPanels(panelID);
        }
    }
    
    public Set<FileObject> instantiate() throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<FileObject>();
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir")); // NOI18N
        dirF.mkdirs();
        
        FileObject dir = FileUtil.toFileObject(dirF);
        ProjectCreator creator = new ProjectCreator(dirF, wiz);
        creator.createAddon();
        // create initial description.xml
        DescriptionXmlHandler dXml = new DescriptionXmlHandler(dir);
        dXml.writeDescriptionXml();
        
        // Always open top dir as a project:
        resultSet.add(dir);
        // Look for nested projects to open as well:
        Enumeration e = dir.getFolders(true);
        while (e.hasMoreElements()) {
            FileObject subfolder = (FileObject) e.nextElement();
            if (ProjectManager.getDefault().isProject(subfolder)) {
                resultSet.add(subfolder);
            }
        }
        
        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }
        
        return resultSet;
    }
    
    public void initialize(WizardDescriptor wiz) {
        ConfigurationValidator.validateSettings();
        this.wiz = wiz;
        index = 0;
        createPanelCache();
        createPanels(ALL_PANELS);
    }
    
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir",null); // NOI18N
        this.wiz.putProperty("name",null); // NOI18N
        this.wiz = null;
        panels = null;
    }
    
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[] {new Integer(index + 1), new Integer(panels.length)}); // NOI18N
    }
    
    public boolean hasNext() {
        return index < panels.length - 1;
    }
    
    public boolean hasPrevious() {
        return index > 0;
    }
    
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }
    
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }
    
    public WizardDescriptor.Panel current() {
        return panels[index];
    }
    
    public final void addChangeListener(ChangeListener l) {
        listeners.add(l);
    }
    public final void removeChangeListener(ChangeListener l) {
        listeners.remove(l);
    }
    
    private static void unZipFile(InputStream source, FileObject projectRoot) throws IOException {
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    FileUtil.createFolder(projectRoot, entry.getName());
                } else {
                    FileObject fo = FileUtil.createData(projectRoot, entry.getName());
                    FileLock lock = fo.lock();
                    try {
                        OutputStream out = fo.getOutputStream(lock);
                        try {
                            FileUtil.copy(str, out);
                        } finally {
                            out.close();
                        }
                    } finally {
                        lock.releaseLock();
                    }
                }
            }
        } finally {
            source.close();
        }
    }
    
}
