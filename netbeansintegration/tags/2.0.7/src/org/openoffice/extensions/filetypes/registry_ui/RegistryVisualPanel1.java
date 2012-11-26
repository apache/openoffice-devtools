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
package org.openoffice.extensions.filetypes.registry_ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openoffice.extensions.util.ProjectTypeHelper;

public final class RegistryVisualPanel1 extends JPanel implements DocumentListener {

    // name for reading file chooser title from Bundle.properties
    private String m_fileType;
    private String m_FileFolder;
    private Project m_proj;

    /** Creates new form RegistryVisualPanel1 */
    public RegistryVisualPanel1(String fileType) {
        m_fileType = fileType; 
        initComponents();
        jFileNameTextField.getDocument().addDocumentListener(this);
        jFolderTextField.getDocument().addDocumentListener(this);
        String defaultFileNamePropName = "DefaultFileName".concat(fileType); // NOI18N
        jFileNameTextField.setText(NbBundle.getMessage(RegistryVisualPanel1.class, defaultFileNamePropName));
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RegistryVisualPanel1.class, "RegistryVisualPanel1.Title".concat(m_fileType)); // NOI18N
    }
   
    public void insertUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts(e);
    }

    private void updateTexts(DocumentEvent e) {
        String dirName = jFolderTextField.getText();
        String fileName = jFileNameTextField.getText().concat(".").concat(m_fileType);
        File f = new File(dirName);
        if (f.exists() && f.canWrite()) { // absolute file already
            m_FileFolder = f.getAbsolutePath();
            jCreatedFileTextField.setText(dirName.concat(File.separator).concat(fileName));
        }
        else {
            dirName = dirName.replaceAll("/", File.separator); // produce a system file
            if (m_proj != null) {
                FileObject projDir = m_proj.getProjectDirectory();
                String projDirName = FileUtil.toFile(projDir).getAbsolutePath();
                m_FileFolder = projDirName.concat(File.separator).concat(dirName);
                jCreatedFileTextField.setText(m_FileFolder.concat(File.separator).concat(fileName));
            }
            else {
                m_FileFolder = "";
                jCreatedFileTextField.setText(fileName);
            }
        }
        
    }
    
    protected void read(WizardDescriptor desc) {
        // project name 
        Project p = Templates.getProject(desc);
        if (p != null) {
            m_proj = p;
            String displayName = ProjectUtils.getInformation(p).getDisplayName();
            jProjectTextField.setText(displayName);
            String registryDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_proj, "registry.dir"); // NOI18N
            if (registryDir == null) registryDir = "registry";
            if (m_fileType.equals(RegistryWizardPanel1.XCS_FILE_TYPE)) {
                jFolderTextField.setText(registryDir.concat("/schema")); // NOI18N
            }
            else {
                jFolderTextField.setText(registryDir.concat("/data")); // NOI18N
            }
        }
    }

    protected void store(WizardDescriptor desc) {
        String fileName = jFileNameTextField.getText().concat(".").concat(m_fileType);
        desc.putProperty("FileName", fileName);
        desc.putProperty("Folder", m_FileFolder);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileNameLabel = new javax.swing.JLabel();
        jProjectLabel = new javax.swing.JLabel();
        jFolderLabel = new javax.swing.JLabel();
        jCreatedFileLabel = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jErrorLabel = new javax.swing.JLabel();
        jBrowseButton = new javax.swing.JButton();
        jFileNameTextField = new javax.swing.JTextField();
        jFolderTextField = new javax.swing.JTextField();
        jProjectTextField = new javax.swing.JTextField();
        jCreatedFileTextField = new javax.swing.JTextField();

        jFileNameLabel.setLabelFor(jFileNameTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jFileNameLabel, org.openide.util.NbBundle.getMessage(RegistryVisualPanel1.class, "RegistryVisualPanel1.jFileNameLabel.text")); // NOI18N

        jProjectLabel.setLabelFor(jProjectTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jProjectLabel, org.openide.util.NbBundle.getMessage(RegistryVisualPanel1.class, "RegistryVisualPanel1.jProjectLabel.text")); // NOI18N

        jFolderLabel.setLabelFor(jFolderTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jFolderLabel, org.openide.util.NbBundle.getMessage(RegistryVisualPanel1.class, "RegistryVisualPanel1.jFolderLabel.text")); // NOI18N

        jCreatedFileLabel.setLabelFor(jCreatedFileTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jCreatedFileLabel, org.openide.util.NbBundle.getMessage(RegistryVisualPanel1.class, "RegistryVisualPanel1.jCreatedFileLabel.text")); // NOI18N

        jErrorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        jErrorLabel.setForeground(new java.awt.Color(255, 0, 0));

        org.openide.awt.Mnemonics.setLocalizedText(jBrowseButton, org.openide.util.NbBundle.getMessage(RegistryVisualPanel1.class, "RegistryVisualPanel1.jBrowseButton.text")); // NOI18N
        jBrowseButton.setActionCommand("BROWSE"); // NOI18N
        jBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseButtonActionPerformed(evt);
            }
        });

        jFileNameTextField.setText("jTextField1"); // NOI18N

        jFolderTextField.setText("jTextField2"); // NOI18N

        jProjectTextField.setEditable(false);
        jProjectTextField.setText("jTextField3"); // NOI18N

        jCreatedFileTextField.setEditable(false);
        jCreatedFileTextField.setText("jTextField4"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jFileNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .add(jProjectLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jFolderLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jCreatedFileLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCreatedFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(jProjectTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(jFileNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .add(jFolderTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jBrowseButton))
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
            .add(jErrorLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jFileNameLabel)
                    .add(jFileNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jProjectLabel)
                    .add(jProjectTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jFolderLabel)
                    .add(jBrowseButton)
                    .add(jFolderTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCreatedFileLabel)
                    .add(jCreatedFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 150, Short.MAX_VALUE)
                .add(jErrorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseButtonActionPerformed

        String command = evt.getActionCommand();
        if ("BROWSE".equals(command)) { // NOI18N
            File startFile = null;
            if (m_proj != null) {
                startFile = FileUtil.toFile(m_proj.getProjectDirectory());
            }
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
            FileUtil.preventFileChooserSymlinkTraversal(chooser, startFile);
            chooser.setDialogTitle(NbBundle.getMessage(RegistryWizardPanel1.class, "FileChooserTitle".concat(m_fileType)));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                String dirName = FileUtil.normalizeFile(projectDir).getAbsolutePath();
                jFolderTextField.setText(dirName);
            }
        }
    
}//GEN-LAST:event_jBrowseButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBrowseButton;
    private javax.swing.JLabel jCreatedFileLabel;
    private javax.swing.JTextField jCreatedFileTextField;
    private javax.swing.JLabel jErrorLabel;
    private javax.swing.JLabel jFileNameLabel;
    private javax.swing.JTextField jFileNameTextField;
    private javax.swing.JLabel jFolderLabel;
    private javax.swing.JTextField jFolderTextField;
    private javax.swing.JLabel jProjectLabel;
    private javax.swing.JTextField jProjectTextField;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}

