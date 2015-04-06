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
import java.util.Vector;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;

/**
 *
 * @author sg128468
 */
public class TypeRootNode extends AbstractNode {

    private Vector<DataType> subTypes;
    
    /** Creates a new instance of TypeRootNode */
    public TypeRootNode(Children children) {
        super(children);
        subTypes = new Vector<DataType>();
    }

    public Collection<DataType> getSubTypesAsCollection() {
        return subTypes;
    }
    
    public boolean hasChildren() {
        return subTypes.size() > 0;
    }
    
    public void addSubType(DataType subType) {
        if (subType != null) {
            subTypes.add(subType);
        }
    }
    
    public void removeSubType(DataType subType) {
        if (subType != null) {
            subTypes.remove(subType);
        }
    }
    
    public static Node createInitialNodeStructure(String serviceName, String ifcName) {
        TypeRootNode node = new TypeRootNode(new TypeNodeChildren());
        DataType serviceDataType = new DataType(null, DataType.FOLDER_TYPE, DataType.SERVICE_TYPE);
        DataType interfaceDataType = new DataType(null, DataType.FOLDER_TYPE, DataType.INTERFACE_TYPE);
        DataType enumDataType = new DataType(null, DataType.FOLDER_TYPE, DataType.ENUM_TYPE);
        DataType structDataType = new DataType(null, DataType.FOLDER_TYPE, DataType.STRUCT_TYPE);
        DataType exceptionDataType = new DataType(null, DataType.FOLDER_TYPE, DataType.EXCEPTION_TYPE);
        DataType polyStructDataType = new DataType(null, DataType.FOLDER_TYPE, DataType.POLY_STRUCT_TYPE);
        node.addSubType(serviceDataType);
        node.addSubType(interfaceDataType);
        node.addSubType(enumDataType);
        node.addSubType(structDataType);
        node.addSubType(exceptionDataType);
        node.addSubType(polyStructDataType);
        return node;
    }
}
