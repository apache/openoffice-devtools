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
public class NewComponentStep3Operator extends NewProjectWizardOperator {
    
    /** Creates new NewComponentStep3Operator that can handle it.
     * public NewComponentStep3Operator() {
     * super(new NameComponentChooser("dialog6"));
     * }
     */
    
    private JLabelOperator _lblImplementedServicesInterfaces;
    private JLabelOperator _lblOwnDefinedDataTypes;
    private JTreeOperator _treeTreeView$ExplorerTree;
    private JTreeOperator _treeTreeView$ExplorerTree2;
    private JButtonOperator _btAddServiceInterface;
    private JButtonOperator _btDefineNewDataType;
    private JButtonOperator _btEdit;
    private JButtonOperator _btDeleteSelected;
    private JButtonOperator _btDeleteSelected2;
    private JLabelOperator _lblLast;
    
    
    //******************************
    // Subcomponents definition part
    //******************************
    
    /** Tries to find "Implemented Services/Interfaces" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblImplementedServicesInterfaces() {
        if (_lblImplementedServicesInterfaces==null) {
            _lblImplementedServicesInterfaces = new JLabelOperator(this, "Implemented Services/Interfaces");
        }
        return _lblImplementedServicesInterfaces;
    }
    
    /** Tries to find "Own Defined Data Types" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblOwnDefinedDataTypes() {
        if (_lblOwnDefinedDataTypes==null) {
            _lblOwnDefinedDataTypes = new JLabelOperator(this, "Own Defined Data Types");
        }
        return _lblOwnDefinedDataTypes;
    }
    
    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treeTreeView$ExplorerTree() {
        if (_treeTreeView$ExplorerTree==null) {
            _treeTreeView$ExplorerTree = new JTreeOperator(this);
        }
        return _treeTreeView$ExplorerTree;
    }
    
    /** Tries to find null TreeView$ExplorerTree in this dialog.
     * @return JTreeOperator
     */
    public JTreeOperator treeTreeView$ExplorerTree2() {
        if (_treeTreeView$ExplorerTree2==null) {
            _treeTreeView$ExplorerTree2 = new JTreeOperator(this, 1);
        }
        return _treeTreeView$ExplorerTree2;
    }
    
    /** Tries to find "Add Service/Interface..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btAddServiceInterface() {
        if (_btAddServiceInterface==null) {
            _btAddServiceInterface = new JButtonOperator(this, "Add Service/Interface...");
        }
        return _btAddServiceInterface;
    }
    
    /** Tries to find "Define New Data Type..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDefineNewDataType() {
        if (_btDefineNewDataType==null) {
            _btDefineNewDataType = new JButtonOperator(this, "Define New Data Type...");
        }
        return _btDefineNewDataType;
    }
    
    /** Tries to find "Edit..." JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btEdit() {
        if (_btEdit==null) {
            _btEdit = new JButtonOperator(this, "Edit...");
        }
        return _btEdit;
    }
    
    /** Tries to find "Delete Selected" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDeleteSelected() {
        if (_btDeleteSelected==null) {
            _btDeleteSelected = new JButtonOperator(this, "Delete Selected");
        }
        return _btDeleteSelected;
    }
    
    /** Tries to find "Delete Selected" JButton in this dialog.
     * @return JButtonOperator
     */
    public JButtonOperator btDeleteSelected2() {
        if (_btDeleteSelected2==null) {
            _btDeleteSelected2 = new JButtonOperator(this, "Delete Selected", 1);
        }
        return _btDeleteSelected2;
    }
    
    /** Tries to find "last label on dialog" JLabel in this dialog.
     * @return JLabelOperator
     */
    public JLabelOperator lblLast() {
        if (_lblLast==null) {
            _lblLast = new JLabelOperator(this, 4);
        }
        return _lblLast;
    }
    
    
    //****************************************
    // Low-level functionality definition part
    //****************************************
    
    /** clicks on "Add Service/Interface..." JButton
     */
    public void addServiceInterface() {
        btAddServiceInterface().push();
    }
    
    /** clicks on "Define New Data Type..." JButton
     */
    public void defineNewDataType() {
        btDefineNewDataType().push();
    }
    
    /** clicks on "Edit..." JButton
     */
    public void edit() {
        btEdit().push();
    }
    
    /** clicks on "Delete Selected" JButton
     */
    public void deleteSelected() {
        btDeleteSelected().push();
    }
    
    /** clicks on "Delete Selected" JButton
     */
    public void deleteSelected2() {
        btDeleteSelected2().push();
    }
    
    
    //*****************************************
    // High-level functionality definition part
    //*****************************************
    
    /** Performs verification of NewComponentStep3Operator by accessing all its components.
     */
    public void verify() {
        lblImplementedServicesInterfaces();
        lblOwnDefinedDataTypes();
        treeTreeView$ExplorerTree();
        treeTreeView$ExplorerTree2();
        btAddServiceInterface();
        btDefineNewDataType();
        btEdit();
        btDeleteSelected();
        btDeleteSelected2();
        lblLast();
    }
    
    /** Performs simple test of NewComponentStep3Operator
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new NewComponentStep3Operator().verify();
        System.out.println("NewComponentStep3Operator verification finished.");
    }
}

