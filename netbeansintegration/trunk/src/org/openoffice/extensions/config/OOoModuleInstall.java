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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openoffice.extensions.config;

import org.openide.modules.ModuleInstall;

/**
 * Class for handling the installation of the module. This is the first stuff
 * that is called when the plugin is installed in NetBeans.
 */
public class OOoModuleInstall extends ModuleInstall {

    /**
     * Registers properties editor, installs options and copies settings.
     * Overrides superclass method.
     */
    @Override
    public void restored() {
        // do nothing with the settings, see only that they are initialized
        // since requestInstallations is not called, initializing must be done explicitly
        ConfigurationSettings.getSettings();
        // do insert the template code abbreviations
        CodeTemplates.createTemplates();
        // not used at the moment, uncomment if needed.
//        requestInstallations();
    }

    private void requestInstallations() {
        ConfigurationValidator.validateSettings();
    }
}
