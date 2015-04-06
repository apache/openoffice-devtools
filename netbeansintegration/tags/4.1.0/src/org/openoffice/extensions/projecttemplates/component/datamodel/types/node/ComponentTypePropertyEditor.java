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
public class ComponentTypePropertyEditor 
        extends PropertyEditorSupport implements ExPropertyEditor {
    
    private PropertyEnv propertyEnv;
    private String[] simpleTypeTags;
    
    /**
     * Creates a new instance of ComponentTypePropertyEditor
     */
    public ComponentTypePropertyEditor() {
        int length = UnoTypes.SIMPLE_TYPE_TAGS.length;
        simpleTypeTags = new String[length];
        System.arraycopy(UnoTypes.SIMPLE_TYPE_TAGS, 1, simpleTypeTags, 0, length - 1);
        simpleTypeTags[length - 1] = "[]"; // NOI18N
    }
    
    /**
     * Creates a new instance of ComponentTypePropertyEditor
     */
    public ComponentTypePropertyEditor(String[] tags) {
        simpleTypeTags = tags;
    }

    public String[] getTags() {
        return simpleTypeTags;
    }
    
    public String getAsText() {
        return (String)getValue();
    }
    
    public void setAsText(String text) {
        if(text.equals("[]")) { // NOI18N
            String value = (String)getValue();
            setValue(value.concat("[]")); // NOI18N
        }
        else {
            setValue(text);
        }
    }
    
    public void attachEnv(PropertyEnv propertyEnv) {
        this.propertyEnv = propertyEnv;
    }
    
    public PropertyEnv getPropertyEnv() {
        return propertyEnv;
    }
    
    public Component getCustomEditor() {
        return new ComponentTypeCustomEditor(this, propertyEnv);
    }
    
    public boolean supportsCustomEditor() {
        return true;
    }    
}
