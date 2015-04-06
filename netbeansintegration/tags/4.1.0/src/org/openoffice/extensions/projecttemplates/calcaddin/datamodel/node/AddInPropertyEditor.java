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

import java.beans.FeatureDescriptor;
import java.beans.PropertyEditorSupport;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.nodes.Node;

/**
 *
 * @author sg128468
 */
public class AddInPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor {
    
    private static final String[] paramTags = new String[] {
        "int", "double", "string", "int[][]", "double[][]", "String[][]",
        "Object[][]", "Object", "XCellRange", "XPropertySet", "Object[]",        
    }; // NOI18N

    private static final String[] returnTags = new String[] {
        "int", "double", "String", "int[][]", "double[][]", "String[][]",
        "Object[][]", "XVolatileResult", "Object",
    }; // NOI18N

    private static final String[] categoryTags = new String[] {
        "Add-In",
        "Database", "Date&Time", "Financial", "Information", "Logical", 
        "Mathematical", "Matrix", "Statistical", "Spreadsheet", "Text", 
    }; // NOI18N

    private String[] actualTags;
    public static final int CATEGORY_EDITOR = 0;
    public static final int PARAMETER_EDITOR = 1;
    public static final int RETURN_TYPE_EDITOR = 2;

    private PropertyEnv propertyEnv;
    
    /**
     * Creates a new instance of AddInPropertyEditor
     */
    public AddInPropertyEditor(int editorType) {
        switch(editorType) {
            case CATEGORY_EDITOR:
                actualTags = categoryTags;
                break;
            case PARAMETER_EDITOR:
                actualTags = paramTags;
                break;
            case RETURN_TYPE_EDITOR:
                actualTags = returnTags;
                break;
            default:
                actualTags = new String[] {"unkown editor"};  // NOI18N
        }
    }
    
    public String[] getTags() {
        return actualTags;
    }
    
    public String getAsText() {
        return (String)getValue();
    }
    
    public void setAsText(String text) {
        setValue(text);
    }
    
    public void attachEnv(PropertyEnv propertyEnv) {
        this.propertyEnv = propertyEnv;
    }
}
