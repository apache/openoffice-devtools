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
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

public class Constants extends PropertyContainer implements NbNodeObject {
    
    public static final int PROPERTY_COUNT = 2;

    public Constants(String nameValue, String pkgValue) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_PACKAGE,
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
    }
    
    public NbNodeObject getParent() {
        return null;
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

    public NbNodeObject[] getAllSubObjects() {
        Vector<Object> nodeObjects = new Vector<Object>();
        // first direct properties
        NbNodeObject[] subs = new NbNodeObject[PROPERTY_COUNT];
        subs[0] = (NbNodeObject)properties[PROPERTY_Name];
        subs[1] = (NbNodeObject)properties[PROPERTY_Package];
        return subs;
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Vector<Object> v = new Vector<Object>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Package]).createProperties(null, listener)));
        
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
        // empty
    }

    public int getType() {
        return NbNodeObject.CONSTANTS_TYPE;
    }
}
