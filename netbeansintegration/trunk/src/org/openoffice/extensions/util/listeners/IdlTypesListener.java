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

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.util.ClasspathUpdater;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class IdlTypesListener extends FileChangeAdapter {

    private DirectoryListener m_parent;
    
    public IdlTypesListener(DirectoryListener parent) {
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### IdlTypesListener ctor");
        // only react to create and delete: just notice the other stuff because I am curious
        m_parent = parent;
        ClasspathUpdater.extendJavaClasspathWithIdlTypes(getProject());
    }
    
    private Project getProject() {
        return m_parent.getProject();
/*        FileObject parent = subDir.getParent();
        if (parent == null) {
            // gone to root without finding nbproject?
            // return original dir and hope for the best
            return subDir;
        }
        // nbproject should exist only once
        if (!parent.getFileObject("nbproject").isValid()) {
            return getProjectDir(parent);
        }
        return parent; */
    }
    
    @Override
    public void fileChanged(FileEvent event) {
        try {
            String name = FileUtil.toFile(event.getFile()).getCanonicalPath();
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### changed thrown: " + name);
            ClasspathUpdater.extendJavaClasspathWithIdlTypes(getProject());
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    @Override
    public void fileDeleted(FileEvent event) {
        try {
            String name = FileUtil.toFile(event.getFile()).getCanonicalPath();
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### deleted thrown: " + name);
            ClasspathUpdater.removeIdlTypesFromJavaClasspath(getProject());
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    @Override
    public void fileRenamed(FileRenameEvent event) {
        try {
            String oldName = event.getName();
            String name = FileUtil.toFile(event.getFile()).getCanonicalPath();
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### renamed thrown: " + oldName + " to " + name);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent event) {
        String name = event.getName();
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### attribute changed thrown " + name);
    }
    
}
