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
public class NewCalcAddInStep3Operator extends NewProjectWizardOperator {

    /**
     * Creates new NewCalcAddInStep3Operator that can handle it.
     *    public NewCalcAddInStep3Operator() {
     *        super(new NameComponentChooser("dialog0"));
     *    }
     */

    private JSplitPaneOperator _sppJSplitPane;
    private JSplitPaneOperator _sppJSplitPane2;
    private JTextAreaOperator _txtDescription;
    private JLabelOperator _lblIntFunction1IntParameter1;
    private JButtonOperator _btJButton;
    private JTableOperator _tabPropertiesTable;
    private JTreeOperator _treeTreeView$ExplorerTree;
    private JButtonOperator _btAddLanguage;
    private JButtonOperator _btDelete;
    private JButtonOperator _btAddParameter;
    private JButtonOperator _btDeleteLanguage;
    private JButtonOperator _btAddFunction;


    //******************************
    // Subcomponents definition part
    //******************************

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
    public JTextAreaOperator txtDescription() {
        if (_txtDescription==null) {
            _txtDescription = new JTextAreaOperator(sppJSplitPane2());
        }
        return _txtDescription;
    }
     */

    /** Tries to find "int function1(int parameter1);" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblIntFunction1IntParameter1() {
        if (_lblIntFunction1IntParameter1==null) {
            _lblIntFunction1IntParameter1 = new JLabelOperator(sppJSplitPane2(), "int function1(int parameter1);");
        }
        return _lblIntFunction1IntParameter1;
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

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treeTreeView$ExplorerTree() {
        if (_treeTreeView$ExplorerTree==null) {
            _treeTreeView$ExplorerTree = new JTreeOperator(sppJSplitPane());
        }
        return _treeTreeView$ExplorerTree;
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

    /** Tries to find "Delete" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDelete() {
        if (_btDelete==null) {
            _btDelete = new JButtonOperator(this, "Delete");
        }
        return _btDelete;
    }

    /** Tries to find "Add Parameter" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddParameter() {
        if (_btAddParameter==null) {
            _btAddParameter = new JButtonOperator(this, "Add Parameter");
        }
        return _btAddParameter;
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

    /** Tries to find "Add Function" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddFunction() {
        if (_btAddFunction==null) {
            _btAddFunction = new JButtonOperator(this, "Add Function");
        }
        return _btAddFunction;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

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

    /** clicks on "Add Language..." JButton
     */
    public void addLanguage() {
        btAddLanguage().push();
    }

    /** clicks on "Delete" JButton
     */
    public void delete() {
        btDelete().push();
    }

    /** clicks on "Add Parameter" JButton
     */
    public void addParameter() {
        btAddParameter().push();
    }

    /** clicks on "Delete Language..." JButton
     */
    public void deleteLanguage() {
        btDeleteLanguage().push();
    }

    /** clicks on "Add Function" JButton
     */
    public void addFunction() {
        btAddFunction().push();
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /**
     * Performs verification of NewCalcAddInStep3Operator by accessing all its components.
     */
    public void verify() {
        sppJSplitPane();
        sppJSplitPane2();
        txtDescription();
        lblIntFunction1IntParameter1();
        btJButton();
        tabPropertiesTable();
        treeTreeView$ExplorerTree();
        btAddLanguage();
        btDelete();
        btAddParameter();
        btDeleteLanguage();
        btAddFunction();
    }

    /**
     * Performs simple test of NewCalcAddInStep3Operator
     * 
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NewCalcAddInStep3Operator().verify();
        System.out.println("NewProject3 verification finished.");
    }
}

