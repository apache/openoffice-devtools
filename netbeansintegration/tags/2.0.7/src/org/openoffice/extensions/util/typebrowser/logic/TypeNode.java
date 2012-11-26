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

package org.openoffice.extensions.util.typebrowser.logic;

import java.awt.Image;
import java.util.Vector;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.office.OfficeConnection;
import org.openoffice.extensions.util.office.ReflectionWrapper;

public class TypeNode extends AbstractNode {
    
    public static final String ERROR_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/moduleerror.png"; // NOI18N
    public static final String MODULE_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/module.png"; // NOI18N
    public static final String INTERFACE_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/interface.png"; // NOI18N
    public static final String SERVICE_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/service.png"; // NOI18N
    public static final String EXCEPTION_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/exception.png"; // NOI18N
    public static final String STRUCT_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/struct.png"; // NOI18N
    public static final String POLY_STRUCT_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/struct.png"; // NOI18N
    public static final String CONSTANTS_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/enum.png"; // NOI18N
    public static final String ENUM_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/enum.png"; // NOI18N
    public static final String FOLDER_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/folder.png"; // NOI18N
    public static final String OPEN_FOLDER_ICON = 
            "org/openoffice/extensions/projecttemplates/component/icons/folderopen.png"; // NOI18N
    
    private static String[] displayedTypes = new String[]{
        UnoTypes.MODULE, UnoTypes.SERVICE, UnoTypes.INTERFACE
    };

    private static NbNodeObject[] addNodes;  // custom stuff from project, not office
    
    private String hierarchicalName;
    private String iconPath;
    // denotes that the node displays a type not from the office but self created
    private boolean ownDesign;
    private int type;
    
    protected TypeNode(String typeName, int type, Children children) {
        super(children);
        hierarchicalName = typeName;
        this.type = type & 127;
        ownDesign = (type >= UnoTypes.OWN_DESIGN_TYPE);
        this.iconPath = getIconPathFromType(this.type);
    }

    public String getDisplayName() {
        // when with '.' only use name from then on
        if (hierarchicalName.indexOf('.') != -1)
            return hierarchicalName.substring(hierarchicalName.lastIndexOf('.') + 1);
        return hierarchicalName;
    }

    public String getHierarchicalName() {
        return hierarchicalName;
    }
    
    public Image getIcon(int type) {
       return ImageUtilities.loadImage(iconPath);
    }
    
    public Image getOpenedIcon (int type) {
        return getIcon(type);
    }
    
    public static void setDisplayedTypes(String[] types) {
        Vector<String> v = new Vector<String>();
        for (int i=0; i<types.length; i++) {
            v.add(types[i]);
        }
        if (!v.contains(UnoTypes.MODULE)) {
            v.add(UnoTypes.MODULE);
        }
        displayedTypes = v.toArray(new String[v.size()]);
    }
    
    public static String[] getDisplayedTypes() {
        return displayedTypes;
    }
    
    public static String[] getDisplayedOfficeTypes() {
        Vector<String>v = new Vector<String>();
        for (int i=0; i<displayedTypes.length; i++) {
            if (!displayedTypes[i].equals(UnoTypes.SIMPLE)) {
                v.add(displayedTypes[i]);
            }
        }
        return v.toArray(new String[v.size()]);
    }
    
    public static boolean displaysType(String type) {
        for (int i=0; i<displayedTypes.length; i++) {
            if (type.equals(displayedTypes[i])) {
                return true;
            }
        }
        return false;
    }
    
    public int getNodeType () {
        return type;
    }
    
    public boolean isOwnDesign() {
        return ownDesign;
    }
    
    private String getIconPathFromType(int type) {
        switch (type) {
            case UnoTypes.ERROR_TYPE:
                return ERROR_ICON;
            case UnoTypes.SERVICE_TYPE:
                return SERVICE_ICON;
            case UnoTypes.INTERFACE_TYPE:
                return INTERFACE_ICON;
            case UnoTypes.MODULE_TYPE:
                return MODULE_ICON;
            case UnoTypes.EXCEPTION_TYPE:
                return EXCEPTION_ICON;
            case UnoTypes.CONSTANTS_TYPE:
                return CONSTANTS_ICON;
            case UnoTypes.STRUCT_TYPE:
                return STRUCT_ICON;
            case UnoTypes.POLY_STRUCT_TYPE:
                return POLY_STRUCT_ICON;
            case UnoTypes.ENUM_TYPE:
                return ENUM_ICON;
            default:
                return SERVICE_ICON;
        }
    }
    
    public static TypeNode createRootNode(String[] types) {
        if (types == null || types.length == 0) {
            types = displayedTypes; // standard as fallback
        }
        setDisplayedTypes(types);
        Object xTDMgr = null;
        Object xCtx = OfficeConnection.getComponentContext();
        ReflectionWrapper wrapper = OfficeConnection.getReflectionWrapper();
        try {
            Object o = wrapper.executeMethod(xCtx, 
                    "getValueByName", new Object[]{
                    "/singletons/com.sun.star.reflection.theTypeDescriptionManager"}); // NOI18N
            Class classType = wrapper.forName("com.sun.star.reflection.XTypeDescriptionEnumerationAccess"); // NOI18N
            xTDMgr = wrapper.executeStaticMethod("com.sun.star.uno.AnyConverter", "toObject", new Object[]{classType, o}); // NOI18N
        }
        catch (java.lang.Exception e) {
            return new TypeNode("<No Types Available>", UnoTypes.ERROR_TYPE, new TypeBrowserChildren()); // NOI18N
        }
        return new TypeNode("root", UnoTypes.MODULE_TYPE, new TypeBrowserChildren(xTDMgr, wrapper)); // NOI18N
    }
    
    public static void setDesignedTypes(NbNodeObject[] aNodes) {
        addNodes = aNodes;
    }
    public static NbNodeObject[] getDesignedTypes() {
        return addNodes;
    }
    
}
