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
import javax.swing.JPanel;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.component.*;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.IdlTypeTreeCreator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.FunctionException;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author  sg128468
 */
public class ExceptionDataTypePanel extends JPanel
        implements ExplorerManager.Provider, PropertyChangeListener {
    
    private ExplorerManager manager = new ExplorerManager();

    private ChangeEventPanel panel;    
    private Node node;
    
    private String oldName; 
    private boolean edit;
    private ComponentActions actions;

    private ValidateDataType dialog;
    
    /**
     * Creates new form InterfaceDataTypeDialog
     */
//    public ExceptionDataTypePanel(java.awt.Frame parent, Component component, 
//            ChangeEventPanel panel, boolean modal, Node object, boolean edit) {
    public ExceptionDataTypePanel(ValidateDataType dialog, DialogProperties props) {
        initComponents();

        this.edit = props.getBooleanProperty(props.EDIT);
        String pkgName = props.getStringProperty(props.PKG);
        String name = props.getStringProperty(props.NAME);
        panel = (ChangeEventPanel)props.getProperty(props.PANEL);
        this.actions = new ComponentActions(manager, panel);
        this.dialog = dialog;
        
        if (edit) {
            node = (Node)props.getProperty(props.NODE);
            NbNodeObject ob = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
            oldName = ob.getDisplayName();
        }
        else {
            if (name == null) name = "Exception"; // NOI18N
            node = (Node)IdlTypeTreeCreator.createInitialException(name, pkgName);
        }
        manager.setRootContext(node);

        PropertySheet propSheet = (PropertySheet)jPanel1;
        propSheet.setNodes(new Node[]{node});
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanel1 = new PropertySheet();

        setBackground(new java.awt.Color(238, 238, 238));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 531, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    public ExplorerManager getExplorerManager() {
        return this.manager;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        valid();
//        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    private void valid() {
        NbNodeObject ob = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
        FunctionException exc = (FunctionException)ob;
        try {
            if (exc.getSimpleProperty(exc.PROPERTY_CONTAINER_NAME).length() == 0
                || exc.getSimpleProperty(exc.PROPERTY_CONTAINER_PACKAGE).length() == 0) {
                dialog.setEnableError(true, 
                    NbBundle.getMessage(NewDataTypeBasePanel.class, "LBL_Error_Name"));
                return;
            }
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (MissingResourceException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        dialog.setEnableError(false, null);
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

}
