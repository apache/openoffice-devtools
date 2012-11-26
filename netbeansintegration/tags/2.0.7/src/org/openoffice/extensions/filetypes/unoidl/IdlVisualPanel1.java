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

package org.openoffice.extensions.filetypes.unoidl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.Document;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;
import org.openoffice.extensions.util.IdlFileHelper;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.typebrowser.logic.TypeNode;

public final class IdlVisualPanel1 extends JPanel implements KeyListener, DocumentListener, ActionListener {
    
    public static final String PROP_NAME = "name"; // NOI18N
    
    private SourceFolderComboBoxModel cbModel;
    private String basePath;
    private String m_sPackageName;
    
    /** Creates new form IdlVisualPanel1 */
    public IdlVisualPanel1() {
        m_sPackageName = "";
        cbModel = new SourceFolderComboBoxModel();
        Image iconImage = ImageUtilities.loadImage("org/openoffice/extensions/projecttemplates/component/icons/module.png");
        SourceFolderListCellRenderer renderer = new SourceFolderListCellRenderer(new ImageIcon(iconImage));
        initComponents();
        implObjectTextField.setText("");
        packageComboBox.setRenderer(renderer);
        packageComboBox.addActionListener(this);
        packageComboBox.getEditor().getEditorComponent().addKeyListener(this);
        dataTypeComboBox.addActionListener(this);
        nameTextField1.getDocument().addDocumentListener(this);
    }
    
