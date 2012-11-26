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
package org.openoffice.extensions.util.nodes;

import java.awt.Image;
import java.io.File;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author sg128468
 */
public class ImportantOOoFilesNode extends FilterNode {

    private String m_DisplayName;
    private Node m_OriginalNode;
    
    public ImportantOOoFilesNode(FileObject importantFileObject) throws DataObjectNotFoundException {
        super(DataObject.find(importantFileObject).getNodeDelegate()); // NOI18N
//        File object = FileUtil.toFile(importantFileObject);
        m_DisplayName = importantFileObject.getNameExt();
        m_OriginalNode = DataObject.find(importantFileObject).getNodeDelegate();
    }

    @Override
    public String getDisplayName() {
        return m_DisplayName;
    }

    @Override
    public Image getIcon(int type) {
        Image original = m_OriginalNode.getIcon(type);
        return original; //Utilities.mergeImages(original, smallImage, 7, 7);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image original = m_OriginalNode.getOpenedIcon(type);
        return original; //Utilities.mergeImages(original, smallImage, 7, 7);
    }
}
