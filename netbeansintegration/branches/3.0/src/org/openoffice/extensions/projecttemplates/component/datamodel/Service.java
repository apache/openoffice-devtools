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
public class Service {
    
    private String name;
    private String displayName;
    private static final String ICON_PATH = 
            "org/openoffice/extensions/projecttemplates/component/icons/service.png"; // NOI18N
    private Interface ifc;

    private boolean isOwnDesign;

    private String pkg;
    
    /** Creates a new instance of Service */
    public Service(String name, String pkg) {
        this(name, pkg, true);
    }

    /** Creates a new instance of Service */
    public Service(String name, String pkg, boolean isOwnDesign) {
        this.name = name;
        this.isOwnDesign = isOwnDesign;
        this.pkg = pkg;
//        displayName = name;// NbBundle.getMessage(Service.class, "LBL_ServiceDisplayName");
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
        this.ifc = ifc;
    }
    
    public void removeInterface(Interface ifc) {
        if (ifc.equals(this.ifc)) {
            this.ifc = null;
        }
    }
    
    public Collection<Interface> getInterfaces() {
        Vector<Interface> v = new Vector<Interface>();
        if (ifc != null)
            v.add(ifc);
        return v;
    }
    
    public boolean isOwnDesign() {
        return isOwnDesign;
    }
}
