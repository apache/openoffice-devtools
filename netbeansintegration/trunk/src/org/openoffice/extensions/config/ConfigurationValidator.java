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

package org.openoffice.extensions.config;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.HelpCtx;
import org.openoffice.extensions.config.office.OpenOfficeLocation;
import org.openoffice.extensions.config.office.PlatformInfo;

/**
 *
 * @author js93811
 */
public class ConfigurationValidator {
    
    /** 
     * Creates a new instance of ConfigurationValidator -
     * do not use, everything's static
     */
    private ConfigurationValidator() {
    }
    
    public static boolean validatePlatform() {
        return !PlatformInfo.getPlatformBinDir().equals(PlatformInfo.UNKNOWN_PLATFORM);
    }
    
    public static boolean validateSettings() {
        final ConfigurationSettings settings = ConfigurationSettings.getSettings();
        String officePath = settings.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION);
        String sdkPath = settings.getValue(ConfigurationSettings.KEY_SDK_INSTALLATION);
        OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation(officePath, sdkPath, false);
        boolean valid = (loc != null && loc.validate());
        if (!valid) {
            String title = ResourceBundle.getBundle(
                "org/openoffice/extensions/config/Bundle").getString(
                "ConfigurationDialog_Title"); // NOI18N
            final ConfigurationValidatorPanel panel = new ConfigurationValidatorPanel(officePath, sdkPath);
            DialogDescriptor ddscr = new DialogDescriptor(panel, title);
            ddscr.setButtonListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (e.getActionCommand().equals("OK")) { // NOI18N
                        settings.setValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION, panel.getOffice());
                        settings.setValue(ConfigurationSettings.KEY_SDK_INSTALLATION, panel.getSDK());
                        settings.store();
                    }
                }
            });
            // set help to this panel
            ddscr.setHelpCtx(new HelpCtx("org.openoffice.extensions.config.paths")); // NOI18N
            panel.setDialogDescriptor(ddscr);
            ddscr.setValid(false); // disable OK button
            panel.validateWithFocus();  // set the initial error message incl. focus on the right text field
            Dialog d = DialogDisplayer.getDefault().createDialog(ddscr);
            d.setVisible(true);
            officePath = settings.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION);
            sdkPath = settings.getValue(ConfigurationSettings.KEY_SDK_INSTALLATION);
            loc = OpenOfficeLocation.getOpenOfficeLocation(officePath, sdkPath, false);
            valid = (loc != null && loc.validate());
        }
        return valid;
    }
}
