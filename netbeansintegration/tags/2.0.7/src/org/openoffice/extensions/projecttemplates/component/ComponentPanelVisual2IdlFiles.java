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

package org.openoffice.extensions.projecttemplates.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.ActionMap;
import javax.swing.tree.TreeSelectionModel;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.ComponentNodeChildren;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.ComponentRootNode;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.DataTypeNode;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.InterfaceNode;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.ServiceNode;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.TypeNodeChildren;
import org.openoffice.extensions.projecttemplates.component.datamodel.node.TypeRootNode;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypeChildren;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypeNode;
import org.openoffice.extensions.projecttemplates.component.dialogs.DialogProperties;
import org.openoffice.extensions.projecttemplates.component.dialogs.NewDataTypeBasePanel;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;
import org.openoffice.extensions.util.typebrowser.logic.TypeNode;

/**
 *
 * @author  sg128468
 */
public class ComponentPanelVisual2IdlFiles extends javax.swing.JPanel 
            implements ExplorerManager.Provider, PropertyChangeListener {
    
    public static final String PROP_PROJECT_NAME = "Component"; // NOI18N
    
    private ComponentWizardPanel2IdlFiles panel;

    private ExplorerManager manager = new ExplorerManager();
    private Lookup lookup;
    private Hashtable<String,NbNodeObject> designedDataTypes;
//    private Hashtable implementedDataTypes;
    
    private String serviceName;
    private String interfaceName;
    private String pkgName;
    private boolean initComponentsOnce;

    /**
     * Creates new form ComponentPanelVisual2IdlFiles
     */
    public ComponentPanelVisual2IdlFiles(ComponentWizardPanel2IdlFiles panel) {
        this.panel = panel;
        initComponentsOnce = true;
    }
    
    private void initialize() {
        initComponents();
        // late init of components because of own data types; must happen only once though        
//        if (initComponentsOnce) { 
//            initComponents();
//            initComponentsOnce = false;
//        }
//        else {
//            ComponentListPanel listPanel = (ComponentListPanel)jPanel1;
//            listPanel.getExplorerManager().setRootContext(getOwnDataTypesRootNode());
//        }
        
        BeanTreeView componentView = (BeanTreeView)jScrollPane1;
        componentView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
//        componentView.setRootVisible(false);
        
        this.manager.setRootContext(
            ComponentRootNode.createInitialNodeStructure(
                serviceName, interfaceName));
        
        // for keyboard actions enable this: handle actions must be done, too
        ActionMap map = getActionMap();
//        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // NOI18N

//        actions = new AddinActions(manager, panel, this);
        // deliver the actual actions to the node-actions
//        Node rootNode = manager.getRootContext();
//        rootNode.setActions(actions);
        
        ComponentListPanel clPanel = (ComponentListPanel)this.jPanel1;
        clPanel.getExplorerManager().addPropertyChangeListener(this);

        manager.addPropertyChangeListener(this);
        lookup = ExplorerUtils.createLookup(manager, map);
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    public Lookup getLookup() {
        return lookup;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPopupMenu1 = new javax.swing.JPopupMenu();
        addServiceButton = new javax.swing.JButton();
        jScrollPane1 = new BeanTreeView();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new ComponentListPanel(panel, getOwnDataTypesRootNode());
        deleteServiceButton = new javax.swing.JButton();
        defineNewDataTypeButton = new javax.swing.JButton();
        deleteDataTypeButton = new javax.swing.JButton();
        editDataTypeButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(addServiceButton, NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ButtonAddType"));
        addServiceButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentPanelVisual2IdlFiles.class, "Button_AddServiceInterface_Tooltip"));
        addServiceButton.setName("addServiceInterface");
        addServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addServiceButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setLabelFor(jScrollPane1);
        jLabel1.setText(NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ServicesAndInterfaces"));

        jLabel2.setLabelFor(jPanel1);
        jLabel2.setText(NbBundle.getMessage(ComponentWizardIterator.class, "LBL_OwnDataTypes"));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.openide.awt.Mnemonics.setLocalizedText(deleteServiceButton, org.openide.util.NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ButtonDeleteSelected"));
        deleteServiceButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentPanelVisual2IdlFiles.class, "Button_DeleteServiceInterface_Tooltip"));
        deleteServiceButton.setEnabled(false);
        deleteServiceButton.setName("deleteSelected");
        deleteServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteServiceButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(defineNewDataTypeButton, NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ButtonDefineType"));
        defineNewDataTypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentPanelVisual2IdlFiles.class, "Button_DefineDataType_Tooltip"));
        defineNewDataTypeButton.setActionCommand("Define New Data Type...");
        defineNewDataTypeButton.setName("defineDataType");
        defineNewDataTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defineNewDataTypeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(deleteDataTypeButton, NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ButtonDeleteSelected2"));
        deleteDataTypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentPanelVisual2IdlFiles.class, "Button_DeletDataType_Tooltip"));
        deleteDataTypeButton.setEnabled(false);
        deleteDataTypeButton.setName("deleteSelected");
        deleteDataTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDataTypeActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(editDataTypeButton, NbBundle.getMessage(ComponentWizardIterator.class, "LBL_ButtonEdit"));
        editDataTypeButton.setToolTipText(org.openide.util.NbBundle.getMessage(ComponentPanelVisual2IdlFiles.class, "Button_EditDataType_Tooltip"));
        editDataTypeButton.setEnabled(false);
        editDataTypeButton.setName("edit");
        editDataTypeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDataTypeActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addServiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(defineNewDataTypeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(editDataTypeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(deleteDataTypeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(deleteServiceButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(addServiceButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteServiceButton)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(defineNewDataTypeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editDataTypeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(deleteDataTypeButton)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteDataTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDataTypeActionPerformed

        // delete own defined type
        ComponentListPanel clPanel = (ComponentListPanel)jPanel1;
        Node[] nodes = clPanel.getExplorerManager().getSelectedNodes();
        if (nodes.length > 0) {
            Node node = nodes[0];
            if (node instanceof DataTypeNode) {
                DataTypeNode dataTypeNode = (DataTypeNode)node;
                Node parentNode = dataTypeNode.getParentNode();
                DataType parentDataType = (DataType)parentNode.getLookup().lookup(DataType.class);
                DataType dataType = (DataType)dataTypeNode.getLookup().lookup(DataType.class);
                NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(dataType.getTypesName());
                
                // delete data type
                designedDataTypes.remove(object);
                parentDataType.deleteType(dataType.getTypesName());

                TypeNodeChildren children = (TypeNodeChildren)parentNode.getChildren();
                children.update();
            }
        }

    }//GEN-LAST:event_deleteDataTypeActionPerformed

    private void deleteServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteServiceButtonActionPerformed

        // delete node
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node selectedNode = selectedNodes[0];
            if (selectedNode instanceof ServiceNode) {
                ComponentRootNode compRootNode = (ComponentRootNode)manager.getRootContext();
                ServiceNode srvNode = (ServiceNode)selectedNode;
                org.openoffice.extensions.projecttemplates.component.datamodel.Service srv = 
                                (org.openoffice.extensions.projecttemplates.component.datamodel.Service)
                                srvNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Service.class);                
                compRootNode.removeSubType(srv);
                ComponentNodeChildren ch = (ComponentNodeChildren)compRootNode.getChildren();
                ch.update();  // update the nodes
            }
            else if (selectedNode instanceof InterfaceNode) {
                InterfaceNode ifcNode = (InterfaceNode)selectedNode;
                org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc = 
                    (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)
                    ifcNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Interface.class);                
                Node parentNode = ifcNode.getParentNode();
                if (parentNode.equals(manager.getRootContext())) {
                    ComponentRootNode compRootNode = (ComponentRootNode)parentNode;
                    compRootNode.removeSubType(ifc);
                }
                else {
                    Object ifcObject = parentNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Interface.class);                    
                    if (ifcObject != null) {
                        org.openoffice.extensions.projecttemplates.component.datamodel.Interface parentIfc = 
                            (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)ifcObject;
                        parentIfc.removeInterface(ifc);
                    }
                    Object srvObject = parentNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Service.class);
                    if (srvObject != null) {
                        org.openoffice.extensions.projecttemplates.component.datamodel.Service parentSrv = 
                            (org.openoffice.extensions.projecttemplates.component.datamodel.Service)srvObject;
                        parentSrv.removeInterface(ifc);
                    }
                }
                ComponentNodeChildren ch = (ComponentNodeChildren)parentNode.getChildren();
                ch.update();
            }
        } 

    }//GEN-LAST:event_deleteServiceButtonActionPerformed

    private void editDataTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDataTypeActionPerformed
        // get the data type
        ComponentListPanel clPanel = (ComponentListPanel)jPanel1;
        Node[] nodes = clPanel.getExplorerManager().getSelectedNodes();
        if (nodes.length > 0) {
            Node node = nodes[0];
            if (node instanceof DataTypeNode) {
                DataTypeNode dataTypeNode = (DataTypeNode)node;
                DataType dataType = (DataType)dataTypeNode.getLookup().lookup(DataType.class);
                if (dataType.getType() == DataType.INTERFACE_TYPE) {
                    NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(dataType.getTypesName());
                    NewDataTypeBasePanel.start(new DialogProperties(
                        new DialogProperties.OneDataType[] {
                            new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                                    new Integer(DataType.INTERFACE_TYPE)),
                            new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                            new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, new String[]{DataType.INTERFACE_TYPE_NAME}),
                            new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                            new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, 
                                    new ComponentTypeNode(object, new ComponentTypeChildren())),
                        }
                    ));
                }
                else if (dataType.getType() == DataType.SERVICE_TYPE) {
                    String typesName = dataType.getTypesName();
                    NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(typesName);
                    NewDataTypeBasePanel.start(new DialogProperties(
                        new DialogProperties.OneDataType[] {
                            new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                                    new Integer(DataType.SERVICE_TYPE)),
                            new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                            new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, new String[]{DataType.SERVICE_TYPE_NAME}),
                            new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                            new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, 
                                    new ComponentTypeNode(object, new ComponentTypeChildren())),
                        }
                    ));
                }
                else if (dataType.getType() == DataType.ENUM_TYPE) {
                    String typesName = dataType.getTypesName();
                    NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(typesName);
                    NewDataTypeBasePanel.start(new DialogProperties(
                        new DialogProperties.OneDataType[] {
                            new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                                    new Integer(DataType.ENUM_TYPE)),
                            new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                            new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, new String[]{DataType.ENUM_TYPE_NAME}),
                            new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                            new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, 
                                    new ComponentTypeNode(object, new ComponentTypeChildren())),
                        }
                    ));
                }
                else if (dataType.getType() == DataType.STRUCT_TYPE) {
                    String typesName = dataType.getTypesName();
                    NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(typesName);
                    NewDataTypeBasePanel.start(new DialogProperties(
                        new DialogProperties.OneDataType[] {
                            new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                                    new Integer(DataType.STRUCT_TYPE)),
                            new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                            new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, new String[]{DataType.STRUCT_TYPE_NAME}),
                            new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                            new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, 
                                    new ComponentTypeNode(object, new ComponentTypeChildren())),
                        }
                    ));
                }
                else if (dataType.getType() == DataType.EXCEPTION_TYPE) {
                    String typesName = dataType.getTypesName();
                    NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(typesName);
                    NewDataTypeBasePanel.start(new DialogProperties(
                        new DialogProperties.OneDataType[] {
                            new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                                    new Integer(DataType.EXCEPTION_TYPE)),
                            new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                            new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                                    new String[]{DataType.EXCEPTION_TYPE_NAME}),
                            new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                            new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, 
                                    new ComponentTypeNode(object, new ComponentTypeChildren())),
                        }
                    ));
                }
                else if (dataType.getType() == DataType.POLY_STRUCT_TYPE) {
                    String typesName = dataType.getTypesName();
                    NbNodeObject object = (NbNodeObject)this.designedDataTypes.get(typesName);
                    NewDataTypeBasePanel.start(new DialogProperties(
                        new DialogProperties.OneDataType[] {
                            new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                                    new Integer(DataType.POLY_STRUCT_TYPE)),
                            new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                            new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, new String[]{DataType.POLY_STRUCT_TYPE_NAME}),
                            new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                            new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, 
                                    new ComponentTypeNode(object, new ComponentTypeChildren())),
                        }
                    ));
                }
            }
        }
    }//GEN-LAST:event_editDataTypeActionPerformed

    private void defineNewDataTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defineNewDataTypeActionPerformed
        // new data type
        ComponentListPanel clPanel = (ComponentListPanel)this.jPanel1;
        String type = clPanel.getSelectedType();
        if (type.equals(DataType.INTERFACE_TYPE_NAME))
            NewDataTypeBasePanel.start(new DialogProperties(
                new DialogProperties.OneDataType[] {
                    new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                            new Integer(DataType.INTERFACE_TYPE)),
                    new DialogProperties.OneDataType<String>(DialogProperties.IFC, interfaceName),
                    new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                    new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                            new String[]{DataType.INTERFACE_TYPE_NAME}),
                    new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                }
            ));
        else if (type.equals(DataType.SERVICE_TYPE_NAME))
            NewDataTypeBasePanel.start(new DialogProperties(
                new DialogProperties.OneDataType[] {
                    new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                            new Integer(DataType.SERVICE_TYPE)),
                    new DialogProperties.OneDataType<String>(DialogProperties.SRV, serviceName),
                    new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                    new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                            new String[]{DataType.SERVICE_TYPE_NAME}),
                    new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                }
            ));
        else if (type.equals(DataType.ENUM_TYPE_NAME))
            NewDataTypeBasePanel.start(new DialogProperties(
                new DialogProperties.OneDataType[] {
                    new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                            new Integer(DataType.ENUM_TYPE)),
                    new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                    new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                            new String[]{DataType.ENUM_TYPE_NAME}),
                    new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                }
            ));
        else if (type.equals(DataType.STRUCT_TYPE_NAME))
            NewDataTypeBasePanel.start(new DialogProperties(
                new DialogProperties.OneDataType[] {
                    new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                            new Integer(DataType.STRUCT_TYPE)),
                    new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                    new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                            new String[]{DataType.STRUCT_TYPE_NAME}),
                    new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                }
            ));
        else if (type.equals(DataType.EXCEPTION_TYPE_NAME))
            NewDataTypeBasePanel.start(new DialogProperties(
                new DialogProperties.OneDataType[] {
                    new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                            new Integer(DataType.EXCEPTION_TYPE)),
                    new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                    new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                            new String[]{DataType.EXCEPTION_TYPE_NAME}),
                    new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                }
            ));
        else if (type.equals(DataType.POLY_STRUCT_TYPE_NAME))
            NewDataTypeBasePanel.start(new DialogProperties(
                new DialogProperties.OneDataType[] {
                    new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                            new Integer(DataType.POLY_STRUCT_TYPE)),
                    new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkgName),
                    new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, 
                            new String[]{DataType.POLY_STRUCT_TYPE_NAME}),
                    new DialogProperties.OneDataType<Object>(DialogProperties.PANEL, this.panel),
                }
            ));
        
    }//GEN-LAST:event_defineNewDataTypeActionPerformed

    
    private void addServiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServiceButtonActionPerformed
        // add service/interface
        NewTypeBrowserDialog.start(this, panel);

    }//GEN-LAST:event_addServiceButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addServiceButton;
    private javax.swing.JButton defineNewDataTypeButton;
    private javax.swing.JButton deleteDataTypeButton;
    private javax.swing.JButton deleteServiceButton;
    private javax.swing.JButton editDataTypeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    public void addNotify() {
        super.addNotify();
        try {
            manager.setSelectedNodes(new Node[]{manager.getRootContext()});
        } catch (PropertyVetoException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    boolean valid(WizardDescriptor wizardDescriptor) {
        org.openoffice.extensions.projecttemplates.component.datamodel.Service srv = 
                getService(manager.getRootContext());
        if (srv == null) {
            String message = NbBundle.getMessage(ComponentWizardIterator.class, "LBL_Error_MandatoryService");
            wizardDescriptor.putProperty("WizardPanel_errorMessage", message); // NOI18N
            return false;
        }
        else {
            Collection c = srv.getInterfaces();
            if (srv.isOwnDesign()) {
                if (c.size() == 0) {
                    String message = NbBundle.getMessage(ComponentWizardIterator.class, "LBL_Error_MandatoryService");
                    wizardDescriptor.putProperty("WizardPanel_errorMessage", message); // NOI18N
                    return false;
                }
            }

        }
        
        wizardDescriptor.putProperty("WizardPanel_errorMessage", ""); // NOI18N
        return true;
    }
    
    void store(WizardDescriptor d) {
        ComponentRootNode compRootNode = (ComponentRootNode)this.manager.getRootContext();
        Hashtable<String,String[]> depTypeStructure = getDependentTypeStructure(compRootNode);
        Vector<String> dumpTypes = getTypes(compRootNode);
        
        Enumeration e = designedDataTypes.keys();
        int ifcCounter = 0;
        int serviceCounter = 0;

        while (e.hasMoreElements()) {
            int objectCounter;
            String el = (String)e.nextElement();
            NbNodeObject object = (NbNodeObject)designedDataTypes.get(el);
            

            // get info about the structure
            Object structureType = depTypeStructure.get(object.getDisplayName());
            String[] depTypes = null;
            if (structureType != null) {
                depTypes = (String[])structureType;
            }
            
            if (object instanceof Interface) {
                Interface ifc = (Interface)object;
                if (depTypes != null) {
                    for (int i=0; i<depTypes.length; i++) {
                        Object o = designedDataTypes.get(depTypes[i]);
                        if (o != null)
                            ifc.addAggregatedInterface((Interface)o);
                    }
                }
            }
            else if (object instanceof Service) {
                Service srvc = (Service)object;
                if (depTypes != null) {
                    for (int i=0; i<depTypes.length; i++) {
                        Object o = designedDataTypes.get(depTypes[i]);
                        if (o != null)
                            srvc.addSetObject("ifc".concat(new Integer(i).toString()), (Interface)o); // NOI18N
                    }
                }
            }
        }
        d.putProperty("DesignedTypes", designedDataTypes); // NOI18N
        d.putProperty("DumpTypes", dumpTypes); // NOI18N
    }
    
    void read(WizardDescriptor settings) {
        designedDataTypes = new Hashtable<String,NbNodeObject>();
        pkgName = (String)settings.getProperty("package"); // NOI18N
        serviceName = ((String)settings.getProperty("service")).concat("Service"); // NOI18N
        interfaceName = "X".concat((String)settings.getProperty("service")); // NOI18N
        
        initialize();
    }
    
    void validate(WizardDescriptor d) throws WizardValidationException {
        // nothing to validate
    }

    public NbNodeObject[] getDesignedTypes() {
        if (designedDataTypes != null) {
            Collection<NbNodeObject> c = designedDataTypes.values();
            return c.toArray(new NbNodeObject[c.size()]);
        }
        return null;
    }
    
    public void addNewDataType(String oldName, String newName, NbNodeObject object) {
        ComponentListPanel compListPanel = (ComponentListPanel)jPanel1;
        ExplorerManager compListManager = compListPanel.getExplorerManager();
        TypeRootNode rootNode = (TypeRootNode)compListManager.getRootContext();
        Node[] nodes = rootNode.getChildren().getNodes();
        boolean updateUpperPanel = false;
        
        // remove from types if already there
        if (!newName.equals(oldName)) {
            designedDataTypes.remove(oldName);
        }
        // enter the new type into the list panel (again, if removed)
//        NbNodeObject object = (NbNodeObject)o;
        designedDataTypes.put(newName, object);
        // write into type node: own types can be used.
        Collection<NbNodeObject> c = designedDataTypes.values();
        TypeNode.setDesignedTypes(c.toArray(new NbNodeObject[c.size()]));

        String dataTypeName = null;
        int type = 0;
        // get the type of object
        int nbNodeObjectType = object.getType();
        switch (nbNodeObjectType) {
            case NbNodeObject.INTERFACE_TYPE:
                dataTypeName = DataType.INTERFACE_TYPE_NAME;
                type = DataType.INTERFACE_TYPE;
                updateUpperPanel = true; // this may affect upper panel
                break;
            case NbNodeObject.SERVICE_TYPE:
                dataTypeName = DataType.SERVICE_TYPE_NAME;
                type = DataType.SERVICE_TYPE;
                updateUpperPanel = true; // this may affect upper panel
                break;
            case NbNodeObject.ENUMERATION_TYPE:
                dataTypeName = DataType.ENUM_TYPE_NAME;
                type = DataType.ENUM_TYPE;
                break;
            case NbNodeObject.STRUCT_TYPE:
                dataTypeName = DataType.STRUCT_TYPE_NAME;
                type = DataType.STRUCT_TYPE;
                break;
            case NbNodeObject.EXCEPTION_TYPE:
                dataTypeName = DataType.EXCEPTION_TYPE_NAME;
                type = DataType.EXCEPTION_TYPE;
                break;
            case NbNodeObject.POLY_STRUCT_TYPE:
                dataTypeName = DataType.POLY_STRUCT_TYPE_NAME;
                type = DataType.POLY_STRUCT_TYPE;
                break;
            default:
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Not handled object type: " + nbNodeObjectType); // NOI18N
        }
        
        // update lower panel
        for (int i=0; i<nodes.length; i++) {
            DataTypeNode dataTypeNode = (DataTypeNode)nodes[i];
            DataType dataType = (DataType)dataTypeNode.getLookup().lookup(DataType.class);
            if (dataType.getType() == DataType.FOLDER_TYPE && dataType.getTypesName().equals(dataTypeName)) {
                if (!newName.equals(oldName)) {
                    if (dataType.getTypeForName(oldName) != null) {
                        dataType.deleteType(oldName);
                    }
                }
                dataType.addTypeWithName(newName, new DataType(dataType, type, newName));
                TypeNodeChildren children = (TypeNodeChildren)dataTypeNode.getChildren();
                children.update(); // updates nodes
//                dataTypeNode.setPreferred(true);
                ComponentListPanel clPanel = (ComponentListPanel)this.jPanel1;
                Node node = children.findChild(newName);
                clPanel.selectNode(node);
            }
        }
        
        // update upper panel
        if (updateUpperPanel) {
            nodes = manager.getRootContext().getChildren().getNodes();
            updateUpperPanel(nodes, oldName, newName);
        }
    }
    
    private void updateUpperPanel(Node[] nodes, String oldName, String newName) {
        if (nodes != null) {
            // step through nodes and update: should not be too much work
            for (int i = 0; i < nodes.length; i++) { 
                Node selectedNode = nodes[i];
                org.openoffice.extensions.projecttemplates.component.datamodel.Service srv = 
                                (org.openoffice.extensions.projecttemplates.component.datamodel.Service)
                                selectedNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Service.class);
                if (srv != null) {
                    if (srv.getDisplayName().equals(oldName)) {
                        srv.setHierarchicalName(newName);
                        
                        ServiceNode sNode = (ServiceNode)selectedNode;
                        sNode.update();
                        
                        panel.fireChangeEvent();
                    }
                    else {
                        updateUpperPanel(selectedNode.getChildren().getNodes(), oldName, newName);
                    }
                }
                org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc = 
                                (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)
                                selectedNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Interface.class);
                if (ifc != null) {
                    if (ifc.getDisplayName().equals(oldName)) {
                        ifc.setHierarchicalName(newName);
                        
                        InterfaceNode iNode = (InterfaceNode)selectedNode;
                        iNode.update();
                        
                        panel.fireChangeEvent();
                    }
                    else {
                        updateUpperPanel(selectedNode.getChildren().getNodes(), oldName, newName);
                    }
                }
            }
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        // because of TreeSelectionModel.SINGLE_TREE_SELECTION max. one node is selected
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node selectedNode = selectedNodes[0];
            if (selectedNode instanceof ServiceNode || selectedNode instanceof InterfaceNode) {
                deleteServiceButton.setEnabled(true); // delete
            }
            else 
                deleteServiceButton.setEnabled(false);
        } 
        
        ComponentListPanel clPanel = (ComponentListPanel)this.jPanel1;
        selectedNodes = clPanel.getExplorerManager().getSelectedNodes();
        if (selectedNodes != null && selectedNodes.length > 0) {
            Object o = selectedNodes[0].getLookup().lookup(DataType.class);
            if (o != null) {
                DataType set = (DataType)o;
                switch (set.getType()) {
                    case DataType.FOLDER_TYPE:  // folder: no edit, no delete
                        editDataTypeButton.setEnabled(false); // edit
                        deleteDataTypeButton.setEnabled(false); // delete
                        break;
                    default:
                        editDataTypeButton.setEnabled(true); // edit
                        deleteDataTypeButton.setEnabled(true); // delete
                        break;
                }
            }
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    public void addNewServiceOrInterface(String hierarchicalName, int type, boolean isOwnDesign) {
        String displayName = hierarchicalName.substring(hierarchicalName.lastIndexOf('.') + 1);
        String pkg = hierarchicalName.substring(0, hierarchicalName.lastIndexOf('.'));
        if (type == UnoTypes.SERVICE_TYPE) {
            org.openoffice.extensions.projecttemplates.component.datamodel.Service srvc =
                new org.openoffice.extensions.projecttemplates.component.datamodel.Service(displayName, pkg, isOwnDesign);
            // get a specified interface from service
            Service s = (Service)this.designedDataTypes.get(hierarchicalName);
            if (s != null) {
                Interface i = (Interface)s.getSetObject();
                if (i != null) {
                    try {
                        String ifcName = i.getSimpleProperty(i.PROPERTY_CONTAINER_NAME);
                        String ifcPackage = i.getSimpleProperty(i.PROPERTY_CONTAINER_PACKAGE);
                        boolean ifcIsOwnDesign = this.designedDataTypes.containsKey(ifcPackage.concat(".").concat(ifcName)); // NOI18N 
                        org.openoffice.extensions.projecttemplates.component.datamodel.Interface newIfc =
                            new org.openoffice.extensions.projecttemplates.component.datamodel.Interface(
                                ifcName, ifcPackage, ifcIsOwnDesign);
                        srvc.addInterface(newIfc);
                    } catch (UnknownOpenOfficeOrgPropertyException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
            }
            ComponentRootNode compRootNode = (ComponentRootNode)this.manager.getRootContext();
            compRootNode.addSubType(srvc);
            ComponentNodeChildren ch = (ComponentNodeChildren)compRootNode.getChildren();
            ch.update();  // update the nodes
//            compRootNode.update();
        }
        else if (type == UnoTypes.INTERFACE_TYPE) {
            // find out what's selected
            Node[] selNodes = this.manager.getSelectedNodes();
            if (selNodes != null && selNodes.length > 0) {
                // create an interface in any case
                org.openoffice.extensions.projecttemplates.component.datamodel.Interface newIfc =
                new org.openoffice.extensions.projecttemplates.component.datamodel.Interface(displayName, pkg, isOwnDesign);
                Node updateNode = selNodes[0];
                if (updateNode instanceof ServiceNode) {
                    org.openoffice.extensions.projecttemplates.component.datamodel.Service srvc =
                            (org.openoffice.extensions.projecttemplates.component.datamodel.Service)
                            updateNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Service.class);
                    if (srvc.isOwnDesign()) { // overwrite an existing one!
                        srvc.addInterface(newIfc);
                    }
                    else {
                        updateNode = manager.getRootContext();
                        ComponentRootNode rootNode = (ComponentRootNode)updateNode;
                        rootNode.addSubType(newIfc);
                    }
                }
                else if (updateNode instanceof InterfaceNode) {
                    org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc =
                            (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)
                            updateNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Interface.class);
                    if (ifc.isOwnDesign()) {
                        ifc.addInterface(newIfc);
                    }
                    else {
                        boolean foundNode = false;
                        updateNode = updateNode.getParentNode();
                        while(!foundNode) {
                            if (updateNode instanceof ComponentRootNode) {  // root node
                                foundNode = true;
                                ComponentRootNode rootNode = (ComponentRootNode)updateNode;
                                rootNode.addSubType(newIfc);
                            }
                            else if (updateNode instanceof InterfaceNode) {
                                org.openoffice.extensions.projecttemplates.component.datamodel.Interface parentIfc =
                                        (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)
                                        updateNode.getLookup().lookup(org.openoffice.extensions.projecttemplates.component.datamodel.Interface.class);
                                if (parentIfc.isOwnDesign()) {
                                    foundNode = true;
                                    parentIfc.addInterface(newIfc);
                                }
                                else {
                                    updateNode = updateNode.getParentNode();
                                }
                            }
                            else {
                                updateNode = updateNode.getParentNode();
                            }
                        }
                    }
                }
                else {   // root node selected.
                    ComponentRootNode rootNode = (ComponentRootNode)updateNode;
                    rootNode.addSubType(newIfc);
                }
                ComponentNodeChildren ch = (ComponentNodeChildren)updateNode.getChildren();
                ch.update();  // update the nodes
            }
       }
    }
    
    private Node getOwnDataTypesRootNode() {
        return TypeRootNode.createInitialNodeStructure(serviceName, interfaceName);
    }
    
    /**
     * analyse the structure of services and interfaces: search for own services and own multi-inheritance interfaces;
     * get dependent types for them
     */
    private Hashtable<String,String[]> getDependentTypeStructure(ComponentRootNode compRootNode) {
        Hashtable<String,String[]> depTypes = new Hashtable<String,String[]>();
        Collection<Object> c = compRootNode.getSubTypesAsCollection();
        for (Iterator i = c.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof org.openoffice.extensions.projecttemplates.component.datamodel.Service) {
                org.openoffice.extensions.projecttemplates.component.datamodel.Service srv = 
                        (org.openoffice.extensions.projecttemplates.component.datamodel.Service)o;
                Collection ifcs = srv.getInterfaces();
                for (Iterator ifcIterator = ifcs.iterator(); ifcIterator.hasNext();) {
                    org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc = 
                    (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)ifcIterator.next();
                    if (srv.isOwnDesign() && ifc.isOwnDesign())
                        depTypes.put(srv.getName(), new String[]{ifc.getName()});
                    putAllDependentIfcNames(ifc, depTypes);
                }
            }
            else if (o instanceof org.openoffice.extensions.projecttemplates.component.datamodel.Interface) {
                org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc = 
                (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)o;
                putAllDependentIfcNames(ifc, depTypes);
            }

        }
        return depTypes;
    }
    
    private void putAllDependentIfcNames(org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc, Hashtable<String,String[]> depTypes) {
        Collection c = ifc.getInterfaces();
        Vector<String> depTypeNames = new Vector<String>();
        int i=0;
        for (Iterator ifcIterator = c.iterator(); ifcIterator.hasNext(); i++) {
            org.openoffice.extensions.projecttemplates.component.datamodel.Interface nextIfc =
                    (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)ifcIterator.next();
            if (nextIfc.isOwnDesign()) {
                if (!nextIfc.getName().equals(ifc.getName())) // just avoid endless recursion
                    putAllDependentIfcNames(nextIfc, depTypes);
                depTypeNames.add(nextIfc.getName());
            }
        }
        int size = depTypeNames.size();
        if(size>0) depTypes.put(ifc.getName(), 
            depTypeNames.toArray(new String[size]));
    }
    
    /** 
     * get all types that need to be dumped by skeletonmaker:
     * own idls as well as office types
     * @parm compRootNode the root node of the types
     */
    private Vector<String> getTypes(ComponentRootNode compRootNode) {
        Vector<String> officeTypes = new Vector<String>();
        Collection c = compRootNode.getSubTypesAsCollection();
        for (Iterator i = c.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof org.openoffice.extensions.projecttemplates.component.datamodel.Service) {
                org.openoffice.extensions.projecttemplates.component.datamodel.Service srv = 
                        (org.openoffice.extensions.projecttemplates.component.datamodel.Service)o;
                officeTypes.add(srv.getHierarchicalName());
            }
            else if (o instanceof org.openoffice.extensions.projecttemplates.component.datamodel.Interface) {
                org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc = 
                (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)o;
                getTypesRecursively(ifc, officeTypes);
            }
        }
        return officeTypes;
    }
    
    /**
     * get types recursively from interface, when it's multi inherited,
     * stuff may be mentioned multiple times
     * @param ifc the interface
     * @param v the vector to put the datat types in; ifc is put in first
     */
    private void getTypesRecursively(org.openoffice.extensions.projecttemplates.component.datamodel.Interface ifc, Vector<String> v) {
        v.add(ifc.getHierarchicalName());
        Collection c = ifc.getInterfaces();
        for (Iterator i = c.iterator(); i.hasNext();) {
            Object o = i.next();
            org.openoffice.extensions.projecttemplates.component.datamodel.Interface newIfc = 
            (org.openoffice.extensions.projecttemplates.component.datamodel.Interface)o;
            getTypesRecursively(newIfc, v);
        }
    }
    
    private org.openoffice.extensions.projecttemplates.component.datamodel.Service getService(Node node) {
        ComponentRootNode rootNode = (ComponentRootNode)node;
        Collection c = rootNode.getSubTypesAsCollection();
        for (Iterator i = c.iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof org.openoffice.extensions.projecttemplates.component.datamodel.Service) {
                return (org.openoffice.extensions.projecttemplates.component.datamodel.Service)o;
            }
        }
        return null;
    }

    static ComponentPanelVisual2IdlFiles componentPanelVisual2IdlFiles;
    static ComponentPanelVisual2IdlFiles getComponentPanelVisual2IdlFiles(ComponentWizardPanel2IdlFiles componentWizardPanel2IdlFiles) {
        componentPanelVisual2IdlFiles = new ComponentPanelVisual2IdlFiles(componentWizardPanel2IdlFiles);
        return componentPanelVisual2IdlFiles;
    }
    public static ComponentPanelVisual2IdlFiles getComponentPanelVisual2IdlFiles() {
        return componentPanelVisual2IdlFiles;
    }
}
