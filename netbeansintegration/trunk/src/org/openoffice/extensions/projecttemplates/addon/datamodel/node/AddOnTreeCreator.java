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

package org.openoffice.extensions.projecttemplates.addon.datamodel.node;

import java.util.Iterator;
import java.util.Vector;
import org.openide.util.Exceptions;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.projecttemplates.addon.datamodel.Command;
import org.openoffice.extensions.projecttemplates.addon.datamodel.SubMenuElement;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 * To handle different settings concernig toolbars and menus, the same data 
 * structure is created for the AddOn. It is reused for basic settings of the 
 * AddOn, for the menu structure and for the toolbar structure.
 * This class helps with creating a new data structure for each panel.
 */
public class AddOnTreeCreator {

    /**
     * Creates a new instance of AddOnTreeCreator
     */
    private AddOnTreeCreator() {
    }
    
    public static AddOnNode createInitialFunctionTree() {
        AddOn addon = new AddOn();
/*        SubMenuElement subElement = addon.addMenuElement(null);
        OpenOfficeOrgProperty oooProp = subElement.getProperty(subElement.PROPERTY_DisplayName);
        try {
            oooProp.setValueForLanguage(LanguageDefinition.LANGUAGE_ID_en, "Top Level Menu"); // NOI18N 
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {  // if we do not know English...
            LogWriter.getLogWriter().printStackTrace(ex);
        } */
        addon.addCommand(null);
        AddOnNode rootNode = new AddOnNode(addon, new AddOnChildren());
        return rootNode;
    }
    
    public static AddOnNode restoreMenuStructure(AddOn restepAddOn, AddOn rootAddOn) {
        AddOn menuAddOn = new AddOn();
        Vector<Command> markForDeletion = new Vector<Command>();
        menuAddOn.addSetObject("Menu0", restepAddOn); // NOI18N
        NbNodeObject[] commands = menuAddOn.getAllCommands();
        NbNodeObject[] newFromPanel1 = rootAddOn.getAllCommands();
        Integer[] langIndexes = null;
        try {
            for (int i = 0; i < newFromPanel1.length; i++) {
                Command newCommand = (Command)newFromPanel1[i];
                if (i == 0) {  // get current languages once for later language completion
                    langIndexes = newCommand.getLanguages();
                }
                String newName = newCommand.getProperty(newCommand.PROPERTY_Name).getValueForLanguage(-1);
                boolean mustBeAdded = true;
                for (int j = 0; j < commands.length; j++) {
                    Command restepCommand = (Command)commands[j];
                    if (i == 0) { // first run: mark all for deletion
                        markForDeletion.add(restepCommand);
                    }
                    String restepName = restepCommand.getProperty(restepCommand.PROPERTY_Name).getValueForLanguage(-1);
                    // double entries are kept and must not be removed
                    if (restepName.equals(newName)) {
                        mustBeAdded = false;
                        markForDeletion.remove(restepCommand);
                        // copy relevant props from panel one into kept command
                        copyUIProperties(newCommand, restepCommand);
                    }
                }
                // entry from panel 1 is not there, add to panel two: at the end
                if (mustBeAdded) {
                    SubMenuElement menu = (SubMenuElement)menuAddOn.getSetObject("Menu0");
                    menu.insertCommand(newCommand.duplicate(menu, langIndexes));
                }
            }
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            Exceptions.printStackTrace(ex);
        }
        // delete in panel 2
        if (markForDeletion.size() > 0) {
            for (Iterator<Command> it = markForDeletion.iterator(); it.hasNext();) {
                Command command = it.next();
                // command may be in some menu structure
                menuAddOn.removeCommandRecursively(command);
            }
        }
        // handle added / deleted languages
        handleLanguages(menuAddOn, langIndexes);
            
        // do not show all command props, and no icon in menus
        return new AddOnNode(menuAddOn, new AddOnChildren(false, false), false, false);
    }
    
    public static AddOnNode createMenuStructure(AddOn addOn) {
        AddOn menuAddOn = createNewMenuAddonStructure(addOn);

        // do not show all command props, and no icon in menus
        return new AddOnNode(menuAddOn, new AddOnChildren(false, false), false, false);
    }

    private static AddOn createNewMenuAddonStructure(AddOn addOn) {
        AddOn newAddOn = new AddOn();
        NbNodeObject[] commands = addOn.getAllCommands();
        SubMenuElement el = newAddOn.addMenuElement(null);

        Command com = (Command)commands[0];
        Integer[] langIndexes = com.getLanguages();
        for (int i = 0; i < langIndexes.length; i++) {
            newAddOn.addLanguage(langIndexes[i].intValue(), "<Dummy>"); // NOI18N
            try {
                el.setLocalizedProperty(SubMenuElement.PROPERTY_CONTAINER_DISPLAY_NAME, 
                        langIndexes[i].intValue(), "<AddOn Menu>"); // NOI18N
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
                ex.printStackTrace();
            }
        }
        
        for (int i=0; i<commands.length; i++) {
            el.addSetObject("Command".concat(Integer.toString(i)),  // NOI18N
                    ((Command)commands[i]).duplicate(el, langIndexes));
        }
        return newAddOn;
    }
    
