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

package org.openoffice.extensions.projecttemplates.component;

import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.component.dialogs.ChangeEventPanel;
import org.openoffice.extensions.projecttemplates.component.dialogs.ValidateDataType;
import org.openoffice.extensions.util.datamodel.Function;
import org.openoffice.extensions.util.datamodel.IdlEnum;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Parameter;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypeChildren;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypeNode;
import org.openoffice.extensions.util.datamodel.IdlEnumeration;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.Struct;

/**
 *
 * @author sg128468
 */
public class ComponentActions {

    private ExplorerManager manager;
    private ChangeEventPanel panel;
    
    /**
     * Creates a new instance of ComponentActions
     */
    public ComponentActions(ExplorerManager manager, ChangeEventPanel panel) {
        this.manager = manager;
        this.panel = panel;
    }

    public void deleteActions() {
        // because of TreeSelectionModel.SINGLE_TREE_SELECTION max. one node is selected
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node selectedNode = selectedNodes[0];
            Node ifcNode = selectedNode.getParentNode();
            NbNodeObject nodeObject = (NbNodeObject)selectedNode.getLookup().lookup(NbNodeObject.class);
            if (nodeObject.getType() == NbNodeObject.FUNCTION_TYPE) {
                Function f = (Function)nodeObject;
                Interface ifc = (Interface)nodeObject.getParent();
                ifc.removeFunction(f);
                // update UI
                updateUI(ifcNode); 
            }
            else if (nodeObject.getType() == NbNodeObject.PARAMETER_TYPE) {
                Parameter p = (Parameter)nodeObject;
                NbNodeObject parentNode = nodeObject.getParent();
                Node functionNode = selectedNode.getParentNode();
                if (parentNode.getType() == NbNodeObject.FUNCTION_TYPE) {
                    Function f = (Function)parentNode;
                    f.removeParameter(p);
                    ((ComponentTypeNode)functionNode).triggerChangeEvent(); // updates function display name
                }
                else if (parentNode.getType() == NbNodeObject.STRUCT_TYPE) {
                    Struct s = (Struct)parentNode;
                    s.removeStructType(p);
                }
                updateUI(functionNode);
            }
            else if (nodeObject.getType() == NbNodeObject.ENUM_TYPE) {
                IdlEnum e = (IdlEnum)nodeObject;
                IdlEnumeration enm = (IdlEnumeration)nodeObject.getParent();
                enm.removeEnum(e);
                // update UI
                updateUI(selectedNode.getParentNode()); 
            }
        }
    }
    
    public void addParameterAction() {
        // because of TreeSelectionModel.SINGLE_TREE_SELECTION max. one node is selected
        Node[] selectedNodes = manager.getSelectedNodes();
        if (selectedNodes.length > 0) {
            Node selectedNode = selectedNodes[0];

            // get the data object
            NbNodeObject nodeObject = (NbNodeObject)selectedNode.getLookup().lookup(NbNodeObject.class);
            if (nodeObject.getType() == NbNodeObject.FUNCTION_TYPE) {
                Function f = (Function)nodeObject;
                f.addParameter(nodeObject); // addParameter after
                // update UI
                ((ComponentTypeNode)selectedNode).triggerChangeEvent();  // updates function display name
                updateUI(selectedNode); 
            }
            else if (nodeObject.getType() == NbNodeObject.STRUCT_TYPE) {
                Struct s = (Struct)nodeObject;
                s.addStructType(nodeObject);
                // update UI
                updateUI(selectedNode); 
            }
            else if (nodeObject.getType() == NbNodeObject.PARAMETER_TYPE) {
                NbNodeObject parentNode = nodeObject.getParent();
                if (parentNode.getType() == NbNodeObject.FUNCTION_TYPE) {
                    Function f = (Function)parentNode;
                    f.addParameter(nodeObject); // addParameter after
                    // update UI
                    Node functionNode = selectedNode.getParentNode();
                    ((ComponentTypeNode)functionNode).triggerChangeEvent(); // updates function display name
                    updateUI(functionNode);
                }
                else if (parentNode.getType() == NbNodeObject.STRUCT_TYPE) {
                    Struct s = (Struct)parentNode;
                    s.addStructType(nodeObject); // addParameter after
                    // update UI
                    updateUI(selectedNode.getParentNode());
                }
            }
        }        
//        panel.fireChangeEvent(); // Notify that the panel changed
    }
    
    public void addFunctionAction() {
        // get root node
        ComponentTypeNode rootNode = (ComponentTypeNode)manager.getRootContext();
        // get the data object 
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        Interface ifc = (Interface)nodeObject;
        // add function
        ifc.addFunction();

        // update UI
        updateUI(rootNode);
    }
    
    public void addEnumAction() {
        // get root node
        ComponentTypeNode rootNode = (ComponentTypeNode)manager.getRootContext();
        // get the data object 
        NbNodeObject nodeObject = (NbNodeObject)rootNode.getLookup().lookup(NbNodeObject.class);
        IdlEnumeration enm = (IdlEnumeration)nodeObject;
        enm.addEnum();

        // update UI
        updateUI(rootNode);
    }
    
    public void updateUI(Node node) {
        ComponentTypeChildren children = (ComponentTypeChildren)node.getChildren();
        children.update(); // updates nodes
        if (panel != null)
            panel.fireChangeEvent(); // Notify that the panel changed
    }
}
