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

package org.openoffice.extensions.projecttemplates.calcaddin;

import java.awt.Component;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.calcaddin.datamodel.AddIn;
import org.openoffice.extensions.util.datamodel.Function;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Parameter;
import org.openoffice.extensions.projecttemplates.calcaddin.datamodel.node.*;
import org.openoffice.extensions.util.datamodel.actions.FunctionAction;
import org.openoffice.extensions.util.datamodel.actions.LanguageAction;
import org.openoffice.extensions.util.datamodel.actions.ParameterAction;

/**
 *
 * @author sg128468
 */
public class AddinActions implements FunctionAction, ParameterAction, LanguageAction {

    private ExplorerManager manager;
    private AddinWizardPanel2Description panel;
    
    /**
     * Creates a new instance of AddinActions
     */
    public AddinActions(ExplorerManager manager, AddinWizardPanel2Description panel) {
        this.manager = manager;
        this.panel = panel;
    }

    public void deleteAction() {
        // because of TreeSelectionModel.SINGLE_TREE_SELECTION max. one node is selected
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node selectedNode = selectedNodes[0];
            NbNodeObject nodeObject = (NbNodeObject)selectedNode.getLookup().lookup(NbNodeObject.class);
            if (nodeObject instanceof Function) {
                Function f = (Function)nodeObject;
                AddIn addin = (AddIn)nodeObject.getParent();
                addin.removeFunction(f);
                // update UI
                AddInChildren children = (AddInChildren)selectedNode.getParentNode().getChildren(); 
                children.update(); // updates nodes
            }
            else if (nodeObject instanceof Parameter) {
                Parameter p = (Parameter)nodeObject;
                Function f = (Function)nodeObject.getParent();
                f.removeParameter(p);
                Node functionNode = selectedNode.getParentNode();
                ((AddInNode)functionNode).triggerChangeEvent(); // updates function display name
                AddInChildren children = (AddInChildren)functionNode.getChildren();
                children.update(); // updates nodes
            }
        }
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    public void addParameterAction() {
        // because of TreeSelectionModel.SINGLE_TREE_SELECTION max. one node is selected
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node selectedNode = selectedNodes[0];

            // get the data object
            NbNodeObject nodeObject = (NbNodeObject)selectedNode.getLookup().lookup(NbNodeObject.class);
            if (nodeObject instanceof Function) {
                Function f = (Function)nodeObject;
                f.addParameter(nodeObject); // addParameter after
                // update UI
                ((AddInNode)selectedNode).triggerChangeEvent();  // updates function display name
                AddInChildren children = (AddInChildren)selectedNode.getChildren(); 
                children.update(); // updates nodes
            }
            else if (nodeObject instanceof Parameter) {
                Function f = (Function)nodeObject.getParent();
                f.addParameter(nodeObject); // addParameter after
                // update UI
                Node functionNode = selectedNode.getParentNode();
                ((AddInNode)functionNode).triggerChangeEvent(); // updates function display name
                AddInChildren children = (AddInChildren)functionNode.getChildren();
                children.update(); // updates nodes
            }

        }        
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    public void addFunctionAction() {
        // get root node
        AddInNode rootNode = (AddInNode)manager.getRootContext();
        // get the data object "AddIn"
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        AddIn addin = (AddIn)nodeObject;
        // add function
        addin.addFunction();

        // update UI
        AddInChildren children = (AddInChildren)rootNode.getChildren();
        children.update();
        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    public void deleteLanguageAction() {
        // get used languages
        AddInNode rootNode = (AddInNode)manager.getRootContext();
        // get the data object "AddIn"
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        AddIn addin = (AddIn)nodeObject;
        NbNodeObject[] subs = addin.getAllSubObjects();
        if (subs.length > 0) {
            Function f = (Function)subs[0];
            LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)f.getProperty(f.PROPERTY_DisplayName);
            Integer[] indexes = prop.getUsedLanguageIndexes();
            int langID = LanguageHandlingDialog.start(true, indexes);  // delete a language
            if (langID != -1) {
                addin.removeLanguage(langID);
                rootNode.triggerLanguageChange();
                if (indexes.length == 1)  // last language deleted: trigger valid() with the change event...
                    panel.fireChangeEvent(); // Notify that the panel changed
            }
        }
    }
    
    public void addLanguageAction() {
        // get used languages
        AddInNode rootNode = (AddInNode)manager.getRootContext();
        // get the data object "AddIn"
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        AddIn addin = (AddIn)nodeObject;
        NbNodeObject[] subs = addin.getAllSubObjects();
        if (subs.length > 0) {
            Function f = (Function)subs[0];
            LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)f.getProperty(f.PROPERTY_DisplayName);
            Integer[] indexes = prop.getUsedLanguageIndexes();
            int langID = LanguageHandlingDialog.start(false, indexes);  // add a language
            if (langID != -1) {
                addin.addLanguage(langID, ""); // NOI18N
                rootNode.triggerLanguageChange();
                if (indexes.length == 0)  // first language added: trigger valid() with the change event...
                    panel.fireChangeEvent(); // Notify that the panel changed
            }
        }
    }
}
