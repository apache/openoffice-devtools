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
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class OOoListenerHelper {

    private static FileObject ms_ProjectDir;
    // not really the name, but the name of the top directory
    private static String ms_ProjectName;
            
    public static void addOOoListeners(FileObject projectDir) {
        ms_ProjectDir = projectDir;
        // TODO: make this more dynamic if src is not the source dir?
        FileObject sourceDir = projectDir.getFileObject("src");
        addListenersRecursively(sourceDir);
        try {
            String path = FileUtil.toFile(projectDir).getCanonicalPath();
            ms_ProjectName = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
            ms_ProjectName = "default";
        }
        
    }

    public static FileObject getProjectDir() { return ms_ProjectDir; }
    
    public static String getProjectName() { return ms_ProjectName; }

    private static void addListenersRecursively(FileObject startDir) {
//        startDir.addFileChangeListener(new DirectoryListener());
//        FileObject[] object = startDir.getChildren();
//        for (int i = 0; i < object.length; i++) {
//            if (object[i].isData() && object[i].getExt().equals("idl"))
//                object[i].addFileChangeListener(new IdlFileListener(object[i]));
//            if (object[i].isFolder())
//                addListenersRecursively(object[i]);
//        }
    }
}
