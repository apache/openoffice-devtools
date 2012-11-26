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

package org.openoffice.extensions.projecttemplates.component.datamodel.types.node;

import java.beans.FeatureDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import javax.swing.tree.TreeSelectionModel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.editors.EnhancedCustomPropertyEditor;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.component.ComponentPanelVisual2IdlFiles;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;
import org.openoffice.extensions.util.typebrowser.logic.TypeNode;

/**
 *
 * @author  sg128468
 */
public class ComponentInterfaceTypeCustomEditor extends javax.swing.JPanel 
        implements ExplorerManager.Provider, PropertyChangeListener, 
        VetoableChangeListener, EnhancedCustomPropertyEditor {
    
    private ExplorerManager manager = new ExplorerManager();
    private PropertyEnv propertyEnv;
    private ComponentInterfaceTypePropertyEditor editor;
    
//    private String selectedType;
    private TypeNode rootNode;

    // the return type, set into the editor
    private String fullName;
    private Service service;
    
    /** Creates new form ComponentTypeCustomEditor */
    public ComponentInterfaceTypeCustomEditor(ComponentInterfaceTypePropertyEditor editor, 
            PropertyEnv propertyEnv) {
        initComponents();
        this.propertyEnv = propertyEnv;
        this.editor = editor;
        propertyEnv.setState(PropertyEnv.STATE_INVALID);
        propertyEnv.addVetoableChangeListener(this);

        FeatureDescriptor desc = propertyEnv.getFeatureDescriptor();
        SimpleOpenOfficeOrgProperty.SimpleProperty property = (SimpleOpenOfficeOrgProperty.SimpleProperty)desc;
        NbNodeObject obj = property.getPropertyParent();
        this.service = (Service)obj;
        
        this.rootNode = TypeNode.createRootNode(
                new String[] { 
                      UnoTypes.INTERFACE,
                }
        );
  
        manager.setRootContext(rootNode);
        BeanTreeView typeView = (BeanTreeView)jScrollPane1;
        typeView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        manager.addPropertyChangeListener(this);    
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollPane1 = new BeanTreeView();

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    public ExplorerManager getExplorerManager() {
        return manager;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        Node[] nodes = manager.getSelectedNodes();
        if (nodes != null && nodes.length > 0) {
            TypeNode node = (TypeNode)nodes[0];  // single tree selection

            if (node.getNodeType() == UnoTypes.INTERFACE_TYPE) { // do only add interfaces
                propertyEnv.setState(PropertyEnv.STATE_NEEDS_VALIDATION);
            }
            else {
                propertyEnv.setState(PropertyEnv.STATE_INVALID);
            }
        }
    }

    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (PropertyEnv.PROP_STATE.equals(evt.getPropertyName()) &&
            PropertyEnv.STATE_VALID.equals(evt.getNewValue()) && 
                    PropertyEnv.STATE_NEEDS_VALIDATION.equals(evt.getOldValue())) { // OK pressed
            setFullName();
            ComponentPanelVisual2IdlFiles comp = ComponentPanelVisual2IdlFiles.getComponentPanelVisual2IdlFiles();
            boolean ifcOwnDesign = false;
            if (comp != null) {  // TODO something else?
                NbNodeObject[] designedTypes = comp.getDesignedTypes();
                if (designedTypes != null) {
                    String aName = fullName.substring(fullName.lastIndexOf('.') + 1); 
                    for (int i = 0; i < designedTypes.length; i++) {
                        if (designedTypes[i].getType() == NbNodeObject.INTERFACE_TYPE) { 
                            Interface ifc = (Interface)designedTypes[i];
                            try {
                                if (ifc.getSimpleProperty(ifc.PROPERTY_CONTAINER_NAME).equals(aName)) {
                                    service.addSetObject(fullName, ifc);
                                    ifcOwnDesign = true;
                                }
                            } catch (UnknownOpenOfficeOrgPropertyException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } 
            }
            if (!ifcOwnDesign) {
                int index = fullName.lastIndexOf('.');
                String pkg = null;
                String name = null;
                if (index != -1) {
                    pkg = fullName.substring(0, index);
                    name = fullName.substring(index + 1);
                }
                else  {
                    pkg = ""; // NOI18N
                    name = fullName;
                }
                Interface ifc = new Interface(name, pkg);
                ifc.setType(NbNodeObject.OFFICE_INTERFACE_TYPE);
                service.addSetObject(fullName, ifc);
            }
        }
    }

    private void setFullName() {
        Node[] nodes = manager.getSelectedNodes();
        if (nodes != null && nodes.length > 0) {
            TypeNode node = (TypeNode)nodes[0];
            fullName = node.getHierarchicalName();
        }
    }
    
    public Object getPropertyValue() throws IllegalStateException {
        return fullName;
    }
    
//    
//    public void addTemplateTypes(String polyStructTypes) {
//        this.type = polyStructTypes;
//    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    
}
