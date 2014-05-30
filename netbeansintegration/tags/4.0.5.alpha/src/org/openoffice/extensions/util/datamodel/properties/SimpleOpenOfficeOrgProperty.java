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
import org.openoffice.extensions.util.datamodel.*;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public class SimpleOpenOfficeOrgProperty implements OpenOfficeOrgProperty, NbNodeObject {
    
    String name;
    String value;
    
    private String description;
    private PropertyEditor editor;
    private NbNodeObject parent;
    boolean readOnly;
    
    public SimpleOpenOfficeOrgProperty(String name, String value, String longDescription, NbNodeObject parent, PropertyEditor editor) {
        this(name, value, longDescription, parent, editor, false);
    }

    /** Creates a new instance of SimpleOpenOfficeOrgProperty */
    public SimpleOpenOfficeOrgProperty(String name, String value, String longDescription, NbNodeObject parent, PropertyEditor editor, boolean readOnly) {
        this.name = name;
        this.value = value;
        this.editor = editor;
        this.readOnly = readOnly;
        this.parent = parent;
        this.description = longDescription;
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

    public String getValueForLanguage(int langID) {
        return value;
    }

    public void setValueForLanguage(int langID, String value) {
        this.value = value;
    }

    public void removeValueForLanguage(int langID) {
        this.value = null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return name;
    }

    public boolean hasActions(int type) {
        return false;
    }

    public Action[] getActions(boolean b) {
        return null;
    }

    public void setActions(BaseAction actions) {
    }

    public NbNodeObject[] getAllSubObjects() {
        return null;
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        
        Node.Property nameProp = new SimpleProperty();
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

    public NbNodeObject getParent() {
        return parent;
    }

    public int getType() {
        return NbNodeObject.PROPERTY_TYPE;
    }

    public class SimpleProperty extends Node.Property<String> {
        public SimpleProperty() {
            super(String.class);
            setName(getPropertyName());
            this.setShortDescription(description);
        }
        public String getValue() {
            return getValueForLanguage(-1);
        }
        public void setValue(String value) {
            setValueForLanguage(-1, (String)value);
        }
        @Override
        public PropertyEditor getPropertyEditor() {
            if (editor == null)
                return super.getPropertyEditor();
            return editor;
        }
        public boolean canRead() {
            return true;
        }
        public boolean canWrite() {
            return !readOnly;
        }
        public NbNodeObject getPropertyParent() {
            return getParent();
        }
    }    
}
