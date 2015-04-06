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

package org.openoffice.extensions.util.datamodel.properties;

import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public class OpenOfficeOrgBooleanProperty implements OpenOfficeOrgProperty, NbNodeObject {//, PropertyChangeListener {
    
    String name;
    boolean value;
    
    private String description;
    private PropertyEditor editor;
    private NbNodeObject parent;
    private String longDescription;
    
    /** Creates a new instance of OpenOfficeOrgIconProperty */
    public OpenOfficeOrgBooleanProperty(String name, boolean value, String longDescription, NbNodeObject parent, PropertyEditor editor) {
        this.name = name;
        this.editor = editor;
        this.parent = parent;
        this.value = value;
        this.longDescription = longDescription;
    }

    public boolean isLocalized() {
        return false;
    }

    public String getPropertyName() {
        return name;
    }

    public void setPropertyName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    public String getValueForLanguage(int langID) throws UnknownOpenOfficeOrgLanguageIDException {
        return Boolean.toString(value);
    }

    public void removeValueForLanguage(int langID) throws UnknownOpenOfficeOrgLanguageIDException {
    }

    public void setValueForLanguage(int langID, String value) throws UnknownOpenOfficeOrgLanguageIDException {
        this.value = Boolean.valueOf(value);
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public String getDisplayName() {
        return name;
    }

    public NbNodeObject[] getAllSubObjects() {
        return null;
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Node.Property nameProp = new BooleanProperty();
        if (sheet != null) {
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
        }
        if (listener != null) {
            nameProp.getPropertyEditor().addPropertyChangeListener(listener);
        }
        return new Node.Property[]{nameProp};
    }

    public boolean hasActions(int type) {
        return false;
    }

    public Action[] getActions(boolean context) {
        return null;
    }

    public void setActions(BaseAction actions) {
    }

    public int getType() {
        return NbNodeObject.PROPERTY_TYPE; // own type for this??
    }

//    public void propertyChange(PropertyChangeEvent evt) {
//        toggleChange();
//    }
    
    public class BooleanProperty extends Node.Property<Boolean> {
        public BooleanProperty() {
            super(Boolean.class);
            setName(getPropertyName());
            this.setShortDescription(longDescription);
        }
        public Boolean getValue() {
            return Boolean.valueOf(value);
        }
        public void setValue(Boolean v) {
            if (Boolean.TRUE.equals(v))
                value = true;
            else 
                value = false;
        }
        public PropertyEditor getPropertyEditor() {
            return super.getPropertyEditor();
        }
        public boolean canRead() {
            return true;
        }
        public boolean canWrite() {
            return true;
        }
        public NbNodeObject getPropertyParent() {
            return getParent();
        }
    }    
}