    public String getName() {
        return NbBundle.getMessage(IdlWizardIterator.class, "IdlVisualPanelName"); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nameTextField1 = new javax.swing.JTextField();
        packageComboBox = new javax.swing.JComboBox();
        projectTextField = new javax.swing.JTextField();
        locationTextField = new javax.swing.JTextField();
        createdFileTextField = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        dataTypeComboBox = new javax.swing.JComboBox();
        implObjectCheckBox = new javax.swing.JCheckBox();
        implObjectTextField = new javax.swing.JTextField();

        jLabel1.setLabelFor(nameTextField1);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_Name")); // NOI18N

        jLabel2.setLabelFor(projectTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_Project")); // NOI18N

        jLabel3.setLabelFor(locationTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_Location")); // NOI18N

        jLabel4.setLabelFor(packageComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_Package")); // NOI18N

        jLabel5.setLabelFor(createdFileTextField);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_CreatedFile")); // NOI18N

        nameTextField1.setText("NewUnoIdlType");

        packageComboBox.setEditable(true);
        packageComboBox.setModel(cbModel);
        packageComboBox.setActionCommand("packageComboBoxChanged");

        projectTextField.setEditable(false);
        projectTextField.setText("jTextField2");

        locationTextField.setEditable(false);
        locationTextField.setText("Source Packages");

        createdFileTextField.setEditable(false);
        createdFileTextField.setText("jTextField4");

        jLabel6.setLabelFor(dataTypeComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_DataType")); // NOI18N

        dataTypeComboBox.setModel(getDataTypeList());
        dataTypeComboBox.setActionCommand("dataTypeComboBoxChanged");

        org.openide.awt.Mnemonics.setLocalizedText(implObjectCheckBox, NbBundle.getMessage(IdlVisualPanel1.class, "LBL_ImplementationObject")); // NOI18N
        implObjectCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        implObjectCheckBox.setEnabled(false);
        implObjectCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        implObjectCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                implObjectCheckBoxActionPerformed(evt);
            }
        });

        implObjectTextField.setBackground(new java.awt.Color(238, 238, 238));
        implObjectTextField.setEditable(false);
        implObjectTextField.setText("jTextField1");
        implObjectTextField.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jSeparator1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                    .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                    .add(jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                    .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                    .add(jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(layout.createSequentialGroup()
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                .add(2, 2, 2)))
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, implObjectTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, implObjectCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, projectTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                            .add(locationTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, packageComboBox, 0, 293, Short.MAX_VALUE)
                            .add(dataTypeComboBox, 0, 293, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, nameTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, createdFileTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(nameTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(40, 40, 40)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(dataTypeComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(projectTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(locationTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(packageComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(createdFileTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(15, 15, 15)
                .add(implObjectCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(implObjectTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(55, 55, 55))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void implObjectCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_implObjectCheckBoxActionPerformed

        if (implObjectCheckBox.isSelected()) {
            implObjectTextField.setEnabled(true);
//            implObjectTextField.setEditable(true);
            updateCreatedFileTextField();
        }
        else {
            implObjectTextField.setEnabled(false);
//            implObjectTextField.setEditable(false);
        }

    }//GEN-LAST:event_implObjectCheckBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField createdFileTextField;
    private javax.swing.JComboBox dataTypeComboBox;
    private javax.swing.JCheckBox implObjectCheckBox;
    private javax.swing.JTextField implObjectTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JTextField nameTextField1;
    private javax.swing.JComboBox packageComboBox;
    private javax.swing.JTextField projectTextField;
    // End of variables declaration//GEN-END:variables
    
    protected void read(WizardDescriptor desc) {
        // project name 
        Project p = Templates.getProject(desc);
        if (p != null) {
            String displayName = ProjectUtils.getInformation(p).getDisplayName();
            projectTextField.setText(displayName);
            FileObject sourceObject = null;
            try {
                FileObject projDir = p.getProjectDirectory();
                desc.putProperty("projdir", FileUtil.toFile(projDir)); // NOI18N
                sourceObject = FileUtil.createFolder(projDir, "src"); // NOI18N
                cbModel.setItems(getOrderedSubFolders(sourceObject));

            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
            
            // initialize local types if possible
            IdlFileHelper ifh = new IdlFileHelper(sourceObject);
            NbNodeObject[] existingIdlFiles = ifh.getAllIdlFiles();
            TypeNode.setDesignedTypes(existingIdlFiles);

            // update UI
            this.dataTypeComboBox.setSelectedItem(DataType.INTERFACE_TYPE_NAME);
            if (sourceObject != null) {
                try {
                    File baseFile = FileUtil.toFile(sourceObject);
                    this.basePath = baseFile.getCanonicalPath();
                    desc.putProperty("srcdir", baseFile); // NOI18N
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
            updateCreatedFileTextField();
        }
    }
    
    protected void store(WizardDescriptor desc) {
        String name = this.nameTextField1.getText();
        desc.putProperty("Name", name); // NOI18N
        desc.putProperty("mainClassName", name.concat("Impl")); // NOI18N

        // TODO: consolidate different keys for the same values
        desc.putProperty("Package", m_sPackageName); // NOI18N
        desc.putProperty("packageName", m_sPackageName); // NOI18N
        desc.putProperty("UnoPackage", m_sPackageName); // NOI18N

        desc.putProperty("DataType", this.dataTypeComboBox.getSelectedItem().toString()); // NOI18N
        boolean createImplObject = this.implObjectCheckBox.isSelected() && this.implObjectCheckBox.isEnabled();
        desc.putProperty("CreateImplObject", createImplObject); // NOI18N
    }
    
    private ComboBoxModel getDataTypeList() {
        return new DefaultComboBoxModel(new String[]{
            DataType.ENUM_TYPE_NAME,
            DataType.EXCEPTION_TYPE_NAME,
            DataType.INTERFACE_TYPE_NAME,
            DataType.POLY_STRUCT_TYPE_NAME,
            DataType.SERVICE_TYPE_NAME,
            DataType.STRUCT_TYPE_NAME,
        });
    }

    private void updateCreatedFileTextField() {
        String selectedPackage = m_sPackageName.replace('.', File.separatorChar);
        String fileName = nameTextField1.getText(); // NOI18N
        String fullFileName = null;
        if (basePath != null) {
            fullFileName = basePath.concat(File.separator).concat(selectedPackage).concat(File.separator).concat(fileName);
            createdFileTextField.setText(fullFileName.concat(".idl"));
        }
        else {
            fullFileName = "...".concat(File.separator).concat(selectedPackage).concat(File.separator).concat(fileName); // NOI18N
            createdFileTextField.setText(fullFileName.concat(".idl"));
        }
        if (implObjectCheckBox.isEnabled())
            implObjectTextField.setText(fullFileName.concat("Impl.java"));
    }
    
    private void updateTexts(DocumentEvent docEvent) {
        Document doc = docEvent.getDocument();
        
        if (doc == this.nameTextField1.getDocument()) {
            updateCreatedFileTextField();
            firePropertyChange(PROP_NAME, null, this.nameTextField1.getText());
        }
    }
    
    private String[] getOrderedSubFolders(FileObject sourceObject) {
        Vector<String>subFolders = new Vector<String>();
        int length = sourceObject.getPath().length();
        if (sourceObject != null) {
            Enumeration sourceChildren = sourceObject.getFolders(true);
            while (sourceChildren.hasMoreElements()) {
                FileObject nextCandidate = (FileObject)sourceChildren.nextElement();
                String p = nextCandidate.getPath();
                subFolders.add(p.substring(length + 1).replace('/', '.'));
            }
        }
        return subFolders.toArray(new String[subFolders.size()]);
    }

    public void insertUpdate(DocumentEvent documentEvent) {
        updateTexts(documentEvent);
    }

    public void removeUpdate(DocumentEvent documentEvent) {
        updateTexts(documentEvent);
    }

    public void changedUpdate(DocumentEvent documentEvent) {
        updateTexts(documentEvent);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(packageComboBox.getActionCommand())) {
            m_sPackageName = packageComboBox.getSelectedItem().toString();
            updateCreatedFileTextField();
        }
        else if (actionEvent.getActionCommand().equals(dataTypeComboBox.getActionCommand())) {
            boolean bHelper = 
                DataType.SERVICE_TYPE_NAME.equals(dataTypeComboBox.getSelectedItem().toString()) ||
                DataType.INTERFACE_TYPE_NAME.equals(dataTypeComboBox.getSelectedItem().toString());
            implObjectCheckBox.setEnabled(bHelper);
            // enable text field when check box is checked and check box is selected
            if (implObjectCheckBox.isSelected() && bHelper) {
                implObjectTextField.setEnabled(true);
                updateCreatedFileTextField(); // also update text inside
            }
            else {
                implObjectTextField.setEnabled(false);
            }
        }
    }

    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        String charc = new String(new char[]{c});
        m_sPackageName = packageComboBox.getEditor().getItem().toString();
        if (Pattern.matches("[a-zA-Z_0-9\\.]", charc)) {
            m_sPackageName = m_sPackageName.concat(charc);
        }
        updateCreatedFileTextField();
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    private class SourceFolderComboBoxModel implements ComboBoxModel {
        Vector<ListDataListener> listeners;
        private String[] items;
        private int index;
        Image iconImage;
        public SourceFolderComboBoxModel() {
            listeners = new Vector<ListDataListener>();
            items = new String[0];
        }
        public void setItems(String[] itemArray) {
            if (itemArray != null && itemArray.length > 0) {
                items = itemArray;
                for (Iterator<ListDataListener>it = listeners.iterator(); it.hasNext();) {
                    ListDataListener l = it.next();
                    l.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 
                            0, items.length - 1));
                }
            }
        }
        public void setSelectedItem(Object object) {
            String compare = null;
            if (object instanceof JLabel) {
                compare = ((JLabel)object).getText();
            }
            else {
                compare = object.toString();
            }
            for (int i = 0; i < items.length; i++) {
                if (items[i].equals(object)) {
                    index = i;
                }
            }
        }
        public Object getSelectedItem() {
            if (index >= 0 && index < items.length) {
                return items[index];
            }
            return null;
        }
        public int getSize() {
            return items.length;
        }
        public Object getElementAt(int i) {
            if (i >= 0 && i < items.length) {
                return items[i];
            }
            return null;
        }
        public void addListDataListener(ListDataListener listDataListener) {
            listeners.add(listDataListener);
        }
        public void removeListDataListener(ListDataListener listDataListener) {
            listeners.remove(listDataListener);
        }
    }
    
    private class SourceFolderListCellRenderer extends JLabel implements ListCellRenderer {
        public SourceFolderListCellRenderer(ImageIcon icon) {
            super(icon, SwingConstants.LEFT);
            setOpaque(true);
        }
        public Component getListCellRendererComponent(JList list, Object value, int index, 
            boolean isSelected, boolean cellHasFocus) {
            Object e = cbModel.getElementAt(index);
            if (e != null) {
                String name = e.toString();
                this.setText(name);
                if (isSelected) {
                    setBackground(new Color(184, 207, 229));
                }
                else {
                    setBackground(new Color(255, 255, 255));
                }
            }
            return this;
        }
    }
}

