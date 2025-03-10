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
import java.util.Enumeration;
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
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class Struct extends PropertyContainer implements NbNodeObject {
    
    private OrderedContainer<Parameter> structTypes;
    
    public static final int PROPERTY_COUNT = 2;

    private boolean languageSupport;

    /** Creates a new instance of Function */
    public Struct(String name, String pkg) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_PACKAGE,
        });
        structTypes = new OrderedContainer<Parameter>();
        initializeProperties(name, pkg);
    }
    
    private void initializeProperties(String name, String pkg) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                name, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Name_Tooltip"),
                this, null);
        properties[PROPERTY_Package] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PACKAGE, 
                pkg, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Type_Tooltip"),
                this, null);
    }
    
    public Parameter getStructType(String name) {
        Enumeration keys = structTypes.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            Parameter p = (Parameter)structTypes.get(keys.nextElement());
            found = name.equals(p.getProperty(p.PROPERTY_Name));
            if (found) return p;
        }
        return null;
    }
    
    public Parameter addStructType() {
        return addStructType(null);
    }
    
    public Parameter addStructType(NbNodeObject topObject) {
        int position = 0;  // use 0 if topObject is function
        if (topObject == null) {  // if null append
            position = structTypes.size();
        }
        else if (topObject instanceof Parameter) {
            position = structTypes.getPositionFromObject((Parameter)topObject) + 1;
        }
        String name = "Type".concat(structTypes.getNextNumber("Type")); // NOI18N
        Parameter param = null;
        param = new Parameter(name, "int", this, null); // NOI18N
        addSetObject(name, param, position);
        return param;
    }
    
    public void removeStructType(String name) {
        Enumeration keys = structTypes.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Parameter p = (Parameter)structTypes.get(key);
            try {
                found = name.equals(p.getProperty(p.PROPERTY_Name).getValueForLanguage(-1));
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            }
            if (found) {
                structTypes.remove((String)key);
            }
        }
    }
    
    public void removeStructType(Parameter parameter) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(parameter)) {
                structTypes.remove(names[i]);
            }
        }
    }
    
    public String[] getAllSetObjectNames() {
        return structTypes.getKeysInOrder();
    }
    
    public Object getSetObject(String internalName) {
        return structTypes.get(internalName);
    }
    
    public void addSetObject(String internalName, Object setObject) {
        structTypes.put(internalName, (Parameter)setObject);
    }
    
    public void addSetObject(String internalName, Object setObject, int position) {
        structTypes.insertElementAt(internalName, (Parameter)setObject, position);
    }
    
    public String getDisplayName() {
        try {
            return properties[PROPERTY_Package].getValueForLanguage(-1).concat(
                    ".").concat(properties[PROPERTY_Name].getValueForLanguage(-1)); // NOI18N
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return ""; // NOI18N
    }
    
    public NbNodeObject[] getAllSubObjects() {
        Vector<Object> nodeObjects = new Vector<Object>();
        // first direct properties
        nodeObjects.add(properties[PROPERTY_Name]);
        nodeObjects.add(properties[PROPERTY_Package]);
        // then parameters
        String[] keys = structTypes.getKeysInOrder();
        for(int i=0; i<keys.length; i++) {
            nodeObjects.add(structTypes.get(keys[i]));
        }
        return (NbNodeObject[])nodeObjects.toArray(new NbNodeObject[nodeObjects.size()]);
    }
    
    public OpenOfficeOrgProperty getProperty(int propertyIndex) {
        return properties[propertyIndex];
    }
    
    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Node.Property[]nameProp = new Node.Property[2];
        nameProp[0] = ((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)[0];
        nameProp[1] = ((NbNodeObject)properties[PROPERTY_Package]).createProperties(null, listener)[0];
        if (sheet != null) {
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
        }
        return nameProp;
    }
    
    public boolean hasActions(int type) {
        return false;
    }
    
    public Action[] getActions(boolean b) {
        return null;
//            new Action[]{new MyNewFunctionAction(), new MyDeleteFunctionAction(),
//        null, new MyAddParameterAction()};
    }
    
    /**
     * set the aczion for context menus
     */
    public void setActions(BaseAction baseAction) {
    }
    
    public NbNodeObject getParent() {
        return null;
    }

    public int getType() {
        return NbNodeObject.STRUCT_TYPE;
    }
    
//    private class MyNewFunctionAction extends AbstractAction {
//        public MyNewFunctionAction() {
//            putValue(NAME, "Add Function");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.addFunctionAction();
//        }
//    }
//    private class MyDeleteFunctionAction extends AbstractAction {
//        public MyDeleteFunctionAction() {
//            putValue(NAME, "Delete Function");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.deleteActions();
//        }
//    }
//    public class MyAddParameterAction extends AbstractAction {
//        public MyAddParameterAction() {
//            putValue(NAME, "Add Parameter");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.addParameterAction();
//        }
//    }
}
