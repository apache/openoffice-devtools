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

import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "New Project" NbDialog.
 *
 * @author Thorsten
 * @version 1.0
 */
public class NewCalcAddInStep2Operator extends NewProjectWizardOperator {

    /**
     * Creates new NewCalcAddInStep2Operator that can handle it.
         public NewCalcAddInStep2Operator() {
             super(new NameComponentChooser("dialog0"));
         }
     */
     
     

    private JLabelOperator _lblJavaPackage;
    private JLabelOperator _lblCreatedFiles;
    private JLabelOperator _lblCalcAddInName;
    private JCheckBoxOperator _cbCreateBackwardCompatibleCalcAddIn;
    private JTextFieldOperator _txtProjectName;
    private JTextFieldOperator _txtProjectFolder;
    private JTextFieldOperator _txtCalcAddInName;
    private JTextFieldOperator _txtJavaPackage;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Java Package:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblJavaPackage() {
        if (_lblJavaPackage==null) {
            String JavaPackage = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.calcaddin.Bundle", "LB_PackageName");
            // "Java Package:""
            _lblJavaPackage = new JLabelOperator(this, JavaPackage);
        }
        return _lblJavaPackage;
    }

    /** Tries to find "Created Files:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCreatedFiles() {
        if (_lblCreatedFiles==null) {
            String CreatedFiles = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.calcaddin.Bundle", "LB_CreatedFiles");
            // "Created Files:"
            _lblCreatedFiles = new JLabelOperator(this, CreatedFiles);
        }
        return _lblCreatedFiles;
    }

    /** Tries to find "Calc Add-In Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblCalcAddInName() {
        if (_lblCalcAddInName==null) {
            String CalcName = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.calcaddin.Bundle", "LB_CalcAddinName");
            // is "Calc Add-In Name:"
            _lblCalcAddInName = new JLabelOperator(this, CalcName);
        }
        return _lblCalcAddInName;
    }

    /** Tries to find "Create backward compatible Calc Add-In" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateBackwardCompatibleCalcAddIn() {
        if (_cbCreateBackwardCompatibleCalcAddIn==null) {
            String Backward = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.calcaddin.Bundle", "CB_Backward_Compatibility");
            //"Create backward compatible Calc Add-In"
            _cbCreateBackwardCompatibleCalcAddIn = new JCheckBoxOperator(this, Backward);
        }
        return _cbCreateBackwardCompatibleCalcAddIn;
    }

    public JTextFieldOperator txtProjectName() {
        if (_txtProjectName==null) {
//              JTextFieldOperator d = new JTextFieldOperator((JTextField)a.getLabelFor());
            _txtProjectName = new JTextFieldOperator(this, 0);
        }
        return _txtProjectName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtProjectFolder() {
        if (_txtProjectFolder==null) {
//              JTextFieldOperator d = new JTextFieldOperator((JTextField)a.getLabelFor());
            _txtProjectFolder = new JTextFieldOperator(this, 5);
        }
        return _txtProjectFolder;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtCalcAddInName() {
        if (_txtCalcAddInName==null) {
//              JTextFieldOperator d = new JTextFieldOperator((JTextField)a.getLabelFor());
            _txtCalcAddInName = new JTextFieldOperator(this, 1);
        }
        return _txtCalcAddInName;
    }

    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtJavaPackage() {
        if (_txtJavaPackage==null) {
            _txtJavaPackage = new JTextFieldOperator(this, 2);
        }
        return _txtJavaPackage;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateBackwardCompatibleCalcAddIn(boolean state) {
        if (cbCreateBackwardCompatibleCalcAddIn().isSelected()!=state) {
            cbCreateBackwardCompatibleCalcAddIn().push();
        }
    }

    /** gets text for txtCalcAddInName
     * @return String text
     */
    public String getCalcAddInName() {
        return txtCalcAddInName().getText();
    }

    /** sets text for txtCalcAddInName
     * @param text String text
     */
    public void setCalcAddInName(String text) {
        txtCalcAddInName().setText(text);
    }

    /** types text for txtCalcAddInName
     * @param text String text
     */
    public void typeCalcAddInName(String text) {
        txtCalcAddInName().typeText(text);
    }

    /** gets text for txtJavaPackage
     * @return String text
     */
    public String getJavaPackage() {
        return txtJavaPackage().getText();
    }

    /** sets text for txtJavaPackage
     * @param text String text
     */
    public void setJavaPackage(String text) {
        txtJavaPackage().setText(text);
    }

    /** types text for txtJavaPackage
     * @param text String text
     */
    public void typeJavaPackage(String text) {
        txtJavaPackage().typeText(text);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /**
     * Performs verification of NewCalcAddInStep2Operator by accessing all its components.
     */
    public void verify() {
        lblJavaPackage();
        lblCreatedFiles();
        lblCalcAddInName();
        cbCreateBackwardCompatibleCalcAddIn();
        txtCalcAddInName();
        txtJavaPackage();
    }

    /**
     * Performs simple test of NewCalcAddInStep2Operator
     * 
     * 
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NewCalcAddInStep2Operator().verify();
        System.out.println("NewProject2 verification finished.");
    }

}

