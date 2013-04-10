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

import java.awt.event.ActionEvent;
import java.util.zip.ZipEntry;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.util.NbBundle;

/**
 *
 * @author sg128468
 */
public class OxtNodeActions {
    
    public static Action[] getDefaultAction(OxtDataNode parentNode, ZipEntry zip) {
        return new Action[]{new OxtNodeActions.OpenAction(parentNode, zip)};
    }
    
    public static Action[] getContextAction(OxtDataNode parentNode, ZipEntry zip) {
        return new Action[]{new OxtNodeActions.OpenAction(parentNode, zip)};
    }
    
    private static class OpenAction extends AbstractAction {
        ZipEntry zip;
        OxtDataNode parentNode;
        
        public OpenAction(OxtDataNode parentNode, ZipEntry zip) {
            this.zip = zip;
            this.parentNode = parentNode;
            putValue(NAME, NbBundle.getMessage(OxtNodeActions.class, "LBL_OpenAction"));
        }
        
        public void actionPerformed(ActionEvent e) {
            OxtDataLoader loader = new OxtDataLoader();
        }
    }
    
}
