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
package org.openoffice.extensions.filetypes.unoidl;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;
import org.openoffice.extensions.projecttemplates.component.dialogs.ChangeEventPanel;

public class IdlWizardPanel2 implements WizardDescriptor.Panel, ChangeEventPanel {
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private IdlVisualPanel2 component;

    private WizardDescriptor wizard;

    public IdlWizardPanel2(WizardDescriptor wizard) {
        this.wizard = wizard;
    }
    
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component  = new IdlVisualPanel2(this, wizard);
        }
        return component;
    }

    
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }
    
    public boolean isValid() {
        return component.valid(wizard);
    }
    
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1);
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    public final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }
    
    public void readSettings(Object settings) {
        component  = new IdlVisualPanel2(this, (WizardDescriptor)settings);
//        component.read((WizardDescriptor)settings);
    }
    
    public void storeSettings(Object settings) {
        component.store((WizardDescriptor)settings);
    }

}

