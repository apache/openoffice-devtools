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

import java.io.File;
import java.io.IOException;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class IdlFileListener extends FileChangeAdapter {

    private static String[] sm_idlFiles = new String[0]; // TODO: evaluate usage of container

    static void addIdlFile(String idlFileName) {
        synchronized (sm_idlFiles) {
            String[] keepFiles = new String[sm_idlFiles.length + 1];
            for (int i = 0; i < sm_idlFiles.length; i++) {
                keepFiles[i] = sm_idlFiles[i];
                // file already there: do not add again
                if (idlFileName.equals(sm_idlFiles[i]))
                    return;
            }
            keepFiles[sm_idlFiles.length] = idlFileName;
            sm_idlFiles = keepFiles;
        }
    }
    
    static void removeIdlFile(String idlFileName) {
        synchronized(sm_idlFiles) {
            String[] keepFiles = new String[sm_idlFiles.length - 1];
            int j = 0;
            for (int i = 0; i < keepFiles.length; i++) {
                if (sm_idlFiles[i].equals(idlFileName))
                    j = 1;
                else
                    keepFiles[i] = sm_idlFiles[i + j];
            }
            // file not there: do not remove
            if (j == 0) return;
            sm_idlFiles = keepFiles;
        }
    }
    
    /**
     * get idl files for background compilation: do not return a reference
     * but a clone.
     * @return
     */
    public static void compileIdlFiles() { 
        synchronized (sm_idlFiles) {
            BackgroundIdlCompilation.compileInBackground(sm_idlFiles);
        }
    }
    
    public IdlFileListener(FileObject file) {
        File f = FileUtil.toFile(file);
        if (f.exists()) {
            try {
                addIdlFile(f.getCanonicalPath());
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    @Override
    public void fileChanged(FileEvent event) {
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### changed thrown");
            String name = FileUtil.toFile(event.getFile()).getCanonicalPath();
            addIdlFile(name);
            compileIdlFiles();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    @Override
    public void fileDeleted(FileEvent event) {
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### deleted thrown");
            String name = FileUtil.toFile(event.getFile()).getCanonicalPath();
            removeIdlFile(name);
            compileIdlFiles();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    @Override
    public void fileRenamed(FileRenameEvent event) {
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### renamed thrown");
            String oldName = event.getName();
            removeIdlFile(oldName);
            String name = FileUtil.toFile(event.getFile()).getCanonicalPath();
            addIdlFile(name);
            compileIdlFiles();
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent event) {
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### attribute changed thrown");
    }

}
