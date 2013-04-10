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

package org.openoffice.extensions.projecttemplates.component.datamodel;

import java.util.Collection;
import java.util.Vector;

/**
 *
 * @author sg128468
 */
public class Interface {
    
    private static final String ICON_PATH = 
            "org/openoffice/extensions/projecttemplates/component/icons/interface.png"; // NOI18N

    private String name;
    private String displayName;
    
    private Vector<Interface> ifcs;

    private boolean isOwnDesign;

    private String pkg;
    
    /** Creates a new instance of Interface */
    public Interface(String name, String pkg) {
        this(name, pkg, false);
    }

    /** Creates a new instance of Interface */
    public Interface(String name, String pkg, boolean isOwnDesign) {
        this.name = name;
        this.isOwnDesign = isOwnDesign;
        this.pkg =pkg;
        ifcs = new Vector<Interface>();
//        displayName = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setHierarchicalName(String fullName) {
        int index = fullName.lastIndexOf('.');
        if (index != -1) {
            pkg = fullName.substring(0, index);
            name = fullName.substring(index + 1);
        }
        else  {
            pkg = ""; // NOI18N
            name = fullName;
        }
    }
    
    public String getHierarchicalName() {
        return pkg.concat(".").concat(name); // NOI18N
    }
    
    public String getDisplayName() {
        return getHierarchicalName();
    }
    
    public String getIconPath() {
        return ICON_PATH;
    }
    
    public void addInterface(Interface ifc) {
        if (isOwnDesign) 
            ifcs.add(ifc);
    }
    
    public void removeInterface(Interface ifc) {
        if (isOwnDesign) 
            ifcs.remove(ifc);
    }
    
    public Collection<Interface> getInterfaces() {
        return ifcs;
    }

    public boolean isOwnDesign() {
        return isOwnDesign;
    }
}
