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

package org.openoffice.extensions.util.listeners;

import org.netbeans.api.project.Project;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class DirectoryListener extends FileChangeAdapter {
    
    // directories whose changes are listened to
    // TODO: think about offering arrays here: these should not be too big
    String[] m_dirNames;
    private String m_fileName;
    private DirectoryListener m_parent;
    private FileObject m_fileObject;
    private Project m_currentProject;
    private FileObject m_currentDir;
    private DirectoryListener[] m_subListeners;
    private IdlTypesListener m_subFileListener;
    
    public DirectoryListener(DirectoryListener parent, FileObject currentDir, String[] dirNames, String fileName) {
        this(parent, null, currentDir, dirNames, fileName);
    }
    
    public DirectoryListener(Project currentProject, String[] dirNames, String fileName) {
        this(null, currentProject, currentProject.getProjectDirectory(), dirNames, fileName);
    }
    
    private DirectoryListener(DirectoryListener parent, Project currentProject, FileObject currentDir, String[] dirNames, String fileName) {
        m_parent = parent; // may be null!
        m_currentProject = currentProject; // may be null!
        // just make sure: parent and project must not be both null!
        if (m_parent == null && m_currentProject == null) {
            throw new IllegalArgumentException("Parent and Project parameters are both null."); // NOI18N
        }
        m_currentDir = currentDir;
        m_dirNames = dirNames;
        m_fileName = fileName;
        m_subListeners = new DirectoryListener[dirNames.length];
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### DirectoryListener ctor");
        // initialize the stuff...
        for (int i = 0; i < m_dirNames.length; i++) {
            FileObject subDir = m_currentDir.getFileObject(m_dirNames[i]);
            if (subDir != null && subDir.isValid() && subDir.isFolder()) {
                m_subListeners[i] = new DirectoryListener(this, subDir, m_dirNames, m_fileName);
            }
        }
        // TODO: stop pretending that this is a general class?
        if (m_currentDir != null && m_fileName != null) {
            m_fileObject = m_currentDir.getFileObject(m_fileName);
            if (m_fileObject != null && m_fileObject.isValid() && m_fileObject.isData()) {
                m_subFileListener = new IdlTypesListener(this);
                m_fileObject.addFileChangeListener(m_subFileListener);
            }
            m_currentDir.addFileChangeListener(this);
        }
    }
    
    public void removeListenerRecursively() {
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### DirectoryListener remove");
        for (int i = 0; i < m_subListeners.length; i++) {
            if(m_subListeners[i] != null) {
                m_subListeners[i].removeListenerRecursively();
            }
        }
        if (m_fileObject != null && m_fileObject.isValid() && m_subFileListener !=  null) {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### IDL_type listener remove");
            m_fileObject.removeFileChangeListener(m_subFileListener);
        }
        m_currentDir.removeFileChangeListener(this);
    }
    
    @Override
    public void fileDataCreated(FileEvent event) {
        String name = event.getFile().getNameExt();
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### File Data created " + name);
        if (name.equals(m_fileName)) {
            // TODO: filename is selectable, but listener is hard coded IDL_types.jar listener...
            m_subFileListener = new IdlTypesListener(this);
            event.getFile().addFileChangeListener(m_subFileListener);
        }
    }

    @Override
    public void fileFolderCreated(FileEvent event) {
        FileObject newDir = event.getFile();
        String name = newDir.getName();
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### File Folder created " + name);
        for (int i = 0; i < m_dirNames.length; i++) {
            if (name.equals(m_dirNames[i])) {
                event.getFile().addFileChangeListener(new DirectoryListener(this, newDir, m_dirNames, m_fileName));
            }
        }
    }
    
    protected Project getProject() {
        if (m_currentProject == null) {
            return m_parent.getProject();
        }
        return m_currentProject;
    }
}
