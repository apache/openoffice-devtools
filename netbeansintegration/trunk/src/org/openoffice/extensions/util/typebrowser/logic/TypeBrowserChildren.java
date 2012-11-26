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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;
import org.openoffice.extensions.util.office.ReflectionWrapper;

/**
 *
 * @author sg128468
 */
public class TypeBrowserChildren extends Children.Keys<TypeBrowserChildren.TypeData> {

    private ReflectionWrapper wrapper;
    private Object xTDMgr;
    
    /** Creates a new instance of TypeBrowserChildren */
    public TypeBrowserChildren(Object xTDMgr, ReflectionWrapper wrapper) {
        this.wrapper = wrapper;
        this.xTDMgr = xTDMgr;
    }

    /** Creates a new instance of TypeBrowserChildren */
    public TypeBrowserChildren() {
    }
    
    protected Node[] createNodes(TypeData newData) {
        Node[] node = null;
        String name = newData.getName();
        int type = newData.getType();
        if ((type & 127) == UnoTypes.MODULE_TYPE)
            node = new Node[]{new TypeNode(name, type, new TypeBrowserChildren(xTDMgr, wrapper))};
        else 
            node = new Node[]{new TypeNode(name, type, Children.LEAF)};
        return node;
    }
    
    @Override
    protected void addNotify() {
        TypeNode node = (TypeNode)getNode();
        String name = node.getHierarchicalName();
        if (name.equals("root")) name = ""; // NOI18N

        String[] types = TypeNode.getDisplayedTypes();
        Vector<TypeData> newTypes = new Vector<TypeData>();

        // primitive data types, if desired
        if (TypeNode.displaysType(UnoTypes.SIMPLE) && name.length() == 0) {
            for (int i=1; i<UnoTypes.SIMPLE_TYPE_TAGS.length; i++) {  // do not take void...
                String newType = UnoTypes.SIMPLE_TYPE_TAGS[i];
                newTypes.add(new TypeData(newType, UnoTypes.SIMPLE_TYPE));
            }
        }
        // additional interfaces and services that were own designed 
        NbNodeObject[] addNodes = TypeNode.getDesignedTypes();
        if (addNodes != null) {
            for (int i=0; i<addNodes.length; i++) {
                if (name.length() == 0) { // first module under root
                    int dataType = UnoTypes.MODULE_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                    if (isIntTypeInStringTypes(addNodes[i].getType(), types)) {
                        String newType = null;
                        try {
                            newType = ((PropertyContainer)addNodes[i]).getSimpleProperty(Service.PROPERTY_CONTAINER_PACKAGE);
                            int index = newType.indexOf('.');
                            if (index > 0) {
                                newType = newType.substring(0, index);
                            }
                            TypeData d = new TypeData(newType, dataType);
                            if (!contains(newTypes, d)) {
                                newTypes.add(d);
                            }
                        } catch (UnknownOpenOfficeOrgPropertyException ex) {
                            LogWriter.getLogWriter().printStackTrace(ex);
                        }
                    }
                }
                else {
                    if (isIntTypeInStringTypes(addNodes[i].getType(), types)) {
                        String newType = null;
                        try {
                            newType = ((PropertyContainer)addNodes[i]).getSimpleProperty(Service.PROPERTY_CONTAINER_PACKAGE);
                            if (newType.startsWith(name)) {
                                int newLength = name.length() + 1;
                                int nextDotIndex = -2;
                                if (newLength <= newType.length()) {  // this mens we have the complete paackage added
                                    nextDotIndex = newType.indexOf('.', newLength);
                                }
                                int dataType = UnoTypes.MODULE_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                                if (nextDotIndex == -2) { // complete package is there: add srvice/ifc name now
                                    String simpleName = ((PropertyContainer)addNodes[i]).getSimpleProperty(Service.PROPERTY_CONTAINER_NAME);
                                    if (newType.indexOf(simpleName) == -1) {
                                        newType = newType.concat(".").concat(simpleName); // NOI18N
                                        int nodesType = addNodes[i].getType();
                                        if (nodesType == NbNodeObject.SERVICE_TYPE) {
                                            dataType = UnoTypes.SERVICE_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                                        }
                                        else if (nodesType == NbNodeObject.INTERFACE_TYPE) {
                                            dataType = UnoTypes.INTERFACE_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                                        }
                                        else if (nodesType == NbNodeObject.STRUCT_TYPE) {
                                            dataType = UnoTypes.STRUCT_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                                        }
                                        else if (nodesType == NbNodeObject.POLY_STRUCT_TYPE) {
                                            dataType = UnoTypes.POLY_STRUCT_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                                        }
                                        else if (nodesType == NbNodeObject.EXCEPTION_TYPE) {
                                            dataType = UnoTypes.EXCEPTION_TYPE + UnoTypes.OWN_DESIGN_TYPE;
                                        }
                                    }
                                }
                                else if (nextDotIndex != -1) {  
                                    newType = newType.substring(0, nextDotIndex);
                                }  // else: keep the complete package name
                                TypeData d = new TypeData(newType, dataType);
                                if (!contains(newTypes, d)) {
                                    newTypes.add(d);
                                }
                            }
                        } catch (UnknownOpenOfficeOrgPropertyException ex) {
                            LogWriter.getLogWriter().printStackTrace(ex);
                        }
                    }
                }
            }
        }
        // types from the Office
        try {
            types = TypeNode.getDisplayedOfficeTypes();
            if (types != null) {
                Object typeClassArray = wrapper.getArray(
                            "com.sun.star.uno.TypeClass", new int[]{types.length}); // NOI18N
                for (int i=0; i<types.length; i++) {
                    wrapper.setArrayValue(
                            typeClassArray, i, wrapper.getStaticField("com.sun.star.uno.TypeClass", types[i])); // NOI18N
                }

                Object tdSearchDepth = wrapper.getStaticField(
                        "com.sun.star.reflection.TypeDescriptionSearchDepth", "ONE"); // NOI18N
                // XTypeDescriptionEnumeration
                Object xTDEnumeration = wrapper.executeMethod(
                        xTDMgr, "createTypeDescriptionEnumeration",  // NOI18N
                        new Object[]{name, typeClassArray, tdSearchDepth});

                while ( wrapper.executeBooleanMethod(
                                xTDEnumeration, "hasMoreElements", null) ) { // NOI18N
                    Object xTD = wrapper.executeMethod(xTDEnumeration, "nextTypeDescription", null); // NOI18N
                    Object typeClass = wrapper.executeMethod(xTD, "getTypeClass", null);  // NOI18N

                    int dataType = UnoTypes.MODULE_TYPE;
                    if (typeClass == wrapper.getStaticField("com.sun.star.uno.TypeClass", UnoTypes.SERVICE)) { // NOI18N
                        dataType = UnoTypes.SERVICE_TYPE;
                    }
                    if (typeClass == wrapper.getStaticField("com.sun.star.uno.TypeClass", UnoTypes.INTERFACE)) { // NOI18N
                        dataType = UnoTypes.INTERFACE_TYPE;
                    }
                    if (typeClass == wrapper.getStaticField("com.sun.star.uno.TypeClass", UnoTypes.EXCEPTION)) { // NOI18N
                        dataType = UnoTypes.EXCEPTION_TYPE;
                    }
                    if (typeClass == wrapper.getStaticField("com.sun.star.uno.TypeClass", UnoTypes.STRUCT)) { // NOI18N
                        dataType = UnoTypes.STRUCT_TYPE;
                    } // 2do: poly struct?

                    String newType = (String)wrapper.executeMethod(xTD, "getName", null); // NOI18N
                    TypeData d = new TypeData(newType, dataType);
                    if (!contains(newTypes, d)) {
                        newTypes.add(d);
                    }
                }
            }
            TypeData[] typeObjects = newTypes.toArray(new TypeData[newTypes.size()]);
            Arrays.sort(typeObjects, new Comparator<TypeData>() {
                public int compare(TypeData o1, TypeData o2) {
                    boolean o1isModule = (o1.getType() & 127) == UnoTypes.MODULE_TYPE;
                    boolean o2isModule = (o2.getType() & 127) == UnoTypes.MODULE_TYPE;
                    if ((o1isModule && o2isModule) || 
                            (!o1isModule && !o2isModule)) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    else {
                        // keep simple types as first position
                        if (o1isModule || (o1.getType() & 127) == UnoTypes.SIMPLE_TYPE) return -1;
                        if (o2isModule || (o2.getType() & 127) == UnoTypes.SIMPLE_TYPE) return 1;
                    }
                    return 0;
                }
            });
            setKeys(typeObjects);
        }
        catch (Exception e) {
            LogWriter.getLogWriter().printStackTrace(e);
            // just do not set keys!
        }
    }
    
