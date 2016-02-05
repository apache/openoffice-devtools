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

package org.openoffice.extensions.projecttemplates.addon;

import java.beans.PropertyVetoException;
import java.util.Iterator;
import java.util.Vector;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.projecttemplates.addon.datamodel.Command;
import org.openoffice.extensions.projecttemplates.addon.datamodel.SeparatorElement;
import org.openoffice.extensions.projecttemplates.addon.datamodel.SubMenuElement;
import org.openoffice.extensions.projecttemplates.addon.datamodel.node.AddOnChildren;
import org.openoffice.extensions.projecttemplates.addon.datamodel.node.AddOnNode;
import org.openoffice.extensions.projecttemplates.calcaddin.LanguageHandlingDialog;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.actions.LanguageAction;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;

/**
 *
 * @author sg128468
 */
public class AddOnActions implements BaseAction, LanguageAction {
    
    private ExplorerManager manager;
    private ActionPanel panel;
    private Vector<Command> deletedCommands;

    /** Creates a new instance of AddOnActions */
    public AddOnActions(ExplorerManager manager, 
            ActionPanel panel) {
        this.manager = manager;
        this.panel = panel;
        this.deletedCommands = new Vector<Command>();
    }

    public Vector<Command> getDeletedCommands() {
        return deletedCommands;
    }
    
    public void addCommandAction() {
        NbNodeObject selectedObject = getSelectedObject();
        Node node = manager.getRootContext();
        if (node != null) {
            // get the data object
            NbNodeObject nodeObject = (NbNodeObject)
                    node.getLookup().lookup(NbNodeObject.class);
            AddOn addon = (AddOn)nodeObject;
            // add function
            addon.addCommand(selectedObject);

            // update UI
            AddOnChildren children = (AddOnChildren)node.getChildren();
            children.update();
            panel.fireChangeEvent(); // Notify that the panel changed
            selectNextNode();
        }
    }

    public SubMenuElement addMenuAction() {
        NbNodeObject selectedObject = getSelectedObject();
        Node node = getNextMenuNode();
        if (node != null) {
            // get the data object "AddIn"
            NbNodeObject nodeObject = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
            AddOn addon = (AddOn)nodeObject;
            // add function
            SubMenuElement subMenu = addon.addMenuElement(selectedObject);

            // update UI
            AddOnChildren children = (AddOnChildren)node.getChildren();
            children.update();
            panel.fireChangeEvent(); // Notify that the panel changed
            return subMenu;
        }
        return null;
    }

    public SeparatorElement addSeparatorAction() {
        NbNodeObject selectedObject = getSelectedObject();
        Node node = getNextMenuNode();
        if (node != null) {
            // get the data object "AddIn"
            NbNodeObject nodeObject = (NbNodeObject)node.getLookup().lookup(NbNodeObject.class);
            AddOn addon = (AddOn)nodeObject;
            // add function
            SeparatorElement sep = addon.addSeparatorElement(selectedObject);

            // update UI
            AddOnChildren children = (AddOnChildren)node.getChildren();
            children.update();
            panel.fireChangeEvent(); // Notify that the panel changed
            return sep;
        }
        return null;
    }

    public void deleteAction() {
        NbNodeObject nodeObject = getSelectedObject();
        if (nodeObject != null) {
            if (nodeObject instanceof Command) {
                Command f = (Command)nodeObject;
                AddOn addon = (AddOn)nodeObject.getParent();
                addon.removeCommand(f);
            }
            else if (nodeObject instanceof AddOn) {
                SubMenuElement m = (SubMenuElement)nodeObject;
                // keep the deleted commands in deleted vector for restoring
                NbNodeObject[] subCommands = m.getAllCommands();
                for (int i = 0; i < subCommands.length; i++) {
                    this.deletedCommands.add((Command)subCommands[i]);
                }
                AddOn addon = (AddOn)nodeObject.getParent();
                addon.removeMenuElement(m);
            }
            else if (nodeObject instanceof SeparatorElement) {
                SeparatorElement se = (SeparatorElement)nodeObject;
                AddOn addon = (AddOn)nodeObject.getParent();
                addon.removeSeparatorElement(se);
            }
            else {
                return; // return and do not update UI unnecessarily
            }
            // update UI
            AddOnChildren children = (AddOnChildren)
                    manager.getSelectedNodes()[0].getParentNode().getChildren(); 
            children.update(); // updates nodes
            panel.fireChangeEvent();
        }
    }