    /**
     * Copy UI props from one command into another: diplay name and icons
     * @param source source command
     * @param target target command
     */
    private static void copyUIProperties(Command source, Command target) {
        OpenOfficeOrgProperty property = source.getProperty(source.PROPERTY_DisplayName);
        target.setProperty(target.PROPERTY_DisplayName, property);
        OpenOfficeOrgProperty iconProp = source.getProperty(source.PROPERTY_Icon_Hires_Big);
        target.setProperty(target.PROPERTY_Icon_Hires_Big, iconProp);
        iconProp = source.getProperty(source.PROPERTY_Icon_Hires_Small);
        target.setProperty(target.PROPERTY_Icon_Hires_Small, iconProp);
        iconProp = source.getProperty(source.PROPERTY_Icon_Lowres_Big);
        target.setProperty(target.PROPERTY_Icon_Lowres_Big, iconProp);
        iconProp = source.getProperty(source.PROPERTY_Icon_Lowres_Small);
        target.setProperty(target.PROPERTY_Icon_Lowres_Small, iconProp);
        
    }
    
    private static void handleLanguages(AddOn topLevelObject, Integer[] langIndexes) {
        for (int j = 0; j < langIndexes.length; j++) {
            if (!topLevelObject.hasLanguage(langIndexes [j])) {
                // add language with internal defaults: null as text
                topLevelObject.addLanguage(langIndexes[j], null);
            }
        }
    }
    
    public static AddOnNode createToolbarStructure(NbNodeObject addOn) {
        NbNodeObject[] commands = ((AddOn)addOn).getAllCommands();
        SubMenuElement el = new SubMenuElement("AddOn Toolbar", null); // NOI18N

        Command com = (Command)commands[0];
        Integer[] langIndexes = com.getLanguages();
        for (int i = 0; i < langIndexes.length; i++) {
            el.addLanguage(langIndexes[i].intValue(), "<Dummy>"); // NOI18N
            try {
                el.setLocalizedProperty(SubMenuElement.PROPERTY_CONTAINER_DISPLAY_NAME, 
                        langIndexes[i].intValue(), "<AddOn Toolbar>"); // NOI18N
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
                ex.printStackTrace();
            }
        }
        
        for (int i=0; i<commands.length; i++) {
            el.addSetObject("Command".concat(Integer.toString(i)), ((Command)commands[i]).duplicate(el, langIndexes)); // NOI18N
        }
        // do not show all command props but icon in toolbar
        return new AddOnNode(el, new AddOnChildren(false, true), false, true);
    }
    
    public static AddOnNode restoreToolbarStructure(AddOn restepAddOn, AddOn rootAddOn) {
        SubMenuElement el = (SubMenuElement)restepAddOn.getSetObject("Toolbar");
        Vector<Command> markForDeletion = new Vector<Command>();
        NbNodeObject[] commands = el.getAllCommands();
        NbNodeObject[] newFromPanel1 = rootAddOn.getAllCommands();
        Integer[] langIndexes = null;
        try {
            for (int i = 0; i < newFromPanel1.length; i++) {
                Command newCommand = (Command)newFromPanel1[i];
                if (i == 0) {  // get current languages once for later language completion
                    langIndexes = newCommand.getLanguages();
                }
                String newName = newCommand.getProperty(newCommand.PROPERTY_Name).getValueForLanguage(-1);
                boolean mustBeAdded = true;
                for (int j = 0; j < commands.length; j++) {
                    Command restepCommand = (Command)commands[j];
                    if (i == 0) { // first run: mark all for deletion
                        markForDeletion.add(restepCommand);
                    }
                    String restepName = restepCommand.getProperty(restepCommand.PROPERTY_Name).getValueForLanguage(-1);
                    // double entries are kept and must not be removed
                    if (restepName.equals(newName)) {
                        mustBeAdded = false;
                        markForDeletion.remove(restepCommand);
                        // copy relevant props from panel one into kept command
                        copyUIProperties(newCommand, restepCommand);
                    }
                }
                // entry from panel 1 is not there, add to panel two: at the end
                if (mustBeAdded) {
                    el.insertCommand(newCommand.duplicate(el, langIndexes));
                }
            }
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            Exceptions.printStackTrace(ex);
        }
        // delete in panel 3
        if (markForDeletion.size() > 0) {
            for (Iterator<Command> it = markForDeletion.iterator(); it.hasNext();) {
                Command command = it.next();
                el.removeCommand(command);
            }
        }
        // handle added / deleted languages
        handleLanguages(el, langIndexes);
            
        // do not show all command props, and no icon in menus
        return new AddOnNode(el, new AddOnChildren(false, true), false, true);
    }
    
}
