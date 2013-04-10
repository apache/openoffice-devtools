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
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentInterfaceTypePropertyEditor;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class Service extends PropertyContainer implements OpenOfficeOrgMultiLanguageSet, NbNodeObject {
    
    private Interface ifc;
    
    /** Creates a new instance of Interface */
    public Service(String name, String pkg) {
        super(new String [] {
            PROPERTY_CONTAINER_NAME, 
            PROPERTY_CONTAINER_PACKAGE, 
            PROPERTY_CONTAINER_INTERFACE
        });
        initializeProperties(name, pkg);
        ifc = null;
    }

    private void initializeProperties(String name, String pkg) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                name, 
                "", //NbBundle.getMessage(AddinWizardIterator.class, "TF_Interface_Name_Tooltip"),
                this, null); // NOI18N
        properties[PROPERTY_Package] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_PACKAGE, 
                pkg, 
                "", //NbBundle.getMessage(AddinWizardIterator.class, "TF_Interface_Name_Tooltip"),
                this, null); // NOI18N
        PropertyEditor editor = new ComponentInterfaceTypePropertyEditor();
        properties[PROPERTY_Interface] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_INTERFACE, 
                "", 
                "", //NbBundle.getMessage(AddinWizardIterator.class, "TF_Interface_Name_Tooltip"),
                this, editor); // NOI18N
    }
    
    public Interface addInterface() {
        String pkg = NbBundle.getMessage(ConfigurationSettings.class, "default.package"); // NOI18N
        try {
            pkg = getSimpleProperty(PROPERTY_CONTAINER_PACKAGE);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            ex.printStackTrace();
        }
        String ifcName = pkg.concat(".").concat("XInterface"); // NOI18N
        ifc = new Interface(ifcName, pkg);
        ifc.addFunction();
        try {
            setSimpleProperty(PROPERTY_CONTAINER_INTERFACE, ifcName);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            ex.printStackTrace();
        }
        return ifc;
    }
    
    public void removeInterface(Interface ifc) {
        try {
            setSimpleProperty(PROPERTY_CONTAINER_INTERFACE, ""); // NOI18N
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            ex.printStackTrace();
        }
        ifc = null;
    }
    
    public int getType() {
        return SERVICE_TYPE;
    }
    
    public String[] getAllSetObjectNames() {
        try {
            return new String[]{getSimpleProperty(PROPERTY_CONTAINER_INTERFACE)};
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return new String[0];
    }

    public Object getSetObject(String name) {
        try {
            String ifcName = getSimpleProperty(PROPERTY_CONTAINER_INTERFACE);
            if (ifcName.equals(name))
                return ifc;
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return null;
    }

    public Object getSetObject() {
        return ifc;
    }

    public void addSetObject(String name, Object setObject) {
        try {
            setSimpleProperty(PROPERTY_CONTAINER_INTERFACE, name);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            ex.printStackTrace();
        }
        ifc = (Interface)setObject;
    }

    public void addLanguage(int languageID, String defaultText) {
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            ((Interface)obj[i]).addLanguage(languageID, defaultText);
        }
    }

    public void removeLanguage(int languageID) {
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            ((Interface)obj[i]).removeLanguage(languageID);
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
        nodeObjects.add((NbNodeObject)properties[PROPERTY_Interface]);
        
        return (NbNodeObject[])nodeObjects.toArray(new NbNodeObject[nodeObjects.size()]);
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Vector<Node.Property> v = new Vector<Node.Property>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Package]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Interface]).createProperties(null, listener)));
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
