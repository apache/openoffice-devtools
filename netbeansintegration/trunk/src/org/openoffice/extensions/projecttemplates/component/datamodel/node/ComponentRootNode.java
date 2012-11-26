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
import java.util.Collection;
import java.util.Vector;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openoffice.extensions.projecttemplates.component.ComponentWizardIterator;

/**
 *
 * @author sg128468
 */
public class ComponentRootNode extends AbstractNode {
    
    Vector<Object> subTypes;
    private static final String ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/folder.png"; // NOI18N
    private static final String OPEN_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/folderopen.png"; // NOI18N
    
    /**
     * Creates a new instance of ComponentRootNode
     */
    public ComponentRootNode(Object obj, Children children) {
        super(children, Lookups.singleton(obj));
        subTypes = new Vector<Object>();
    }
    
    public Collection<Object> getSubTypesAsCollection() {
        return subTypes;
    }
   
    public boolean hasChildren() {
        return subTypes.size() > 0;
    }
    
    public void addSubType(Object subType) {
        if (subType != null) {
            subTypes.add(subType);
        }
    }

    public void removeSubType(Object subType) {
        if (subType != null) {
            subTypes.remove(subType);
        }
    }
    
    public static Node createInitialNodeStructure(String serviceName, String interfaceName) {
        ComponentRootNode node = new ComponentRootNode(new Object(), new ComponentNodeChildren());
/*        Service serv = new Service(serviceName, false);
        Interface ifc = new Interface(interfaceName);
        serv.addInterface(ifc);
        node.addSubType(serv); */
        return node;
    }
    
    public void update() {
        this.fireDisplayNameChange("", "newname"); // NOI18N
    }

    public String getDisplayName() {
        return NbBundle.getMessage(ComponentWizardIterator.class, "LBL_Implement");
    }

    public Image getIcon(int i) {
        return ImageUtilities.loadImage(ICON);
    }

    public Image getOpenedIcon(int i) {
        return ImageUtilities.loadImage(OPEN_ICON);
    }
}

