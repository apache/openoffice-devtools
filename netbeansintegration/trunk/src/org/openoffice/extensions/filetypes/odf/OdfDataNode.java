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
package org.openoffice.extensions.filetypes.odf;

import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

public class OdfDataNode extends DataNode {

    public OdfDataNode(OdfDataObject obj) {
        super(obj, Children.LEAF);
        setIconBaseWithExtension(obj.getIconString());
    }

    OdfDataNode(OdfDataObject obj, Lookup lookup) {
        super(obj, Children.LEAF, lookup);
        setIconBaseWithExtension(obj.getIconString());
    }//    /** Creates a property sheet. */
//    @Override
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
}
