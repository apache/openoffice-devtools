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

package org.openoffice.extensions.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Vector;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
//import org.openoffice.extensions.editors.unoidl.UnoIdlSyntax;
//import org.openoffice.extensions.editors.unoidl.UnoIdlTokenContext;
import org.openoffice.extensions.util.datamodel.Constants;
import org.openoffice.extensions.util.datamodel.FunctionException;
import org.openoffice.extensions.util.datamodel.IdlEnumeration;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.Struct;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;

/**
 *
 * @author sg128468
 */
public class IdlFileHelper {
    
    Enumeration m_childObjects;
    String m_sourcePath;
    
    /** Creates a new instance of IdlFileHelper */
    public IdlFileHelper(FileObject sourcePath) {
        try {
            m_sourcePath = FileUtil.toFile(sourcePath).getCanonicalPath();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
            m_sourcePath = "";
        }
        m_childObjects = sourcePath.getChildren(true);
    }
    
    public NbNodeObject[] getAllIdlFiles() {
        Vector<NbNodeObject> nbNodeObjects = new Vector<NbNodeObject>();
        while (m_childObjects.hasMoreElements()) {
            try {
                FileObject element = (FileObject)m_childObjects.nextElement();
                if (element != null && element.isData() && element.getExt().equals("idl")) { // NOI18N
                    int type = getType(element);
                    NameAndPackage nameAndPackage = getNameAndPackage(element);
                    NbNodeObject object = null;
                    switch(type) {
                        case UnoTypes.STRUCT_TYPE:
                            object = new Struct(nameAndPackage.name, nameAndPackage.pkg);
                            break;
                        case UnoTypes.EXCEPTION_TYPE:
                            object = new FunctionException(nameAndPackage.name, nameAndPackage.pkg);
                            break;
                        case UnoTypes.ENUM_TYPE:
                            object = new IdlEnumeration(nameAndPackage.name, nameAndPackage.pkg);
                            break;
                        case UnoTypes.INTERFACE_TYPE:
                            object = new Interface(nameAndPackage.name, nameAndPackage.pkg);;
                            break;
                        case UnoTypes.SERVICE_TYPE:
                            object = new Service(nameAndPackage.name, nameAndPackage.pkg);
                            break;
                        case UnoTypes.CONSTANTS_TYPE: // not so much supported, but maybe created by hand...
                            object = new Constants(nameAndPackage.name, nameAndPackage.pkg);
                            break;
                        default:
                            LogWriter.getLogWriter().log(LogWriter.LEVEL_WARNING, 
                                "Unkown/unsupported idl type: " + 
                                nameAndPackage.pkg + "." + nameAndPackage.name);
                    }
                    if (object != null) {
                        nbNodeObjects.add(object);
                    }
                }
            }
            catch (ClassCastException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        return nbNodeObjects.toArray(new NbNodeObject[nbNodeObjects.size()]);
    }
    
    private int getType(FileObject idlFile) {
        int type = UnoTypes.ANY_TYPE;
        
        try {
            InputStream inStream = idlFile.getInputStream();
            InputStreamReader reader = new InputStreamReader(inStream);
            char[] characters = new char[inStream.available()];
            reader.read(characters, 0, characters.length);
            UnoIdlSyntax idlSyntax = new UnoIdlSyntax(characters);
            UnoIdlSyntax.TokenID id = UnoIdlSyntax.TokenID.CHAR;
            int offset = 0;
            while ( ((offset = idlSyntax.getOffset()) < characters.length - 1) &&
                    (type == UnoTypes.ANY_TYPE) ) {  // only check till file finished
                id = idlSyntax.parseNextToken();
                // quite simple: the first keyword that donates a idl type is it!
                if (id == UnoIdlSyntax.TokenID.KEYWORD) {
                    char[] tokenChar = new char[idlSyntax.getTokenLength()];
                    System.arraycopy(characters, offset, tokenChar, 0, tokenChar.length);
                    String name = new String(tokenChar);
                    if (name.equals("constants")) { // NOI18N
                        type = UnoTypes.CONSTANTS_TYPE;
                    }
                    else if (name.equals("enum")) { // NOI18N
                        type = UnoTypes.ENUM_TYPE;
                    }
                    else if (name.equals("exception")) { // NOI18N
                        type = UnoTypes.EXCEPTION_TYPE;
                    }
                    else if (name.equals("interface")) { // NOI18N
                        type = UnoTypes.INTERFACE_TYPE;
                    }
                    else if (name.equals("service")) { // NOI18N
                        type = UnoTypes.SERVICE_TYPE;
                    }
                    else if (name.equals("struct")) { // NOI18N
                        type = UnoTypes.STRUCT_TYPE;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        
        return type;
    }
    
    private NameAndPackage getNameAndPackage(FileObject element) {
        NameAndPackage nap = new NameAndPackage();
        try {
            String name = FileUtil.toFile(element).getCanonicalPath();
            // remove part till src
            name = name.substring(m_sourcePath.length() + 1);
            // remove extension; one dot must be there because file is not selected without .idl
            name = name.substring(0, name.lastIndexOf('.'));
            int index = name.lastIndexOf(File.separatorChar);
            nap.name = name.substring(index + 1);
            if (index != -1) {
                nap.pkg = name.substring(0, index).replace(File.separatorChar, '.');
            }
            else {
                nap.pkg = ""; // NOI18N
            }
            
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
            // name and package should not be null.
            nap.name = "";  // NOI18N
            nap.pkg = ""; // NOI18N
        }
        
        return nap;
    }

    private class NameAndPackage {
        String name;
        String pkg;
    }
}
