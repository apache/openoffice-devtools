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

package org.openoffice.extensions.test;

import java.awt.Component;
import javax.swing.JCheckBox;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.WizardOperator;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;

/**
 * 
 * @author sg128468
 */
public class PluginInstallationWizardOperator extends WizardOperator {

    public JCheckBoxOperator getAcceptLicenseCheckbox() {
        return new JCheckBoxOperator(this, new ComponentChooser() {
            public boolean checkComponent(Component arg0) {
                // instanceof costs time, but tests are already slow
                if (arg0 instanceof JCheckBox) {
                    // works because there is only one checkbox
                    return true;
                }
                return false;
            }
            public String getDescription() {
                return "Search for the license checkbox";
            }
        });
        
    }
    
    public JButtonOperator getButton(String name) {
        JButtonOperator button = new JButtonOperator(this, name);
        return button;
    }
    
    /**
     * Handle validation warning dialog.
     * TODO: handle better when dialog is not there
     */
    public void clickOverValidationWarning() {
        try {
            NbDialogOperator valDialog = new NbDialogOperator("Validation Warning");
            Timeouts out = new Timeouts();
            out.setTimeout("Validation Dialog", 10000); // wait for 10 seconds
            valDialog.setTimeouts(out);
            valDialog.waitComponentVisible(true);
            if (valDialog.isVisible()) { // when appeared, click.
                JButtonOperator contnue = new JButtonOperator(valDialog, "Continue");
                contnue.clickMouse();
            }
        }
        catch(TimeoutExpiredException ex) {
            // empty by design: plugin is signed, no warning appears
        }
    }
    
    public PluginInstallationWizardOperator(String title) {
        super(title);
    }
}
