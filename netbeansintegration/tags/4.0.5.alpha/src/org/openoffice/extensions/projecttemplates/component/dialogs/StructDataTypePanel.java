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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.MissingResourceException;
import javax.swing.tree.TreeSelectionModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.component.*;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.IdlTypeTreeCreator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Struct;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author  sg128468
 */
public class StructDataTypePanel extends javax.swing.JPanel 
        implements ExplorerManager.Provider, PropertyChangeListener {
    
    private ExplorerManager manager = new ExplorerManager();
    private PropertySheet propSheet;

    private ChangeEventPanel panel;    
    private Node node;

    // all actions that can be done here
    private ComponentActions actions;
    private String oldName; 
    private boolean edit;

    private ValidateDataType dialog;
    
    /**
     * Creates new form InterfaceDataTypeDialog
     */
    public StructDataTypePanel(ValidateDataType dialog, DialogProperties props) {
        initComponents();
        this.edit = props.getBooleanProperty(props.EDIT);
        String pkgName = props.getStringProperty(props.PKG);
        String name = props.getStringProperty(props.NAME);
        
        this.panel = (ChangeEventPanel)props.getProperty(props.PANEL);
        this.dialog = dialog;
        this.actions = new ComponentActions(manager, panel);

        if (edit) {
            node = (Node)props.getProperty(props.NODE);
            NbNodeObject ob = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
            oldName = ob.getDisplayName();
        }
        else {
            if (name == null) name = "Struct"; // NOI18N
            node = (Node)IdlTypeTreeCreator.createInitialStructTree(name, pkgName);
        }
        manager.setRootContext(node);
        
        manager.addPropertyChangeListener(this);
        
        propSheet = (PropertySheet)jPanel1;
        propSheet.setNodes(new Node[]{node});

        BeanTreeView componentView = (BeanTreeView)jScrollPane1;
        componentView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new PropertySheet();
        jScrollPane1 = new BeanTreeView();
        addFunctionjButton = new javax.swing.JButton();
        deletejButton = new javax.swing.JButton();

        jSplitPane1.setDividerLocation(200);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSplitPane1.setRightComponent(jPanel1);

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSplitPane1.setLeftComponent(jScrollPane1);

        org.openide.awt.Mnemonics.setLocalizedText(addFunctionjButton, NbBundle.getMessage(ComponentWizardIterator.class, "LBL_Button_AddType")); // NOI18N
        addFunctionjButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFunctionActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deletejButton, NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ButtonDelete")); // NOI18N
        deletejButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deletejButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addFunctionjButton, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(addFunctionjButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deletejButton))
            .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed

        // no valid necessary: this triggers a propertyChange.
        actions.deleteActions();

    }//GEN-LAST:event_deleteActionPerformed

    private void addFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFunctionActionPerformed

        // this does not trigger a propertyChange.
        actions.addParameterAction();
        // 2do: add this everywhere; problem: custom editor does also not trigger a property change
        valid();

    }//GEN-LAST:event_addFunctionActionPerformed
    
    public ExplorerManager getExplorerManager() {
        return this.manager;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes != null && selectedNodes.length == 1) {
            propSheet.setNodes(selectedNodes);
            NbNodeObject nodeObject = (NbNodeObject)selectedNodes[0].getLookup().lookup(NbNodeObject.class);
            if (nodeObject.getType() == NbNodeObject.PROPERTY_TYPE){
                deletejButton.setEnabled(false);
                addFunctionjButton.setEnabled(false);
            }
            else if (nodeObject.getType() == NbNodeObject.PARAMETER_TYPE) {
                deletejButton.setEnabled(true);
                addFunctionjButton.setEnabled(true);
            }
            else {
                deletejButton.setEnabled(false);
                addFunctionjButton.setEnabled(true);
            }
        }
        valid();
    } 
    
    private void valid() {
        NbNodeObject ob = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
        Struct struct = (Struct)ob;
        try {
            if (struct.getSimpleProperty(struct.PROPERTY_CONTAINER_NAME).length() == 0 ||
                struct.getSimpleProperty(struct.PROPERTY_CONTAINER_PACKAGE).length() == 0) {
                dialog.setEnableError(true, 
                    NbBundle.getMessage(NewDataTypeBasePanel.class, "LBL_Error_Name"));
                return;
            }
            else if (struct.getAllSetObjectNames().length == 0) {
                dialog.setEnableError(true, 
                    NbBundle.getMessage(NewDataTypeBasePanel.class, "LBL_Error_Struct"));
                return;
            }
        } catch (MissingResourceException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        dialog.setEnableError(false, null);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addFunctionjButton;
    private javax.swing.JButton deletejButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

}
