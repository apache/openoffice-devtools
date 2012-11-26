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

package org.openoffice.extensions.projecttemplates.component.datamodel.types.node;

import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.FunctionException;
import org.openoffice.extensions.util.datamodel.IdlEnumeration;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PolyStruct;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.Struct;
import org.openoffice.extensions.util.datamodel.TemplateType;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class IdlTypeTreeCreator {

    /**
     */
    private IdlTypeTreeCreator() {
    }
    
    public static ComponentTypeNode createInitialInterfaceTree(String interfaceName, String pkgName) {
        Interface ifc = new Interface(interfaceName, pkgName);
        ifc.addFunction();
        ComponentTypeNode rootNode = new ComponentTypeNode(ifc, new ComponentTypeChildren());
        return rootNode;
    }

    public static ComponentTypeNode createInitialServiceTree(String serviceName, String pkgName) {
        Service svrc = new Service(serviceName, pkgName);
        ComponentTypeNode rootNode = new ComponentTypeNode(svrc, new ComponentTypeChildren(true));
        return rootNode;
    }

    public static ComponentTypeNode createInitialEnumTree(String enumName, String pkgName) {
        IdlEnumeration enm = new IdlEnumeration(enumName, pkgName);
        enm.addEnum();
        ComponentTypeNode rootNode = new ComponentTypeNode(enm, new ComponentTypeChildren(true));
        return rootNode;
    }

    public static ComponentTypeNode createInitialStructTree(String structName, String pkgName) {
        Struct str = new Struct(structName, pkgName);
        str.addStructType();
        ComponentTypeNode rootNode = new ComponentTypeNode(str, new ComponentTypeChildren(true));
        return rootNode;
    }

    public static ComponentTypeNode createInitialException(String exName, String pkgName) {
        FunctionException ex = new FunctionException(exName, pkgName);
        ComponentTypeNode rootNode = new ComponentTypeNode(ex, new ComponentTypeChildren(true));
        return rootNode;
    }

    public static ComponentTypeNode createInitialPolyStruct(String structName, String pkgName) {
        PolyStruct ps = new PolyStruct(structName, pkgName);
        ps.addTemplateType();
        ps.addPropertyType();
        ComponentTypeNode rootNode = new ComponentTypeNode(ps, new ComponentTypeChildren(true));
        return rootNode;
    }
    
    public static ComponentTypeNode createTemplateNode(PolyStruct plStruct) {
        
        String[] typeNames = plStruct.getTemplateTypeNames();
        Vector<TemplateType>templTypes = new Vector<TemplateType>();
        for (int i=0; i<typeNames.length; i++) {
            templTypes.add(plStruct.getTemplateType(typeNames[i]));
        }
        
        
        // create new template types with the right editor.
        final TemplateType[] newTemplateTypes = new TemplateType[templTypes.size()];
        for (int i=0; i<newTemplateTypes.length; i++) {
            TemplateType oldType = templTypes.get(i);
            try {
                newTemplateTypes[i] = new TemplateType(
                        oldType.getSimpleProperty(TemplateType.PROPERTY_CONTAINER_NAME),
                        oldType.getSimpleProperty(TemplateType.PROPERTY_CONTAINER_TYPE),
                        null,
                        true
                );
            } catch (UnknownOpenOfficeOrgPropertyException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        // need a simple root object for the templates
        NbNodeObject ob = new NbNodeObject(){
            public NbNodeObject getParent() { // no parent
                return null;
            }
            public String getDisplayName() { // will not be displayed
                return ""; // NOI18N
            }
            public NbNodeObject[] getAllSubObjects() { // return the templates
                return newTemplateTypes;
            }
            public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
                // this is important: the properties have to be set for the sheet
                Sheet.Set set = null;
                if (sheet != null) {
                    for (int i=0; i<newTemplateTypes.length; i++) {
                        set = sheet.createPropertiesSet();
                        set.setName(new StringBuffer("Template ").append(i+1).toString()); // NOI18N
                        set.setDisplayName(set.getName());
                        set.put(newTemplateTypes[i].createProperties(null, listener));
                        sheet.put(set);
                    } 
                }
                return new Node.Property[0];
            }
            public boolean hasActions(int type) { // no nodes, no actions
                return false;
            }
            public Action[] getActions(boolean context) { 
                return null;
            }
            public void setActions(BaseAction actions) {}  // not interested
            public int getType() { // return the template type: will not be asked anyway
                return NbNodeObject.TEMPLATE_TYPE;  
            }
        };
        return new ComponentTypeNode(ob, new ComponentTypeChildren(true));
    }
}
