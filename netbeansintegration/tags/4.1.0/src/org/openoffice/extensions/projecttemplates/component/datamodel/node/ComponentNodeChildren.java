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

package org.openoffice.extensions.projecttemplates.component.datamodel.node;

import java.util.Collection;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.component.datamodel.Interface;
import org.openoffice.extensions.projecttemplates.component.datamodel.Service;

/**
 *
 * @author sg128468
 */
public class ComponentNodeChildren extends Children.Keys<Object> {
    
    /**
     * Creates a new instance of ComponentNodeChildren
     */
    public ComponentNodeChildren() {
    }

    protected Node[] createNodes(Object object) {
        Node[] node = null;
        if (object instanceof Service) {
            node = new Node[]{new ServiceNode(
                    (Service)object, new ComponentNodeChildren())};
        }
        else if (object instanceof Interface) {
            node = new Node[]{new InterfaceNode(
                    (Interface)object, new ComponentNodeChildren())};
        }
/*        else {
            node = new Node[]{new ComponentRootNode(
                    object, new ComponentNodeChildren())};
        } */

        return node;
    }

    @Override
    protected void addNotify() {
        Node node = getNode();
        if (node instanceof ComponentRootNode) {
            ComponentRootNode set = (ComponentRootNode)node;
            if (set.hasChildren()) {
                setKeys(set.getSubTypesAsCollection());
            }
            else {
                setKeys(new Object[]{});
            }
        }
        else if (node instanceof ServiceNode) {
            Service serv = (Service)node.getLookup().lookup(Service.class);
            if (serv.getInterfaces().size() > 0) {
                Collection<Interface> ifcs = serv.getInterfaces();
                setKeys(ifcs);
            }
            else {
                setKeys(new Object[]{});
            }
        }
        else if (node instanceof InterfaceNode) {
            Interface inf = (Interface)node.getLookup().lookup(Interface.class);
            if (inf.getInterfaces().size() > 0) {
                Collection<Interface> ifcs = inf.getInterfaces();
                setKeys(ifcs);
            }
            else {
                setKeys(new Object[]{});
            }
        }
    }
    
    @Override
    protected void removeNotify() {
        setKeys(new Object[]{} );
    }
    
    public void update() {
        addNotify();
    }
}
