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

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsNames;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContext;
import org.netbeans.editor.TokenContextPath;

/**
 *
 * @author Steffen
 */
public class UnoIdlSettingsInitializer extends Settings.AbstractInitializer {
    
    public static final String NAME = "uno-idl-settings-initializer"; // NOI18N
    
    /**
     * Constructor
     */
    public UnoIdlSettingsInitializer() {
        super(NAME);
    }
    
    /**
     * Update map filled with the settings.
     * @param kitClass kit class for which the settings are being updated.
     *   It is always non-null value.
     * @param settingsMap map holding [setting-name, setting-value] pairs.
     *   The map can be empty if this is the first initializer
     *   that updates it or if no previous initializers updated it.
     */
    public void updateSettingsMap(Class kitClass, Map settingsMap) {
        if (kitClass == BaseKit.class) {
            new IdlTokenColoringInitializer().updateSettingsMap(kitClass, settingsMap);
        }
        
        if (kitClass == UnoIdlEditorKit.class) {
            SettingsUtil.updateListSetting(
                    settingsMap,
                    SettingsNames.TOKEN_CONTEXT_LIST,
                    new TokenContext[] { UnoIdlTokenContext.context }
            );
        }
    }
    
    /**
     * Class for adding syntax coloring to the editor
     */
    /** Properties token coloring initializer. */
    private static class IdlTokenColoringInitializer extends SettingsUtil.TokenColoringInitializer {
        
        /** Bold font. */
        private static final Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
        /** Italic font. */
        private static final Font italicFont = SettingsDefaults.defaultFont.deriveFont(Font.ITALIC);
        
        /** Key coloring. */
	private static final Coloring textColoring
		= new Coloring(null, Color.BLACK, null);
        /** Keyword coloring. */
        private static final Coloring keywordColoring = new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE, Color.BLUE, null);
        /** Bracket coloring. */
        private static final Coloring bracketColoring = new Coloring(null, Color.BLUE, null);
        /** Precompile coloring. */
        private static final Coloring precompileColoring = new Coloring(null, Color.GREEN, null);
        /** Comment coloring. */
        private static final Coloring commentColoring = new Coloring(null, Color.GRAY, null);
        /** Empty coloring. */
        private static final Coloring emptyColoring = new Coloring(null, null, null);
        /** Precompile coloring. */
        private static final Coloring charColoring = new Coloring(null, Color.GREEN, null);
        /** Comment coloring. */
        private static final Coloring stringColoring = new Coloring(null, Color.MAGENTA, null);
        /** Empty coloring. */
        private static final Coloring numberColoring = new Coloring(null, Color.RED, null);
        
        /** Constructs PropertiesTokenColoringInitializer. */
        public IdlTokenColoringInitializer() {
            super(UnoIdlTokenContext.context);
        }       
        
        /** Gets token coloring. */
        public Object getTokenColoring(TokenContextPath tokenContextPath,
                TokenCategory tokenIDOrCategory, boolean printingSet) {
            
            if(!printingSet) {
                int tokenID = tokenIDOrCategory.getNumericID();

                switch (tokenID) {
                    case UnoIdlTokenContext.TEXT_ID:
                        return textColoring;
                    case UnoIdlTokenContext.FUNCTION_ID:
                        return textColoring;
                    case UnoIdlTokenContext.COMMENT_ID:
                        return commentColoring;
                    case UnoIdlTokenContext.BRACKET_TEXT_ID:
                        return bracketColoring;
                    case UnoIdlTokenContext.KEYWORD_ID:
                        return keywordColoring;
                    case UnoIdlTokenContext.NUMBER_ID:
                        return numberColoring;
                    case UnoIdlTokenContext.CHAR_ID:
                        return charColoring;
                    case UnoIdlTokenContext.STRING_ID:
                        return stringColoring;
                    case UnoIdlTokenContext.PRECOMPILE_ID:
                        return precompileColoring;
                    default:        
                        return null;
                }
            } else { // printing set
                return SettingsUtil.defaultPrintColoringEvaluator;
            }
        }
        
    } 
}
