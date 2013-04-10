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

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.projecttemplates.addon.datamodel.Command;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class AddOnChildren extends Children.Keys<NbNodeObject> {

    private boolean displayCommandProps;
    private boolean showSeparatorIcon;
    private String context;
    public static final String BACK_END = "Back-End"; // NOI18N
    public static final String ALL = "All"; // NOI18N

    /**
     * Creates a new instance of AddOnChildren
     */
    public AddOnChildren(boolean displayCommandProps, boolean showSeparatorIcon) {
        this(ALL, displayCommandProps, showSeparatorIcon);
    }

    /**
     * Creates a new instance of AddOnChildren
     */
    public AddOnChildren(String context, boolean displayCommandProps, boolean showSeparatorIcon) {
        this.displayCommandProps = displayCommandProps;
        this.showSeparatorIcon = showSeparatorIcon;
        this.context = context;
    }

    public AddOnChildren() {
        this(ALL, true, false);
    }

    protected Node[] createNodes(NbNodeObject nbNode) {
        Node[] vNodes = null;
        boolean display = getNodeDisplay(nbNode);
        if (display) {
            if (nbNode.getType() != NbNodeObject.ADDON_TYPE && nbNode.getType() != NbNodeObject.UI_MENU_TYPE) {
                vNodes = new Node[]{new AddOnNode(nbNode, Children.LEAF, displayCommandProps, showSeparatorIcon)};
            } else {
                vNodes = new Node[]{new AddOnNode(nbNode, new AddOnChildren(context, displayCommandProps, showSeparatorIcon), displayCommandProps, showSeparatorIcon)};
            }
        }
        return vNodes;
    }

    @Override
    protected void addNotify() {
        Node node = getNode();
        Object o = node.getLookup().lookup(NbNodeObject.class);
        if (o != null) {
            NbNodeObject set = (NbNodeObject) o;
            NbNodeObject[] objects = set.getAllSubObjects();
            if (objects != null) {
                setKeys(objects);
            }
        }
    }

    @Override
    protected void removeNotify() {
        setKeys(new NbNodeObject[]{});
    }

    public void update() {
        addNotify();
    }

    /**
     * Determine if this object's node should be displayed.
     * @param set the object whose node may be displayed
     * @return true if the node has to be displayed
     */
    private boolean getNodeDisplay(NbNodeObject set) {
        // no need to do much for diplaying all
        if (context.equals(ALL)) {
            return true;
        }
        switch (set.getType()) {
            // display when commands inside have the correct context
            case NbNodeObject.ADDON_TYPE:
            case NbNodeObject.UI_MENU_TYPE:
                AddOn addon = (AddOn) set;
                NbNodeObject[] commandObjects = addon.getAllCommands();
                for (int i = 0; i < commandObjects.length; i++) {
                    if (getCommandDisplay((Command) commandObjects[i], context)) {
                        return true;
                    }
                }
                break;
            case NbNodeObject.UI_SEPARATOR_TYPE:
                // must check if there are displayable objects before and after the separator
                NbNodeObject parentObject = set.getParent();
                AddOn parent = (AddOn) parentObject;
                NbNodeObject[] subObjects = parent.getAllSubObjects();
                boolean candidate = false;
                boolean preSeparator = false;
                for (int i = 0; i < subObjects.length; i++) {
                    if (subObjects[i].getType() != NbNodeObject.UI_SEPARATOR_TYPE) {
                        candidate |= getNodeDisplay(subObjects[i]);
                    }
                    // a valid sep is between stuff, not at start, not at end
                    if (subObjects[i].equals(set)) {
                        if (candidate) {
                            preSeparator = true;
                        }
                        candidate = false;
                    }
                }
                if (candidate && preSeparator) {
                    return true;
                }
                break;
            case NbNodeObject.FUNCTION_TYPE:
                return getCommandDisplay((Command) set, context);
            default:  // if display cannot be determined, do not display
        }
        return false;
    }

    /**
     * Determine if this command's node should be displayed.
     * @param command the command whose node may be displayed
     * @return true if the command has to be displayed
     */
    public static boolean getCommandDisplay(Command command, String context) {
        try {
            if (!context.equals(BACK_END)) {
                OpenOfficeOrgBooleanProperty boolProp = (OpenOfficeOrgBooleanProperty) command.getProperty(context);
                if (boolProp.getValue()) {
                    return true;
                }
            }
            // this is no else: if context is false, maybe none is set
            int count = PropertyContainer.PROPERTY_CONTAINER_CONTEXTS.length;
            for (int j = 0; j < PropertyContainer.PROPERTY_CONTAINER_CONTEXTS.length; j++) {
                OpenOfficeOrgBooleanProperty boolProp =
                        (OpenOfficeOrgBooleanProperty) command.getProperty(
                        PropertyContainer.PROPERTY_CONTAINER_CONTEXTS[j]);
                if (!boolProp.getValue()) {
                    // maybe none is selected: meaning "Back-End"
                    count--;
                }
            }
            if (count == 0) {
                return true;
            }
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
