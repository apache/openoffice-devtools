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
public class NewAddOnStep2Operator extends NewProjectWizardOperator {
    
    /**
     * Creates new NewCalcAddInStep2Operator that can handle it.
     * public NewCalcAddInStep2Operator() {
     * super(new NameComponentChooser("dialog0"));
     * }
     */
    
    
    
    private JLabelOperator _lblProjectName;
    private JTextFieldOperator _txtProjectName;
    private JLabelOperator _lblMainClassName;
    private JTextFieldOperator _txtMainClassName;
    private JLabelOperator _lblJavaPackage;
    private JTextFieldOperator _txtJavaPackage;

    private JLabelOperator _lblCreatedFiles;
    
    private JCheckBoxOperator _cbCreateToolbar;
    private JCheckBoxOperator _cbCreateMenu;
    
    
    //******************************
    // Subcomponents definition part
    //******************************
    
    /** Tries to find "Java Package:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblJavaPackage() {
        if (_lblJavaPackage==null) {
            String JavaPackage = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "LBL_JavaPackage");
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
            String CreatedFiles = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "LBL_CreatedFiles");
            // "Created Files:"
            _lblCreatedFiles = new JLabelOperator(this, CreatedFiles);
        }
        return _lblCreatedFiles;
    }
    
    /** Tries to find "Project Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblProjectName() {
        if (_lblProjectName==null) {
            String name = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "LBL_ProjectName");
            // is "Calc Add-In Name:"
            _lblProjectName = new JLabelOperator(this, name);
        }
        return _lblProjectName;
    }
    
    /** Tries to find "Main Class Name:" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblMainClassName() {
        if (_lblMainClassName==null) {
            String CalcName = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "LBL_MainClassName");
            // is "Calc Add-In Name:"
            _lblMainClassName = new JLabelOperator(this, CalcName);
        }
        return _lblMainClassName;
    }
    
    /** Tries to find "Create Toolbar" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateToolbar() {
        if (_cbCreateToolbar==null) {
            String Backward = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "LBL_CreateToolbar");
            //"Create backward compatible Calc Add-In"
            _cbCreateToolbar = new JCheckBoxOperator(this, Backward);
        }
        return _cbCreateToolbar;
    }
    
    /** Tries to find "Create Menu" JCheckBox in this dialog.
     * @return JCheckBoxOperator
     */
    public JCheckBoxOperator cbCreateMenu() {
        if (_cbCreateMenu==null) {
            String Backward = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "LBL_CreateMenu");
            //"Create backward compatible Calc Add-In"
            _cbCreateMenu = new JCheckBoxOperator(this, Backward);
        }
        return _cbCreateMenu;
    }
    
    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtProjectName() {
        if (_txtProjectName==null) {
            
//            for (int i = 0; i < 5; i++) {
//                _txtProjectName = new JTextFieldOperator(this, i);
//                System.out.println("Text " + i + ": " + _txtProjectName.getText());
//            }
            
//              JTextFieldOperator d = new JTextFieldOperator((JTextField)a.getLabelFor());
            _txtProjectName = new JTextFieldOperator(this, 2);
        }
        return _txtProjectName;
    }
    
    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtMainClassName() {
        if (_txtMainClassName==null) {
//              JTextFieldOperator d = new JTextFieldOperator((JTextField)a.getLabelFor());
            _txtMainClassName = new JTextFieldOperator(this, 3);
        }
        return _txtMainClassName;
    }
    
    /** Tries to find null JTextField in this dialog.
     * @return JTextFieldOperator
     */
    public JTextFieldOperator txtJavaPackage() {
        if (_txtJavaPackage==null) {
            _txtJavaPackage = new JTextFieldOperator(this, 0);
        }
        return _txtJavaPackage;
    }
    
    
    //****************************************
    // Low-level functionality definition part
    //****************************************
    
    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateToolbar(boolean state) {
        if (cbCreateToolbar().isSelected()!=state) {
            cbCreateToolbar().push();
        }
    }
    
    /** checks or unchecks given JCheckBox
     * @param state boolean requested state
     */
    public void checkCreateMenu(boolean state) {
        if (cbCreateMenu().isSelected()!=state) {
            cbCreateMenu().push();
        }
    }
    
    /** gets text for txtCalcAddInName
     * @return String text
     */
    public String getProjectName() {
        return txtProjectName().getText();
    }
    
    /** sets text for txtCalcAddInName
     * @param text String text
     */
    public void setProjectName(String text) {
        txtProjectName().setText(text);
    }
    
    /** types text for txtCalcAddInName
     * @param text String text
     */
    public void typeProjectName(String text) {
        txtProjectName().typeText(text);
    }
    
    /** gets text for txtCalcAddInName
     * @return String text
     */
    public String getMainClassName() {
        return txtMainClassName().getText();
    }
    
    /** sets text for txtCalcAddInName
     * @param text String text
     */
    public void setMainClassName(String text) {
        txtMainClassName().setText(text);
    }
    
    /** types text for txtCalcAddInName
     * @param text String text
     */
    public void typeMainClassName(String text) {
        txtMainClassName().typeText(text);
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
        lblProjectName();
        lblMainClassName();
        cbCreateMenu();
        cbCreateToolbar();
        txtProjectName();
        txtMainClassName();
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

