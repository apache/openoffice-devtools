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
public class IdlEnumeration extends PropertyContainer implements OpenOfficeOrgPropertyContainer, NbNodeObject {
    
    private OrderedContainer<IdlEnum> enums;
    
    private boolean languageSupport;

    public IdlEnumeration(String nameValue, String pkgValue) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_PACKAGE,
        });
        enums = new OrderedContainer<IdlEnum>();
        initializeProperties(nameValue, pkgValue);
    }
    
    private void initializeProperties(String nameValue, String pkgValue) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Name_Tooltip"),
                this, null);
        properties[PROPERTY_Package] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PACKAGE, 
                pkgValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Name_Tooltip"),
                this, null);
    }
    
    public IdlEnum getEnum(String name) {
        Enumeration keys = enums.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            IdlEnum p = enums.get(keys.nextElement());
            try {
                found = name.equals(p.getProperty(PROPERTY_Name).getValueForLanguage(-1));
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
            if (found) return p;
        }
        return null;
    }
    
    public IdlEnum addEnum() {
        return addEnum(null);
    }
    
    public IdlEnum addEnum(NbNodeObject topObject) {
        int position = 0;  // use 0 if topObject is function
        if (topObject == null) {  // if null append
            position = enums.size();
        }
        else if (topObject instanceof IdlEnum) {
            position = enums.getPositionFromObject((IdlEnum)topObject) + 1;
        }
        String name = "enum".concat(enums.getNextNumber("enum")); // NOI18N
        IdlEnum enm = new IdlEnum(name, this);
        addSetObject(name, enm, position);
        return enm;
    }
    
    public void removeEnum(String name) {
        Enumeration<String> keys = enums.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            String key = keys.nextElement();
            IdlEnum p = enums.get(key);
            try {
                found = name.equals(p.getProperty(PROPERTY_Name).getValueForLanguage(-1));
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            }
            if (found) {
                enums.remove(key);
            }
        }
    }
    
    public void removeEnum(IdlEnum enm) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(enm)) {
                enums.remove(names[i]);
            }
        }
    }
    
    public String[] getAllSetObjectNames() {
        return enums.getKeysInOrder();
    }
    
    public Object getSetObject(String internalName) {
        return enums.get(internalName);
    }
    
    public void addSetObject(String internalName, Object setObject) {
        if (setObject instanceof IdlEnum)
            enums.put(internalName, (IdlEnum)setObject);
    }
    
    public void addSetObject(String internalName, Object setObject, int position) {
        if (setObject instanceof IdlEnum)
            enums.insertElementAt(internalName, (IdlEnum)setObject, position);
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
        // then enums
        String[] keys = enums.getKeysInOrder();
        for(int i=0; i<keys.length; i++) {
            nodeObjects.add(enums.get(keys[i]));
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
//        return new Action[]{new MyNewFunctionAction(), new MyDeleteFunctionAction(),
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
        return NbNodeObject.ENUMERATION_TYPE;
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
