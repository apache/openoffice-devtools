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

package org.openoffice.extensions.util.datamodel;

import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public interface NbNodeObject {

    public static final int SERVICE_TYPE = 0;
    public static final int INTERFACE_TYPE = 1;
    public static final int ADDIN_TYPE = 2;
    public static final int PROPERTY_TYPE = 3;
    public static final int LOCALIZED_PROPERTY_TYPE = 4;
    public static final int FUNCTION_TYPE = 5;
    public static final int PARAMETER_TYPE = 6;
    public static final int MULTI_INHERITANCE_INTERFACE_TYPE = 7;
    public static final int OFFICE_SERVICE_TYPE = 8;
    public static final int OFFICE_INTERFACE_TYPE = 9;
    public static final int ENUM_TYPE = 10;
    public static final int ENUMERATION_TYPE = 11;
    public static final int STRUCT_TYPE = 12;
    public static final int EXCEPTION_TYPE = 13;
    public static final int POLY_STRUCT_TYPE = 14;
    public static final int TEMPLATE_TYPE = 15;
    public static final int ADDON_TYPE = 16;
    public static final int COMPONENT_TYPE = 17;
    public static final int CONSTANTS_TYPE = 18;

    public static final int UI_MENU_TYPE = 1024;
    public static final int UI_SEPARATOR_TYPE = 1025;
    
    /**
     * return the parent; maybe null for the root object
     * @return the parent node object
     */
    public NbNodeObject getParent();
    
    public String getDisplayName();
    
    public NbNodeObject[] getAllSubObjects();
    
    public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener);
    
    public boolean hasActions(int type);
    
    public Action[] getActions(boolean context);
    
    // this name is confusing rename to make the meaning clear: 
    // getActions() will deliver a wrapper around the actions set here
    public void setActions(BaseAction actions);

//    public void fireDataChangeEvent();
    
    public int getType();

}
