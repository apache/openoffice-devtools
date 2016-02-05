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

import org.netbeans.jemmy.operators.*;
import org.netbeans.jemmy.util.NameComponentChooser;

/** Class implementing all necessary methods for handling "Create New Data Type" NbDialog.
 *
 * @author Thorsten
 * @version 1.0
 */
public class CreateNewDataType extends JDialogOperator {

    /** Creates new CreateNewDataType that can handle it.
     */
    public CreateNewDataType() {
        super(new NameComponentChooser(new JDialogOperator("Create New Data Type").getName()));
         
    }

    private JLabelOperator _lblDataType;
    private JComboBoxOperator _cboDataType;
    /**
     * 
     */
    public static final String ITEM_SERVICE = "Service";
    private JSplitPaneOperator _sppJSplitPane;
    private JSplitPaneOperator _sppJSplitPane2;
    private JTextAreaOperator _txtDescription;
    private JLabelOperator _lblOrgOpenofficeHereUnoComponent1Service;
    private JButtonOperator _btJButton;
    private JTableOperator _tabPropertiesTable;
    private JTreeOperator _treeTreeView$ExplorerTree;
    private JLabelOperator _lblJLabel;
    private JButtonOperator _btOK;
    private JButtonOperator _btCancel;
    private JButtonOperator _btHelp;
    private JButtonOperator _btDelete;
    private JButtonOperator _btNewParameter;
    private JButtonOperator _btNewFunction;
    private JButtonOperator _btAddEnum;
    private JButtonOperator _btAddType;
    private JButtonOperator _btAddTemplate;
    private JButtonOperator _btAddMember;


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find "Data Type" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblDataType() {
        if (_lblDataType==null) {
            _lblDataType = new JLabelOperator(this, "Data Type");
        }
        return _lblDataType;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboDataType() {
        if (_cboDataType==null) {
            _cboDataType = new JComboBoxOperator(this);
        }
        return _cboDataType;
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
    public JTextAreaOperator txtDescription() {
        if (_txtDescription==null) {
            _txtDescription = new JTextAreaOperator(sppJSplitPane2());
        }
        return _txtDescription;
    }

    /** Tries to find "org.openoffice.here.UnoComponent1Service" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblOrgOpenofficeHereUnoComponent1Service() {
        if (_lblOrgOpenofficeHereUnoComponent1Service==null) {
            _lblOrgOpenofficeHereUnoComponent1Service = new JLabelOperator(sppJSplitPane2(), "org.openoffice.here.UnoComponent1Service");
        }
        return _lblOrgOpenofficeHereUnoComponent1Service;
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

    /** Tries to find " " JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblJLabel() {
        if (_lblJLabel==null) {
            _lblJLabel = new JLabelOperator(this, " ", 1);
        }
        return _lblJLabel;
    }

  /** Tries to find "OK" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btOK() {
        if (_btOK==null) {
            _btOK = new JButtonOperator(this, "OK");
        }
        return _btOK;
    }

    /** Tries to find "Cancel" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btCancel() {
        if (_btCancel==null) {
            _btCancel = new JButtonOperator(this, "Cancel");
        }
        return _btCancel;
    }

    /** Tries to find "Help" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btHelp() {
        if (_btHelp==null) {
            _btHelp = new JButtonOperator(this, "Help");
        }
        return _btHelp;
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

    /** Tries to find "New Parameter" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btNewParameter() {
        if (_btNewParameter==null) {
            _btNewParameter = new JButtonOperator(this, "New Parameter");
        }
        return _btNewParameter;
    }

    /** Tries to find "New Function" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btNewFunction() {
        if (_btNewFunction==null) {
            _btNewFunction = new JButtonOperator(this, "New Function");
        }
        return _btNewFunction;
    }

    /** Tries to find "Add Enum" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddEnum() {
        if (_btAddEnum==null) {
            _btAddEnum = new JButtonOperator(this, "Add Enum");
        }
        return _btAddEnum;
    }

    /** Tries to find "Add Type" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddType() {
        if (_btAddType==null) {
            _btAddType = new JButtonOperator(this, "Add Type");
        }
        return _btAddType;
    }

    /** Tries to find "Add Template" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddTemplate() {
        if (_btAddTemplate==null) {
            _btAddTemplate = new JButtonOperator(this, "Add Template");
        }
        return _btAddTemplate;
    }

    /** Tries to find "Add Member" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddMember() {
        if (_btAddMember==null) {
            _btAddMember = new JButtonOperator(this, "Add Member");
        }
        return _btAddMember;
    }

    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** returns selected item for cboDataType
     * @return String item
     */
    public String getSelectedDataType() {
        return cboDataType().getSelectedItem().toString();
    }

    /** selects item for cboDataType
     * @param item String item
     */
    public void selectDataType(String item) {
        cboDataType().selectItem(item);
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

  /** clicks on "OK" JButton
     */
    public void ok() {
        btOK().push();
    }

    /** clicks on "Cancel" JButton
     */
    public void cancel() {
        btCancel().push();
    }

    /** clicks on "Help" JButton
     */
    public void help() {
        btHelp().push();
    }

/** clicks on "Delete" JButton
     */
    public void delete() {
        btDelete().push();
    }

    /** clicks on "New Parameter" JButton
     */
    public void newParameter() {
        btNewParameter().push();
    }

    /** clicks on "New Function" JButton
     */
    public void newFunction() {
        btNewFunction().push();
    }

    /** clicks on "Add Enum" JButton
     */
    public void addEnum() {
        btAddEnum().push();
    }

    /** clicks on "Add Type" JButton
     */
    public void addType() {
        btAddType().push();
    }

    /** clicks on "Add Template" JButton
     */
    public void addTemplate() {
        btAddTemplate().push();
    }

    /** clicks on "Add Member" JButton
     */
    public void addMember() {
        btAddMember().push();
    }

    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of CreateNewDataType by accessing all its components.
     */
    public void verify() {
        lblDataType();
        cboDataType();
        sppJSplitPane();
        sppJSplitPane2();
        txtDescription();
        lblOrgOpenofficeHereUnoComponent1Service();
        btJButton();
        tabPropertiesTable();
        treeTreeView$ExplorerTree();
        lblJLabel();
        btOK();
        btCancel();
        btHelp();    
        btDelete();
        btNewParameter();
        btNewFunction();
        btAddEnum();
        btAddType();
        btAddTemplate();
        btAddMember();
    }

    /** Performs simple test of CreateNewDataType
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        new CreateNewDataType().verify();
        System.out.println("CreateNewDataType verification finished.");
    }
}

