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

package org.openoffice.extensions.filetypes.oxt;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.zip.ZipEntry;

/**
 *
 * @author sg128468
 */
public class OxtSubDirectoryStorage {
    
    Hashtable<String,ZipEntry[]> m_storage;

    private static OxtSubDirectoryStorage m_subDirStorage;
    
    public static OxtSubDirectoryStorage getStorage() {
        if (m_subDirStorage == null)
            m_subDirStorage = new OxtSubDirectoryStorage();
        return m_subDirStorage;
    }
    
    /** Creates a new instance of OxtSubDirectoryStorage */
    private OxtSubDirectoryStorage() {
        m_storage = new Hashtable<String,ZipEntry[]>();
    }
    
    public void addEntryForDirectory(String directory, ZipEntry entry) {
        ZipEntry[] zips = m_storage.get(directory);
        if (zips != null) {
            ZipEntry[] newZips = new ZipEntry[zips.length + 1];
            for (int i = 0; i < zips.length; i++) {
                newZips[i] = zips[i];
            }
            newZips[zips.length] = entry;
            m_storage.put(directory, newZips);
        }
        else {
            m_storage.put(directory, new ZipEntry[]{entry});
        }
    }
    
    public void addEntry(ZipEntry entry) {
        String directory = entry.getName();
        directory = directory.substring(0, directory.lastIndexOf(File.separatorChar) + 1);
        addEntryForDirectory(directory, entry);
    }
    
    public ZipEntry[] getEntriesForDirectory(String directory) {
        return m_storage.get(directory);
    }
}
