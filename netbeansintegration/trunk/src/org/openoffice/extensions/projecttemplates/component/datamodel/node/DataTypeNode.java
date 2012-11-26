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

import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
import org.openoffice.extensions.projecttemplates.component.datamodel.DataType;
import org.openoffice.extensions.util.typebrowser.logic.TypeNode;

/**
 *
 * @author sg128468
 */
public class DataTypeNode extends AbstractNode {
    
    String displayName;
    String iconPath;
    
    /**
     * Creates a new instance of DataTypeNode
     */
    public DataTypeNode(DataType obj, Children children) {
        super(children, Lookups.singleton(obj));
        this.displayName = obj.getTypesName();
        this.iconPath = obj.getIconPath();
        this.setName(displayName);
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public Image getIcon(int type) {
       return ImageUtilities.loadImage(iconPath);
    }
    
    public Image getOpenedIcon (int type) {
        DataType dataType = (DataType)this.getLookup().lookup(DataType.class);
        if (dataType.getType() == DataType.FOLDER_TYPE) {
           return ImageUtilities.loadImage(TypeNode.OPEN_FOLDER_ICON);
        }
        return getIcon(type);
    }
}
