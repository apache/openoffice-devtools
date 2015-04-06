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

import org.netbeans.editor.Settings;
import org.openide.modules.ModuleInstall;
import org.openide.util.NbBundle;
import org.openoffice.extensions.config.CodeTemplates;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.config.ConfigurationValidator;

@SuppressWarnings( "deprecation" )         
public class RestoreColoring extends ModuleInstall {

    /**
     * Localizer passed to editor.
     */
    private static org.netbeans.editor.LocaleSupport.Localizer localizer;

    /**
     * Registers properties editor, installs options and copies settings.
     * Overrides superclass method.
     */
    @Override
    public void restored() {
        addInitializer();
        installOptions();
        // do nothing with the settings, see only that they are initialized
        // since requestInstallations is not called, initializing must be done explicitly
        ConfigurationSettings.getSettings();
        // do insert the template code abbreviations
        CodeTemplates.createTemplates();
//        requestInstallations();
    }
    
    /**
     * Uninstalls properties options.
     * And cleans up editor settings copy.
     * Overrides superclass method.
     */
    @Override
    public void uninstalled() {
        uninstallOptions();
    }
    
    /**
     * Adds initializer and registers editor kit.
     */
    public void addInitializer() {
        Settings.addInitializer(new UnoIdlSettingsInitializer());
    }
    
    /**
     * Installs properties editor and print options.
     */
    public void installOptions() {
        // Adds localizer.
        org.netbeans.editor.LocaleSupport.addLocalizer(localizer = new org.netbeans.editor.LocaleSupport.Localizer() {
            public String getString(String key) {
                return NbBundle.getMessage(RestoreColoring.class, key);
            }
        }); 
    }
    
    /** Uninstalls properties editor and print options. */
    public void uninstallOptions() {
        // remove localizer
        org.netbeans.editor.LocaleSupport.removeLocalizer(localizer);
    }
 
    private void requestInstallations() {
        ConfigurationValidator.validateSettings();
    }

}
