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
package org.openoffice.extensions.util.nodes;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openoffice.extensions.util.ProjectTypeHelper;
import org.openoffice.extensions.util.lookup.DummyHook;

/**
 *
 * @author sg128468
 */
public class ImportantFilesNodeFactoryImpl implements NodeFactory {

    Project m_prj;
    Properties m_props;
    FileObject m_projectRoot;
    
    public NodeList<?> createNodes(Project prj) {
        m_prj = prj;
        //return a new node in the node list: only for our projects
        DummyHook hook = m_prj.getLookup().lookup(DummyHook.class);
        if (hook != null) {
            m_projectRoot = m_prj.getProjectDirectory();
            if (m_projectRoot != null) {
                m_props = ProjectTypeHelper.getUnoProperties(m_projectRoot);
                Vector<String> v = new Vector<String>();
                String dir = m_props.getProperty("images.dir"); // NOI18N
                if (dir != null) {
                    v.add(dir);
                }
                dir = m_props.getProperty("help.dir"); // NOI18N
                if (dir != null) {
                    v.add(dir);
                }
                dir = m_props.getProperty("dialogs.dir"); // NOI18N
                if (dir != null) {
                    v.add(dir);
                }
                dir = m_props.getProperty("description.dir"); // NOI18N
                if (dir != null) {
                    v.add(dir);
                }
                dir = m_props.getProperty("licenses.dir"); // NOI18N
                if (dir != null) {
                    v.add(dir);
                }
                dir = m_props.getProperty("registry.dir"); // NOI18N
                if (dir != null) {
                    v.add(dir);
                }
                // TODO: change src with something better
                v.add("src/description.xml");
                v.add("src/uno-extension-manifest.xml");
                // find xcu files recursively
                FileObject sourceRoot = m_projectRoot.getFileObject("src");
                if (sourceRoot != null) {
                    Enumeration<? extends FileObject> dataFiles = sourceRoot.getData(true);
                    int projectPathLength = m_projectRoot.getPath().length();
                    while (dataFiles.hasMoreElements()) {
                        FileObject cand = dataFiles.nextElement();
                        String ext = cand.getExt();
                        if (ext.equals("xcu") || ext.equals("xcs")) {
                            String path = cand.getPath();
                            String searchPath = path.substring(projectPathLength + 1);
                            v.add(searchPath);
                        }
                    }
                }
                if (v.size() > 0) {
                    ImportantOOoFilesFolderNode node = ImportantOOoFilesFolderNode.createNodeStructure(m_projectRoot, v);
                    return NodeFactorySupport.fixedNodeList(node);
                }
            }
        }

        // return empty list
        return NodeFactorySupport.fixedNodeList();        
    }
    
}
