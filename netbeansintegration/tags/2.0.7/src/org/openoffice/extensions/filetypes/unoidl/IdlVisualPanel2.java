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

import javax.swing.JPanel;
import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypeNode;
import org.openoffice.extensions.projecttemplates.component.dialogs.ChangeEventPanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.DialogProperties;
import org.openoffice.extensions.projecttemplates.component.dialogs.EnumDataTypePanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.ExceptionDataTypePanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.InterfaceDataTypePanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.PolyStructDataTypePanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.ServiceDataTypePanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.StructDataTypePanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.ValidateDataType;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;

public final class IdlVisualPanel2 extends JPanel implements ValidateDataType {
    
    private static final String ICON_PATH = 
            "/org/openoffice/extensions/projecttemplates/component/icons/error.png"; // NOI18N

    private DialogProperties props;
    private IdlWizardPanel2 panel;

    private boolean m_bError;
    private String m_errorMessage;
    
    /** Creates new form IdlVisualPanel2 */
    public IdlVisualPanel2(IdlWizardPanel2 panel, WizardDescriptor wizard) {
        this.panel = panel;
        String name = (String)wizard.getProperty("Name"); // NOI18N
        if (name == null) name = ""; // NOI18N
        String pkg = (String)wizard.getProperty("Package"); // NOI18N
        if (pkg == null) pkg = ""; // NOI18N
        String dataType = (String)wizard.getProperty("DataType"); // NOI18N
        if (dataType == null) dataType = DataType.ENUM_TYPE_NAME;
        props = new DialogProperties(
            new DialogProperties.OneDataType[] {
                new DialogProperties.OneDataType<Integer>(DialogProperties.DATA_TYPE, 
                        Integer.valueOf(DataType.getTypeIdForName(dataType))),
                new DialogProperties.OneDataType<String>(DialogProperties.NAME, name),
                new DialogProperties.OneDataType<String>(DialogProperties.IFC, name),
                new DialogProperties.OneDataType<String>(DialogProperties.SRV, name),
                new DialogProperties.OneDataType<String>(DialogProperties.PKG, pkg),
                new DialogProperties.OneDataType<String[]>(DialogProperties.SELECTION, new String[]{
                    DataType.ENUM_TYPE_NAME,
                    DataType.EXCEPTION_TYPE_NAME,
                    DataType.INTERFACE_TYPE_NAME,
                    DataType.POLY_STRUCT_TYPE_NAME,
                    DataType.SERVICE_TYPE_NAME,
                    DataType.STRUCT_TYPE_NAME,
                }),
                new DialogProperties.OneDataType<Boolean>(DialogProperties.EDIT, Boolean.FALSE),
                new DialogProperties.OneDataType<Boolean>(DialogProperties.ALLOW_SELECTION, Boolean.TRUE),
                new DialogProperties.OneDataType<ChangeEventPanel>(DialogProperties.PANEL, (ChangeEventPanel)this.panel),
                new DialogProperties.OneDataType<ComponentTypeNode>(DialogProperties.NODE, null)
            }
        );   
        initComponents();
    }
    
    public String getName() {
        return NbBundle.getMessage(IdlWizardIterator.class, "IdlVisualPanelTypes");
    }
    
    protected void store(WizardDescriptor desc) {
        ExplorerManager.Provider prov = (ExplorerManager.Provider)dataTypeBasePanel;
        ComponentTypeNode node = (ComponentTypeNode)prov.getExplorerManager().getRootContext();
        NbNodeObject object = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
        desc.putProperty("NbNodeObject", object); // NOI18N
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        dataTypeBasePanel = getContentPanel();

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(dataTypeBasePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(dataTypeBasePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataTypeBasePanel;
    // End of variables declaration//GEN-END:variables
    

    private JPanel getContentPanel() {
        switch(props.getIntProperty(props.DATA_TYPE)) {
            case DataType.EXCEPTION_TYPE:
                return new ExceptionDataTypePanel(this, props);
            case DataType.POLY_STRUCT_TYPE:
                return new PolyStructDataTypePanel(this, props);
            case DataType.ENUM_TYPE:
                return new EnumDataTypePanel(this, props);
            case DataType.INTERFACE_TYPE:
                return new InterfaceDataTypePanel(this, props);
            case DataType.SERVICE_TYPE:
                return new ServiceDataTypePanel(this, props);
            case DataType.STRUCT_TYPE:
                return new StructDataTypePanel(this, props);
            default:
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Unkown type for plugged panel creation."); // NOI18N
        }
        return new JPanel();
    }
    
    protected boolean valid(WizardDescriptor wizardDescriptor) {
        if (m_bError) {
            wizardDescriptor.putProperty("WizardPanel_errorMessage", m_errorMessage); // NOI18N
            return false;
        }
        wizardDescriptor.putProperty("WizardPanel_errorMessage", ""); // NOI18N
        return true;
    }
    
    public void setEnableError(boolean error, String message) {
        this.m_bError = error;
        if (error) {
            m_errorMessage = message;
//            errorMessageLabel.setIcon(
//                new javax.swing.ImageIcon(getClass().getResource(ICON_PATH)));
        }
        else {
            m_errorMessage = "";
        }
        panel.fireChangeEvent();
    }
}

