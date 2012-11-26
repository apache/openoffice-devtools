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

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.projecttemplates.calcaddin.datamodel.node.AddInPropertyEditor;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypePropertyEditor;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.actions.ParameterAction;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;

/**
 *
 * @author sg128468
 */
public class Parameter extends PropertyContainer implements NbNodeObject {

    private NbNodeObject parent;

    private BaseAction baseAction;

    private boolean languageSupport;

    /** Creates a new instance of Parameter */
    public Parameter(String nameValue, String typeValue, NbNodeObject parent) {
        this(nameValue, typeValue, parent, null);
    }

    public Parameter(String nameValue, String typeValue, NbNodeObject parent, Integer[] languages) {
        super(new String[]{
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_TYPE,
            PROPERTY_CONTAINER_DISPLAY_NAME,
            PROPERTY_CONTAINER_DESCRIPTION,
        });
        this.parent = parent;
        if (languages == null) {
            this.languageSupport = false;
            initializeProperties(nameValue, typeValue);
        }
        else if (languages.length > 0) {
            this.languageSupport = true;
            initializeProperties(nameValue, typeValue, languages[0].intValue());
            for (int i=1; i<languages.length; i++) {
                try {
                    setLanguageWithDefaultText(languages[i].intValue(), ""); // NOI18N
                } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
                    // ignore exception: language simply is not filled
                }
            }
        }
        else { // use default as fallback
            this.languageSupport = true;
            initializeProperties(nameValue, typeValue, LanguageDefinition.LANGUAGE_ID_en); 
        }
    }

    private void initializeProperties(String nameValue, String typeValue) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Name_Tooltip"),
                this, null);
        PropertyEditor editor = null;  // 2do: this will not work for reuse as AddOn
        if (languageSupport)  // 2do: replace this by parent type
            editor = new AddInPropertyEditor(AddInPropertyEditor.PARAMETER_EDITOR);
        else 
            if (parent.getType() == NbNodeObject.POLY_STRUCT_TYPE) {
                PolyStruct polyStruct = (PolyStruct)parent;
                int length = UnoTypes.SIMPLE_TYPE_TAGS.length - 1;
                String[] templateNames = polyStruct.getTemplateTypeNames();
                // add the template types, e.g. T1, T2, ... to the types
                for (int i = 0; i < templateNames.length; i++) {
                    String name = templateNames[i];
                    TemplateType type = polyStruct.getTemplateType(name);
                    try {
                        templateNames[i] = type.getSimpleProperty(type.PROPERTY_CONTAINER_TYPE);
                    } catch (UnknownOpenOfficeOrgPropertyException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
                String[] tags = new String[length + templateNames.length + 1];
                System.arraycopy(UnoTypes.SIMPLE_TYPE_TAGS, 1, tags, 0, length);
                System.arraycopy(templateNames, 0, tags, length, templateNames.length);
                tags[tags.length - 1] = "[]"; // NOI18N
                editor = new ComponentTypePropertyEditor(tags);
            }
            else {
                editor = new ComponentTypePropertyEditor();
            }
        
        properties[PROPERTY_Type] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_TYPE, 
                typeValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Type_Tooltip"),
                this, editor);
    }
    
    private void initializeProperties(String nameValue, String typeValue, int language) {
        // get the following from xcs file.
        properties[PROPERTY_DisplayName] = new LocalizedOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_DISPLAY_NAME, 
                "<".concat(nameValue).concat(">"),  // NOI18N
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_DisplayName_Tooltip"),
                this, language, null);
        properties[PROPERTY_Description] = new LocalizedOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_DESCRIPTION, 
                NbBundle.getMessage(AddinWizardIterator.class, "PROP_Function_Description"), 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Description_Tooltip"),
                this, language, null);
        // not xcu/xcs relevant, only for skeleton creation
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Name_Tooltip"),
                this, null);
        properties[PROPERTY_Type] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_TYPE, 
                typeValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Type_Tooltip"),
                this, new AddInPropertyEditor(AddInPropertyEditor.PARAMETER_EDITOR));
    }
    
    public void printLanguages() {
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "#### Parameter " + properties[PROPERTY_Name].getValueForLanguage(-1)); // NOI18N
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "  -> DisplayName: " + ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_DisplayName]).getUsedLanguageIndexes().length); // NOI18N
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "  -> Description: " + ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_Description]).getUsedLanguageIndexes().length); // NOI18N
    }
    
    public String getDisplayName() {
        try {
            return properties[PROPERTY_Name].getValueForLanguage(-1);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return "unkown parameter"; // NOI18N
    }

    public OpenOfficeOrgProperty getProperty(int propertyIndex) {
        return properties[propertyIndex];
    }
    

    public NbNodeObject[] getAllSubObjects() {
        Vector<Object> nodeObjects = new Vector<Object>();
        // first direct properties
        nodeObjects.add(properties[PROPERTY_Name]);
        nodeObjects.add(properties[PROPERTY_Type]);
        if (this.languageSupport) {
            nodeObjects.add(properties[PROPERTY_DisplayName]);
            nodeObjects.add(properties[PROPERTY_Description]);
        }
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

            if (this.languageSupport) {
                set = sheet.createPropertiesSet();
                set.setName(((NbNodeObject)properties[PROPERTY_DisplayName]).getDisplayName());
                set.setDisplayName(set.getName().concat(" - ").concat( // NOI18N
                        NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty")));

                set.put(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener));
                sheet.put(set);

                set = sheet.createPropertiesSet();
                set.setName(((NbNodeObject)properties[PROPERTY_Description]).getDisplayName());
                set.setDisplayName(set.getName().concat(" - ").concat( // NOI18N
                        NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty")));
                set.put(((NbNodeObject)properties[PROPERTY_Description]).createProperties(null, listener));
                sheet.put(set);
            }
        }
        if (this.languageSupport) {
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Description]).createProperties(null, listener))); 
        }
        return (Node.Property[])v.toArray(new Node.Property[v.size()]);
    }
    
    /**
     * set the action for context menus
     */
    public void setActions(BaseAction baseAction) {
        this.baseAction = baseAction;
    }
    
    public Action[] getActions(boolean b) {
        return new Action[]{new MyAddParameterAction(), new MyDeleteParameterAction()};
    }

    public boolean hasActions(int type) {
        return true;
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public int getType() {
        return NbNodeObject.PARAMETER_TYPE;
    }
    
    private class MyAddParameterAction extends AbstractAction {
        int counter;
        public MyAddParameterAction() {
            putValue(NAME, NbBundle.getMessage(LocalizedOpenOfficeOrgProperty.class, "LBL_AddParameter"));
        }
        public void actionPerformed(ActionEvent e) {
            ((ParameterAction)baseAction).addParameterAction();
        }
    }
    private class MyDeleteParameterAction extends AbstractAction {
        public MyDeleteParameterAction() {
            putValue(NAME, NbBundle.getMessage(LocalizedOpenOfficeOrgProperty.class, "LBL_DeleteParameter"));
        }
        public void actionPerformed(ActionEvent e) {
            baseAction.deleteAction();
        }
    }
    
}
