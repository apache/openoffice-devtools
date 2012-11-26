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
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.XMLDataObject;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.xml.sax.InputSource;

public class XcsDataObject extends XMLDataObject {
    
//    private transient final DataObjectCookieManager cookieManager;
//    private transient Synchronizator synchronizator;

    public XcsDataObject(FileObject pf, XcsDataLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
//        cookieManager = new DataObjectCookieManager (this, cookies);

        cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
       
        InputSource is = DataObjectAdapters.inputSource(this);
        cookies.add(new CheckXMLSupport(is));
        cookies.add(new ValidateXMLSupport(is));
       
        /*
        // editor support defines MIME type understood by EditorKits registry        
        TextEditorSupport.TextEditorSupportFactory editorFactory =
            new TextEditorSupport.TextEditorSupportFactory (
                this, org.netbeans.modules.xml.core.XMLDataObject.MIME_TYPE);
        editorFactory.registerCookies (cookies);
         */
    }
    
    protected Node createNodeDelegate() {
        return new XcsDataNode(this);
    }
    
//    public Synchronizator getSyncInterface() {
//         if (synchronizator == null) {
//            synchronizator = new DataObjectSyncSupport(XcsDataObject.this);
//        }
//        return synchronizator;   
//    }
//
//    public DataObjectCookieManager getCookieManager() {
//        return cookieManager;
//    }
}
