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
package org.openoffice.extensions.config;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import javax.swing.text.EditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplate;
import org.netbeans.lib.editor.codetemplates.api.CodeTemplateManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.text.CloneableEditorSupport;
import org.openide.xml.XMLUtil;
import org.openoffice.extensions.util.LogWriter;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Extend the code templates of Java in NetBeans with some cool stuff for 
 * Apache OpenOffice. 
 * @author sg128468
 */
public class CodeTemplates {

    private static final String HEADER = 
        ("/*************************************************************************\n").concat
        (" *\n").concat
        (" * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.\n").concat
        (" *\n").concat
        (" * Copyright 2008 by Sun Microsystems, Inc.\n").concat
        (" *\n").concat
        (" * OpenOffice.org - a multi-platform office productivity suite\n").concat
        (" *\n").concat
        (" * $").concat("RCSfile: XY.java,v $\n").concat
        (" * $").concat("Revision: 1.0 $\n").concat
        (" *\n").concat
        (" * This file is part of OpenOffice.org.\n").concat
        (" *\n").concat
        (" * OpenOffice.org is free software: you can redistribute it and/or modify\n").concat
        (" * it under the terms of the GNU Lesser General Public License version 3\n").concat
        (" * only, as published by the Free Software Foundation.\n").concat
        (" *\n").concat
        (" * OpenOffice.org is distributed in the hope that it will be useful,\n").concat
        (" * but WITHOUT ANY WARRANTY; without even the implied warranty of\n").concat
        (" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n").concat
        (" * GNU Lesser General Public License version 3 for more details\n").concat
        (" * (a copy is included in the LICENSE file that accompanied this code).\n").concat
        (" *\n").concat
        (" * You should have received a copy of the GNU Lesser General Public License\n").concat
        (" * version 3 along with OpenOffice.org.  If not, see\n").concat
        (" * <http://www.openoffice.org/license.html>\n").concat
        (" * for a copy of the LGPLv3 License.\n").concat
        (" *\n").concat
        (" ************************************************************************/\n");

    /**
     * Array for all templates in form "Abbreviation" and "Expanded Code" 
     */
    private static final String[][] theTemplates = new String[][] {
        // UnoRUntime.queryInterface
        {"urqi", "X${Interface} x${Interface} = (X${Interface})UnoRuntime.queryInterface(X${Interface}.class, ${EXP leftSideType instanceof=\"java.lang.Object\" default=\"oObject\"});"},
        // ooo header
        {"oohd", HEADER},
//        TODO: add further templates
//        {"", ""},
    }; // NOI18N
    
    Document m_DescriptionDocument;
    FileObject m_JavaCustomCodeTemplates;
    boolean m_FileNeedsSaving;
    private FileObject m_PreferencesFolder;
    
    public CodeTemplates(){
//         Lookup l = MimeLookup.getLookup(MimePath.parse("text/x-java"));
//         CodeTemplateSettings cds = l.lookup(CodeTemplateSettings.class);
//         List<CodeTemplateDescription> codeTemplates = cds.getCodeTemplateDescriptions();
//         for (Iterator<CodeTemplateDescription> it = codeTemplates.iterator(); it.hasNext();) {
//             CodeTemplateDescription object = it.next();
//             System.out.println("Abb " + object.getAbbreviation());
//        }
    }
    
    public static void createTemplates() {
        CodeTemplates tem = new CodeTemplates();
        // get files or create if necessary
        tem.createNecessaryFolders();
        // read the file
        tem.readXmlFile();
        // crate an empty document if necessary
        if (tem.m_DescriptionDocument == null) {
            tem.createDocument();
        }
        // insert OOo code templates
        tem.addTheTemplates();
        if (tem.m_FileNeedsSaving) {
            // save back
            tem.saveTemplates();
        }
        
        // force reading the code templates ??
        EditorKit kit = CloneableEditorSupport.getEditorKit("text/x-java");
        javax.swing.text.Document doc = kit.createDefaultDocument();
        CodeTemplateManager manager = CodeTemplateManager.get(doc);
        manager.waitLoaded();
        Collection<? extends CodeTemplate> codeTemplates = manager.getCodeTemplates();
//        for (CodeTemplate codeTemplate : codeTemplates) {
//            System.out.println("Abb: " + codeTemplate.getAbbreviation());     
//        }
    }

