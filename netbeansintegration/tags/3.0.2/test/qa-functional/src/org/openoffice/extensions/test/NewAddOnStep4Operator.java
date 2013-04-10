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
public class NewAddOnStep4Operator extends NewProjectWizardOperator {

    /**
     * Creates new NewAddOnStep3Operator that can handle it.
    public NewAddOnStep3Operator() {
        super(new NameComponentChooser("dialog0"));
    }
   */
  
    private JButtonOperator _btLeft;
    private JButtonOperator _btRight;
    private JSplitPaneOperator _sppJSplitPane;
    private JSplitPaneOperator _sppJSplitPane2;
    private JTextAreaOperator _txtDescription;
    private JLabelOperator _lblTopLevelMenu;
    private JButtonOperator _btJButton;
    private JTableOperator _tabPropertiesTable;
    private JTableOperator _tabTreeTable;
    private JButtonOperator _btAddCommand;
    private JButtonOperator _btAddMenu;
    private JButtonOperator _btAddSeparator;
    private JButtonOperator _btDelete;
    private JButtonOperator _btAddLanguage;
    private JButtonOperator _btDeleteLanguage;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Up" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btUp() {
        if (_btLeft==null) {
            _btLeft = new JButtonOperator(this, "Left");
        }
        return _btLeft;
    }

    /** Tries to find "Down" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDown() {
        if (_btRight==null) {
            _btRight = new JButtonOperator(this, "Right");
        }
        return _btRight;
    }

    /** Tries to find null JSplitPane in this dialog.
     * @return JSplitPaneOperator
     */
    public JSplitPaneOperator sppJSplitPane() {
        if (_sppJSplitPane==null) {
            _sppJSplitPane = new JSplitPaneOperator(this);
        }
        return _sppJSplitPane;
    }

    /** Tries to find null JSplitPane in this dialog.
     * @return JSplitPaneOperator
     */
    public JSplitPaneOperator sppJSplitPane2() {
        if (_sppJSplitPane2==null) {
            _sppJSplitPane2 = new JSplitPaneOperator(sppJSplitPane());
        }
        return _sppJSplitPane2;
    }

    /** Tries to find null JTextArea in this dialog.
     * @return JTextAreaOperator
     */
    public JTextAreaOperator txtDescription2() {
        if (_txtDescription==null) {
            _txtDescription = new JTextAreaOperator(sppJSplitPane2());
        }
        return _txtDescription;
    }

    /** Tries to find "Top Level Menu" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblTopLevelMenu() {
        if (_lblTopLevelMenu==null) {
            _lblTopLevelMenu = new JLabelOperator(sppJSplitPane2(), "Top Level Menu");
        }
        return _lblTopLevelMenu;
    }

    /** Tries to find null JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btJButton() {
        if (_btJButton==null) {
            _btJButton = new JButtonOperator(sppJSplitPane2());
        }
        return _btJButton;
    }

    /** Tries to find null SheetTable in this dialog.
     * @return JTableOperator
     */
    public JTableOperator tabPropertiesTable() {
        if (_tabPropertiesTable==null) {
            _tabPropertiesTable = new JTableOperator(sppJSplitPane2());
        }
        return _tabPropertiesTable;
    }

    /** Tries to find null TreeTable in this dialog.
     * @return JTableOperator
     */
    public JTableOperator tabTreeTable() {
        if (_tabTreeTable==null) {
            _tabTreeTable = new JTableOperator(sppJSplitPane(), 1);
        }
        return _tabTreeTable;
    }

    /** Tries to find "Add Command" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddCommand() {
        if (_btAddCommand==null) {
            _btAddCommand = new JButtonOperator(this, "Add Command");
        }
        return _btAddCommand;
    }

    /** Tries to find "Add Menu" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddMenu() {
        if (_btAddMenu==null) {
            _btAddMenu = new JButtonOperator(this, "Add Menu");
        }
        return _btAddMenu;
    }

    /** Tries to find "Add Separator" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddSeparator() {
        if (_btAddSeparator==null) {
            _btAddSeparator = new JButtonOperator(this, "Add Separator");
        }
        return _btAddSeparator;
    }

    /** Tries to find "Delete" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDelete() {
        if (_btDelete==null) {
            _btDelete = new JButtonOperator(this, "Delete");
        }
        return _btDelete;
    }

    /** Tries to find "Add Language..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddLanguage() {
        if (_btAddLanguage==null) {
            _btAddLanguage = new JButtonOperator(this, "Add Language...");
        }
        return _btAddLanguage;
    }

    /** Tries to find "Delete Language..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDeleteLanguage() {
        if (_btDeleteLanguage==null) {
            _btDeleteLanguage = new JButtonOperator(this, "Delete Language...");
        }
        return _btDeleteLanguage;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** clicks on "Up" JButton
     */
    public void up() {
        btUp().push();
    }

    /** clicks on "Down" JButton
     */
    public void down() {
        btDown().push();
    }

    /** gets text for txtDescription
     * @return String text
     */
    public String getDescription() {
        return txtDescription().getText();
    }

    /** sets text for txtDescription
     * @param text String text
     */
    public void setDescription(String text) {
        txtDescription().setText(text);
    }

    /** types text for txtDescription
     * @param text String text
     */
    public void typeDescription(String text) {
        txtDescription().typeText(text);
    }

    /** clicks on null JButton
     */
    public void jButton() {
        btJButton().push();
    }

    /** clicks on "Add Command" JButton
     */
    public void addCommand() {
        btAddCommand().push();
    }

    /** clicks on "Add Menu" JButton
     */
    public void addMenu() {
        btAddMenu().push();
    }

    /** clicks on "Add Separator" JButton
     */
    public void addSeparator() {
        btAddSeparator().push();
    }

    /** clicks on "Delete" JButton
     */
    public void delete() {
        btDelete().push();
    }

    /** clicks on "Add Language..." JButton
     */
    public void addLanguage() {
        btAddLanguage().push();
    }

    /** clicks on "Delete Language..." JButton
     */
    public void deleteLanguage() {
        btDeleteLanguage().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /**
     * Performs verification of NewAddOnStep3Operator by accessing all its components.
     */
    public void verify() {
/*        btUp();
        btDown();
        sppJSplitPane();
        sppJSplitPane2();
        txtDescription();
        lblTopLevelMenu();
        btJButton();
        tabPropertiesTable();
        tabTreeTable();
        btAddCommand();
        btAddMenu();
        btAddSeparator();
        btDelete();
        btAddLanguage();
        btDeleteLanguage(); */
    }

    /**
     * Performs simple test of NewAddOnStep3Operator
     * 
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NewAddOnStep4Operator().verify();
        System.out.println("NewProjectDefine verification finished.");
    }
}

