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
import org.openoffice.extensions.projecttemplates.component.datamodel.Service;

/**
 *
 * @author sg128468
 */
public class ServiceNode extends AbstractNode {

    private boolean deletable;
    
    /**
     * Creates a new instance of ServiceNode
     */
    public ServiceNode(Service obj, Children children) {
        super(children, Lookups.singleton(obj));
    }

    public Image getIcon(int type) {
        Service svc = (Service)this.getLookup().lookup(Service.class);
        return ImageUtilities.loadImage(svc.getIconPath());
    }
    
    public Image getOpenedIcon (int type) {
        return getIcon(type);
    }

    public String getDisplayName() {
        Service svc = (Service)this.getLookup().lookup(Service.class);
        return svc.getDisplayName();
    }
    
    public void update() {
        Service svc = (Service)this.getLookup().lookup(Service.class);
        this.fireDisplayNameChange("", svc.getDisplayName()); // NOI18N
    }
}
