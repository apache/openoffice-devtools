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

import org.netbeans.jellytools.OptionsOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Options" NbDialog.
 *
 * @author Thorsten
 * @version 1.0
 */
public class Options extends OptionsOperator {

    
    private JLabelOperator _lblStarOfficeOpenOfficeOrgExtensions;
    private JLabelOperator _lblOfficeInstallation;
    private JLabelOperator _lblSDKInstallation;
    private JTextFieldOperator _txtSDKInstallation;
    private JButtonOperator _btSDKInstallation;
    private JTextFieldOperator _txtOfficeInstallation;
    private JButtonOperator _btOfficeInstallation;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "StarOffice/OpenOffice.org Extensions" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblStarOfficeOpenOfficeOrgExtensions() {
        if (_lblStarOfficeOpenOfficeOrgExtensions==null) {
            String category = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.config.Bundle", "AdvancedOption_DisplayName");
            // "StarOffice/OpenOffice.org Extensions"
            _lblStarOfficeOpenOfficeOrgExtensions = new JLabelOperator(this, category);
        }
        return _lblStarOfficeOpenOfficeOrgExtensions;
    }

    /** Tries to find "Office Installation:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblOfficeInstallation() {
        if (_lblOfficeInstallation==null) {
            _lblOfficeInstallation = new JLabelOperator(this, "Office Installation:");
      //      JScrollPaneOperator a = new JScrollPaneOperator(this, 1);
        }
        return _lblOfficeInstallation;
    }

    /** Tries to find "SDK Installation:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblSDKInstallation() {
        if (_lblSDKInstallation==null) {
            _lblSDKInstallation = new JLabelOperator(this, "SDK Installation:");
        }
        return _lblSDKInstallation;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtSDKInstallation() {
        if (_txtSDKInstallation==null) {
            _txtSDKInstallation = new JTextFieldOperator(this);
        }
        return _txtSDKInstallation;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btSDKInstallation() {
        if (_btSDKInstallation==null) {
            _btSDKInstallation = new JButtonOperator(this, "Browse...");
        }
        return _btSDKInstallation;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtOfficeInstallation() {
        if (_txtOfficeInstallation==null) {
            _txtOfficeInstallation = new JTextFieldOperator(this, 1);
        }
        return _txtOfficeInstallation;
    }

    /** Tries to find "Browse..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOfficeInstallation() {
        if (_btOfficeInstallation==null) {
            _btOfficeInstallation = new JButtonOperator(this, "Browse...", 1);
        }
        return _btOfficeInstallation;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** gets text for txtSDKInstallation
     * @return String text
     */
    public String getSDKInstallation() {
        return txtSDKInstallation().getText();
    }

    /** sets text for txtSDKInstallation
     * @param text String text
     */
    public void setSDKInstallation(String text) {
        txtSDKInstallation().setText(text);
    }

    /** types text for txtSDKInstallation
     * @param text String text
     */
    public void typeSDKInstallation(String text) {
        txtSDKInstallation().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public void sDKInstallation() {
        btSDKInstallation().push();
    }

    /** gets text for txtOfficeInstallation
     * @return String text
     */
    public String getOfficeInstallation() {
        return txtOfficeInstallation().getText();
    }

    /** sets text for txtOfficeInstallation
     * @param text String text
     */
    public void setOfficeInstallation(String text) {
        txtOfficeInstallation().setText(text);
    }

    /** types text for txtOfficeInstallation
     * @param text String text
     */
    public void typeOfficeInstallation(String text) {
        txtOfficeInstallation().typeText(text);
    }

    /** clicks on "Browse..." JButton
     */
    public void officeInstallation() {
        btOfficeInstallation().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of Options by accessing all its components.
     */
    @Override
    public void verify() {
        lblStarOfficeOpenOfficeOrgExtensions();
        lblOfficeInstallation();
        lblSDKInstallation();
        txtSDKInstallation();
        btSDKInstallation();
        txtOfficeInstallation();
        btOfficeInstallation();
    }

    /** Performs simple test of Options
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new Options().verify();
        System.out.println("Options verification finished.");
    }
}

