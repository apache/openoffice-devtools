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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.util.datamodel.*;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.actions.LanguageAction;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;

/**
 *
 * @author sg128468
 */
public class LocalizedOpenOfficeOrgProperty implements OpenOfficeOrgProperty, NbNodeObject {
    
    OrderedContainer<String>values;
    String name;
    String description;
    // description for tool tips etc.
    String longDescription;

    private PropertyEditor editor;
    private NbNodeObject parent;

    private LanguageAction langActions;
    
    /** Creates a new instance of LocalizedOpenOfficeOrgProperty */
    public LocalizedOpenOfficeOrgProperty(String name, String value, String longDescription, NbNodeObject parent, int languageID, PropertyEditor editor) {
        values = new OrderedContainer<String>();
        setValueForLanguage(languageID, value);
        this.parent = parent;
        this.name = name;
        this.editor = editor;
        this.longDescription = longDescription;
    }
    
    public boolean isLocalized() {
        return true;
    }

    public String getPropertyName() {
        return name;
    }

    public Integer[] getUsedLanguageIndexes() {
        String[] keys = values.getKeysInOrder();
        Integer[] intKeys = new Integer[keys.length];
        for (int i = 0; i < keys.length; i++) {
            intKeys[i] = new Integer(keys[i]);
        }
        return intKeys;
    }
    
    public void setPropertyName(String name) {
        this.name = name;
    }

    public String getValueForLanguage(int langID) {
        String key = Integer.toString(langID);
        return values.get(key);
    }

    public void setValueForLanguage(int langID, String value) {
        String key = Integer.toString(langID);
        this.values.put(key, value);
    }

    public void removeValueForLanguage(int langID) {
        String key = Integer.toString(langID);
        this.values.remove(key);
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

    public NbNodeObject[] getAllSubObjects() {
        return null;
    }

//    public void fireDataChangeEvent() {
//    }
    
    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Integer[] indexes = getUsedLanguageIndexes();
        Node.Property[] nameProp = new Node.Property[indexes.length];
        for (int i=0; i<indexes.length; i++) {
            nameProp[i] = new LocalizedProperty(indexes[i].intValue());
            if (listener != null) {
                nameProp[i].getPropertyEditor().addPropertyChangeListener(listener);
            }
        }
        if (sheet != null) {
            Sheet.Set set = Sheet.createPropertiesSet();
            set.setName(getDisplayName());
            set.setDisplayName(getDisplayName().concat(" - ").concat( // NOI18N
                    NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty")));
            set.put(nameProp);
            sheet.put(set);
        }
        return nameProp;
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public boolean hasActions(int type) {
        // not type specific
        return true;
    }

    public Action[] getActions(boolean b) {
        return new Action[]{new MyNewLanguageAction(), new MyDeleteLanguageAction()};
    }

    public void setActions(BaseAction langActions) {
        this.langActions = (LanguageAction)langActions;
    }
    
    public int getType() {
        return NbNodeObject.LOCALIZED_PROPERTY_TYPE;
    }
    
    private class MyNewLanguageAction extends AbstractAction {
        public MyNewLanguageAction() {
            putValue(NAME, NbBundle.getMessage(LocalizedOpenOfficeOrgProperty.class, "LBL_AddLanguage"));
        }
        public void actionPerformed(ActionEvent e) {
            langActions.addLanguageAction();
        }
    }
    private class MyDeleteLanguageAction extends AbstractAction {
        public MyDeleteLanguageAction() {
            putValue(NAME, NbBundle.getMessage(LocalizedOpenOfficeOrgProperty.class, "LBL_DeleteLanguage"));
        }
        public void actionPerformed(ActionEvent e) {
            langActions.deleteLanguageAction();
        }
    }
    
    class LocalizedProperty extends Node.Property<String> {
        int languageID;
//        @SuppressWarnings(value = "unchecked")
        public LocalizedProperty(int languageID) {
            super(String.class);
            this.languageID = languageID;
            setName(LanguageDefinition.getLanguageNameForId(languageID));
            this.setShortDescription(longDescription);
        }
        public Object getValue(String attributeName) {
            if ("LocalizedOpenOfficeOrgProperty".equals(attributeName)) { // NOI18N
                return LocalizedOpenOfficeOrgProperty.this;
            }
            return super.getValue(attributeName);
        }
        public String getValue() {
            return getValueForLanguage(this.languageID);
        }
        public void setValue(String value) {
            setValueForLanguage(this.languageID, value);
        }

        public boolean canRead() {
            return true;
        }

        public boolean canWrite() {
            return true;
        }
        
        public PropertyEditor getPropertyEditor() {
            if (editor == null) {
                return super.getPropertyEditor();
            }
            return editor;
        }
   }
}
