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

package org.openoffice.extensions.config;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.addon.AddOnWizardIterator;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author  sg128468
 */
public class LogOutputSettingsDialog extends javax.swing.JPanel 
        implements DocumentListener, ActionListener {
    
    /**
     * Creates new form LogOutputSettingsDialog
     */
    public LogOutputSettingsDialog() {
        initComponents();
        initializeFields();
        directoryTextField.getDocument().addDocumentListener(this);
        fileNameTextField.getDocument().addDocumentListener(this);
        enableLoggingCheckBox.addActionListener(this);
        valid();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enableLoggingCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        logLevelComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        clearButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        logLevelFileName = new javax.swing.JLabel();
        directoryTextField = new javax.swing.JTextField();
        fileNameTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorTextPane = new javax.swing.JTextPane();

        enableLoggingCheckBox.setBackground(new java.awt.Color(255, 255, 255));
        enableLoggingCheckBox.setText(NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_Log_Checkbox")); // NOI18N
        enableLoggingCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        enableLoggingCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        enableLoggingCheckBox.setOpaque(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(184, 207, 229)), NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_LogProperties"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11), new java.awt.Color(0, 0, 0))); // NOI18N

        jLabel4.setLabelFor(fileNameTextField);
        jLabel4.setText(NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_FileName")); // NOI18N

        jLabel3.setLabelFor(directoryTextField);
        jLabel3.setText(NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_Store_Directory")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_BUTTON_BrowseSDK")); // NOI18N
        browseButton.setActionCommand("BROWSE");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        logLevelComboBox.setModel(new javax.swing.DefaultComboBoxModel(LogWriter.getLoglevels()));

        jLabel1.setLabelFor(logLevelComboBox);
        jLabel1.setText(NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_LogLevel_ComboBox")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(clearButton, NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_ButtonClear")); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        jLabel2.setLabelFor(logLevelFileName);
        jLabel2.setText(NbBundle.getMessage(LogOutputSettingsDialog.class, "LBL_GeneratedFileName")); // NOI18N

        logLevelFileName.setText("jLabel6");
        logLevelFileName.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        logLevelFileName.setOpaque(true);

        directoryTextField.setText("jTextField1");

        fileNameTextField.setText("jTextField2");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fileNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, directoryTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(browseButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(clearButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(logLevelFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                    .add(logLevelComboBox, 0, 321, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(logLevelComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(browseButton)
                    .add(directoryTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(clearButton)
                    .add(fileNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(logLevelFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jScrollPane1.setBorder(null);

        errorTextPane.setBackground(new java.awt.Color(238, 238, 238));
        errorTextPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        errorTextPane.setEditable(false);
        errorTextPane.setForeground(new java.awt.Color(255, 0, 0));
        jScrollPane1.setViewportView(errorTextPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, enableLoggingCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(enableLoggingCheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(19, 19, 19)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

        LogWriter.getLogWriter().clearLogFile();
        
    }//GEN-LAST:event_clearButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed

        String command = evt.getActionCommand();
        if ("BROWSE".equals(command)) { // NOI18N
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
            FileUtil.preventFileChooserSymlinkTraversal(chooser, ConfigurationSettings.getDefaultFileChooserStartingDir());
            chooser.setDialogTitle(NbBundle.getMessage(AddOnWizardIterator.class, "LBL_FileChooserTitle"));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            String path = this.directoryTextField.getText();
            if (path.length() > 0) {
                File f = new File(path);
                if (f.exists()) {
                    chooser.setSelectedFile(f);
                }
            }
            if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
                File projectDir = chooser.getSelectedFile();
                ConfigurationSettings.storeDefaultFileChooserStartingDir(projectDir);
                directoryTextField.setText(FileUtil.normalizeFile(projectDir).getAbsolutePath());
            }
        }

    }//GEN-LAST:event_browseButtonActionPerformed
    
    public boolean valid() {
        if (enableLoggingCheckBox.isSelected()) {
            String dir = directoryTextField.getText();
            File logDir = new File(dir);
            boolean valid = logDir.exists();
            if (!valid) {  // directory wrong
                errorTextPane.setText(
                    NbBundle.getMessage(LogOutputSettingsDialog.class, 
                    "ERROR_WrongDirectory"));
                return false;
            }

            String log = logLevelFileName.getText();
            File logFile = new File(log);
            valid = (logFile.canWrite() || !logFile.exists()) && logDir.canWrite();
            if (!valid) {  // log file cannot be created
                errorTextPane.setText(
                    NbBundle.getMessage(LogOutputSettingsDialog.class, 
                    "ERROR_NoLogCreated"));
                return false;
            }
        }
        errorTextPane.setText("");
        return true;
    }

    public String getLogLevel() {
        // this will work, because static final LogWriter.getLoglevels() is used for the combo box model, too
        return LogWriter.getLoglevels()[logLevelComboBox.getSelectedIndex()];
    }

    public String getLogFile() {
        return logLevelFileName.getText();
    }
    
    protected void initializeFields() {
        LogWriter logWriter = LogWriter.getLogWriter();
        int levelIndex = -1;//logWriter.getIndexFromLocalizedLogLevel(logWriter.getLogLevelLocalized());
        String logFile = logWriter.getLogFile();
        String logPath = null;
        String logFileName = null;
        int index = logFile.lastIndexOf(File.separatorChar);
        if (index != -1) {
            logPath = logFile.substring(0, index);
            logFileName = logFile.substring(index + 1);
        }
        else {
            logPath = ".";
            logFileName = logFile;
        }
        directoryTextField.setText(logPath);
        fileNameTextField.setText(logFileName);
        logLevelFileName.setText(logFile);
        if (levelIndex != -1) {
            logLevelComboBox.setSelectedIndex(levelIndex);
        }
        if (logWriter.isActive()) {
            enableLoggingCheckBox.setSelected(false);
            directoryTextField.setEnabled(false);
            logLevelComboBox.setEnabled(false);
            logLevelFileName.setEnabled(false);
            fileNameTextField.setEnabled(false);
        }
        else {
            enableLoggingCheckBox.setSelected(true);
        }
    }

    public void insertUpdate(DocumentEvent e) {
        updateTexts();
    }

    public void removeUpdate(DocumentEvent e) {
        updateTexts();
    }

    public void changedUpdate(DocumentEvent e) {
        updateTexts();
    }

    public void updateTexts() {
        String logFile = directoryTextField.getText().concat(
            File.separator).concat(fileNameTextField.getText());
        logLevelFileName.setText(logFile);
        valid();
    }

    public void actionPerformed(ActionEvent e) {
        if (enableLoggingCheckBox.isSelected()) {
            directoryTextField.setEnabled(true);
            logLevelComboBox.setEnabled(true);
            logLevelFileName.setEnabled(true);
            fileNameTextField.setEnabled(true);
        }
        else {
            directoryTextField.setEnabled(false);
            logLevelComboBox.setEnabled(false);
            logLevelFileName.setEnabled(false);
            fileNameTextField.setEnabled(false);
        }
        valid();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField directoryTextField;
    private javax.swing.JCheckBox enableLoggingCheckBox;
    private javax.swing.JTextPane errorTextPane;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox logLevelComboBox;
    private javax.swing.JLabel logLevelFileName;
    // End of variables declaration//GEN-END:variables
    
}
