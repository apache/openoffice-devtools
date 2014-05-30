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

import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jemmy.operators.*;

/** Class implementing all necessary methods for handling "Select a new Data type" NbDialog.
 *
 * @author Thorsten
 * @version 1.0
 */
public class SelectANewDataType extends NbDialogOperator {

    /** Creates new SelectANewDataType that can handle it.
     * @param testName 
     */
    public SelectANewDataType(String testName) {
       super(testName);
    }

    private JTreeOperator _treeTreeView$ExplorerTree;
    private JLabelOperator _lblDisplayType;
    private JComboBoxOperator _cboJComboBox;
    /**
     * 
     */
    public static final String ITEM_ALL = "All";
    /**
     * 
     */
    public static final String ITEM_SERVICES = "Services";
    /**
     * 
     */
    public static final String ITEM_INTERFACES = "Interfaces";


    //******************************
    // Subcomponents definition part
    //******************************

    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treeTreeView$ExplorerTree() {
        if (_treeTreeView$ExplorerTree==null) {
            _treeTreeView$ExplorerTree = new JTreeOperator(this);
        }
        return _treeTreeView$ExplorerTree;
    }

    /** Tries to find "Display Type" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblDisplayType() {
        if (_lblDisplayType==null) {
            _lblDisplayType = new JLabelOperator(this, "Display Type");
        }
        return _lblDisplayType;
    }

    /** Tries to find null JComboBox in this dialog.
     * @return JComboBoxOperator
     */
    public JComboBoxOperator cboJComboBox() {
        if (_cboJComboBox==null) {
            _cboJComboBox = new JComboBoxOperator(this);
        }
        return _cboJComboBox;
    }


    //****************************************
    // Low-level functionality definition part
    //****************************************

    /** returns selected item for cboJComboBox
     * @return String item
     */
    public String getSelectedJComboBox() {
        return cboJComboBox().getSelectedItem().toString();
    }

    /** selects item for cboJComboBox
     * @param item String item
     */
    public void selectJComboBox(String item) {
        cboJComboBox().selectItem(item);
    }


    //*****************************************
    // High-level functionality definition part
    //*****************************************

    /** Performs verification of SelectANewDataType by accessing all its components.
     */
    public void verify() {
        treeTreeView$ExplorerTree();
        lblDisplayType();
        cboJComboBox();
    }

    /** Performs simple test of SelectANewDataType
    * @param args the command line arguments
    public static void main(String args[]) {
        new SelectANewDataType().verify();
        System.out.println("SelectANewDataType verification finished.");
    }
    */
}

