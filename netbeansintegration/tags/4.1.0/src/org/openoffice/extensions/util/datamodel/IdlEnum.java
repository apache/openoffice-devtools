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

import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class IdlEnum extends PropertyContainer implements NbNodeObject {

    public static final int PROPERTY_COUNT = 1;

    private NbNodeObject parent;

    private boolean languageSupport;

    public IdlEnum(String nameValue, NbNodeObject parent) {
        super(new String[] {
            PROPERTY_CONTAINER_NAME
        });
        this.parent = parent;
        initializeProperties(nameValue);
    }

    private void initializeProperties(String nameValue) {
        properties[PROPERTY_Name] = new SimpleOpenOfficeOrgProperty(
                PROPERTY_CONTAINER_NAME, 
                nameValue, 
                NbBundle.getMessage(AddinWizardIterator.class, "TF_Parameter_Name_Tooltip"),
                this, null);
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
        return new NbNodeObject[]{(NbNodeObject)properties[PROPERTY_Name]};
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        Node.Property[]nameProp = new Node.Property[1];
        nameProp[0] = ((NbNodeObject)properties[PROPERTY_Name]).createProperties(null, listener)[0];
        if (sheet != null) {
            Sheet.Set set = sheet.createPropertiesSet();
            set.put(nameProp);
            sheet.put(set);
        }
        return nameProp;
    }
    
    /**
     * set the action for context menus
     */
    public void setActions(BaseAction baseAction) {
    }
    
    public Action[] getActions(boolean b) {
        return null;//new Action[]{new MyAddParameterAction(), new MyDeleteParameterAction()};
    }

    public boolean hasActions(int type) {
        return false;
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public int getType() {
        return NbNodeObject.ENUM_TYPE;
    }
    
//    private class MyAddParameterAction extends AbstractAction {
//        int counter;
//        public MyAddParameterAction() {
//            putValue(NAME, "Add Parameter");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.addParameterAction();
//        }
//    }
//    private class MyDeleteParameterAction extends AbstractAction {
//        public MyDeleteParameterAction() {
//            putValue(NAME, "Delete Parameter");
//        }
//        public void actionPerformed(ActionEvent e) {
//            addinAction.deleteActions();
//        }
//    }
    
}
