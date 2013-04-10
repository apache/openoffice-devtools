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
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.OpenOfficeOrgMultiLanguageSet;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;

/**
 *
 * @author sg128468
 */
public class SubMenuElement extends AddOn implements OpenOfficeOrgMultiLanguageSet, NbNodeObject {

    private NbNodeObject parent;
    private String name;
    private BaseAction baseActions;
    int selectedLanguage;
    
    /** Creates a new instance of SubMenuElement */
    public SubMenuElement(String name, NbNodeObject parent) {
        this(name, parent, new Integer[] {new Integer(LanguageDefinition.LANGUAGE_ID_en)});
    }

    /** Creates a new instance of SubMenuElement */
    public SubMenuElement(String name, NbNodeObject parent, Integer[] languages) {
        super(new String[]{PROPERTY_CONTAINER_DISPLAY_NAME, PROPERTY_CONTAINER_NAME});
        this.name = name;
        this.parent = parent;
        selectedLanguage = -1;
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME,
                name,
                "",
                this, null); // NOI18N
        if (languages == null || languages.length == 0) 
            languages = new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)};
        String localizedName = "<".concat(name).concat(">"); // NOI18N
        properties[PROPERTY_DisplayName] = new LocalizedOpenOfficeOrgProperty(
            PROPERTY_CONTAINER_DISPLAY_NAME, 
            localizedName, 
            NbBundle.getMessage(AddOnWizardIterator.class, "TF_Menu_DisplayName_Tooltip"),
            this, languages[0].intValue(), null);
        for (int i=1; i<languages.length; i++) {
            addLanguage(languages[i].intValue(), localizedName);
        }
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public void setParent(NbNodeObject parent) {
        if (parent.getType() == NbNodeObject.UI_MENU_TYPE || 
                parent.getType() == NbNodeObject.ADDON_TYPE)
            this.parent = parent;
    }

    public String getDisplayName() {
        String retValue = null;
        OpenOfficeOrgProperty prop = getProperty(this.PROPERTY_DisplayName);
        if (prop.isLocalized()) {
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

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Vector<Node.Property>list = new Vector<Node.Property>();
        list.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)));
        if (sheet != null) {
            Node.Property[]nameProp = (Node.Property[])list.toArray(new Node.Property[list.size()]);
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);

            set = sheet.createPropertiesSet();
            set.setName(((NbNodeObject)properties[PROPERTY_DisplayName]).getDisplayName());
            set.setDisplayName(set.getName().concat(" - ").concat( // NOI18N
                    NbBundle.getMessage(AddinWizardIterator.class, "LB_LocalizedProperty")));
            set.put(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener));
            sheet.put(set);
        }
        list.addAll(Arrays.asList(((NbNodeObject)properties[PROPERTY_DisplayName]).createProperties(null, listener)));
        return list.toArray(new Node.Property[list.size()]);
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
        return NbNodeObject.UI_MENU_TYPE;
    }
    
}
