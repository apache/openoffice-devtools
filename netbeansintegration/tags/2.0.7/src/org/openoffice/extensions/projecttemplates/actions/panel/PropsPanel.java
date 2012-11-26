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
package org.openoffice.extensions.projecttemplates.actions.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.Document;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ProjectTypeHelper;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;

/**
 *
 * @author  sg128468
 */
public class PropsPanel extends javax.swing.JPanel implements ActionListener, DocumentListener, HelpCtx.Provider {

    private EditAndOpenPanel m_descriptionPanel;
    private EditAndOpenPanel m_defaultIconPanel;
    private EditAndOpenPanel m_highDefinitionIconPanel;
    private EditAndOpenPanel m_licensePanel;
    private SimpleEditPanel m_displayNamePanel;
    private String m_CurrentLocale;
    private FileObject m_projDir;
    private DataHandler m_Handler;
    
    private static final String DEF_ICON = "ICON";
    private static final String HIGH_DEF_ICON = "HIGH_DEF_ICON";
    private static final String LICENSE = "LICENSE";
    private static final String DESCRIPTION = "DESCRIPTION";
    private static final String USER = "USER";
    private static final String ADMIN = "ADMIN";

    /** Creates new form PropsPanel */
    public PropsPanel(DataHandler handler) {
        m_Handler = handler;
        m_projDir = handler.getProjectDir();
        initComponents();
        jErrorLabel.setForeground(Color.RED);
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(jRadioButtonUser);
        buttonGroup.add(jRadioButtonAdmin);
        initPanels();
        jComboBoxCurrentLocale.setRenderer(new MyListCellRenderer());
        jComboBoxDefaultLocale.setRenderer(new MyListCellRenderer());
        jRadioButtonUser.setActionCommand(USER);
        jRadioButtonAdmin.setActionCommand(ADMIN);
        jRadioButtonAdmin.addActionListener(this);
        jRadioButtonUser.addActionListener(this);
        jPanelPublisherLink.getDocument().addDocumentListener(this);
        jPanelPublisherName.getDocument().addDocumentListener(this);
        m_displayNamePanel.getDocument().addDocumentListener(this);
        m_descriptionPanel.getDocument().addDocumentListener(this);
        m_defaultIconPanel.getDocument().addDocumentListener(this);
        m_highDefinitionIconPanel.getDocument().addDocumentListener(this);
        m_licensePanel.getDocument().addDocumentListener(this);
    }

    private ComboBoxModel createComboBoxModel() {
        return new DefaultComboBoxModel(LanguageDefinition.getLanguages());
    }

    private void initPanels() {
        // create empty text field to set the correct border around the 
        // components - border is platfrom dependent
        JTextField borderTextField = new JTextField();
        Border b = borderTextField.getBorder();
        
        m_descriptionPanel.setBorder(b);
        m_defaultIconPanel.setBorder(b);
        m_highDefinitionIconPanel.setBorder(b);
        m_licensePanel.setBorder(b);
        m_displayNamePanel.setBorder(b);
        jPanelPublisherLink.setBorder(b);
        jPanelPublisherName.setBorder(b);

        m_CurrentLocale = m_Handler.getDefaultShortLocale();
        if (m_CurrentLocale == null) {
            m_CurrentLocale = LanguageDefinition.getLanguageNameForId(LanguageDefinition.LANGUAGE_ID_en);
            m_Handler.setDefaultShortLocale(LanguageDefinition.getLanguageShortNameForId(LanguageDefinition.LANGUAGE_ID_en));
        } else { // make sure name is long...
            m_CurrentLocale = LanguageDefinition.getLanguageNameForShortName(m_CurrentLocale);
        }
        jComboBoxDefaultLocale.setSelectedItem(m_CurrentLocale);
        jComboBoxCurrentLocale.setSelectedItem(m_CurrentLocale);
        loadData(""); // NOI18N
        loadIconAndAcceptBy();
    }

    private JPanel createDescriptionPanel() {
        m_descriptionPanel = new EditAndOpenPanel(); // NOI18N
        m_descriptionPanel.initialize(DESCRIPTION, this);
        return m_descriptionPanel;
    }

