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

package org.openoffice.extensions.projecttemplates.component.datamodel.types.node;

import java.awt.Component;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;

/**
 *
 * @author sg128468
 */
public class ComponentInterfaceTypePropertyEditor 
        extends PropertyEditorSupport implements ExPropertyEditor {
    
    private PropertyEnv propertyEnv;
    
    /**
     * Creates a new instance of ComponentTypePropertyEditor
     */
    public ComponentInterfaceTypePropertyEditor() {
    }
    
    public void attachEnv(PropertyEnv propertyEnv) {
        this.propertyEnv = propertyEnv;
    }
    
    public Component getCustomEditor() {
        return new ComponentInterfaceTypeCustomEditor(this, propertyEnv);
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }    
}