    private void createDocument() {
        // annoyingly enough, the folowing does not work, no idea why.
//            try {
//                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = builderFactory.newDocumentBuilder();
//                DOMImplementation domImpl = builder.getDOMImplementation();
//                
//                DocumentType type = domImpl.createDocumentType(
//                        "codetemplates", 
//                        "-//NetBeans//DTD Editor Code Templates settings 1.0//EN", 
//                        "http://www.netbeans.org/dtds/EditorCodeTemplates-1_0.dtd");
//                m_DescriptionDocument = domImpl.createDocument(null, null, type);
//            } catch (ParserConfigurationException ex) {
//                Exceptions.printStackTrace(ex);
//            }
        
        // so instead, write minimal xml doc and load it - any better ideas?
        OutputStream outStream = null;
        try {
            // if description not already exists, create it
            if (m_JavaCustomCodeTemplates == null) {
                m_JavaCustomCodeTemplates = m_PreferencesFolder.createData("org-netbeans-modules-editor-settings-CustomCodeTemplates", "xml"); // NOI18N
            }

            final String dummyXmlDoc = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".concat(
                    "<!DOCTYPE codetemplates PUBLIC \"-//NetBeans//DTD Editor Code Templates settings 1.0//EN\" \"http://www.netbeans.org/dtds/EditorCodeTemplates-1_0.dtd\">\n").concat(
                    "<codetemplates>\n").concat(
                    "</codetemplates>\n"); // NOI18N

            outStream = m_JavaCustomCodeTemplates.getOutputStream();
            BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(outStream));
            buf.write(dummyXmlDoc);
            buf.flush();
            
        }
        catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally {
            try {
                if (outStream != null)
                    outStream.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        readXmlFile();
    }
    
    private void addTheTemplates() {
        m_FileNeedsSaving = false;
        if (m_DescriptionDocument != null) {
            NodeList templatesList = m_DescriptionDocument.getElementsByTagName("codetemplates"); // NOI18N
            Node templatesNode = templatesList.item(0);
            NodeList codeTemplateList = templatesNode.getChildNodes();
            for (int i = 0; i < theTemplates.length; i++) {
                String abbreviation = theTemplates[i][0];
                if (!hasChildNodeWithAbbreviation(codeTemplateList, abbreviation)) {
                    Element newTemplate = m_DescriptionDocument.createElement("codetemplate"); // NOI18N
                    newTemplate.setAttribute("abbreviation", abbreviation); // NOI18N
                    newTemplate.setAttribute("xml:space", "preserve"); // NOI18N
                    // worked until (and including) NB 6.1
/*                    Element newCode = m_DescriptionDocument.createElement("code"); // NOI18N
                    newCode.setTextContent(theTemplates[i][1]);
                    newTemplate.appendChild(newCode);
                    templatesNode.appendChild(newTemplate); */
                    // new part for NB 6.5
                    Element newCode = m_DescriptionDocument.createElement("code"); // NOI18N
                    CDATASection section = m_DescriptionDocument.createCDATASection(theTemplates[i][1]);
                    newCode.appendChild(section);
                    newTemplate.appendChild(newCode);
                    templatesNode.appendChild(newTemplate);
                    // added at least one template, need saving
                    m_FileNeedsSaving = true;
                }
            }
        }
    }
    
    private void saveTemplates() {
        OutputStream outStream = null;
        try {
            // if description not already exists, create it
            if (m_JavaCustomCodeTemplates == null) {
                m_JavaCustomCodeTemplates = m_PreferencesFolder.createData("org-netbeans-modules-editor-settings-CustomCodeTemplates", "xml"); // NOI18N
            }

            // Transmit the request document
            outStream = m_JavaCustomCodeTemplates.getOutputStream();

            XMLUtil.write(m_DescriptionDocument, outStream, "UTF-8"); // NOI18N
            outStream.flush();
        }
        catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        finally {
            try {
                if (outStream != null)
                    outStream.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }

    private void createNecessaryFolders() {
        FileObject editors = FileUtil.getConfigRoot().getFileObject("Editors"); // NOI18N
        try {
            FileObject textFolder = getOrCreateFileObject(editors, "text"); // NOI18N
            FileObject javaFolder = getOrCreateFileObject(textFolder, "x-java"); // NOI18N
            m_PreferencesFolder = getOrCreateFileObject(javaFolder, "CodeTemplates"); // NOI18N
            m_JavaCustomCodeTemplates = m_PreferencesFolder.getFileObject("org-netbeans-modules-editor-settings-CustomCodeTemplates", "xml"); // NOI18N
        } catch (IOException ex) {
             LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private void readXmlFile() {
        if (m_JavaCustomCodeTemplates != null) {
            InputStream inStream = null;
            try {
                inStream = m_JavaCustomCodeTemplates.getInputStream();
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder(); 
                m_DescriptionDocument = builder.parse(inStream);

            } catch (ParserConfigurationException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (SAXException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (NullPointerException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
            finally {
                try {
                    if (inStream != null)
                        inStream.close();
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        }
    }
    
    private boolean hasChildNodeWithAbbreviation(NodeList parentNode, String abbreviationValue) {
        if (parentNode != null || abbreviationValue != null) {
            for (int i = 0; i < parentNode.getLength(); i++) {
                Node cand = parentNode.item(i);
                if (cand != null) {
                    NamedNodeMap attributes = cand.getAttributes();
                    if (attributes != null) {
                        Node attrNode = attributes.getNamedItem("abbreviation"); // NOI18N
                        if (attrNode != null) {
                            String value = attrNode.getNodeValue();
                            if (abbreviationValue.equals(value)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private FileObject getOrCreateFileObject(FileObject root, String name) throws IOException {
        if (root == null) {
            throw new IOException("Could not create file object " + name); // NOI18N
        }
        FileObject newObject = root.getFileObject(name);
        if (newObject == null) {
            newObject = root.createFolder(name);
            if (newObject == null) {
                throw new IOException("Could not create file object " + name); // NOI18N
            }
        }
        return newObject;
    }
}
