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
package org.openoffice.extensions.filetypes.xcs;

import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.UniFileLoader;
import org.openide.util.NbBundle;

public class XcsDataLoader extends UniFileLoader {
    
    public static final String REQUIRED_MIME = "text/xcs+xml"; // NOI18N
    
    private static final long serialVersionUID = 1L;
    
    public XcsDataLoader() {
        super("org.openoffice.extensions.filetypes.xcs.XcsDataObject"); // NOI18N
    }
    
    protected String defaultDisplayName() {
        return NbBundle.getMessage(XcsDataLoader.class, "LBL_Xcs_loader_name"); // NOI18N
    }
    
    protected void initialize() {
        super.initialize();
        getExtensions().addMimeType(REQUIRED_MIME);
    }
    
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException, IOException {
        return new XcsDataObject(primaryFile, this);
    }
    
//    protected MultiDataObject.Entry createPrimaryEntry(
//            MultiDataObject multiDataObject, FileObject fileObject) {
//        return new XMLDataLoader.XMLFileEntry (multiDataObject, fileObject); //adds smart templating
//    }
    
    protected String actionsContext() {
        return "Loaders/" + REQUIRED_MIME + "/Actions"; // NOI18N
    }
    
}
