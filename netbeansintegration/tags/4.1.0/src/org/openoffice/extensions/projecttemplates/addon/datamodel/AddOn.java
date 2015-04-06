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
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.MultiLanguage;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.OpenOfficeOrgMultiLanguageSet;
import org.openoffice.extensions.util.datamodel.OrderedContainer;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class AddOn extends PropertyContainer implements OpenOfficeOrgMultiLanguageSet, NbNodeObject {
    
    private static final String MENU = "Menu"; // NOI18N
    private static final String SEPARATOR = "Separator"; // NOI18N
    private static final String COMMAND = "Command"; // NOI18N
    
    private OrderedContainer<NbNodeObject> subElements;
    int selectedLanguage;

    private String displayName;
    
    /** Creates a new instance of AddOn */
    public AddOn(String[] propNames) {
        super(propNames);
        selectedLanguage = -1;
        properties[PROPERTY_DisplayName] = new LocalizedOpenOfficeOrgProperty(
            PROPERTY_CONTAINER_DISPLAY_NAME, 
            "<Dummy>", 
            "",
            this, LanguageDefinition.LANGUAGE_ID_en, null); // NOI18N
        subElements = new OrderedContainer<NbNodeObject>();
        this.displayName = "AddOn"; // NOI18N
    }

    /** Creates a new instance of AddOn */
    public AddOn() {
        this(new String[]{PROPERTY_CONTAINER_DISPLAY_NAME});
    }

    public Command addCommand(NbNodeObject topObject) {
        String number = getNextNumber(NbNodeObject.FUNCTION_TYPE);
        String internalName = COMMAND.concat(number);
        Integer[] indexes = getUsedLanguages(getTopObject(topObject));
        Command command = new Command(internalName, this, indexes);
        addSetObject(internalName, command, getPosition(topObject));
        return command;
    }

    public void insertCommand(Command command, NbNodeObject topObject) {
        String number = getNextNumber(NbNodeObject.FUNCTION_TYPE);
        command.setParent(this);
        String internalName = COMMAND.concat(number);
        addSetObject(internalName, command, getPosition(topObject));
    }
    
    public void insertCommand(Command command) {
        insertCommand(command, null);
    }

    public NbNodeObject[] getAllCommands() {
        return getSpecialElements(NbNodeObject.FUNCTION_TYPE);
    }
    
    public boolean removeCommand(Command c) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(c)) {
                subElements.remove(names[i]);
                return true;
            }
        }
        return false;
    }

    public boolean removeCommandRecursively(Command x) {
        // use result to exit fast when command is deleted: exists only once
        boolean result = removeCommand(x);
        if (result) return true;
        NbNodeObject[] menus = getAllMenus();
        for (int i = 0; i < menus.length; i++) {
            AddOn addOn = (AddOn)menus[i];
            result = addOn.removeCommandRecursively(x);
            if (result) return true;
        }
        return false;
    }
    
    public SubMenuElement addMenuElement(NbNodeObject topObject) {
        String number = getNextNumber(NbNodeObject.UI_MENU_TYPE);
        String internalName = MENU.concat(number);
        Integer[] indexes = getUsedLanguages(getTopObject(topObject));
        SubMenuElement menu = null;
        if (indexes == null) {
            menu = new SubMenuElement(internalName, this);
        }
        else {
            menu = new SubMenuElement(internalName, this, indexes);
        }
        addSetObject(internalName, menu, getPosition(topObject));
        return menu;
    }
    
    public NbNodeObject[] getAllMenus() {
        return getSpecialElements(NbNodeObject.UI_MENU_TYPE);
    }
    
    public void removeMenuElement(SubMenuElement m) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(m)) {
                subElements.remove(names[i]);
            }
        }
    }

    public SeparatorElement addSeparatorElement(NbNodeObject topObject) {
        String number = getNextNumber(NbNodeObject.UI_SEPARATOR_TYPE);
        String internalName = SEPARATOR.concat(number);
        SeparatorElement sep = new SeparatorElement(this);
        addSetObject(internalName, sep, getPosition(topObject));
        return sep;
    }
    
    public void removeSeparatorElement(SeparatorElement sep) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(sep)) {
                subElements.remove(names[i]);
            }
        }
    }
    
    private String getNextNumber(int type) {
        int number = 0;
        Vector<String> names = new Vector<String>();
        int namePrefix = 0;
        switch (type) {
            case NbNodeObject.UI_MENU_TYPE:
                namePrefix = MENU.length();
                break;
            case NbNodeObject.UI_SEPARATOR_TYPE:
                namePrefix = SEPARATOR.length();
                break;
            default:
                namePrefix = COMMAND.length();
        }
        NbNodeObject parent = this;
        while (parent.getParent() != null) {
            parent = parent.getParent();
        }
        AddOn parentAddon = ((AddOn)parent);
        getSpecialElementsInternalNames(parentAddon, type, names);
        for (Iterator<String> it = names.iterator(); it.hasNext();) {
            try {
                int n = Integer.parseInt(it.next().substring(namePrefix));
                if (number <= n) {
                    number = n + 1;
                }
            }
            catch (NumberFormatException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        return String.valueOf(number);
    }
    
    protected NbNodeObject[] getSpecialElements(int elementType) {
        NbNodeObject[] objects = getAllSubObjects();
        Vector<NbNodeObject>v = new Vector<NbNodeObject>();
        for (int i=0; i<objects.length; i++) {
            if (objects[i].getType() == NbNodeObject.UI_MENU_TYPE) {
                SubMenuElement se = (SubMenuElement)objects[i];
                NbNodeObject[] subObjects = se.getSpecialElements(elementType);
                for (int j=0; j<subObjects.length; j++)
                    v.add(subObjects[j]);
            }
            if (objects[i].getType() == elementType) {
                v.add(objects[i]);
            }
        }
        return v.toArray(new NbNodeObject[v.size()]);
    }
    
    protected void getSpecialElementsInternalNames(AddOn parentAddon, int elementType, Vector<String> result) {
        String[] names = parentAddon.getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            NbNodeObject o = (NbNodeObject)parentAddon.getSetObject(names[i]);
            int type = o.getType();
            if (type == elementType) {
                result.add(names[i]);
            }
            if (type == NbNodeObject.UI_MENU_TYPE) {
                AddOn addon = (AddOn)o;
                addon.getSpecialElementsInternalNames(addon, elementType, result);
            }
        }
    }
    
    public String[] getAllSetObjectNames() {
        return subElements.getKeysInOrder();
    }

    public Object getSetObject(String internalName) {
        return subElements.get(internalName);
    }

    public void addSetObject(String internalName, Object setObject) {
        subElements.put(internalName, (NbNodeObject)setObject);
    }

    public void addSetObject(String internalName, Object setObject, int position) {
        subElements.insertElementAt(internalName, (NbNodeObject)setObject, position);
    }

    public void addLanguage(int languageID, String defaultText) {
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            // three types of sub objects, only separator is not localized
            if (obj[i].getType() != NbNodeObject.UI_SEPARATOR_TYPE)  
                ((MultiLanguage)obj[i]).addLanguage(languageID, defaultText);
        }
        try {
            if (defaultText == null)
                defaultText = "<Menu>"; // NOI18N
            // set text on name here
            this.setLanguageWithDefaultText(languageID, defaultText);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    public void removeLanguage(int languageID) {
        try {
            super.removeLanguageAndText(languageID);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            ex.printStackTrace();
        }
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            // three types of sub objects, only separator is not localized
            if (obj[i].getType() != NbNodeObject.UI_SEPARATOR_TYPE)  
                ((MultiLanguage)obj[i]).removeLanguage(languageID);
        }
    }

    public boolean hasLanguage(int languageID) {
        Integer[] indexes = getUsedLanguages(this);
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i].intValue() == languageID) {
                return true;
            }
        }
        return false;
    }
    
    public NbNodeObject getParent() {
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public NbNodeObject[] getAllSubObjects() {
        return (NbNodeObject[])subElements.values().toArray(new NbNodeObject[subElements.size()]);
    }

    public NbNodeObject[] getAllSubObjects(String context) {
        return (NbNodeObject[])subElements.values().toArray(new NbNodeObject[subElements.size()]);
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        return null;
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
        return NbNodeObject.ADDON_TYPE;
    }
    
    public void moveUp(NbNodeObject object) {
        int pos = subElements.getPositionFromObject(object);
        if (pos > 0) {
            // remove object at pos and insert it at pos - 1
            String name = subElements.getKeyFromObject(object);
            subElements.remove(name);
            subElements.insertElementAt(name, object, pos - 1);
        }
    }
    
    public void moveDown(NbNodeObject object) {
        int pos = subElements.getPositionFromObject(object);
        if (pos < subElements.size() - 1) {
            // remove object at pos and insert it at pos + 1
            String name = subElements.getKeyFromObject(object);
            subElements.remove(name);
            subElements.insertElementAt(name, object, pos + 1);
        }
    }
    
    private int getPosition(NbNodeObject topObject) {
        int position = 0;
        if (topObject == null) {  // if null append
            position = subElements.size();
        }
        else {
            position = subElements.getPositionFromObject(topObject) + 1;
        }
        return position;
    }
    
    protected NbNodeObject getTopObject(NbNodeObject object) {
        if (object != null && object.getParent() != null) {
            return getTopObject(object.getParent());
        }
        return object;
    }
    
    protected Integer[] getUsedLanguages(NbNodeObject object) {
        if (object == null) return null;
        if (object.getType() != NbNodeObject.UI_SEPARATOR_TYPE) { // separator is not localized
            PropertyContainer propContainer = (PropertyContainer)object;
            OpenOfficeOrgProperty p = propContainer.getProperty(propContainer.PROPERTY_DisplayName);
            if (p != null && p.isLocalized()) {
                LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)p;
                Integer[] indexes = prop.getUsedLanguageIndexes();
                return indexes;
            }
        }
        NbNodeObject[] subObjects = object.getAllSubObjects();
        for (int i=0; i<subObjects.length; i++) {
            Integer[] indexes = getUsedLanguages(subObjects[i]);
            if (indexes != null)
                return indexes;
        }
        return null;
    }
    
}
