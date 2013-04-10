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

package org.openoffice.extensions.projecttemplates.addon.datamodel;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.addon.AddOnWizardIterator;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.MultiLanguage;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 */
public class Command extends PropertyContainer implements MultiLanguage, NbNodeObject {
    
    int selectedLanguage;
    
    private NbNodeObject parent;
    private BaseAction baseActions;

    private static final String HIRES_BIG = NbBundle.getMessage(AddOnWizardIterator.class, "TF_CommandIconHighContrastBig"); // NOI18N
    private static final String HIRES_SMALL = NbBundle.getMessage(AddOnWizardIterator.class, "TF_CommandIconHighContrastSmall"); // NOI18N
    private static final String LOWRES_BIG = NbBundle.getMessage(AddOnWizardIterator.class, "TF_CommandIconLowContrastBig"); // NOI18N
    private static final String LOWRES_SMALL = NbBundle.getMessage(AddOnWizardIterator.class, "TF_CommandIconLowContrastSmall"); // NOI18N
    
    /** Creates a new instance of Command */
    public Command(String name, NbNodeObject parent, Integer[] languages) {
        super(new String[]{
            PROPERTY_CONTAINER_NAME,
            PROPERTY_CONTAINER_DISPLAY_NAME,
            PROPERTY_CONTAINER_CONTEXTS[0],
            PROPERTY_CONTAINER_CONTEXTS[1],
            PROPERTY_CONTAINER_CONTEXTS[2],
            PROPERTY_CONTAINER_CONTEXTS[3],
            PROPERTY_CONTAINER_CONTEXTS[4],
            PROPERTY_CONTAINER_CONTEXTS[5],
            PROPERTY_CONTAINER_CONTEXTS[6],
            PROPERTY_CONTAINER_CONTEXTS[7],
            PROPERTY_CONTAINER_CONTEXTS[8],
            PROPERTY_CONTAINER_ICON_HIRES_BIG,
            PROPERTY_CONTAINER_ICON_HIRES_SMALL,
            PROPERTY_CONTAINER_ICON_LOWRES_BIG,
            PROPERTY_CONTAINER_ICON_LOWRES_SMALL,
        });
        this.parent = parent;
        selectedLanguage = -1;
        if (languages == null || languages.length == 0) 
            languages = new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)};
        initializeProperties(name, languages[0].intValue());
        for (int i=1; i<languages.length; i++) {
            String commandName = new StringBuffer("<").append(name).append(">").toString(); // NOI18N
            addLanguage(languages[i].intValue(), commandName);
        }
    }
    
    private void initializeProperties(String name, int language) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                name, 
                NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_Name_Tooltip"), // NOI18N
                this, null);
        for (int i=0; i<PROPERTY_Context.length; i++) {
            properties[PROPERTY_Context[i]] = new OpenOfficeOrgBooleanProperty(
                    PROPERTY_CONTAINER_CONTEXTS[i],
                    false, 
                    NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_Context_Tooltip"),  // NOI18N
                    this, null);
        }
        // not a language property and only used in an AddOn, so create it here
        properties[PROPERTY_Icon_Hires_Big] = new OpenOfficeOrgIconProperty(
                PROPERTY_CONTAINER_ICON_HIRES_BIG, 
                null, // icon path...
                NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_Icon_Tooltip"),  // NOI18N
                this, new IconCustomEditor());
        properties[PROPERTY_Icon_Hires_Small] = new OpenOfficeOrgIconProperty(
                PROPERTY_CONTAINER_ICON_HIRES_SMALL, 
                null, // icon path...
                NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_Icon_Tooltip"), // NOI18N
                this, new IconCustomEditor());
        properties[PROPERTY_Icon_Lowres_Big] = new OpenOfficeOrgIconProperty(
                PROPERTY_CONTAINER_ICON_LOWRES_BIG, 
                null, // icon path...
                NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_Icon_Tooltip"), // NOI18N
                this, new IconCustomEditor());
        properties[PROPERTY_Icon_Lowres_Small] = new OpenOfficeOrgIconProperty(
                PROPERTY_CONTAINER_ICON_LOWRES_SMALL, 
                null, // icon path...
                NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_Icon_Tooltip"), // NOI18N
                this, new IconCustomEditor());
        properties[PROPERTY_DisplayName] = new LocalizedOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_DISPLAY_NAME, 
                "<".concat(name).concat(">"),  // NOI18N
                NbBundle.getMessage(AddOnWizardIterator.class, "TF_Command_DisplayName_Tooltip"), // NOI18N
                this, language, null);
    }
    
    public NbNodeObject getParent() {
        return parent;
    }
    
    public void setParent(NbNodeObject parent) throws IllegalArgumentException {
        if (parent == null) throw new IllegalArgumentException(NbBundle.getMessage(AddOnWizardIterator.class, "TF_CommandErrorMessage")); // NOI18N
        this.parent = parent;
    }
    
    public String getDisplayName() {
        String retValue = null;
        OpenOfficeOrgProperty prop = getProperty(this.PROPERTY_DisplayName);
        if (prop.isLocalized()) {
            // try selected language...
            if (selectedLanguage != -1) {
                retValue = ((LocalizedOpenOfficeOrgProperty)prop).getValueForLanguage(selectedLanguage);
            }
            // fallback English...
            if (retValue == null || retValue.length() == 0) {
                retValue = ((LocalizedOpenOfficeOrgProperty)prop).getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
            }
            // take first that comes to mind
            boolean noValueFound = (retValue == null || retValue.length() == 0);
            Integer[] languageIndexes = ((LocalizedOpenOfficeOrgProperty)prop).getUsedLanguageIndexes();
            int index = 0;
            while (languageIndexes != null && noValueFound && index < languageIndexes.length) {
                int selected = languageIndexes[index++].intValue();
                retValue = ((LocalizedOpenOfficeOrgProperty)prop).getValueForLanguage(selected);
                noValueFound = (retValue == null || retValue.length() == 0);
            }
        }
        return retValue==null?"":retValue; // NOI18N
    }
    
    public NbNodeObject[] getAllSubObjects() {
        return new NbNodeObject[0];
    }
    
    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener, boolean createCommandProps) {
        Vector<Node.Property> v = new Vector<Node.Property>();
        v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        if (sheet != null) {
            if (createCommandProps) {
                Node.Property[]nameProp = (Node.Property[])v.toArray(new Node.Property[v.size()]);
                Sheet.Set set = Sheet.createPropertiesSet();
                set.put(nameProp);
                sheet.put(set);

                set = Sheet.createPropertiesSet();
                set.setName("Icons"); // NOI18N
                set.setDisplayName(set.getName());
                set.put(((NbNodeObject)properties[PROPERTY_Icon_Hires_Big]).createProperties(null, listener));
                set.put(((NbNodeObject)properties[PROPERTY_Icon_Hires_Small]).createProperties(null, listener));
                set.put(((NbNodeObject)properties[PROPERTY_Icon_Lowres_Big]).createProperties(null, listener));
                set.put(((NbNodeObject)properties[PROPERTY_Icon_Lowres_Small]).createProperties(null, listener));
                sheet.put(set);

                set = Sheet.createPropertiesSet();
                set.setName(((NbNodeObject)properties[PROPERTY_DisplayName]).getDisplayName());
                set.setDisplayName(set.getName().concat(" - ").concat(
                        NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty"))); // NOI18N
                set.put(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener));
                sheet.put(set);
            }
            else {
                Node.Property[]nameProp = (Node.Property[])v.toArray(new Node.Property[v.size()]);
                Sheet.Set set = Sheet.createPropertiesSet();
                set.put(nameProp);
                sheet.put(set);

                set = Sheet.createPropertiesSet();
                set.setName("Context"); // NOI18N
                set.setDisplayName(set.getName());
                for (int i = 0; i < PROPERTY_Context.length; i++) {
                    set.put(((NbNodeObject)properties[PROPERTY_Context[i]]).createProperties(null, listener));
                }
                sheet.put(set);
            }
        }
        if (!createCommandProps) {
            for (int i = 0; i < PROPERTY_Context.length; i++) {
                v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Context[i]]).createProperties(null, listener)));
            }
        }
        else {
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Icon_Hires_Big]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Icon_Hires_Small]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Icon_Lowres_Big]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Icon_Lowres_Small]).createProperties(null, listener)));
            v.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener)));
        }
        return v.toArray(new Node.Property[v.size()]);
    }
    
    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        return createProperties(sheet, listener, true);
    }

    public boolean hasActions(int type) {
        return baseActions != null;
    }
    
    public Action[] getActions(boolean context) {
        return null;
    }
    
    public void setActions(BaseAction actions) {
        this.baseActions = actions;
    }
    
    public int getType() {
        return NbNodeObject.FUNCTION_TYPE;  // not really good, but will work
    }
    
    public void addLanguage(int languageID, String text) {
        LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)
                getProperty(this.PROPERTY_DisplayName);
        if (text == null) {
            try {
                text = new StringBuffer("<").append(
                        this.getSimpleProperty(
                        this.PROPERTY_CONTAINER_NAME)).append(">").toString(); // NOI18N
            } catch (UnknownOpenOfficeOrgPropertyException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
                text = ""; // NOI18N
            }
        }
        prop.setValueForLanguage(languageID, text);
    }
    
    public void removeLanguage(int languageID) {
        LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)
                getProperty(this.PROPERTY_DisplayName);
        prop.removeValueForLanguage(languageID);
    }

    public Integer[] getLanguages() {
        LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)
                getProperty(this.PROPERTY_DisplayName);
        return prop.getUsedLanguageIndexes();
    }
    
    public Command duplicate(NbNodeObject parent, Integer[] languages) {
        try {
            Command c = new Command(this.getSimpleProperty(this.PROPERTY_CONTAINER_NAME), parent, languages);
            c.setProperty(c.PROPERTY_Name, this.getProperty(this.PROPERTY_Name));
            c.setProperty(c.PROPERTY_DisplayName, this.getProperty(this.PROPERTY_DisplayName));
            c.setProperty(c.PROPERTY_Icon_Hires_Big, this.getProperty(this.PROPERTY_Icon_Hires_Big));
            c.setProperty(c.PROPERTY_Icon_Hires_Small, this.getProperty(this.PROPERTY_Icon_Hires_Small));
            c.setProperty(c.PROPERTY_Icon_Lowres_Big, this.getProperty(this.PROPERTY_Icon_Lowres_Big));
            c.setProperty(c.PROPERTY_Icon_Lowres_Small, this.getProperty(this.PROPERTY_Icon_Lowres_Small));
            return c;
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return null;
    }
}
