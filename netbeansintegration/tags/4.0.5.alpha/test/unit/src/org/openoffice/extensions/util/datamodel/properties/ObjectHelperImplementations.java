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

package org.openoffice.extensions.util.datamodel.properties;

import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.actions.LanguageAction;

/**
 *
 * @author sg128468
 */
public class ObjectHelperImplementations {
    
    
    public MockNbNodeObject getMockNbNodeObject() {
        return new MockNbNodeObject();
    }
    
    public MockNbNodeObject getAddinNbNodeObject() {
        MockNbNodeObject obj = new MockNbNodeObject();
        obj.type = NbNodeObject.ADDIN_TYPE;
        return obj;
    }
    
    public class MockNbNodeObject implements NbNodeObject {
        int type = NbNodeObject.FUNCTION_TYPE; // some type with props
        public NbNodeObject getParent() {
            return null;  // we assume this is the topmost object
        }

        public String getDisplayName() {
            return "myDisplayName";
        }

        public NbNodeObject[] getAllSubObjects() {
            return null; // no sub objects either
        }

        public Node.Property[] createProperties(Sheet sheet, PropertyChangeListener listener) {
            return null; // do not ask for a prop sheet in tests -> NetBeans domain
        }

        public boolean hasActions(int type) {
            return false;
        }

        public Action[] getActions(boolean context) {
            return null; // no actions there nothing to get
        }

        public void setActions(BaseAction actions) {
            // ignore
        }

        public int getType() {
            return type;
        }
        
    }

    public MyLanguageAction getMyLanguageAction() {
        return new MyLanguageAction();
    }
        
    public class MyLanguageAction implements BaseAction, LanguageAction {
        boolean deleteCalled = false;
        boolean addLangCalled = false;
        boolean delLangCalled = false;
        public void deleteAction() {
            deleteCalled = true;
        }
        public void addLanguageAction() {
            addLangCalled = true;
        }
        public void deleteLanguageAction() {
            delLangCalled = true;
        }
    }
    
}
