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
import java.util.Hashtable;
import org.openoffice.extensions.util.typebrowser.logic.TypeNode;

/**
 *
 * @author sg128468
 */
public class DataType {
    
    public static final int INTERFACE_TYPE = 0;
    public static final int STRUCT_TYPE = 1;
    public static final int ENUM_TYPE = 2;
    public static final int FOLDER_TYPE = 3;
    public static final int SERVICE_TYPE = 4;
    public static final int EXCEPTION_TYPE = 5;
    public static final int CONSTANT_TYPE = 6;
    public static final int POLY_STRUCT_TYPE = 7;

    
    public static final String SERVICE_TYPE_NAME = "Service"; // NOI18N
    public static final String INTERFACE_TYPE_NAME = "Interface"; // NOI18N
    public static final String STRUCT_TYPE_NAME = "Struct"; // NOI18N
    public static final String ENUM_TYPE_NAME = "Enumeration"; // NOI18N
    public static final String FOLDER_TYPE_NAME = "Folder"; // NOI18N
    public static final String EXCEPTION_TYPE_NAME = "Exception"; // NOI18N
    public static final String CONSTANT_TYPE_NAME = "Constant"; // NOI18N
    public static final String POLY_STRUCT_TYPE_NAME = "PolyStruct"; // NOI18N
    
    private String typeName;
    private int type;
    private String iconPath;
    private Hashtable<String, DataType> actualTypeObjects;  // only for a folder type that contains other types

    private DataType parent;
            
    public DataType(DataType parent, int type) {
        this(parent, type, FOLDER_TYPE);  // type shouldn't be FOLDER_TYPE for this c'tor
    }
    
    public DataType(DataType parent, int type, int nameType) {
        this.type = type;
        this.parent = parent;
        switch (type) {
            case STRUCT_TYPE:
                typeName = STRUCT_TYPE_NAME;
                iconPath = TypeNode.STRUCT_ICON;
                break;
            case ENUM_TYPE:
                typeName = ENUM_TYPE_NAME;
                iconPath = TypeNode.ENUM_ICON;
                break;
            case SERVICE_TYPE:
                typeName = SERVICE_TYPE_NAME;
                iconPath = TypeNode.SERVICE_ICON;
                break;
            case EXCEPTION_TYPE:
                typeName = EXCEPTION_TYPE_NAME;
                iconPath = TypeNode.EXCEPTION_ICON;
                break;
            case CONSTANT_TYPE:
                typeName = CONSTANT_TYPE_NAME;
                iconPath = TypeNode.CONSTANTS_ICON;
                break;
            case POLY_STRUCT_TYPE:
                typeName = POLY_STRUCT_TYPE_NAME;
                iconPath = TypeNode.POLY_STRUCT_ICON;
                break;
            case FOLDER_TYPE:
                switch(nameType) {
                    case STRUCT_TYPE:
                        typeName = STRUCT_TYPE_NAME;
                        break;
                    case ENUM_TYPE:
                        typeName = ENUM_TYPE_NAME;
                        break;
                    case INTERFACE_TYPE:
                        typeName = INTERFACE_TYPE_NAME;
                        break;
                    case SERVICE_TYPE:
                        typeName = SERVICE_TYPE_NAME;
                        break;
                    case EXCEPTION_TYPE:
                        typeName = EXCEPTION_TYPE_NAME;
                        break;
                    case CONSTANT_TYPE:
                        typeName = CONSTANT_TYPE_NAME;
                        break;
                    case POLY_STRUCT_TYPE:
                        typeName = POLY_STRUCT_TYPE_NAME;
                        break;
                    default:
                        typeName = FOLDER_TYPE_NAME;
                }
                iconPath = TypeNode.FOLDER_ICON;
                actualTypeObjects = new Hashtable<String, DataType>();
                break;
            default:
                typeName = INTERFACE_TYPE_NAME;
                iconPath = TypeNode.INTERFACE_ICON;
                this.type = INTERFACE_TYPE;
        }
    }
    
    public DataType(DataType parent, int type, String typeName) {
        this(parent, type);
        this.typeName = typeName;
    }
    
    public DataType getParent() {
        return this.parent;
    }
    
    public String getTypesName() {
        return typeName;
    }
   
    public int getType() {
        return type;
    }
    
    public String getIconPath() {
        return iconPath;
    }

    public DataType getTypeForName(String name) {
        if (actualTypeObjects != null) {
            return actualTypeObjects.get(name);
        }
        return null;
    }
    
    public void deleteType(String name) {
        if (actualTypeObjects != null) {
            actualTypeObjects.remove(name);
        }
    }
    
    public void addTypeWithName(String name, DataType type) {
        if (actualTypeObjects != null) {
            actualTypeObjects.put(name, type);
        }
    }
    
    public Collection<DataType> getAllTypes() {
        if (actualTypeObjects != null) {
            return actualTypeObjects.values();
        }
        return null;
    }
    
    public static final int getTypeIdForName(String name) {
        if (name.equals(INTERFACE_TYPE_NAME))
            return INTERFACE_TYPE;
        else if (name.equals(ENUM_TYPE_NAME))
            return ENUM_TYPE;
        else if (name.equals(FOLDER_TYPE_NAME))
            return FOLDER_TYPE;
        else if (name.equals(SERVICE_TYPE_NAME))
            return SERVICE_TYPE;
        else if (name.equals(EXCEPTION_TYPE_NAME))
            return EXCEPTION_TYPE;
        else if (name.equals(CONSTANT_TYPE_NAME))
            return CONSTANT_TYPE;
        else if (name.equals(POLY_STRUCT_TYPE_NAME))
            return POLY_STRUCT_TYPE;
        else if (name.equals(STRUCT_TYPE_NAME))
            return STRUCT_TYPE;
        return -1;
    }
    
    public static final String getTypeNameForId(int id) {
        switch(id) {
            case INTERFACE_TYPE: return INTERFACE_TYPE_NAME;
            case STRUCT_TYPE: return STRUCT_TYPE_NAME;
            case ENUM_TYPE: return ENUM_TYPE_NAME;
            case FOLDER_TYPE: return FOLDER_TYPE_NAME;
            case SERVICE_TYPE: return SERVICE_TYPE_NAME;
            case EXCEPTION_TYPE: return EXCEPTION_TYPE_NAME;
            case CONSTANT_TYPE: return CONSTANT_TYPE_NAME;
            case POLY_STRUCT_TYPE: return POLY_STRUCT_TYPE_NAME;
        }
        return null;
    }
    
}