    public void moveUp() {
        Node[] selectedNodes = manager.getSelectedNodes();
        NbNodeObject object = getSelectedObject();
        if (object != null) {
            NbNodeObject parent = object.getParent();
            AddOn addon = (AddOn)parent;
            addon.moveUp(object);
            // update UI
            Node parentNode = selectedNodes[0].getParentNode();
            AddOnChildren children = (AddOnChildren)parentNode.getChildren();
            children.update(); // updates nodes
            panel.fireChangeEvent();
        }
        try {
            manager.setSelectedNodes(selectedNodes);
        } catch (PropertyVetoException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    public void moveDown() {
        Node[] selectedNodes = manager.getSelectedNodes();
        NbNodeObject object = getSelectedObject();
        if (object != null) {
            NbNodeObject parent = object.getParent();
            AddOn addon = (AddOn)parent;
            addon.moveDown(object);
            // update UI
            AddOnChildren children = (AddOnChildren)
                    manager.getSelectedNodes()[0].getParentNode().getChildren(); 
            children.update(); // updates nodes
            panel.fireChangeEvent();
        }
        try {
            manager.setSelectedNodes(selectedNodes);
        } catch (PropertyVetoException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    public void toggleVisibility() {
        Node[] selected = manager.getSelectedNodes();
        if (selected == null || selected.length == 0) {
            return; // nothing to do
        }
        Node selectedNode = selected[0]; // single tree selection
        Node parent = selectedNode.getParentNode();
        NbNodeObject object = (NbNodeObject)selectedNode.getLookup().lookup(NbNodeObject.class);

        // if not a command selected, delete the stuff!
        if (object.getType() != NbNodeObject.FUNCTION_TYPE) {
            this.deleteAction();
            return;
        }
        Command f = (Command)object;
        AddOn addon = (AddOn)f.getParent();
        deletedCommands.add(f);
        addon.removeCommand(f);
        // update UI
        AddOnChildren children = (AddOnChildren)parent.getChildren(); 
        children.update(); // updates nodes
        panel.fireChangeEvent();
    }

    public void restoreCommands() {
        Node rootNode = manager.getRootContext();
        AddOn addOn = (AddOn)rootNode.getLookup().lookup(NbNodeObject.class);
        AddOn subMenu = null;
        NbNodeObject[] subMenus = addOn.getAllSubObjects();
        if (subMenus != null && subMenus.length != 0)
            subMenu = (AddOn)subMenus[0];
        else 
            subMenu = addOn;
        for (Iterator<Command> it = deletedCommands.iterator(); it.hasNext(); ) {
            Command com = it.next();
            subMenu.insertCommand(com);
        }
        deletedCommands.clear();
        // update UI
        AddOnChildren children = (AddOnChildren)
                    rootNode.getChildren().getNodes()[0].getChildren(); 
        children.update(); // updates nodes
        panel.fireChangeEvent();
    }
    
    public void restoreCommand(Command com) {
        Node[] selected = manager.getSelectedNodes();
        Node selectedNode = null;
        if (selected == null || selected.length == 0) { // all commands removed
            selected = manager.getRootContext().getChildren().getNodes();
            if (selected != null && selected.length > 0) { // nothing selected: take top menu
                selectedNode = selected[0];
            }
            else { // top menu gone: must not happen!
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, 
                    "Top Level Menu is gone from menu structure"); // NOI18N
                selectedNode = manager.getRootContext(); // insert top level?
            }
        }
        else {
            selectedNode = selected[0]; // single tree selection
        }
        NbNodeObject object = (NbNodeObject)selectedNode.getLookup().lookup(NbNodeObject.class);
        int type = object.getType();
        if (type == NbNodeObject.UI_MENU_TYPE || type == NbNodeObject.ADDON_TYPE) {
            AddOn addOn = (AddOn)object;
            addOn.insertCommand(com);
        }
        else {
            AddOn addOn = (AddOn)object.getParent();
            selectedNode = selectedNode.getParentNode();
            addOn.insertCommand(com, object);
        }
        deletedCommands.remove(com);
        // update UI
        AddOnChildren children = (AddOnChildren)selectedNode.getChildren(); 
        children.update(); // updates nodes
        panel.fireChangeEvent();
    }
    
    public void addLanguageAction() {
        // get used languages
        AddOnNode rootNode = (AddOnNode)manager.getRootContext();
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        AddOn addon = (AddOn)nodeObject;
        LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)addon.getProperty(addon.PROPERTY_DisplayName);
        Integer[] indexes = prop.getUsedLanguageIndexes();
        int langID = LanguageHandlingDialog.start(false, indexes);  // add a language
        if (langID != -1) {
            addon.addLanguage(langID, null);
            rootNode.triggerLanguageChange();
            if (indexes.length == 0)  // first language added: trigger valid() with the change event...
                panel.fireChangeEvent(); // Notify that the panel changed
        }
    }

    public void deleteLanguageAction() {
        // get used languages
        AddOnNode rootNode = (AddOnNode)manager.getRootContext();
        // get the data object "AddIn"
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        AddOn addon = (AddOn)nodeObject;
        LocalizedOpenOfficeOrgProperty prop = (LocalizedOpenOfficeOrgProperty)addon.getProperty(addon.PROPERTY_DisplayName);
        Integer[] indexes = prop.getUsedLanguageIndexes();
        int langID = LanguageHandlingDialog.start(true, indexes);  // delete a language
        if (langID != -1) {
            addon.removeLanguage(langID);
            rootNode.triggerLanguageChange();
            if (indexes.length == 1)  // last language deleted: trigger valid() with the change event...
                panel.fireChangeEvent(); // Notify that the panel changed
        }
    }
    
    private NbNodeObject getSelectedObject() {
        NbNodeObject object = null;
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes != null && selectedNodes.length > 0) {
            AddOnNode addonNode = (AddOnNode)selectedNodes[0];
            if(addonNode != null)
                object = (NbNodeObject)addonNode.getLookup().lookup(NbNodeObject.class);
        }
        return object;
    }
    
    private void selectNextNode() {
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes != null && selectedNodes.length > 0) {
            Node theNode = (Node)selectedNodes[0];
            Node parentNode = theNode.getParentNode();
            Node[] nodes = parentNode.getChildren().getNodes(); // the array is sorted!
            boolean selectThisOne = false;
            for (int i = 0; i < nodes.length; i++) {
                if (selectThisOne) {
                    selectThisOne = false;
                    try {
                        manager.setSelectedNodes(new Node[]{nodes[i]});
                    } catch (PropertyVetoException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
                if (nodes[i].equals(theNode)) {
                    selectThisOne = true;
                }
            }
        }        
    }
    
    private Node getNextMenuNode() {
        AddOnNode addonNode = null;
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes != null && selectedNodes.length > 0) {
            addonNode = (AddOnNode)selectedNodes[0];
            while (addonNode != null && !addonNode.isMenu()) {
                addonNode = (AddOnNode)addonNode.getParentNode();
            }
        } 
        return addonNode;
    }

    public interface ActionPanel {
        void fireChangeEvent();
    }
}
