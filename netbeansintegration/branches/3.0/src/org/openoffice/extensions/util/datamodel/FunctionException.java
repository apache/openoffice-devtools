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
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class FunctionException extends PropertyContainer implements NbNodeObject {

    public static final int PROPERTY_COUNT = 3;

    /** Creates a new instance of Parameter */
    public FunctionException(String nameValue, String pkgValue) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_PACKAGE,
            PROPERTY_CONTAINER_PARENT_EXCPTION,
        });
        initializeProperties(nameValue, pkgValue);
    }

    private void initializeProperties(String nameValue, String pkgValue) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Name_Tooltip"),
                this, null);
        properties[PROPERTY_Package] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PACKAGE, 
                pkgValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Type_Tooltip"),
                this, null);
        properties[PROPERTY_ParentException] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PARENT_EXCPTION, 
                "com.sun.star.uno.Exception", 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Type_Tooltip"),
                this, null); // NOI18N
    }
    
    public String getDisplayName() {
        try {
            return getSimpleProperty(PROPERTY_CONTAINER_PACKAGE).concat(".").concat(
                getSimpleProperty(PROPERTY_CONTAINER_NAME)); // NOI18N
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return "unkwon display name"; // NOI18N
    }

    public OpenOfficeOrgProperty getProperty(int propertyIndex) {
        return properties[propertyIndex];
    }
    
    public NbNodeObject[] getAllSubObjects() {
        Vector<Object> nodeObjects = new Vector<Object>();
        // first direct properties
        NbNodeObject[] subs = new NbNodeObject[PROPERTY_COUNT];
        subs[0] = (NbNodeObject)properties[PROPERTY_Name];
        subs[1] = (NbNodeObject)properties[PROPERTY_Package];
        subs[2] = (NbNodeObject)properties[PROPERTY_ParentException];
        return subs;
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Vector<Object> v = new Vector<Object>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Package]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_ParentException]).createProperties(null, listener)));
        
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
        return null; //new Action[]{new MyAddParameterAction(), new MyDeleteParameterAction()};
    }

    public boolean hasActions(int type) {
        return false;
    }

    public NbNodeObject getParent() {
        return null;
    }

    public int getType() {
        return NbNodeObject.EXCEPTION_TYPE;
    }
    
/*    private class MyAddParameterAction extends AbstractAction {
        int counter;
        public MyAddParameterAction() {
            putValue(NAME, NbBundle.getMessag("Add Parameter"));
        }
        public void actionPerformed(ActionEvent e) {
            addinAction.addParameterAction();
        }
    }
    private class MyDeleteParameterAction extends AbstractAction {
        public MyDeleteParameterAction() {
            putValue(NAME, NbBundle.getMessag("Delete Parameter"));
        }
        public void actionPerformed(ActionEvent e) {
            addinAction.deleteActions();
        }
    }
*/    
}
