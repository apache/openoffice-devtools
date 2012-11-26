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
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;

/**
 *
 * @author sg128468
 */
public class TypeNodeChildren extends Children.Keys<DataType> {
    
    /** Creates a new instance of TypeNodeChildren */
    public TypeNodeChildren() {
    }

    protected Node[] createNodes(DataType object) {
        Node[] node = null;
        if (object != null) {
            Collection coll = object.getAllTypes();
            if ((coll != null && coll.size() > 0) || object.getType() == DataType.FOLDER_TYPE)
                node = new Node[]{new DataTypeNode(object, new TypeNodeChildren())};
            else 
                node = new Node[]{new DataTypeNode(object, Children.LEAF)};
        }
        return node;
    }

    @Override
    protected void addNotify() {
        Node node = getNode();
        if (node instanceof TypeRootNode) {
            TypeRootNode set = (TypeRootNode)node;
            if (set.hasChildren()) {
                setKeys(set.getSubTypesAsCollection());
            }
        }
        else {
            Object o = node.getLookup().lookup(DataType.class);
            if (o != null) {
                DataType set = (DataType)o;
                Collection<DataType> objects = set.getAllTypes();
                if (objects != null)
                    setKeys(objects);
            }
        }
    }
    
    @Override
    protected void removeNotify() {
        setKeys(new DataType[]{});
    }
    
    public void update() {
        addNotify();
    }
}
