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
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class Interface extends PropertyContainer implements OpenOfficeOrgMultiLanguageSet, NbNodeObject {
    
    int interfaceType;
    private OrderedContainer<Function> functions;
    private OrderedContainer<Interface> ifcAggregation;

    /** Creates a new instance of Interface */
    public Interface(String name, String pkg) {
        super( 
            new String[]{
                PROPERTY_CONTAINER_NAME, 
                PROPERTY_CONTAINER_PACKAGE,
                PROPERTY_CONTAINER_PARENT,
        });
        initializeProperties(name, pkg);
        functions = new OrderedContainer<Function>();
        ifcAggregation = new OrderedContainer<Interface>();
        interfaceType = NbNodeObject.INTERFACE_TYPE;
    }

    private void initializeProperties(String name, String pkg) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                name, 
                "", //NbBundle.getMessage(AddinWizardIterator.class, "TF_Interface_Name_Tooltip"),
                this, null);
        properties[PROPERTY_Package] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PACKAGE, 
                pkg, 
                "", //NbBundle.getMessage(AddinWizardIterator.class, "TF_Interface_Name_Tooltip"),
                this, null);
        properties[PROPERTY_Parent] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PARENT, 
                "com.sun.star.uno.XInterface", 
                "", //NbBundle.getMessage(AddinWizardIterator.class, "TF_Interface_Name_Tooltip"),
                this, null); // NOI18N
    }
    
    public void addAggregatedInterface(Interface ifc) {
        String internalName = "interface".concat(ifcAggregation.getNextNumber("interface")); // NOI18N
        ifcAggregation.put(internalName, ifc);
    }

    public Interface[] getAllAggregatedInterfaces() {
        return (Interface[])ifcAggregation.values().toArray(new Interface[ifcAggregation.size()]);
    }
    
    public Interface removeAggregatedInterface(String name) {
        String[] keys = ifcAggregation.getKeysInOrder();
        for (int i=0; i<keys.length; i++) {
            Interface ifc = (Interface)ifcAggregation.get(keys[i]);
            try {
                String propName = ifc.getSimpleProperty(Interface.PROPERTY_CONTAINER_NAME);
                if (propName.equals(name)) {
                    ifcAggregation.remove(keys[i]);
                    return ifc;
                }
            }
            catch (UnknownOpenOfficeOrgPropertyException e) {
                // ignore, go on
            }
        }
        return null;
    }
    
    public Function addFunction() {
        String internalName = "function".concat(functions.getNextNumber("function")); // NOI18N
        NbNodeObject[] subFunctions = this.getAllSubObjects();
        Function function = new Function(internalName, "int", this, null); // NOI18N
        function.addParameter();
        addSetObject(internalName, function);
        return function;
    }
    
    public void removeFunction(Function function) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(function)) {
                functions.remove(names[i]);
            }
        }
    }
    
    public void setType(int type) {
        interfaceType = type;
    }

    public int getType() {
        if (ifcAggregation.size() > 0) {
            return MULTI_INHERITANCE_INTERFACE_TYPE;
        }
        return interfaceType;
    }
    
    public String[] getAllSetObjectNames() {
        return functions.getKeysInOrder();
    }

    public Object getSetObject(String internalName) {
        return functions.get(internalName);
    }

    public void addSetObject(String internalName, Object setObject) {
        functions.put(internalName, (Function)setObject);
    }

    public void addLanguage(int languageID, String defaultText) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            ((Function)getSetObject(names[i])).addLanguage(languageID, defaultText);
        }
    }

    public void removeLanguage(int languageID) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            ((Function)getSetObject(names[i])).removeLanguage(languageID);
        }
    } 

    public NbNodeObject getParent() {
        return null;
    }

    public String getDisplayName() {
        try {
            return properties[PROPERTY_Package].getValueForLanguage(-1).concat(
                    ".").concat(properties[PROPERTY_Name].getValueForLanguage(-1)); // NOI18N
        }
        catch (Exception e) {
            return "no name"; // NOI18N
        }
    }

    public NbNodeObject[] getAllSubObjects() {
        Vector<NbNodeObject> nodeObjects = new Vector<NbNodeObject>();
        // first direct properties
        nodeObjects.add((NbNodeObject)properties[PROPERTY_Name]);
        nodeObjects.add((NbNodeObject)properties[PROPERTY_Package]);
        nodeObjects.add((NbNodeObject)properties[PROPERTY_Parent]);
        
        // then parameters
        String[] keys = functions.getKeysInOrder();
        for(int i=0; i<keys.length; i++) {
            nodeObjects.add(functions.get(keys[i]));
        }
        return nodeObjects.toArray(new NbNodeObject[nodeObjects.size()]);
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Vector<Node.Property> v = new Vector<Node.Property>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Package]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Parent]).createProperties(null, listener)));
        Node.Property[]nameProp = (Node.Property[])v.toArray(new Node.Property[v.size()]);
        if (sheet != null) {
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
        }
        return (Node.Property[])v.toArray(new Node.Property[v.size()]);
    }

    public boolean hasActions(int type) {
        return false;
    }

    public Action[] getActions(boolean context) {
        return null;
    }

    public void setActions(BaseAction actions) {
    }
}
