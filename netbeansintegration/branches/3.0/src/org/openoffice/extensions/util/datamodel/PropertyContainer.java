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

import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class PropertyContainer implements OpenOfficeOrgPropertyContainer {

    // CAUTION! PROPERTY_CONTAINER_CONTEXTS and PROPERTY_CONTAINER_CONTEXT_REPRESENTATIONS
    // must always have the same length! Adapt 
    // org.openoffice.extensions.projecttemplates.addon.datamodel.Command c'tor 
    // when something changes here.
    public static final String[] PROPERTY_CONTAINER_CONTEXTS = new String[] {
        "Bibilography", 
        "Chart", 
        "Database",
        "Draw", 
        "Formula", 
        "Presentation", 
        "Spreadsheet", 
        "Writer",
        "Global",
    }; // NOI18N
    
    public static final String[] PROPERTY_CONTAINER_CONTEXT_REPRESENTATIONS = new String[] {
        "com.sun.star.frame.Bibliography", 
        "com.sun.star.chart.ChartDocument", 
        "com.sun.star.sdb.OfficeDatabaseDocument",
        "com.sun.star.drawing.DrawingDocument", 
        "com.sun.star.formula.FormulaProperties", 
        "com.sun.star.presentation.PresentationDocument", 
        "com.sun.star.sheet.SpreadsheetDocument", 
        "com.sun.star.text.TextDocument",
        "com.sun.star.text.GlobalDocument",
    }; // NOI18N

    // typical properties index
    public int PROPERTY_DisplayName;
    public int PROPERTY_Description;
    public int PROPERTY_Type;
    public int PROPERTY_Name;
    public int PROPERTY_Category;
    public int PROPERTY_CompatibilityName;
    public int PROPERTY_Exceptions;
    public int PROPERTY_Icon_Hires_Big;
    public int PROPERTY_Icon_Hires_Small;
    public int PROPERTY_Icon_Lowres_Big;
    public int PROPERTY_Icon_Lowres_Small;
    public int PROPERTY_Package;
    public int PROPERTY_ParentException;
    public int PROPERTY_Interface;
    public int PROPERTY_Parent;
    public int[] PROPERTY_Context = new int[PROPERTY_CONTAINER_CONTEXTS.length];
            
    // typical properties name
    public static final String PROPERTY_CONTAINER_DISPLAY_NAME = "DisplayName"; // NOI18N
    public static final String PROPERTY_CONTAINER_DESCRIPTION = "Description"; // NOI18N
    public static final String PROPERTY_CONTAINER_EXCEPTIONS = "Exceptions"; // NOI18N
    public static final String PROPERTY_CONTAINER_NAME = "Name"; // NOI18N
    public static final String PROPERTY_CONTAINER_CATEGORY = "Category"; // NOI18N
    public static final String PROPERTY_CONTAINER_ICON_HIRES_BIG = "Icon, High Contrast, 26x26"; // NOI18N
    public static final String PROPERTY_CONTAINER_ICON_HIRES_SMALL = "Icon, High Contrast, 16x16"; // NOI18N
    public static final String PROPERTY_CONTAINER_ICON_LOWRES_BIG = "Icon, Low Contrast, 26x26"; // NOI18N
    public static final String PROPERTY_CONTAINER_ICON_LOWRES_SMALL = "Icon, Low Contrast, 16x16"; // NOI18N
    public static final String PROPERTY_CONTAINER_COMPATIBILITY_NAME = "CompatibilityName"; // NOI18N
    public static final String PROPERTY_CONTAINER_PACKAGE = "Package"; // NOI18N
    public static final String PROPERTY_CONTAINER_PARENT_EXCPTION = "ParentException"; // NOI18N
    public static final String PROPERTY_CONTAINER_TYPE = "Type"; // NOI18N
    public static final String PROPERTY_CONTAINER_INTERFACE = "Interface"; // NOI18N
    public static final String PROPERTY_CONTAINER_PARENT = "Parent"; // NOI18N
    
    // category display name is not used for now
//    public int PROPERTY_CategoryDisplayName;
//    private static final String CATEGORY_DISPLAY_NAME = "CategoryDisplayName"; // NOI18N

    protected OpenOfficeOrgProperty[] properties;
    
    public PropertyContainer(String[] names) {
        properties = new OpenOfficeOrgProperty[names.length];
        for (int i=0; i<names.length; i++) {
            if (names[i].equals(PROPERTY_CONTAINER_DISPLAY_NAME))
                PROPERTY_DisplayName = i;
            else if (names[i].equals(PROPERTY_CONTAINER_DESCRIPTION))
                PROPERTY_Description = i;
            else if (names[i].equals(PROPERTY_CONTAINER_EXCEPTIONS))
                PROPERTY_Exceptions = i;
            else if (names[i].equals(PROPERTY_CONTAINER_NAME))
                PROPERTY_Name = i;
            else if (names[i].equals(PROPERTY_CONTAINER_CATEGORY))
                PROPERTY_Category = i;
            else if (names[i].equals(PROPERTY_CONTAINER_ICON_HIRES_BIG))
                PROPERTY_Icon_Hires_Big = i;
            else if (names[i].equals(PROPERTY_CONTAINER_ICON_HIRES_SMALL))
                PROPERTY_Icon_Hires_Small = i;
            else if (names[i].equals(PROPERTY_CONTAINER_ICON_LOWRES_BIG))
                PROPERTY_Icon_Lowres_Big = i;
            else if (names[i].equals(PROPERTY_CONTAINER_ICON_LOWRES_SMALL))
                PROPERTY_Icon_Lowres_Small = i;
            else if (names[i].equals(PROPERTY_CONTAINER_COMPATIBILITY_NAME))
                PROPERTY_CompatibilityName = i;
            else if (names[i].equals(PROPERTY_CONTAINER_PACKAGE))
                PROPERTY_Package = i;
            else if (names[i].equals(PROPERTY_CONTAINER_PARENT_EXCPTION))
                PROPERTY_ParentException = i;
            else if (names[i].equals(PROPERTY_CONTAINER_TYPE))
                PROPERTY_Type = i;
            else if (names[i].equals(PROPERTY_CONTAINER_INTERFACE))
                PROPERTY_Interface = i;
            else if (names[i].equals(PROPERTY_CONTAINER_PARENT))
                PROPERTY_Parent = i;
            else {
                for (int j = 0; j < PROPERTY_CONTAINER_CONTEXTS.length; j++) {
                    if (names[i].equals(PROPERTY_CONTAINER_CONTEXTS[j]))
                        PROPERTY_Context[j] = i;
                }
            }
        }
    }
    
    public String getLocalizedProperty(String propertyName, int languageID)
        throws UnknownOpenOfficeOrgPropertyException, UnknownOpenOfficeOrgLanguageIDException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && properties[i].isLocalized() && propertyName.equals(properties[i].getPropertyName())) {
                return properties[i].getValueForLanguage(languageID);
            }
        }
        throw new UnknownOpenOfficeOrgPropertyException("Cannot get property " + propertyName); // NOI18N
    }

    public void setLocalizedProperty(String propertyName, int languageID, String value)
        throws UnknownOpenOfficeOrgLanguageIDException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && properties[i].isLocalized() && propertyName.equals(properties[i].getPropertyName())) {
                properties[i].setValueForLanguage(languageID, value);
            }
        }
    }
    
    public String getSimpleProperty(String propertyName)
            throws UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && !properties[i].isLocalized() && propertyName.equals(properties[i].getPropertyName())) {
                try {
                    return properties[i].getValueForLanguage(-1);
                } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
                    // will never be thrown
                }
            }
        }
        throw new UnknownOpenOfficeOrgPropertyException("Unknown: " + propertyName);// NOI18N 
    }

    public void setSimpleProperty(String propertyName, String value) 
            throws UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && !properties[i].isLocalized() && propertyName.equals(properties[i].getPropertyName())) {
                try {
                    properties[i].setValueForLanguage(-1, value);
                    return; // evil construct, but function is short anyway
                }
                catch (UnknownOpenOfficeOrgLanguageIDException e) {
                    // will never be thrown
                }
            }
        }
        throw new UnknownOpenOfficeOrgPropertyException("Cannot set property " + propertyName); // NOI18N
    }
    
    public boolean isPropertyLocalized(String propertyName) 
            throws UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && propertyName.equals(properties[i].getPropertyName())) {
                return properties[i].isLocalized();
            }
        }
        throw new UnknownOpenOfficeOrgPropertyException("No property with name " + propertyName); // NOI18N
    }

    /**
     * some default text is set for a new language on all localized properties
     * existing text is not overwritten
     */
    public void setLanguageWithDefaultText(int languageID, String text) 
            throws UnknownOpenOfficeOrgLanguageIDException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && properties[i].isLocalized()) {
//                if (properties[i].getValueForLanguage(languageID) == null)
                    properties[i].setValueForLanguage(languageID, text);
            }
        }
    }
    
    public void removeLanguageAndText(int languageID) 
            throws UnknownOpenOfficeOrgLanguageIDException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && properties[i].isLocalized()) {
                properties[i].removeValueForLanguage(languageID);
            }
        }
    }
    
    public String[] getAllPropertyNames() {
        String[] propNames = new String[properties.length];
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null)
                propNames[i] = properties[i].getPropertyName();
        }
        return propNames;
    }

    public OpenOfficeOrgProperty getProperty(String propertyName)
            throws UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && propertyName.equals(properties[i].getPropertyName())) {
                return properties[i];
            }
        }
        throw new UnknownOpenOfficeOrgPropertyException("Cannot get property " + propertyName); // NOI18N
    }

    public void setProperty(String propertyName, OpenOfficeOrgProperty value) 
            throws UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<properties.length; i++) {
            if (properties[i] != null && propertyName.equals(properties[i].getPropertyName())) {
                properties[i] = value;
            }
        }
        throw new UnknownOpenOfficeOrgPropertyException("Cannot set property " + propertyName); // NOI18N
    }
    
    public OpenOfficeOrgProperty getProperty(int propertyIndex) {
        return properties[propertyIndex];
    }

    public void setProperty(int propertyIndex, OpenOfficeOrgProperty property) {
        properties[propertyIndex] = property;
    }
}