    private JPanel createDefaultIconPanel() {
        m_defaultIconPanel = new EditAndOpenPanel(); // NOI18N
        m_defaultIconPanel.initialize(DEF_ICON, this);
        return m_defaultIconPanel;
    }

    private JPanel createHighDefinitionIconPanel() {
        m_highDefinitionIconPanel = new EditAndOpenPanel(); // NOI18N
        m_highDefinitionIconPanel.initialize(HIGH_DEF_ICON, this);
        return m_highDefinitionIconPanel;
    }

    private JPanel createLicensePanel() {
        m_licensePanel = new EditAndOpenPanel(); // NOI18N
        m_licensePanel.initialize(LICENSE, this);
        return m_licensePanel;
    }
	
    private JPanel createDisplayNamePanel() {
        m_displayNamePanel = new SimpleEditPanel();
        return m_displayNamePanel;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelCurrentLocale = new javax.swing.JLabel();
        jComboBoxCurrentLocale = new javax.swing.JComboBox();
        jComboBoxDefaultLocale = new javax.swing.JComboBox();
        jLabelDefaultLocale = new javax.swing.JLabel();
        jLabelDisplayName = new javax.swing.JLabel();
        jLabelIcon = new javax.swing.JLabel();
        jLabelDescription = new javax.swing.JLabel();
        jLabelLicense = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelPublisherName = new javax.swing.JLabel();
        jPanelDisplayName = createDisplayNamePanel();
        jButtonBrowseLicense = new javax.swing.JButton();
        jButtonBrowseIcon = new javax.swing.JButton();
        jLabelPublisherLink = new javax.swing.JLabel();
        jButtonBrowseDescription = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jRadioButtonUser = new javax.swing.JRadioButton();
        jRadioButtonAdmin = new javax.swing.JRadioButton();
        jPanel1 = createDescriptionPanel();
        jPanel2 = createLicensePanel();
        jPanel3 = createDefaultIconPanel();
        jPanel4 = createHighDefinitionIconPanel();
        jButtonBrowseHiDefIcon = new javax.swing.JButton();
        jLabelIcon1 = new javax.swing.JLabel();
        jErrorLabel = new javax.swing.JLabel();
        jPanelPublisherName = new org.openoffice.extensions.projecttemplates.actions.panel.SimpleEditPanel();
        jPanelPublisherLink = new org.openoffice.extensions.projecttemplates.actions.panel.SimpleEditPanel();

        jLabelCurrentLocale.setLabelFor(jComboBoxCurrentLocale);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelCurrentLocale, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelCurrentLocale.text")); // NOI18N

        jComboBoxCurrentLocale.setModel(createComboBoxModel()       );
        jComboBoxCurrentLocale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxCurrentLocaleActionPerformed(evt);
            }
        });

        jComboBoxDefaultLocale.setModel(createComboBoxModel());
        jComboBoxDefaultLocale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxDefaultLocaleActionPerformed(evt);
            }
        });

        jLabelDefaultLocale.setLabelFor(jComboBoxDefaultLocale);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelDefaultLocale, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelDefaultLocale.text")); // NOI18N

        jLabelDisplayName.setLabelFor(jPanelDisplayName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelDisplayName, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelDisplayName.text")); // NOI18N

        jLabelIcon.setLabelFor(jPanel3);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelIcon, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelIcon.text")); // NOI18N

        jLabelDescription.setLabelFor(jPanel1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelDescription, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelDescription.text")); // NOI18N

        jLabelLicense.setLabelFor(jPanel2);
        jLabelLicense.setText(org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelLicense.text")); // NOI18N

        jLabelPublisherName.setLabelFor(jPanelPublisherName);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPublisherName, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelPublisherName.text")); // NOI18N

        jPanelDisplayName.setBackground(new java.awt.Color(255, 255, 255));

        org.openide.awt.Mnemonics.setLocalizedText(jButtonBrowseLicense, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.Browse.text2")); // NOI18N
        jButtonBrowseLicense.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseLicenseActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jButtonBrowseIcon, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.Browse.text3")); // NOI18N
        jButtonBrowseIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseIconActionPerformed(evt);
            }
        });

        jLabelPublisherLink.setLabelFor(jPanelPublisherLink);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelPublisherLink, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelPublisherLink.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButtonBrowseDescription, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.Browse.text")); // NOI18N
        jButtonBrowseDescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseDescriptionActionPerformed(evt);
            }
        });

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonUser, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jRadioButtonUser.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jRadioButtonAdmin, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jRadioButtonAdmin.text")); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        org.openide.awt.Mnemonics.setLocalizedText(jButtonBrowseHiDefIcon, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.Browse.text4")); // NOI18N
        jButtonBrowseHiDefIcon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowseHiDefIconActionPerformed(evt);
            }
        });

        jLabelIcon1.setLabelFor(jPanel4);
        org.openide.awt.Mnemonics.setLocalizedText(jLabelIcon1, org.openide.util.NbBundle.getMessage(PropsPanel.class, "PropsPanel.jLabelIcon1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelIcon1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabelIcon, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabelDescription, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabelLicense, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabelPublisherLink, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabelPublisherName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabelCurrentLocale, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(jLabelDisplayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(jRadioButtonUser)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(jComboBoxCurrentLocale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 90, Short.MAX_VALUE)
                        .add(jLabelDefaultLocale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jComboBoxDefaultLocale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(jRadioButtonAdmin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(jPanelDisplayName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(jPanelPublisherName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .add(jPanelPublisherLink, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonBrowseLicense)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonBrowseDescription)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonBrowseHiDefIcon)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonBrowseIcon)))))
            .add(jErrorLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBoxDefaultLocale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jComboBoxCurrentLocale, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelCurrentLocale)
                    .add(jLabelDefaultLocale))
                .add(14, 14, 14)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanelDisplayName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelDisplayName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButtonBrowseDescription)
                        .add(jLabelDescription))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabelPublisherName)
                    .add(jPanelPublisherName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabelPublisherLink)
                    .add(jPanelPublisherLink, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButtonBrowseLicense)
                        .add(jLabelLicense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jRadioButtonUser)
                    .add(jRadioButtonAdmin))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabelIcon, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jButtonBrowseIcon))
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jLabelIcon1)
                        .add(jButtonBrowseHiDefIcon))
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 49, Short.MAX_VALUE)
                .add(jErrorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Open file browser for certain file types
     * @return
     */
    private File openBrowseDialog(FileFilter filter) {
        File selectedFile = null;
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
        FileUtil.preventFileChooserSymlinkTraversal(chooser, ConfigurationSettings.getDefaultFileChooserStartingDir());
        chooser.setDialogTitle(NbBundle.getMessage(PropsPanel.class, "PropsPanel.FileChooserTitle"));
        chooser.setFileFilter(filter);
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(this)) {
            selectedFile = chooser.getSelectedFile();
            ConfigurationSettings.storeDefaultFileChooserStartingDir(selectedFile.getParentFile());
        }
        return selectedFile;
    }

