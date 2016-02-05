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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.editor.BaseTokenID;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

/**
 *
 */
public class UnoIdlTokenContext extends TokenContext  {
    
      // Numeric-ids for token categories
    public static final int TEXT_ID = 1;
    public static final int STRING_ID = 2;
    public static final int NUMBER_ID = 3;   
    public static final int BRACKET_TEXT_ID = 4;
    public static final int PRECOMPILE_ID = 5;
    public static final int COMMENT_ID = 6;
    public static final int KEYWORD_ID = 7;
    public static final int CHAR_ID = 8;
    public static final int FUNCTION_ID = 9;
    
    // Token-ids
    public static final BaseTokenID TEXT = new BaseTokenID("text", TEXT_ID); // NOI18N
    public static final BaseTokenID STRING = new BaseTokenID("string", STRING_ID); // NOI18N
    public static final BaseTokenID NUMBER = new BaseTokenID("number", NUMBER_ID); // NOI18N
    public static final BaseTokenID COMMENT = new BaseTokenID("comment", COMMENT_ID); // NOI18N
    public static final BaseTokenID KEYWORD = new BaseTokenID("keyword", KEYWORD_ID); // NOI18N
    public static final BaseTokenID PRECOMPILE = new BaseTokenID("precompiler", PRECOMPILE_ID); // NOI18N
    public static final BaseTokenID BRACKET_TEXT = new BaseTokenID("bracket", BRACKET_TEXT_ID); // NOI18N
    public static final BaseTokenID CHAR = new BaseTokenID("char", CHAR_ID); // NOI18N
    public static final BaseTokenID FUNCTION = new BaseTokenID("function", FUNCTION_ID); // NOI18N

    // Context instance declaration
    public static final UnoIdlTokenContext context = new UnoIdlTokenContext();
    public static final TokenContextPath contextPath = context.getContextPath();
    
    /**
     * Construct a new UnoIdlTokenContext
     */
    private UnoIdlTokenContext() {
        super("idl-"); // NOI18N
        
        try {
            addDeclaredTokenIDs();
        } catch (Exception e) {
            Logger.getLogger(UnoIdlTokenContext.class.getName()).log(Level.SEVERE, null, e);
        }
    }    
}
