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

import java.util.Vector;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class ImportantFilesFolderChildren extends Children.Keys<String[]> {

    private FileObject m_projDir;
    // react on extensions to keep it simple: let createNodes(String[] subdirs) decide what is displayed
    private String[] m_fileExtensionList = new String[] {
            "xml",
            "xcu",
            "xcs",
        };
    
    public ImportantFilesFolderChildren(FileObject projDir) {
        this.m_projDir = projDir;
        ImportantFolderListener listener = new ImportantFolderListener();
        m_projDir.addFileChangeListener(listener);
        FileObject sourceDir = m_projDir.getFileObject("src"); // TODO: replace src with better stuff
        if (sourceDir != null) {
            // get notified of creations in source dir: description.xml, uno-extension-manifest.xml, xcu, xcs files maybe
            ImportantFileListener fListener = new ImportantFileListener();
            sourceDir.addFileChangeListener(fListener);
        }
    }
    
    @Override
    protected Node[] createNodes(String[] subdirs) {
        Vector<ImportantOOoFilesNode> myNodes = new Vector<ImportantOOoFilesNode>();
        // these are not really subdirs, but also files: description.xml and uno-extension-manifest.xml
        for (int i = 0; i < subdirs.length; i++) {
            FileObject subdir = m_projDir.getFileObject(subdirs[i]);
            if (subdir != null && !subdir.getName().startsWith(".")) {
                try {
                    // add listener for files additionally, because delete and create will not be recognized else
                    ImportantOOoFilesNode node = new ImportantOOoFilesNode(subdir);
                    myNodes.add(node);
                } catch (DataObjectNotFoundException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        }
        return myNodes.toArray(new ImportantOOoFilesNode[myNodes.size()]);
    }

    @Override
    protected void addNotify() {
        Node node = getNode();
        String[] subdirs = node.getLookup().lookup(String[].class);
        String[][] keys = new String[1][subdirs.length];
        for (int i = 0; i < keys[0].length; i++) {
            keys[0][i] = subdirs[i];
        }
        this.setKeys(keys);
    }

    /**
     * Listener for updateing this node when a directory is created or deleted.
     * 
     */
    private class ImportantFolderListener extends FileChangeAdapter {
        @Override
        public void fileFolderCreated(FileEvent event) {
            // TODO: Should listener check for the directory names? this reacts on all
            addNotify();
        }
        @Override
        public void fileDeleted(FileEvent arg0) {
            // TODO: Should listener check for the directory names? this reacts on all
            addNotify();
        }
    }

    /**
     * Listener for updateing this node when a file is created or deleted.
     * 
     */
    private class ImportantFileListener extends FileChangeAdapter {
        @Override
        public void fileDataCreated(FileEvent arg0) {
            // TODO: Should listener check for the directory names? this reacts on all
            FileObject file = arg0.getFile();
            if (file != null) {
                String ext = file.getExt();
                for (int i = 0; i < m_fileExtensionList.length; i++) {
                    String fileExt = m_fileExtensionList[i];
                    if (ext.equals(fileExt)) {
                        addNotify();
                    }
                }
            }
        }
        @Override
        public void fileDeleted(FileEvent arg0) {
            // TODO: Should listener check for the file names? this reacts on all
            addNotify();
        }
    }

    /**
     * class to get notified when a file is deleted. Only needed for nodes where
     * the file is recognized, not the folder it is located in. 
     *
    private class ImportantFileNodeListener implements NodeListener {
        
        public void childrenAdded(NodeMemberEvent arg0) { //ignore event
        }
        public void childrenRemoved(NodeMemberEvent arg0) { //ignore event
        }
        public void childrenReordered(NodeReorderEvent arg0) { //ignore event
        }
        public void nodeDestroyed(NodeEvent arg0) {
            // update view on OXT folder
            addNotify();
        }
        public void propertyChange(PropertyChangeEvent evt) { //ignore event
        }
        
    }*/
}
