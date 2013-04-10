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
import java.util.Enumeration;
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
import org.openoffice.extensions.util.datamodel.actions.FunctionAction;
import org.openoffice.extensions.util.datamodel.actions.ParameterAction;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.ExceptionPropertyEditor;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;

/**
 *
 * @author sg128468
 */
public class Function extends PropertyContainer implements OpenOfficeOrgMultiLanguageSet, NbNodeObject {
    
    private OrderedContainer<Parameter> parameters;
    NbNodeObject parent;

    // executes the actual selected action for context menu
    private BaseAction baseActions;
    
    private boolean languageSupport;

    /** Creates a new instance of Function */
    public Function(String nameValue, String typeValue, NbNodeObject parent) {
        this(nameValue, typeValue, parent, new Integer[0]);
    }
    
    public Function(String nameValue, String typeValue, NbNodeObject parent, Integer[] languages) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_TYPE,
            PROPERTY_CONTAINER_DESCRIPTION,
            PROPERTY_CONTAINER_CATEGORY,
            PROPERTY_CONTAINER_DISPLAY_NAME,
            PROPERTY_CONTAINER_EXCEPTIONS,
            PROPERTY_CONTAINER_COMPATIBILITY_NAME,
        });
        this.parent = parent;
        parameters = new OrderedContainer<Parameter>();
        initializeProperties(nameValue, typeValue);
        if (languages == null) { // no language support
            this.languageSupport = false;
        }
        else if (languages.length > 0) {
            this.languageSupport = true;
            initializeLanguageProperties(nameValue, typeValue, languages[0].intValue());
            for (int i=1; i<languages.length; i++) {
                addLanguage(languages[i].intValue(), ""); // NOI18N
            }
        } else { // use default as fallback
            this.languageSupport = true;
            initializeLanguageProperties(nameValue, typeValue, LanguageDefinition.LANGUAGE_ID_en);
        }
    }
    
    private void initializeProperties(String nameValue, String typeValue) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Name_Tooltip"),
                this, null);
        // settings for the return type; different depending on usage
        boolean readOnly = false;
        PropertyEditor editor = null;  
        if (parent.getType() == NbNodeObject.ADDIN_TYPE) {
            editor = new AddInPropertyEditor(AddInPropertyEditor.RETURN_TYPE_EDITOR);
        }
        else {
            int length = UnoTypes.SIMPLE_TYPE_TAGS.length;
            String []tags = new String[length + 1];
            System.arraycopy(UnoTypes.SIMPLE_TYPE_TAGS, 0, tags, 0, length);
            tags[length] = "[]"; // NOI18N
            editor = new ComponentTypePropertyEditor(tags);
        }
        properties[PROPERTY_Type] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_TYPE, 
                typeValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Type_Tooltip"),
                this, editor, readOnly); // NOI18N
        properties[PROPERTY_Exceptions] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_EXCEPTIONS, 
                "", 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Type_Tooltip"),
                this, new ExceptionPropertyEditor()); // NOI18N
    }
    
    private void initializeLanguageProperties(String nameValue, String typeValue, int language) {
        if (parent.getType() == NbNodeObject.ADDIN_TYPE) {
            properties[PROPERTY_DisplayName] = new LocalizedOpenOfficeOrgProperty(
                    PROPERTY_CONTAINER_DISPLAY_NAME, 
                    "<".concat(nameValue).concat(">"), 
                    NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_DisplayName_Tooltip"),
                    this, language, null); // NOI18N
            properties[PROPERTY_Description] = new LocalizedOpenOfficeOrgProperty(
                    PROPERTY_CONTAINER_DESCRIPTION, 
                    NbBundle.getMessage(AddinWizardIterator.class, "PROP_Function_Description"), 
                    NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Description_Tooltip"),
                    this, language, null); // NOI18N
    //        properties[PROPERTY_CategoryDisplayName] = new LocalizedOpenOfficeOrgProperty(CATEGORY_DISPLAY_NAME, "add your localized category display name here.", this,
    //                language, null);
            properties[PROPERTY_CompatibilityName] = new LocalizedOpenOfficeOrgProperty(
                    PROPERTY_CONTAINER_COMPATIBILITY_NAME, 
                    "<".concat(nameValue).concat(">"), 
                    NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_CompatibilityName_Tooltip"),
                    this, language, null); // NOI18N
            // not xcu/xcs relevant, only for skeleton creation
            properties[PROPERTY_Category] = new SimpleOpenOfficeOrgProperty(
                    PROPERTY_CONTAINER_CATEGORY, 
                    "Add-In", 
                    NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_Category_Tooltip"),
                    this, new AddInPropertyEditor(AddInPropertyEditor.CATEGORY_EDITOR)); // NOI18N
        }
        else if (parent.getType() == NbNodeObject.ADDON_TYPE) {
            properties[PROPERTY_DisplayName] = new LocalizedOpenOfficeOrgProperty(
                    PROPERTY_CONTAINER_DISPLAY_NAME, 
                    "<".concat(nameValue).concat(">"), 
                    NbBundle.getMessage(AddinWizardIterator.class, "TF_Function_DisplayName_Tooltip"),
                    this, language, null); // NOI18N
        }
    }
    
    public void addLanguage(int languageID, String defaultText) {
        try {
            // set text on all parameters
            Enumeration keys = parameters.keys();
            while (keys.hasMoreElements()) {
                Parameter p = (Parameter)parameters.get(keys.nextElement());
                p.setLanguageWithDefaultText(languageID, defaultText);
            }
            // set text on all props here
            this.setLanguageWithDefaultText(languageID, defaultText);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    public void printLanguages() { // just for debugging
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, 
                "## Function " + properties[PROPERTY_Name].getValueForLanguage(-1)); // NOI18N
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "-> DisplayName: " + ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_DisplayName]).getUsedLanguageIndexes().length); // NOI18N
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "-> Description: " + ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_Description]).getUsedLanguageIndexes().length); // NOI18N
//        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "-> CategoryDisplayName: " + ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_CategoryDisplayName]).getUsedLanguageIndexes().length);ni 
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "-> CompatibiltyName: " + ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_CompatibilityName]).getUsedLanguageIndexes().length); // NOI18N
        
        Enumeration keys = parameters.keys();
        while (keys.hasMoreElements()) {
            Parameter p = (Parameter)parameters.get(keys.nextElement());
            p.printLanguages();
        }
    }
    
    public void removeLanguage(int languageID) {
        try {
            // remove language on all props here
            this.removeLanguageAndText(languageID);
            // set text on all parameters
            Enumeration keys = parameters.keys();
            while (keys.hasMoreElements()) {
                Parameter p = (Parameter)parameters.get(keys.nextElement());
                p.removeLanguageAndText(languageID);
            }
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    public Parameter getParameter(String name) {
        Enumeration keys = parameters.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            Parameter p = (Parameter)parameters.get(keys.nextElement());
            try {
                found = name.equals(p.getSimpleProperty(p.PROPERTY_CONTAINER_NAME));
            } catch (UnknownOpenOfficeOrgPropertyException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
            if (found) return p;
        }
        return null;
    }
    
    public Parameter addParameter() {
        return addParameter(null);
    }
    
    public Parameter addParameter(NbNodeObject topObject) {
        int position = 0;  // use 0 if topObject is function
        if (topObject == null) {  // if null append
            position = parameters.size();
        }
        else if (topObject instanceof Parameter) {
            position = parameters.getPositionFromObject((Parameter)topObject) + 1;
        }
        String name = "parameter".concat(parameters.getNextNumber("parameter")); // NOI18N
        Parameter param = null;
        if (this.languageSupport) {
            Integer[] indexes = ((LocalizedOpenOfficeOrgProperty)properties[PROPERTY_DisplayName]).getUsedLanguageIndexes();
            param = new Parameter(name, "int", this, indexes); // NOI18N
        }
        else {
            param = new Parameter(name, "int", this, null); // NOI18N
        }
        addSetObject(name, param, position);
        return param;
    }
    
    public void removeParameter(String name) {
        Enumeration<String> keys = parameters.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            String key = keys.nextElement();
            Parameter p = (Parameter)parameters.get(key);
            try {
                found = name.equals(p.getProperty(p.PROPERTY_Name).getValueForLanguage(-1));
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            }
            if (found) {
                parameters.remove(key);
            }
        }
    }
    
    public void removeParameter(Parameter parameter) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(parameter)) {
                parameters.remove(names[i]);
            }
        }
    }
    
    public String[] getAllSetObjectNames() {
        return parameters.getKeysInOrder();
    }
    
    public Object getSetObject(String internalName) {
        return parameters.get(internalName);
    }
    
    public void addSetObject(String internalName, Object setObject) {
        parameters.put(internalName, (Parameter)setObject);
    }
    
    public void addSetObject(String internalName, Object setObject, int position) {
        parameters.insertElementAt(internalName, (Parameter)setObject, position);
    }
    
    public String getDisplayName() {
        StringBuffer buf = new StringBuffer();
        try {
            buf.append(properties[PROPERTY_Type].getValueForLanguage(-1));
            buf.append(" ").append(properties[PROPERTY_Name].getValueForLanguage(-1)); // NOI18N
            buf.append("("); // NOI18N
            String[] paramNames = getAllSetObjectNames();
            for (int i=0; i<paramNames.length; i++) {
                if (i > 0) {
                    buf.append(", "); // NOI18N
                }
                Parameter param = (Parameter)getSetObject(paramNames[i]);
                // 2do: parametrize this:
                buf.append(param.getProperty(PROPERTY_Type).getValueForLanguage(-1)).append(" "); // NOI18N
                buf.append(param.getProperty(PROPERTY_Name).getValueForLanguage(-1));
            }
            buf.append(");"); // NOI18N
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            buf.append(ex.toString());
        }
        return buf.toString();
    }
    
    public NbNodeObject[] getAllSubObjects() {
        Vector<Object> nodeObjects = new Vector<Object>();
        // first direct properties
        nodeObjects.add(properties[PROPERTY_Name]);
        nodeObjects.add(properties[PROPERTY_Type]);
        nodeObjects.add(properties[PROPERTY_Exceptions]);
        if (getParent().getType() == NbNodeObject.ADDIN_TYPE) {
            nodeObjects.add(properties[PROPERTY_Category]);
//          nodeObjects.add(properties[PROPERTY_CategoryDisplayName]);
            nodeObjects.add(properties[PROPERTY_DisplayName]);
            nodeObjects.add(properties[PROPERTY_Description]);
            nodeObjects.add(properties[PROPERTY_CompatibilityName]);
        }
        // then parameters
        String[] keys = parameters.getKeysInOrder();
        for(int i=0; i<keys.length; i++) {
            nodeObjects.add(parameters.get(keys[i]));
        }
        return (NbNodeObject[])nodeObjects.toArray(new NbNodeObject[nodeObjects.size()]);
    }
    