    @Override
    protected void removeNotify() {
        setKeys(new TypeData[]{});
    }
    
    public void update() {
        addNotify();
    }

    private boolean isIntTypeInStringTypes(int type, String[] types) {
        for (int i=0; i<types.length; i++) {
            switch (type) {
                case NbNodeObject.INTERFACE_TYPE:
                    if (types[i].equals(UnoTypes.INTERFACE)) return true;
                    break;
                case NbNodeObject.SERVICE_TYPE:
                    if (types[i].equals(UnoTypes.SERVICE)) return true;
                    break;
                case NbNodeObject.STRUCT_TYPE:
                    if (types[i].equals(UnoTypes.STRUCT)) return true;
                    break;
                case NbNodeObject.POLY_STRUCT_TYPE:
                    if (types[i].equals(UnoTypes.STRUCT)) return true;
                    break;
                case NbNodeObject.EXCEPTION_TYPE:
                    if (types[i].equals(UnoTypes.EXCEPTION)) return true;
                    break;
            }
        }
        return false;
    }
    
    private boolean contains(Vector<TypeData> v, TypeData d) {
        Enumeration<TypeData> e = v.elements();
        int type1 = d.getType() & 127;
        while(e.hasMoreElements()) {
            TypeData comp = e.nextElement();
            int type2 = comp.getType() & 127;
            if (d.getName().equals(comp.getName())
                && type1 == type2 
                &&  type1 == UnoTypes.MODULE_TYPE) {
                return true;
            }
        }
        return false;
    }
    
    public class TypeData {
        int t;
        String n;
        public TypeData(String name, int type) {
            n = name;
            t = type;
        }
        public int    getType() { return t; }
        public String getName() { return n; }
    }
}
