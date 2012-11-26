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

package org.openoffice.extensions.projecttemplates.calcaddin.datamodel.node;

import org.openoffice.extensions.projecttemplates.calcaddin.datamodel.AddIn;
import org.openoffice.extensions.util.datamodel.Function;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Parameter;

/**
 *
 * @author sg128468
 */
public class AddInTreeCreator {

    /**
     * Creates a new instance of AddInTreeCreator
     */
    private AddInTreeCreator() {
    }
    
    public static AddInNode createInitialFunctionTree() {
        AddIn addin = new AddIn();
        addin.addFunction();
        AddInNode rootNode = new AddInNode(addin, new AddInChildren());
        return rootNode;
    }
}
