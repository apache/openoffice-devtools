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

package org.openoffice.extensions.projecttemplates.calcaddin.datamodel;

import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.Function;
import org.openoffice.extensions.util.datamodel.OrderedContainer;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.OpenOfficeOrgMultiLanguageSet;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class AddIn implements OpenOfficeOrgMultiLanguageSet, NbNodeObject {

    private OrderedContainer<Function> functions;
    private int funcCounter = 0;

//    private Node node;
    
    /** Creates a new instance of AddIn */
    public AddIn() {
        functions = new OrderedContainer<Function>();
    }
    
    public Function getFunction(String name) {
        Enumeration keys = functions.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            Function f = (Function)functions.get(keys.nextElement());
            found = name.equals(f.getProperty(f.PROPERTY_Name));
            if (found) return f;
        }
        return null;
    }

/*    public void addFunction(Function function) {
        addSetObject("function" + funcCounter++, function); // NOI18N
    }
*/
    public Function addFunction() {
        String number = getNextNumber(NbNodeObject.FUNCTION_TYPE);
        String internalName = "function".concat(number); // NOI18N
        NbNodeObject[] subFunctions = this.getAllSubObjects();
        Function function = null;
        if (subFunctions == null || subFunctions.length == 0) {
            function = new Function(internalName, "int", this); // NOI18N
        }
        else {
            Function f = (Function)subFunctions[0];
            LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)
                f.getProperty(f.PROPERTY_DisplayName);
            Integer[] indexes = prop.getUsedLanguageIndexes();
            function = new Function(internalName, "int", this, indexes); // NOI18N
        }
        function.addParameter();
        addSetObject(internalName, function);
        return function;
    }
    
    public void removeFunction(String name) {
        Enumeration keys = functions.keys();
        boolean found = false;
        while (!found && keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Function f = (Function)functions.get(key);
            try {
                found = name.equals(f.getProperty(f.PROPERTY_Name).getValueForLanguage(-1));
            } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            }
            if (found) {
                functions.remove((String) key);
            }
        }        
    }
    
    public void removeFunction(Function function) {
        String[] names = getAllSetObjectNames();
        for (int i=0; i<names.length; i++) {
            if (getSetObject(names[i]).equals(function)) {
                functions.remove(names[i]);
                funcCounter--;
            }
        }
    }
    
    public String[] getAllSetObjectNames() {
        return functions.getKeysInOrder();
    }

    public Object getSetObject(String internalName) {
        return functions.get(internalName);
    }

    public void addSetObject(String internalName, Object setObject) {
        Function function = (Function)setObject;
        functions.put(internalName, function);
    }

    public String getDisplayName() {
        // not localized, should never be displayed anyway.
        return "AddIn";  // NOI18N
    }

    public void addLanguage(int languageID, String defaultText) {
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            ((Function)obj[i]).addLanguage(languageID, defaultText);
        }
    }
    
    public void printLanguages() {
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            ((Function)obj[i]).printLanguages();
        }
    }
    
    public void removeLanguage(int languageID) {
        NbNodeObject[] obj = getAllSubObjects();
        for (int i=0; i<obj.length; i++) {
            ((Function)obj[i]).removeLanguage(languageID);
        }
    }
    
    public NbNodeObject[] getAllSubObjects() {
        return (NbNodeObject[])functions.values().toArray(new NbNodeObject[functions.size()]);
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        return null;
    }

    public boolean hasActions(int type) {
        return false;
    }

    public Action[] getActions(boolean B) {
        return null;
    }

    public void setActions(BaseAction actions) {
    }

    public NbNodeObject getParent() {
        return null;
    }

    public int getType() {
        return NbNodeObject.ADDIN_TYPE;
    }
    
    public String getNextNumber(int type) {
        int number = 1;
            int namePrefix = "function".length(); // NOI18N
        String[] names = this.getAllSetObjectNames();
        for (int i = 0; i < names.length; i++) {
            try {
                int n = Integer.parseInt(names[i].substring(namePrefix));
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
}
