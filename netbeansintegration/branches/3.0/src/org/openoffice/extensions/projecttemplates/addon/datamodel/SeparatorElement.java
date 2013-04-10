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
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public class SeparatorElement implements NbNodeObject {

    private NbNodeObject parent;

    private BaseAction baseActions;
    private String name;
    private String displayName;
    
    /** Creates a new instance of SeparatorElement */
    public SeparatorElement(NbNodeObject parent) {
        displayName = "----------"; // NOI18N
        name = ""; // NOI18N
        this.parent = parent;
    }

    public NbNodeObject getParent() {
        return parent;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public NbNodeObject[] getAllSubObjects() {
        return new NbNodeObject[0];
    }

    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
        return new Node.Property[0];
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
        return NbNodeObject.UI_SEPARATOR_TYPE;
    }
    
}
