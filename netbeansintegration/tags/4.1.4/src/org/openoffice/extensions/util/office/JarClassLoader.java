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

package org.openoffice.extensions.util.office;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 */
public class JarClassLoader  {

    // cache of loaded classes
    private Hashtable<String, Class> cache;

    // search paths for classes: either jars or directories
    private static Vector<File> searchPaths;

    private static URLClassLoader theJarClassLoader;
    
    /**
     * Keep the class loader for a set of searchPaths, so use a factory
     * method to make sure of that.
     * @param searchPaths directorys or jar files that have to be added
     * to the class path.
     */
    public static URLClassLoader getClassLoader(Vector<File> _searchPaths) {
        if (theJarClassLoader == null || searchPaths == null || !searchPaths.equals(_searchPaths)) {
            searchPaths = _searchPaths;
            // now the class loader needs a new setup
            theJarClassLoader = new URLClassLoader(makeUrlArrayFromPaths(), searchPaths.getClass().getClassLoader());
        }
        return theJarClassLoader;
    }
    
    private static URL[] makeUrlArrayFromPaths() {
        Vector<URL> urls = new Vector<URL>(searchPaths.size());
        for (Iterator<File> it = searchPaths.iterator(); it.hasNext();) {
            File path = it.next();
            FileObject obj = FileUtil.toFileObject(path);
            try {
                urls.add(obj.getURL());
            } catch (FileStateInvalidException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }
    
}