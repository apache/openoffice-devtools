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
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

public class OxtDataNode extends DataNode {
    
    private static final String IMAGE_ICON_BASE = 
        "org/openoffice/extensions/projecttemplates/calcaddin/calcaddin.gif";
    
    public OxtDataNode(OxtDataObject obj) {
        super(obj, new OxtFirstChildren(), Lookups.singleton(obj));
        setIconBaseWithExtension(IMAGE_ICON_BASE);
    }
    
//    /** Creates a property sheet. */
//    protected Sheet createSheet() {
//        Sheet s = super.createSheet();
//        Sheet.Set ss = s.get(Sheet.PROPERTIES);
//        if (ss == null) {
//            ss = Sheet.createPropertiesSet();
//            s.put(ss);
//        }
//        // TODO add some relevant properties: ss.put(...)
//        return s;
//    }

    private static class OxtFirstChildren extends Children.Keys<OxtFileEntryDataNode> {
        /** Creates a new instance of OxtFileEntryChildren */
        public OxtFirstChildren() {
        }

        //protected Node[] createNodes(Object object) {
        protected Node[] createNodes(OxtFileEntryDataNode object) {
            return new Node[]{(Node)object};
        }
        
        @Override
        protected void addNotify() {
            Node node = getNode();
            OxtDataNode dataNode = (OxtDataNode)node;
            OxtDataObject obj = (OxtDataObject)node.getLookup().lookup(OxtDataObject.class);

            FileObject file = obj.getPrimaryFile();
            Vector<OxtFileEntryDataNode> vNodes = new Vector<OxtFileEntryDataNode>();

            try {
                ZipFile zipFile = new ZipFile(FileUtil.toFile(file));
                Enumeration<? extends ZipEntry>entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry elem = entries.nextElement();
                    if (elem.isDirectory() || elem.getName().indexOf(File.separatorChar) == -1) {
                        vNodes.add(new OxtFileEntryDataNode(elem, new OxtFileEntryChildren()));
                    }
                    else {
                        OxtSubDirectoryStorage.getStorage().addEntry(elem);
                    }
                    
                }
            } catch (ZipException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            setKeys(vNodes);
        }
        
        @Override
        protected void removeNotify() {
            setKeys(new OxtFileEntryDataNode[] {});
        }
    }
}