private void jButtonBrowseIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseIconActionPerformed
    File icon = openBrowseDialog(new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) { // always accept dirs
                return true;
            }
            else {
                String name = f.getName();
                int index = name.length() - 4;
                if (index > 0) {
                    String ext = name.substring(index);
                    if (ext.equalsIgnoreCase(".ico") ||
                            ext.equalsIgnoreCase(".bmp") ||
                            ext.equalsIgnoreCase(".png") ||
                            ext.equalsIgnoreCase(".jpg") ||
                            ext.equalsIgnoreCase(".jpeg") ||
                            ext.equalsIgnoreCase(".gif")) { // NOI18N
                        return true;
                    }
                }
            }
            return false;
        }
        @Override
        public String getDescription() {
            return "*.ico *.bmp *.png *.jpeg *.gif"; // NOI18N
        }
    });
    if (icon != null) {
//        m_Handler.setIconFile(icon);
        m_defaultIconPanel.setText(icon.getPath());
    }

}//GEN-LAST:event_jButtonBrowseIconActionPerformed

private void jButtonBrowseDescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseDescriptionActionPerformed
// TODO add your handling code here:
    File description = openBrowseDialog(new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) { // always accept dirs
                return true;
            }
            else {
                String name = f.getName();
                int index = name.length() - 4;
                if (index > 0) {
                    String ext = name.substring(index);
                    if (ext.equalsIgnoreCase(".txt")) { // NOI18N
                        return true;
                    }
                }
            }
            return false;
        }
        @Override
        public String getDescription() {
            return "*.txt"; // NOI18N
        }
    });
    if (description != null) {
//        try {
//            GenericDescriptionProperty<String[]> descriptionData = m_Handler.getDescriptionData();
            // write the simple name out: that one will appear in the panel; complete name is stored for later copying
//            descriptionData.setPropertyAndLocale(m_CurrentLocale, new String[]{description.getName(), description.getCanonicalPath()});
            m_descriptionPanel.setText(description.getPath());
//        } catch (IOException ex) {
//            LogWriter.getLogWriter().printStackTrace(ex);
//        }
    }

}//GEN-LAST:event_jButtonBrowseDescriptionActionPerformed

