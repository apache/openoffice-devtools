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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openoffice.extensions.config.office.OpenOfficeLocation;

final class ConfigurationOptionsPanelController extends OptionsPanelController {

    private static final String HELP_CTX_STRING = 
            "org.openoffice.extensions.config.paths"; // NOI18N
    
    private ConfigurationOptionsContainer panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;
    
    private ConfigurationSettings settings;

    public void update() {
        panel.setOffice(settings.getValue(settings.KEY_OFFICE_INSTALLATION));
        panel.setSDK(settings.getValue(settings.KEY_SDK_INSTALLATION));
        panel.updateLogging();
//        getPanel().load();
//        changed = false;
    }
    
    public void applyChanges() {
        settings.setValue(settings.KEY_OFFICE_INSTALLATION, panel.getOffice());
        settings.setValue(settings.KEY_SDK_INSTALLATION, panel.getSDK());
        settings.setValue(settings.KEY_LOG_LEVEL, panel.getLogLevel());
        settings.setValue(settings.KEY_LOG_FILE, panel.getLogFile());
        settings.store();
        changed=false;        
//        getPanel().store();
//        changed = false;
    }
    
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }
    
    public boolean isValid() {
        return panel.valid();
    }
    
    public boolean isChanged() {
        
        return (!panel.getOffice().trim().equals(settings.getValue(settings.KEY_OFFICE_INSTALLATION)) ||
                !panel.getSDK().trim().equals(settings.getValue(settings.KEY_SDK_INSTALLATION)));
//        return changed;
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(HELP_CTX_STRING);
    }
    
    public JComponent getComponent(Lookup masterLookup) {
        return panel;
    }
    
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }
    
//    private ConfigurationOptionsContainer getPanel() {
//        if (panel == null) {
//            panel = new ConfigurationPanel(settings.getValue(settings.KEY_OFFICE_INSTALLATION),
//                                           settings.getValue(settings.KEY_SDK_INSTALLATION));
//        }
//        return panel;
//    }
    
    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
    
    public ConfigurationOptionsPanelController() {
        settings = ConfigurationSettings.getSettings();
        
        panel = new ConfigurationOptionsContainer(
            settings.getValue(settings.KEY_OFFICE_INSTALLATION),
            settings.getValue(settings.KEY_SDK_INSTALLATION));
    }
}
