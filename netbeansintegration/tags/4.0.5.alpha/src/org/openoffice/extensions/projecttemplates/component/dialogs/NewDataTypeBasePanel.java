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

package org.openoffice.extensions.projecttemplates.component.dialogs;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.component.ComponentPanelVisual2IdlFiles;
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypeNode;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;

/**
 *
 * @author  sg128468
 */
public class NewDataTypeBasePanel extends javax.swing.JPanel 
        implements ActionListener, ValidateDataType {
    
    private static final String ICON_PATH = 
            "/org/openoffice/extensions/projecttemplates/component/icons/error.png"; // NOI18N
    
    private static final String NEW_DATA_TYPE_ENUM_HELP = 
            "org.openoffice.extensions.data.type.enum"; // NOI18N
    private static final String NEW_DATA_TYPE_EXCEPTION_HELP = 
            "org.openoffice.extensions.data.type.exception"; // NOI18N
    private static final String NEW_DATA_TYPE_INTERFACE_HELP = 
            "org.openoffice.extensions.data.type.interface"; // NOI18N
    private static final String NEW_DATA_TYPE_POLYSTRUCT_HELP = 
            "org.openoffice.extensions.data.type.polystruct"; // NOI18N
    private static final String NEW_DATA_TYPE_SERVICE_HELP = 
            "org.openoffice.extensions.data.type.service"; // NOI18N
    private static final String NEW_DATA_TYPE_STRUCT_HELP = 
            "org.openoffice.extensions.data.type.struct"; // NOI18N
    
    private DialogProperties props;
    private DialogDescriptor dialogDescriptor;
    private String oldName;
    private String helpCtxString;
    
    /** Creates new form NewDataTypeBasePanel */
    public NewDataTypeBasePanel(DialogProperties props) {
        this.props = props;
        Node object = (Node)props.getProperty(props.NODE);
        if (object != null) {
            NbNodeObject ob = (NbNodeObject)object.getLookup().lookup(NbNodeObject.class);
            this.oldName = ob.getDisplayName();
        }
        initComponents();
        errorMessageLabel.setText(" "); // NOI18N
    }
    
    public void setEnableError(boolean enable, String message) {
        if (enable) {
            errorMessageLabel.setText(message);
            errorMessageLabel.setIcon(
                new javax.swing.ImageIcon(getClass().getResource(ICON_PATH)));
        }
        else {
            errorMessageLabel.setText(" "); // NOI18N
            errorMessageLabel.setIcon(null);
        }
        dialogDescriptor.setValid(!enable); // set ok button valid/invalid
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        containerPanel = getPluggedInPanel();
        errorMessageLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        dataTypeComboBox = new javax.swing.JComboBox();

        containerPanel.setName("dataTypeList"); // NOI18N

        errorMessageLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        errorMessageLabel.setForeground(new java.awt.Color(255, 0, 0));
        errorMessageLabel.setText("jLabel1");

        jLabel1.setLabelFor(dataTypeComboBox);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, NbBundle.getMessage(NewDataTypeBasePanel.class, "LBL_DataType")); // NOI18N

        dataTypeComboBox.setModel(new DefaultComboBoxModel(getItems()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataTypeComboBox, 0, 373, Short.MAX_VALUE))
                    .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addComponent(errorMessageLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dataTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorMessageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel containerPanel;
    private javax.swing.JComboBox dataTypeComboBox;
    private javax.swing.JLabel errorMessageLabel;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    
    public static synchronized void start(final DialogProperties props) {
        // get the title
        String title = null;
        if (props.getBooleanProperty(props.EDIT)) {
            title = 
                NbBundle.getMessage(NewDataTypeBasePanel.class, "LBL_Title_Edit");
        }
        else {
            title = 
                NbBundle.getMessage(NewDataTypeBasePanel.class, "LBL_Title_Create");
        }
        NewDataTypeBasePanel basePanel = new NewDataTypeBasePanel(props);
        DialogDescriptor ddsc = new DialogDescriptor(basePanel, title);
        basePanel.addDialogDescriptor(ddsc);
        Dialog d = DialogDisplayer.getDefault().createDialog(ddsc);
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "type " + props.getIntProperty(props.DATA_TYPE)); // NOI18N
        d.setVisible(true);
    }
    
    private JPanel getPluggedInPanel() {
        switch(props.getIntProperty(props.DATA_TYPE)) {
            case DataType.EXCEPTION_TYPE:
                helpCtxString = NEW_DATA_TYPE_EXCEPTION_HELP;
                return new ExceptionDataTypePanel(this, props);
            case DataType.POLY_STRUCT_TYPE:
                helpCtxString = NEW_DATA_TYPE_POLYSTRUCT_HELP;
                return new PolyStructDataTypePanel(this, props);
            case DataType.ENUM_TYPE:
                helpCtxString = NEW_DATA_TYPE_ENUM_HELP;
                return new EnumDataTypePanel(this, props);
            case DataType.INTERFACE_TYPE:
                helpCtxString = NEW_DATA_TYPE_INTERFACE_HELP;
                return new InterfaceDataTypePanel(this, props);
            case DataType.SERVICE_TYPE:
                helpCtxString = NEW_DATA_TYPE_SERVICE_HELP;
                return new ServiceDataTypePanel(this, props);
            case DataType.STRUCT_TYPE:
                helpCtxString = NEW_DATA_TYPE_STRUCT_HELP;
                return new StructDataTypePanel(this, props);
            default:
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Unkown type for plugged panel creation."); // NOI18N
        }
        return null;
    }

    private void addDialogDescriptor(DialogDescriptor ddsc) {
        this.dialogDescriptor = ddsc;
        dialogDescriptor.setButtonListener(this);
        // helpCtxString is set in c'tor, so it's available here        
        dialogDescriptor.setHelpCtx(new HelpCtx(helpCtxString));  
    }
    
    private String[] getItems() {
        return (String[])props.getProperty(props.SELECTION);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) { // NOI18N
            ExplorerManager.Provider prov = (ExplorerManager.Provider)containerPanel;
            ComponentTypeNode node = (ComponentTypeNode)prov.getExplorerManager().getRootContext();
            NbNodeObject object = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
            if (oldName == null) oldName = object.getDisplayName();
            ComponentPanelVisual2IdlFiles.getComponentPanelVisual2IdlFiles().addNewDataType(
                    oldName, object.getDisplayName(), object);
        }
    }
}