private void jButtonBrowseLicenseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseLicenseActionPerformed
// TODO add your handling code here:
    File license = openBrowseDialog(new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) { // always accept dirs
                return true;
            }
            else {
                String name = f.getName();
                int index = name.length() - 4;
                if (index > 0) {
                    String ext = name.substring(index);
                    if (ext.equalsIgnoreCase(".txt")) { // NOI18N
                        return true;
                    }
                }
            }
            return false;
        }
        @Override
        public String getDescription() {
            return "*.txt"; // NOI18N
        }
    });
    if (license != null) {
//        try {
//            GenericDescriptionProperty<String[]> descriptionData = m_Handler.getDescriptionData();
            // write the simple name out: that one will appear in the panel; complete name is stored for later copying
//            descriptionData.setPropertyAndLocale(m_CurrentLocale, new String[]{license.getName(), license.getCanonicalPath()});
            m_licensePanel.setText(license.getPath());
//        } catch (IOException ex) {
//            LogWriter.getLogWriter().printStackTrace(ex);
//        }
    }

}//GEN-LAST:event_jButtonBrowseLicenseActionPerformed

private void jComboBoxCurrentLocaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxCurrentLocaleActionPerformed
        // keep publisher link as service
    String defaultLocale = m_Handler.getDefaultShortLocale();
    GenericDescriptionProperty<String[]> publisherData = m_Handler.getPublisherData();
    String[] object = publisherData.getPropertyForLocale(defaultLocale);
    String publisherLink = null;
    if (object != null && object.length == 2) {
        publisherLink = object[1];
    }
    else {
        publisherLink = jPanelPublisherLink.getText();
    }
    
    // load all data of new locale
    m_CurrentLocale = jComboBoxCurrentLocale.getSelectedItem().toString();
    loadData(publisherLink);
}//GEN-LAST:event_jComboBoxCurrentLocaleActionPerformed

private void jButtonBrowseHiDefIconActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowseHiDefIconActionPerformed
    
    File icon = openBrowseDialog(new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) { // always accept dirs
                return true;
            }
            else {
                String name = f.getName();
                int index = name.length() - 4;
                if (index > 0) {
                    String ext = name.substring(index);
                    if (ext.equalsIgnoreCase(".ico") ||
                            ext.equalsIgnoreCase(".bmp") ||
                            ext.equalsIgnoreCase(".png") ||
                            ext.equalsIgnoreCase(".jpg") ||
                            ext.equalsIgnoreCase(".jpeg") ||
                            ext.equalsIgnoreCase(".gif")) { // NOI18N
                        return true;
                    }
                }
            }
            return false;
        }
        @Override
        public String getDescription() {
            return "*.ico *.bmp *.png *.jpeg *.gif"; // NOI18N
        }
    });
    if (icon != null) {
//        m_Handler.setIconFile(icon);
        m_highDefinitionIconPanel.setText(icon.getPath());
    }
    
}//GEN-LAST:event_jButtonBrowseHiDefIconActionPerformed

private void jComboBoxDefaultLocaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxDefaultLocaleActionPerformed
    
    // store new default locale
    String longLocale = jComboBoxDefaultLocale.getSelectedItem().toString();
    String shortLocale = LanguageDefinition.getLanguageShortNameForName(longLocale);
    if (shortLocale != null) {
        m_Handler.setDefaultShortLocale(shortLocale);
    }
    
}//GEN-LAST:event_jComboBoxDefaultLocaleActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBrowseDescription;
    private javax.swing.JButton jButtonBrowseHiDefIcon;
    private javax.swing.JButton jButtonBrowseIcon;
    private javax.swing.JButton jButtonBrowseLicense;
    private javax.swing.JComboBox jComboBoxCurrentLocale;
    private javax.swing.JComboBox jComboBoxDefaultLocale;
    private javax.swing.JLabel jErrorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelCurrentLocale;
    private javax.swing.JLabel jLabelDefaultLocale;
    private javax.swing.JLabel jLabelDescription;
    private javax.swing.JLabel jLabelDisplayName;
    private javax.swing.JLabel jLabelIcon;
    private javax.swing.JLabel jLabelIcon1;
    private javax.swing.JLabel jLabelLicense;
    private javax.swing.JLabel jLabelPublisherLink;
    private javax.swing.JLabel jLabelPublisherName;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelDisplayName;
    private org.openoffice.extensions.projecttemplates.actions.panel.SimpleEditPanel jPanelPublisherLink;
    private org.openoffice.extensions.projecttemplates.actions.panel.SimpleEditPanel jPanelPublisherName;
    private javax.swing.JRadioButton jRadioButtonAdmin;
    private javax.swing.JRadioButton jRadioButtonUser;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
    
    /**
     * load data for a locale and display it
     */
    private void loadData(String oldPublisherLink) {
        GenericDescriptionProperty<String> displayName = m_Handler.getDisplayData();
        if (displayName != null && displayName.getSize() > 0) {
            String prop = displayName.getPropertyForLocale(m_CurrentLocale);
            if (prop != null) {
                m_displayNamePanel.setText(prop);
            } else {
                m_displayNamePanel.setText(""); // NOI18N
            }
        }

        GenericDescriptionProperty<String> description = m_Handler.getDescriptionData();
        if (description != null && description.getSize() > 0) {
            String prop = description.getPropertyForLocale(m_CurrentLocale);
            if (prop != null) {
                m_descriptionPanel.setText(prop);
            } else {
                m_descriptionPanel.setText(""); // NOI18N
            }
        }

        GenericDescriptionProperty<String[]> publisherData = m_Handler.getPublisherData();
        if (publisherData != null && publisherData.getSize() > 0) {
            String[] props = publisherData.getPropertyForLocale(m_CurrentLocale);
            if (props != null) {
                jPanelPublisherName.setText(props[0]);
                // select if the link already exists or the existing one is kept
                if (props[1].length() == 0) { // keep the old one
                    jPanelPublisherLink.setText(oldPublisherLink);
                }
                else {
                    jPanelPublisherLink.setText(props[1]);
                }
            } else {
                jPanelPublisherName.setText(""); // NOI18N
                // no further editing necessary: either the old link exists or
                // it's empty anyway
                jPanelPublisherLink.setText(oldPublisherLink);
            }
        }

        GenericDescriptionProperty<String> license = m_Handler.getLicenseFiles();
        if (license != null && license.getSize() > 0) {
            String props = license.getPropertyForLocale(m_CurrentLocale);
            if (props != null) {
                m_licensePanel.setText(props);
            } else {
                m_licensePanel.setText("");
            }
        }
        VerifyData verifyer = VerifyData.getVerifyer();
        if (!verifyer.verifyLocalizedData(m_Handler)) {
            String message = verifyer.getErrorMessage();
            jErrorLabel.setText(message);
            jErrorLabel.setIcon(verifyer.getErrorIcon());
        }
        else {
            jErrorLabel.setText("");
            jErrorLabel.setIcon(null);
        }
    }

    /**
     * Icon is not depending on locale, so set it once
     */
    private void loadIconAndAcceptBy() {
        // icon
        String icon = m_Handler.getIconFile();
        if (icon != null) {
            m_defaultIconPanel.setText(icon);
        }
        else {
            m_defaultIconPanel.setText(""); // NOI18N
        }
        String highdefIcon = m_Handler.getHighDefIconFile();
        if (highdefIcon != null) {
            m_highDefinitionIconPanel.setText(highdefIcon);
        }
        else {
            m_highDefinitionIconPanel.setText(""); // NOI18N
        }
        // accept by
        if (m_Handler.getLicenseAcceptByUser()) {
            jRadioButtonUser.setSelected(true);
        }
        else {
            jRadioButtonAdmin.setSelected(true);
        }
        if (!m_Handler.hasLicenseFiles()) {
            jRadioButtonAdmin.setEnabled(false);
            jRadioButtonUser.setEnabled(false);
        }
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx("org.openoffice.extensions.actions.display.properties"); // NOI18N
    }
    
    /**
     * update the text fields when the content of one has changed
     * @param e
     */
    private void storeDataFromUserEntry(DocumentEvent e) {
        Document doc = e.getDocument();
        if (doc.equals(m_displayNamePanel.getDocument())) {
            GenericDescriptionProperty<String> displayName = m_Handler.getDisplayData();
            String text = m_displayNamePanel.getText();
            if (text.length() > 0) {
                displayName.setPropertyAndLocale(m_CurrentLocale, text);
            } else {
                displayName.deletePropertyAndLocale(m_CurrentLocale);
            }
        }
        else if (doc.equals(jPanelPublisherLink.getDocument())) {
            GenericDescriptionProperty<String[]> publisherData = m_Handler.getPublisherData();
            String link = jPanelPublisherLink.getText();
            String[] prop = publisherData.getPropertyForLocale(m_CurrentLocale);
            if (link.length() > 0) {
                if (prop == null) {
                    prop = new String[2];
                }
                prop[1] = link;
                publisherData.setPropertyAndLocale(m_CurrentLocale, prop);
            } else {
                if (prop == null || prop[0] == null || prop[0].length() == 0) {
                    publisherData.deletePropertyAndLocale(m_CurrentLocale);
                }
            }
        }
        else if (doc.equals(jPanelPublisherName.getDocument())) {
            GenericDescriptionProperty<String[]> publisherData = m_Handler.getPublisherData();
            String text = jPanelPublisherName.getText();
            String[] prop = publisherData.getPropertyForLocale(m_CurrentLocale);
            if (text.length() > 0) {
                if (prop == null) {
                    prop = new String[2];
                }
                prop[0] = text;
                publisherData.setPropertyAndLocale(m_CurrentLocale, prop);
            } else {
                if (prop == null || prop[1] == null || prop[1].length() == 0) {
                    publisherData.deletePropertyAndLocale(m_CurrentLocale);
                }
            }
        }
        else if (doc.equals(m_descriptionPanel.getDocument())) {
            GenericDescriptionProperty<String> description = m_Handler.getDescriptionData();
            String text = m_descriptionPanel.getText();
            if (text.length() > 0) {
                description.setPropertyAndLocale(m_CurrentLocale, text);
            } else {
                description.deletePropertyAndLocale(m_CurrentLocale);
            }
        }
        else if (doc.equals(m_licensePanel.getDocument())){
            GenericDescriptionProperty<String> license = m_Handler.getLicenseFiles();
            String text = m_licensePanel.getText();
            if (text.length() > 0) {
                license.setPropertyAndLocale(m_CurrentLocale, text);
            } else {
                license.deletePropertyAndLocale(m_CurrentLocale);
            }
            if (m_Handler.hasLicenseFiles()) { // first license file entry: enable user/admin buttons
                jRadioButtonAdmin.setEnabled(true);
                jRadioButtonUser.setEnabled(true);
                m_Handler.setLicenseAcceptByUser(jRadioButtonUser.isSelected());
            }
            else {
                jRadioButtonAdmin.setEnabled(false);
                jRadioButtonUser.setEnabled(false);
            }
        }
        else if (doc.equals(m_defaultIconPanel.getDocument())) {
            String file = m_defaultIconPanel.getText();
            File iconFile = new File(file);
            if (!iconFile.canRead()) {
                String imagesDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_projDir, "images.dir"); // NOI18N
                if (imagesDir != null) {
                    FileObject imagesFileObject = m_projDir.getFileObject(imagesDir);
                    if (imagesFileObject != null) {
                        FileObject iconFileObject = imagesFileObject.getFileObject(file);
                        if (iconFileObject != null)
                            iconFile = FileUtil.toFile(iconFileObject);
                    }
                }
            }
            if (iconFile.canRead()) {
                m_Handler.setIconFile(iconFile.getPath());
            }
        }
        else if (doc.equals(m_highDefinitionIconPanel.getDocument())) {
            String file = m_highDefinitionIconPanel.getText();
            File iconFile = new File(file);
            if (!iconFile.canRead()) {
                String imagesDir = (String)ProjectTypeHelper.getObjectFromUnoProperties(m_projDir, "images.dir"); // NOI18N
                if (imagesDir != null) {
                    FileObject imagesFileObject = m_projDir.getFileObject(imagesDir);
                    if (imagesFileObject != null) {
                        FileObject iconFileObject = imagesFileObject.getFileObject(file);
                        if (iconFileObject != null)
                            iconFile = FileUtil.toFile(iconFileObject);
                    }
                }
            }
            if (iconFile.canRead()) {
                m_Handler.setHighDefIconFile(iconFile.getPath());
            }
        }
        else {
            // error
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "No known document on sheet");
        }
        VerifyData verifyer = VerifyData.getVerifyer();
        if (!verifyer.verifyLocalizedData(m_Handler)) {
            String message = verifyer.getErrorMessage();
            jErrorLabel.setText(message);
            jErrorLabel.setIcon(verifyer.getErrorIcon());
        }
        else {
            jErrorLabel.setText("");
            jErrorLabel.setIcon(null);
        }
    }

