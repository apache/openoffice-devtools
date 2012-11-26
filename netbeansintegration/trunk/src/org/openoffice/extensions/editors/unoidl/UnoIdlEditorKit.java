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

package org.openoffice.extensions.editors.unoidl;

import javax.swing.text.Document;
//import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Syntax;
//import org.netbeans.editor.SyntaxSupport;
import org.netbeans.modules.editor.NbEditorKit;
//import org.openide.ErrorManager;

/**
 *
 */
public class UnoIdlEditorKit extends NbEditorKit {
    
    public static final String MIME_TYPE = "text/x-uno-idl"; // NOI18N
    
    /**
     * 
     * Creates a new instance of UnoIdlEditorKit
     */
    public UnoIdlEditorKit() { 
    }
    
    /**
     * Create a syntax object suitable for highlighting Idl file syntax
     */
    @Override
    public Syntax createSyntax(Document doc) {  
        return new UnoIdlSyntax();
    }
    
    /**
     * Retrieves the content type for this editor kit
     */
    @Override
    public String getContentType() {
        return MIME_TYPE;
    }    
}
