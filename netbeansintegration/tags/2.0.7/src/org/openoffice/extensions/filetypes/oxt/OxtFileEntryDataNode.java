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

import java.awt.Image;
import java.util.zip.ZipEntry;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
import org.openoffice.extensions.util.typebrowser.logic.TypeNode;

class OxtFileEntryDataNode extends AbstractNode {

    private static final int DIRECTORY = 1;
    
    int m_Type;
    String m_Name;
    
    public OxtFileEntryDataNode(ZipEntry obj, Children children) {
        super(children, Lookups.singleton(obj));
        m_Name = obj.getName();
        determineType();
    }

    public String getDisplayName() {
//        ZipEntry zip = (ZipEntry)this.getLookup().lookup(ZipEntry.class);
        return m_Name;
    }

    public Image getIcon(int i) {
        if (m_Type == DIRECTORY) {
            return ImageUtilities.loadImage(TypeNode.FOLDER_ICON);
        }
        return super.getIcon(i);
    }

    public Image getOpenedIcon(int i) {
        if (m_Type == DIRECTORY) {
            return ImageUtilities.loadImage(TypeNode.OPEN_FOLDER_ICON);
        }
        return super.getIcon(i);
    }

    private void determineType() {
        if (m_Name.endsWith("/")) {
            m_Type = DIRECTORY;
            m_Name = m_Name.substring(0, m_Name.length() - 1);
        }
        int index = 0;
        if ((index = m_Name.indexOf('/')) != -1) { // subentry
            m_Name = m_Name.substring(index + 1);
        }
    }

    public Action[] getActions(boolean context) {
//        if (context) {
//            
//        } 
        ZipEntry zip = (ZipEntry)this.getLookup().lookup(ZipEntry.class);
        Node parentNode = this;
        while (parentNode.getParentNode() != null) {
            parentNode = parentNode.getParentNode();
        }
        
        return OxtNodeActions.getDefaultAction((OxtDataNode)parentNode, zip);
    }

}