//    private class MyComboBoxModel extends DefaultComboBoxModel {
//        public MyComboBoxModel(Object[] items) {
//            super(items);
//        }
//        @Override
//        public Object getSelectedItem() {
//            String locale = super.getSelectedItem().toString();
//            Font f = getFont();
//            Font newFont = null;
//            if (GenericDescriptionProperty.hasDataForLocale(locale)) {
//                newFont = new Font(f.getName(), Font.BOLD, f.getSize());
//            } else {
//                newFont = new Font(f.getName(), Font.PLAIN, f.getSize());
//            }
//            setFont(newFont);
//            return locale;
//        }
//    }
    
    /**
     * Own Cell Renderer implementation: locales with entries are bold
     */
    private class MyListCellRenderer extends JLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            String locale = value.toString();
            setText(locale);
            Font f = this.getFont();
            Font newFont = null;
            if (m_Handler.hasDataForLocale(locale)) {
                newFont = new Font(f.getName(), Font.BOLD, f.getSize());
            } else {
                newFont = new Font(f.getName(), Font.PLAIN, f.getSize());
            }
            setFont(newFont);
            return this;
        }
    }

    /**
     * ActionListener method for reacting on clicks:
     * Click "..." button in
     * description, license and icon: display/edit file.
     * Click on 
     * @param e The Action Event
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(DEF_ICON) || command.equals(HIGH_DEF_ICON)) { // NOI18N
            String icon = null;
            if (command.equals(DEF_ICON)) {
                icon = m_Handler.getIconFile();
            }
            else {
                icon = m_Handler.getHighDefIconFile();
            }
            String title = NbBundle.getMessage(PropsPanel.class, "PropsPanel.ShowIcon.Title"); // NOI18N
            File iconFile = m_Handler.getProjectOrSystemPath(icon);
            URL url = null;
            if (iconFile != null) {
                try {
                    url = iconFile.toURI().toURL();
                } catch (MalformedURLException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
            if (url != null && iconFile.canRead()) {
                // these width and height look good with standard icon size
                final int PREFERRED_WIDTH = 200;
                final int PREFERRED_HEIGHT = 80;
                ImageIcon iImage = new ImageIcon(url);
                int width = iImage.getIconWidth();
                int height = iImage.getIconHeight();
                JLabel iconLabel = new JLabel(iImage);
                JPanel jPanel = new JPanel();
                jPanel.add(iconLabel);
                if (width < PREFERRED_WIDTH) {
                    width = PREFERRED_WIDTH;
                }
                if (height < PREFERRED_HEIGHT) {
                    height = PREFERRED_HEIGHT;
                }
                jPanel.setPreferredSize(new Dimension(width, height));
                NotifyDescriptor ddscr = new NotifyDescriptor.Message(jPanel, NotifyDescriptor.PLAIN_MESSAGE);
                ddscr.setTitle(title);
                DialogDisplayer.getDefault().notify(ddscr);                    
            }
            else { // show error dialog: no reaction on button is bad interaction
                String error = NbBundle.getMessage(PropsPanel.class, "PropsPanel.ShowIcon.ErrorMessage"); // NOI18N
                NotifyDescriptor ddscr = new NotifyDescriptor.Message(error);
                ddscr.setTitle(title);
                DialogDisplayer.getDefault().notify(ddscr);                    
            }
        }
        else if (command.equals(DESCRIPTION)) { // NOI18N
            GenericDescriptionProperty<String> descriptionData = m_Handler.getDescriptionData();
            String prop = descriptionData.getPropertyForLocale(m_CurrentLocale);
            String title = NbBundle.getMessage(PropsPanel.class, "PropsPanel.EditDescription.Title"); // NOI18N
            editDescriptionOrLicenseFile(prop, title); // NOI18N
        }
        else if (command.equals(LICENSE)) { // NOI18N
            GenericDescriptionProperty<String> licenseData = m_Handler.getLicenseFiles();
            String prop = licenseData.getPropertyForLocale(m_CurrentLocale);
            String title = NbBundle.getMessage(PropsPanel.class, "PropsPanel.EditLicense.Title"); // NOI18N
            editDescriptionOrLicenseFile(prop, title); // NOI18N
        }
        else if (command.equals(USER)) {
            m_Handler.setLicenseAcceptByUser(true);
        }
        else if (command.equals(ADMIN)) {
            m_Handler.setLicenseAcceptByUser(false);
        }
    }

    /**
     * Show dialog for editing license or dialog files. Since both are text 
     * files, use just one method.
     * @param prop String array containing the license file
     * @param baseProjectPropName name for reading the directory where licenses or descriptions are stored
     * @param title string for the title of the editing panel
     */
    private void editDescriptionOrLicenseFile(String prop, String dialogTitle) {
        final File textFile = m_Handler.getProjectOrSystemPath(prop);
        StringBuffer content = new StringBuffer();
        String filePath = NbBundle.getMessage(PropsPanel.class, "EditTextContentPanel.jFileLabel.text"); // NOI18N
        if (textFile != null && textFile.isFile()) { 
            char[] cbuf = new char[256];
            if (textFile.canRead()) {
                BufferedReader read = null;
                try {
                    read = new BufferedReader(new FileReader(textFile));
                    while (read.ready()) {
                        int bytesRead = read.read(cbuf);
                        content.append(cbuf, 0, bytesRead);
                    }
                } catch (FileNotFoundException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } finally {
                    try {
                        if (read != null) {
                            read.close();
                        }
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
            }
            try {
                filePath = filePath.concat(textFile.getCanonicalPath());
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
                filePath = filePath.concat(textFile.getPath());
            }
        }
        final EditTextContentPanel panel = new EditTextContentPanel(filePath);
        panel.setTextContent(content.toString());
        DialogDescriptor ddscr = new DialogDescriptor(panel, dialogTitle, true, 
                DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null);
        // complicated writing: either (file can write) or (file does not exist and parent can write)
        boolean enableOK = false;
        if (textFile != null && textFile.getParentFile() != null) {
            enableOK = textFile.canWrite() || (!textFile.exists() && textFile.getParentFile().canWrite());
        }
        ddscr.setValid(enableOK);
        ddscr.setButtonListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("OK")) { // NOI18N
                    FileWriter writer = null;
                    try {
                        // NOI18N
                        String text = panel.getTextContent();
                        writer = new FileWriter(textFile);
                        writer.write(text);
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    } finally {
                        try {
                            if (writer != null) {
                                writer.close();
                            }
                        } catch (IOException ex) {
                            LogWriter.getLogWriter().printStackTrace(ex);
                        }
                    }
                }
            }
        });
        Dialog d = DialogDisplayer.getDefault().createDialog(ddscr);
        d.setVisible(true);
    }
    
    /**
     * DocumentListener impelentation
     * @param e The Event
     */
    public void insertUpdate(DocumentEvent e) {
        storeDataFromUserEntry(e);
    }

    /**
     * DocumentListener impelentation
     * @param e The Event
     */
    public void removeUpdate(DocumentEvent e) {
        storeDataFromUserEntry(e);
    }

    /**
     * DocumentListener impelentation
     * @param e The Event
     */
    public void changedUpdate(DocumentEvent e) {
        storeDataFromUserEntry(e);
    }
    
}
