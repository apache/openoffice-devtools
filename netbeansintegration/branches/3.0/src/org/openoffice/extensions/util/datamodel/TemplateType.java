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

package org.openoffice.extensions.util.datamodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypePropertyEditor;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class TemplateType extends PropertyContainer implements NbNodeObject {

    private NbNodeObject parent;
    private boolean customPropertyEditor;

    public TemplateType(String nameValue, String typeValue, NbNodeObject parent) {
        this(nameValue, typeValue, parent, false);
    }
            
    public TemplateType(String nameValue, String typeValue, NbNodeObject parent, boolean customPropertyEditor) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_TYPE,
        });
        this.parent = parent;
        this.customPropertyEditor = customPropertyEditor;
        initializeProperties(nameValue, typeValue);
    }

    private void initializeProperties(String nameValue, String typeValue) {
        PropertyEditor edit = null;
        if (customPropertyEditor) {
            edit = new ComponentTypePropertyEditor();
        }
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Name_Tooltip"),
                this, null, customPropertyEditor);
        properties[PROPERTY_Type] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_TYPE, 
                typeValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Type_Tooltip"),
                this, edit);
    }
    
    public String getDisplayName() {
        try {
            return properties[PROPERTY_Name].getValueForLanguage(-1);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return "unkown name"; // NOI18N
    }

    public OpenOfficeOrgProperty getProperty(int propertyIndex) {
        return properties[propertyIndex];
    }
    
    public NbNodeObject[] getAllSubObjects() {
        Vector<Object> nodeObjects = new Vector<Object>();
        // first direct properties
        nodeObjects.add(properties[PROPERTY_Name]);
        nodeObjects.add(properties[PROPERTY_Type]);
        return (NbNodeObject[])nodeObjects.toArray(new NbNodeObject[nodeObjects.size()]);
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Vector<Object> v = new Vector<Object>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Type]).createProperties(null, listener)));
        
        Node.Property[]nameProp = (Node.Property[])v.toArray(new Node.Property[v.size()]);
        if (sheet != null) {
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
        }
        return (Node.Property[])v.toArray(new Node.Property[v.size()]);
    }
    
    /**
     * set the action for context menus
     */
    public void setActions(BaseAction baseAction) {
    }
    
    public Action[] getActions(boolean b) {
        return null;//new Action[]{new MyAddParameterAction(), new MyDeleteParameterAction()};
    }

    public boolean hasActions(int type) {
        return false;
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public int getType() {
        return NbNodeObject.TEMPLATE_TYPE;
    }
    
//    private class MyAddParameterAction extends AbstractAction {
//        int counter;
//        public MyAddParameterAction() {
//            putValue(NAME, "Add Parameter");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.addParameterAction();
//        }
//    }
//    private class MyDeleteParameterAction extends AbstractAction {
//        public MyDeleteParameterAction() {
//            putValue(NAME, "Delete Parameter");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.deleteActions();
//        }
//    }
    
}
