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
import java.util.Vector;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author sg128468
 */
public class ImportantOOoFilesFolderNode extends AbstractNode {

    public static ImportantOOoFilesFolderNode createNodeStructure(FileObject projDir, Vector<String> subdirs) {
        return new ImportantOOoFilesFolderNode(subdirs.toArray(new String[subdirs.size()]), new ImportantFilesFolderChildren(projDir));
    }
    
    public ImportantOOoFilesFolderNode(String[] obj, Children children) {
        super(children, Lookups.singleton(obj));
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(ImportantOOoFilesFolderNode.class, "ImportantOOoFilesFolderNode.OXT.Name"); // NOI18N
    }
    
    @Override
    public Image getIcon(int type) {        
        DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
        Image original = root.getNodeDelegate().getIcon(type);
        return original; //Utilities.mergeImages(original, smallImage, 7, 7);
    }
    
    @Override
    public Image getOpenedIcon (int type) {
        DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
        Image original = root.getNodeDelegate().getOpenedIcon(type);
        return original; //Utilities.mergeImages(original, smallImage, 7, 7);
    }
}