//    public void fireDataChangeEvent() {
//        parent.fireDataChangeEvent();
//    }
    
    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        int parentType = getParent().getType();
        Vector<Object> v = new Vector<Object>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Type]).createProperties(null, listener)));
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Exceptions]).createProperties(null, listener)));
        if (parentType == NbNodeObject.ADDIN_TYPE) {
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Category]).createProperties(null, listener)));
        }
        Node.Property[]nameProp = (Node.Property[])v.toArray(new Node.Property[v.size()]);
        if (sheet != null) {
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
            
//            set = sheet.createPropertiesSet();
//            set.setName(((NbNodeObject)properties[PROPERTY_CategoryDisplayName]).getDisplayName());
//            set.setDisplayName(set.getName().concat(" - Localized Property"));
//            set.put(((NbNodeObject)properties[PROPERTY_CategoryDisplayName]).createProperties(null, listener));
//            sheet.put(set);

            if (parentType == NbNodeObject.ADDIN_TYPE) {
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

                set = sheet.createPropertiesSet();
                set.setName(((NbNodeObject)properties[PROPERTY_CompatibilityName]).getDisplayName());
                set.setDisplayName(set.getName().concat(" - ").concat( // NOI18N
                        NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty")));
                set.put(((NbNodeObject)properties[PROPERTY_CompatibilityName]).createProperties(null, listener));
                sheet.put(set);
            }
            else if (parentType == NbNodeObject.ADDON_TYPE) {
                set = sheet.createPropertiesSet();
                set.setName(((NbNodeObject)properties[PROPERTY_DisplayName]).getDisplayName());
                set.setDisplayName(set.getName().concat(" - ").concat( // NOI18N
                        NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty")));
                set.put(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener));
                sheet.put(set);
            }
        }
        if (parentType == NbNodeObject.ADDIN_TYPE) {
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Description]).createProperties(null, listener)));
//          v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_CategoryDisplayName]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_CompatibilityName]).createProperties(null, listener)));
        }
        else if (parentType == NbNodeObject.ADDON_TYPE) {
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener)));
        }
        return (Node.Property[])v.toArray(new Node.Property[v.size()]);
    }
    
    public boolean hasActions(int type) {
        return baseActions != null;
    }
    
    public Action[] getActions(boolean b) {
        Action[] rightMouseClickAction = null;
        if (getParent().getType() == NbNodeObject.ADDIN_TYPE) {
            rightMouseClickAction = new Action[]{new MyNewFunctionAction(), new MyDeleteFunctionAction(),
                null, new MyAddParameterAction()};
        }
        else if (getParent().getType() == NbNodeObject.ADDON_TYPE) {
            rightMouseClickAction = new Action[]{new MyNewFunctionAction(), new MyDeleteFunctionAction()};
        }
        return rightMouseClickAction;
    }
    
    /**
     * set the actions for all operations
     */
    public void setActions(BaseAction baseActions) {
        this.baseActions = baseActions;
    }
    
    public NbNodeObject getParent() {
        return parent;
    }

    public int getType() {
        return NbNodeObject.FUNCTION_TYPE;
    }
    
    private class MyNewFunctionAction extends AbstractAction {
        public MyNewFunctionAction() {
            putValue(NAME, NbBundle.getMessage(LocalizedOpenOfficeOrgProperty.class, "LBL_AddFunction"));
        }
        public void actionPerformed(ActionEvent e) {
            ((FunctionAction)baseActions).addFunctionAction();
        }
    }
    private class MyDeleteFunctionAction extends AbstractAction {
        public MyDeleteFunctionAction() {
            putValue(NAME, NbBundle.getMessage(LocalizedOpenOfficeOrgProperty.class, "LBL_DeleteFunction"));
        }
        public void actionPerformed(ActionEvent e) {
            baseActions.deleteAction();
        }
    }
    public class MyAddParameterAction extends AbstractAction {
        public MyAddParameterAction() {
            putValue(NAME, NbBundle.getMessage(Function.class, "LBL_AddParameter"));
        }
        public void actionPerformed(ActionEvent e) {
            ((ParameterAction)baseActions).addParameterAction();
        }
    }
}
