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
package org.openoffice.extensions.projecttemplates.component.datamodel.types.node;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openoffice.extensions.util.datamodel.NbNodeObject;

/**
 *
 * @author sg128468
 */
public class ComponentTypeChildren extends Children.Keys<NbNodeObject> {

    private boolean noChildren;

    /**
     * Creates a new instance of ComponentTypeChildren
     */
    public ComponentTypeChildren() {
        this(false);
    }

    public ComponentTypeChildren(boolean noChildren) {
        this.noChildren = noChildren;
    }

    protected Node[] createNodes(NbNodeObject nbNode) {
        Node[] vNodes = null;
        if (nbNode.getAllSubObjects() == null || noChildren) {
            vNodes = new Node[]{new ComponentTypeNode(nbNode, Children.LEAF)};
        } else {
            vNodes = new Node[]{new ComponentTypeNode(nbNode, new ComponentTypeChildren())};
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
}
