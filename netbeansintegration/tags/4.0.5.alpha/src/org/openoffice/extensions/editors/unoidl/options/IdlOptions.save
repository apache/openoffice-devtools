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

package org.openoffice.extensions.editors.unoidl.options;

import java.util.MissingResourceException;
import org.netbeans.modules.editor.options.BaseOptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openoffice.extensions.editors.unoidl.UnoIdlEditorKit;

public class IdlOptions extends BaseOptions {
    
    public static String IDL = "Idl"; // NOI18N
    
    /** Name of property. */
    private static final String HELP_ID = "editing.editor.idl"; // NOI18N
    
    //no idl specific options at this time
    static final String[] IDL_PROP_NAMES = new String[] {};
    
    public IdlOptions() {
        super(UnoIdlEditorKit.class, IDL);
    }
    
    /**
     * Gets the help ID
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(HELP_ID);
    }
    
    /**
     * Look up a resource bundle message, if it is not found locally defer to
     * the super implementation
     */
    protected String getString(String key) {
        try {
            return NbBundle.getMessage(IdlOptions.class, key);
        } catch (MissingResourceException e) {
            return super.getString(key);
        }
    }
}