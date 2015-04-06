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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTabbedPane;
import org.netbeans.jellytools.Bundle;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.NewProjectWizardOperator;
import org.netbeans.jellytools.OptionsOperator;
import org.netbeans.jellytools.OutputTabOperator;
import org.netbeans.jellytools.PluginsOperator;
import org.netbeans.jellytools.ProjectsTabOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jellytools.nodes.ProjectRootNode;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JCheckBoxOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTreeOperator;
// import org.netbeans.junit.NbTestSuite;
// import org.netbeans.junit.ide.ProjectSupport;
import org.netbeans.junit.NbModuleSuite;
import junit.framework.Test;
import org.netbeans.api.java.source.SourceUtils;
import org.openide.ErrorManager;

/**
 * A Test based on JellyTestCase. JellyTestCase redirects Jemmy output
 * to a log file provided by NbTestCase. It can be inspected in results.
 * It also sets timeouts necessary for NetBeans GUI testing.
 *
 * Any JemmyException (which is normally thrown as a result of an unsuccessful
 * operation in Jemmy) going from a test is treated by JellyTestCase as a test
 * failure; any other exception - as a test error.
 *
 * Additionally it:
 *    - closes all modal dialogs at the end of the test case (property jemmy.close.modal - default true)
 *    - generates component dump (XML file containing components information) in case of test failure (property jemmy.screen.xmldump - default false)
 *    - captures screen into a PNG file in case of test failure (property jemmy.screen.capture - default true)
 *    - waits at least 1000 ms between test cases (property jelly.wait.no.event - default true)
 *
 * @author Thorsten Bosbach
 * Created on 21. April 2007, 20:25
 */
public class TestAllResourcesTest extends JellyTestCase {
    static File fUserFolder = null;
    
    /** Constructor required by JUnit
     * @param name
     */
    public TestAllResourcesTest(String name) {
        super(name);
    }
    
   /** Creates suite from particular test cases. You can define order of testcases here.
     * @return
     */
    public static Test suite() {
        Test nbsuite = NbModuleSuite.create(
                NbModuleSuite.createConfiguration(TestAllResourcesTest.class).addTest(
                "testInstallation", "testOptions", "testComponent", 
                "testCalcAddIn", "testAddOn")
                );
        
        // missing tests for filetypes
        // missing test for debugging feature
        // Make directory for backup of extensions
        fUserFolder = new java.io.File(System.getProperty("user.home") + File.separator + String.valueOf(System.currentTimeMillis()));
        if (fUserFolder.mkdir()) {
            System.out.println("Created directory: " + fUserFolder.toString());
        } else {
            System.out.println("ERROR: Couldn't created directory: " + fUserFolder.toString());
        }
        return nbsuite;
    }
    
    /** Method allowing test execution directly from the IDE.
     *
     * @param args
     */
    public static void main(java.lang.String[] args) {
        // run whole suite
        junit.textui.TestRunner.run(suite());
        // run only selected test case
        //junit.textui.TestRunner.run(new TestAllResources("test1"));
    }
    
    /** Called before every test case. */
    @Override
    public void setUp() {
        System.out.println("\n########  "+getName()+"  ####### "+ new Date(System.currentTimeMillis()).toString());
    }
    
    /** Called after every test case. */
    @Override
    public void tearDown() {
        System.out.println("#################################");
    }
    
    // Add test methods here, they have to start with 'test' name.
    
    /** Resource test of 'OpenOffice CalcAdd-In'.
     * <ul>
     * TODO: Presupposion: configured Apache OpenOffice SDK and Apache OpenOffice Application in NetBeans options. (check needs to get implemented)
     * <li>Open 'File - New Project...'
     * <li>First Step of New Project wizard: Choose project
     * <li>Select Category: Apache OpenOffice
     * <li>Select Project: OpenOffice CalcAdd-In
     * <li>Press button: Next
     * <li>Second step: Create a new ... project
     * <li>Press button: Next
     * <li>Third step: Define Functions And Descriptions
     * <li>Add Function
     * <li>Add Parameter, delete parameter1, add parameter (i76262)
     * <li>Delete Function
     * <li>
     * <li>
     * <li>Press button: Finished
     * <li>From project context menu select: Build Project
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>From project context menu select: Create OXT
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>(Copy created .oxt file for further reference)
     * <li>From project context menu select: Deploy Office Extension
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>From project context menu select: Delete Project
     * <li>Dialog comes up: Delete Project - Are you shure you want to delete project ...?
     * <li>Check the checkbox: Also Delete Sources Under ... Folder.
     * <li>Press button: Yes
     * <li>Wait for completing: Scanning Class Paths
     * </ul>
     */
    public void testCalcAddIn() {
        //assertFalse(true);// stops test here with error
        //fail("t?t?d?"); // stops test here with error
        // Get strings to select for new project
        String ProjectCategory = Bundle.getStringTrimmed("org.openoffice.extensions.Bundle", "Templates/Project/org-openoffice-extensions");
        System.out.println("ProjectCategory: " + ProjectCategory);
        if (! ProjectCategory.equals("OpenOffice.org")){
            System.out.println("ERROR - Category OpenOffice.org doesn't exist.");
        }
        String CalcLabel = Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.calcaddin.Bundle", "Templates/Project/org-openoffice-extensions/AddinProject");
        System.out.println("Wizard: " + CalcLabel);
        //System.out.println(System.getProperty("netbeans.user"));
        
        // First step
        NewProjectWizardOperator npwop = NewProjectWizardOperator.invoke();
        npwop.selectCategory(ProjectCategory);
        npwop.selectProject(CalcLabel);
        //System.out.println(npwop.getDescription());
        npwop.next();
        
        // Second step of wizard
        NewCalcAddInStep2Operator ncais2o = new NewCalcAddInStep2Operator();
        System.out.println("Title of 2. dialog: " + ncais2o.getTitle());
        String sProjectName = ncais2o.txtProjectName().getText();
        System.out.println("Project name: " + sProjectName);
        String sProjectFolder = ncais2o.txtProjectFolder().getText();
        System.out.println("Project folder: " + sProjectFolder);
        ncais2o.next();
        
        // Third step of wizard
        NewCalcAddInStep3Operator ncais3o = new NewCalcAddInStep3Operator();
        System.out.println("Title of 3. dialog: " + ncais3o.getTitle());
        JTreeOperator jto = ncais3o.treeTreeView$ExplorerTree();
        int iRC = jto.getRowCount();
        if (iRC != 1) {
            System.out.println("ERROR: Expected just one defined function, but there are: " + iRC);
        }
        Node noxx = new Node(jto,"");
        System.out.println(noxx.getText());
        String [] noxxChild = noxx.getChildren();
        System.out.println("Name of first children: " + noxxChild[0]);
        
        // Add a function
        ncais3o.addFunction();
        jto = ncais3o.treeTreeView$ExplorerTree();
        int iRC2 = jto.getRowCount();
        if (iRC2 != 2) {
            System.out.println("ERROR: Added function, but result is not 2 it is: " + iRC2);
        }
        noxx = new Node(jto,"");
        noxxChild = noxx.getChildren();
        System.out.println("Current Functions are:");
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
        if (noxxChild[0].equals(noxxChild[1])) {
            System.out.println("ERROR: Added function has same name as first one.");
        }
        
        // Add Parameter
        // unfold first function
        jto.doExpandRow(0);
        noxx = new Node(jto,jto.getPathForRow(0));
        noxxChild = noxx.getChildren();
        System.out.println("Current Items for function1 are:");
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
        ncais3o.addParameter();
        jto = ncais3o.treeTreeView$ExplorerTree();
        noxx = new Node(jto,jto.getPathForRow(0));
        noxxChild = noxx.getChildren();
        System.out.println("Current Items for function1 after adding parameter are:");
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
   /* known issue 'double parameter name'
        // Delete Parameter1 in function1 via context menu
        noxx = new Node(noxx,8);
        System.out.println("Node to delete: " + noxx.getText());
        try {
            new Action(null, "Delete").perform(noxx);
        } catch (org.netbeans.junit.AssertionFailedErrorException e) {
            System.out.println("ERROR: delete failed");
        }
    
        // Add Parameter
        ncais3o.addParameter();
        jto = ncais3o.treeTreeView$ExplorerTree();
        noxx = new Node(jto,jto.getPathForRow(0));
        noxxChild = noxx.getChildren();
        System.out.println("Current Items for function1 after adding parameter are:");
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
        if (noxxChild[noxxChild.length-1].equals(noxxChild[noxxChild.length-2])) {
            System.out.println("ERROR: Added parameter has same name as first one.");
            JLabelOperator jlblo = new JLabelOperator(ncais3o,3);
            if (jlblo.getText().length() > 0) {
                System.out.println(jlblo.getText());
            }
        }
        if (ncais3o.btFinish().isEnabled()) {
            System.out.println("ERROR: Finish button is enabled, but added parameter has same name as first one.");
        }
        // In NB6.0 there is a NullPointer exeption on deleting the parameter
        jto = ncais3o.treeTreeView$ExplorerTree();
        iRC2 = jto.getRowCount();
        noxx = new Node(noxx,noxxChild.length-1);
        System.out.println("Node to delete: " + noxx.getText());
        try {
            new Action(null, "Delete").perform(noxx);
        } catch (org.netbeans.junit.AssertionFailedErrorException e) {
            System.out.println("ERROR: delete failed");
        }
        jto = ncais3o.treeTreeView$ExplorerTree();
        iRC = jto.getRowCount();
        if (iRC == iRC2) {
            System.out.println("ERROR: delete failed");
        }
        noxx = new Node(jto,jto.getPathForRow(0));
        noxxChild = noxx.getChildren();
        System.out.println("Current Items for function1 after adding parameter are:");
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
    
        //TODO: try to make a double name by renaming a parameter, then try to change the name to unique -> both get changed!
        //TODO: check what happens, if double name get's changed to unique name by manual editing'
    
        // Delete a function
        jto = ncais3o.treeTreeView$ExplorerTree();
        jto.selectRow(0);
        ncais3o.delete();
        jto = ncais3o.treeTreeView$ExplorerTree();
        iRC = jto.getRowCount();
        if (iRC != 1) {
            System.out.println("ERROR: function not deleted: " + iRC);
        }
        noxx = new Node(jto,"");
        noxxChild = noxx.getChildren();
        System.out.println("Name of first children: " + noxxChild[0]);
    
        //i75328 stupid function name generation ;-)
        // Add a function
        ncais3o.addFunction();
        jto = ncais3o.treeTreeView$ExplorerTree();
        iRC2 = jto.getRowCount();
        if (iRC2 != 2) {
            System.out.println("ERROR: Added function, but result is not 2 it is: " + iRC2);
        }
        noxx = new Node(jto,"");
        noxxChild = noxx.getChildren();
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
        if (noxxChild[0].equals(noxxChild[1])) {
            System.out.println("ERROR: Added function has same name as first one.");
        }
    */
        
        //TODO: press button for language
        
        ncais3o.finish();
        System.out.println("-------------------------------------");
        
        // Opening Projects
        String openingProjectsTitle = Bundle.getString("org.netbeans.modules.project.ui.Bundle", "LBL_Opening_Projects_Progress");
        try  {
            // wait at most 60 second until progress dialog dismiss
            JemmyProperties.setCurrentTimeout("ComponentOperator.WaitStateTimeout", 60000);
            new NbDialogOperator(openingProjectsTitle).waitClosed();
        } catch (TimeoutExpiredException e)  {
            // ignore when progress dialog was closed before we started to wait for it
        }
        // ProjectSupport.waitScanFinished();
       waitScanFinished();

        // Compile
        build(sProjectName);
        
        // Move generated extension (.oxt) away for further testing
        copyOXT(sProjectFolder,sProjectName);
        // delete Project
        delete(sProjectName);
        // (clean)
        log("finished");
    }
    
    public void testInstallation() {
        Properties props = new Properties();
        String oooPluginNBMPath = null;

        try {
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(
                "/org/openoffice/extensions/test/Test.properties");
            System.out.println("Stream " + inStream);
            props.load(inStream);
            oooPluginNBMPath = props.getProperty("PluginPath");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        
        // install the plugin
        PluginsOperator pop = PluginsOperator.invoke();
        System.out.println("Plugins opened");
        pop.selectDownloaded();
        System.out.println("Downloaded selected");
        pop.addPlugin(oooPluginNBMPath);
        System.out.println("Plugin added");
        pop.install();
        System.out.println("Install Wizard opened");
        PluginInstallationWizardOperator piwo = new PluginInstallationWizardOperator("NetBeans IDE Installer");
        piwo.next();
        
        // accept license
        JCheckBoxOperator op = piwo.getAcceptLicenseCheckbox();
        if (!op.isSelected()) {
            op.clickMouse();
        }
        System.out.println("License accepted");
        JButtonOperator installButton = piwo.getButton("Install");
        try {
            installButton.waitComponentEnabled();
        } catch (InterruptedException ex) {
            Logger.getLogger(TestAllResourcesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        installButton.clickMouse();
        System.out.println("Installation started");

        piwo.clickOverValidationWarning();
        
        JButtonOperator finishButton = piwo.getButton("Finish");
        finishButton.press();
        
        pop.close();
    }
    
    /**
     *
     */
    public void testOptions() {
        Properties props = new Properties();
        String office = null;
        String sdk = null;

        try {
            InputStream inStream = this.getClass().getClassLoader().getResourceAsStream(
                "/org/openoffice/extensions/test/Test.properties");
            System.out.println("Stream " + inStream);
            props.load(inStream);
            office = props.getProperty("OfficeInstallation");
            sdk = props.getProperty("SDKInstallation");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        
        System.out.println(getWorkDirPath() );
        OptionsOperator op = OptionsOperator.invoke();
        System.out.println("Options opened");
        // op.switchToModernView(); // method removed
        // System.out.println("switched to modern view");
        op.selectMiscellaneous();
        System.out.println("selected Miscellaneous");
        String category = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.config.Bundle", "AdvancedOption_DisplayName");
        System.out.println(category);

        JTabbedPaneOperator tabPane = new JTabbedPaneOperator(op, new ComponentChooser() {
            public boolean checkComponent(Component arg0) {
                // only one tabbed pane there
                if (arg0 instanceof JTabbedPane) {
                    return true;
                }
                return false;
            }

            public String getDescription() {
                return "Get the tab pane with OOo plugin settings.";
            }
        });
        // go to the right category
        tabPane.selectPage(category);
        
        Options b = new Options();
        
        b.txtOfficeInstallation().setText(office);
        b.txtSDKInstallation().setText(sdk);

        System.out.println(b.txtOfficeInstallation().getText());
        System.out.println(b.txtSDKInstallation().getText());
        
        op.ok();
    }
    
    /** Resource test of 'OpenOffice Add-On'.
     *<ul>
     *TODO: Presupposion: configured OpenOffice.org SDK and OpenOffice.org Application in NetBeans options. (check needs to get implemented)
     * <li>Open 'File - New Project...'
     * <li>First Step of New Project wizard: Choose project
     * <li>Select Category: OpenOffice.org
     * <li>Select Project: OpenOffice Add-On
     * <li>Press button: Next
     * <li>Second step: Define Add-On Name And Location
     * <li>Press button: Next
     * <li>Third step: Define User Interface Entry
     * <li>Press button: Finished
     * <li>From project context menu select: Build Project
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>From project context menu select: Create OXT
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>(Copy created .oxt file for further reference)
     * <li>From project context menu select: Deploy Office Extension
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>From project context menu select: Delete Project
     * <li>Dialog comes up: Delete Project - Are you shure you want to delete project ...?
     * <li>Check the checkbox: Also Delete Sources Under ... Folder.
     * <li>Press button: Yes
     * <li>Wait for completing: Scanning Class Paths
     *</ul>
     */
    public void testAddOn() {
        System.out.println("Start");
        
        String ProjectCategory = Bundle.getStringTrimmed("org.openoffice.extensions.Bundle", "Templates/Project/org-openoffice-extensions");
        System.out.println(ProjectCategory);
        String addOnLabel = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.addon.Bundle", "Templates/Project/org-openoffice-extensions/AddOnProject");
        System.out.println(addOnLabel);
        
        NewProjectWizardOperator npwop = NewProjectWizardOperator.invoke();
        npwop.selectCategory(ProjectCategory);
        npwop.selectProject(addOnLabel);
        npwop.next();
        
        // Second step of wizard
        NewAddOnStep2Operator addOnOperator = new NewAddOnStep2Operator();
        String sProjectName;
        sProjectName = addOnOperator.txtProjectName().getText();
        System.out.println(sProjectName);
        String sProjectFolder = addOnOperator.txtProjectFolder().getText();
        System.out.println("Project folder: " + sProjectFolder);
        addOnOperator.next();
        
        // TODO: urgent! create own class for this!
        NewAddOnStep3Operator commandDefinition = new NewAddOnStep3Operator();
        // TODO: testing, adding of icons etc.
        commandDefinition.next();

        // Third step of wizard
        NewAddOnStep3Operator menuStructure = new NewAddOnStep3Operator();
        // TODO: testing, adding of icons etc.
        menuStructure.next();

        NewAddOnStep4Operator toolbarStructure = new NewAddOnStep4Operator();
        toolbarStructure.finish();
        
        System.out.println("finished addon");
        
        // Opening Projects
        String openingProjectsTitle = Bundle.getString("org.netbeans.modules.project.ui.Bundle", "LBL_Opening_Projects_Progress");
        try  {
            // wait at most 60 second until progress dialog dismiss
            JemmyProperties.setCurrentTimeout("ComponentOperator.WaitStateTimeout", 60000);
            new NbDialogOperator(openingProjectsTitle).waitClosed();
        } catch (TimeoutExpiredException e)  {
            // ignore when progress dialog was closed before we started to wait for it
        }
        // ProjectSupport.waitScanFinished();
        waitScanFinished();
        
        // Compile
        build(sProjectName);
        
        // Move generated extension (.oxt) away for further testing
        copyOXT(sProjectFolder,sProjectName);

        // delete Project
        delete(sProjectName);
        
        log("finished");
    }
    
    /** Resource test of 'OpenOffice Component'.
     *<ul>
     * TODO: Presupposion: configured OpenOffice.org SDK and OpenOffice.org Application in NetBeans options. (check needs to get implemented)
     * <li>Open 'File - New Project...'
     * <li>First Step of New Project wizard: Choose project
     * <li>Select Category: OpenOffice.org
     * <li>Select Project: OpenOffice Component
     * <li>Press button: Next
     * <li>Second step: Name And Location
     * <li>Press button: Next
     * <li>Third step: Define Service
     * <li>Press button: Add Service/Interface ...
     * <li>Dialog comes up: Select a new Data type
     * <li>In listbox 'Display Type', select: Services
     * <li>In treebox select a service
     * <li>Press button: Ok
     * <li>Press button: Define New Data Type ...
     * <li>Dialog comes up: Create New Data Type
     * <li>In listbox 'Data Type', select: Interface
     * <li>Press button: Ok
     * <li>Press button: Finished
     * <li>From project context menu select: Build Project
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>From project context menu select: Create OXT
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>(Copy created .oxt file for further reference)
     * <li>From project context menu select: Deploy Office Extension
     * <li>Wait for message in output window: BUILD SUCCESSFUL  (total time: ... seconds)
     * <li>From project context menu select: Delete Project
     * <li>Dialog comes up: Delete Project - Are you shure you want to delete project ...?
     * <li>Check the checkbox: Also Delete Sources Under ... Folder.
     * <li>Press button: Yes
     * <li>Wait for completing: Scanning Class Paths
     *</ul>
     */
    public void testComponent() {
        String ProjectCategory = Bundle.getStringTrimmed("org.openoffice.extensions.Bundle", "Templates/Project/org-openoffice-extensions");
        System.out.println(ProjectCategory);
        String CalcLabel = org.netbeans.jellytools.Bundle.getStringTrimmed("org.openoffice.extensions.projecttemplates.component.Bundle","Templates/Project/org-openoffice-extensions/ComponentProject");
        System.out.println(CalcLabel);
        
        NewProjectWizardOperator npwop = NewProjectWizardOperator.invoke();
        npwop.selectCategory(ProjectCategory);
        npwop.selectProject(CalcLabel);
        npwop.next();
        
        // Second step of wizard
        NewCalcAddInStep2Operator ncais2o = new NewCalcAddInStep2Operator();
        System.out.println("Title of 2. dialog: " + ncais2o.getTitle());
        String sProjectName = ncais2o.txtProjectName().getText();
        System.out.println("Project name: " + sProjectName);
        String sProjectFolder = ncais2o.txtProjectFolder().getText();
        System.out.println("Project folder: " + sProjectFolder);
        ncais2o.next();
        
        // Third step of wizard
        NewComponentStep3Operator ncais3o = new NewComponentStep3Operator();
        if (ncais3o.lblLast().getText().length() > 0){
            System.out.println(ncais3o.lblLast().getText());
        } else {
            System.out.println("ERROR: Message is missing about missing services...");
        }
        if (ncais3o.btFinish().isEnabled()) {
            System.out.println("ERROR: Finish button is enabled, but no service is defined.");
        }
        ncais3o.btAddServiceInterface().push();
        
        SelectANewDataType sands = new SelectANewDataType("Select a new Data type");
        sands.cboJComboBox().selectItem("Services");
        //JTreeOperator
        sands.treeTreeView$ExplorerTree().doExpandRow(0);
        sands.treeTreeView$ExplorerTree().doExpandRow(1);
        sands.treeTreeView$ExplorerTree().doExpandRow(2);
        sands.treeTreeView$ExplorerTree().doExpandRow(3);
        sands.treeTreeView$ExplorerTree().waitExpanded(3);
        sands.treeTreeView$ExplorerTree().selectRow(4);
        System.out.println("1: " + sands.treeTreeView$ExplorerTree().getPathForRow(4).toString());
        sands.btOK().push();
        
        ncais3o = new NewComponentStep3Operator();
        JTreeOperator jto = ncais3o.treeTreeView$ExplorerTree2();
        int iRC = jto.getRowCount();
        if (iRC != 2) {
            System.out.println("ERROR: Expected just one defined service and the root node, but there are: " + iRC);
        }
        Node noxx = new Node(jto,"");
        System.out.println(noxx.getText());
        String [] noxxChild = noxx.getChildren();
        System.out.println("Added service: " + noxxChild[0]);
        if (ncais3o.lblLast().getText().length() > 0){
            System.out.println("ERROR: Errormessage should disappear: " + ncais3o.lblLast().getText());
        }
        if (! ncais3o.btFinish().isEnabled()) {
            System.out.println("ERROR: Finish button should be enabled, because a service is defined.");
        }
        
        ncais3o.btAddServiceInterface().push();
        sands = new SelectANewDataType("Select a new Data type");
        sands.cboJComboBox().selectItem("Interfaces");
        //JTreeOperator
        sands.treeTreeView$ExplorerTree().doExpandRow(0);
        sands.treeTreeView$ExplorerTree().doExpandRow(1);
        sands.treeTreeView$ExplorerTree().doExpandRow(2);
        sands.treeTreeView$ExplorerTree().doExpandRow(3);
        sands.treeTreeView$ExplorerTree().selectRow(4);
        System.out.println("3: " + sands.treeTreeView$ExplorerTree().getPathForRow(4).toString());
        sands.btOK().push();
        
        ncais3o = new NewComponentStep3Operator();
        jto = ncais3o.treeTreeView$ExplorerTree2();
        int iRC2 = jto.getRowCount();
        if (iRC2 != 3) {
            System.out.println("ERROR: Expected root node, service and interface, but there are: " + iRC2);
        }
        noxx = new Node(jto,"");
        System.out.println(noxx.getText());
        noxxChild = noxx.getChildren();
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
        
        // Define own data types
        ncais3o = new NewComponentStep3Operator();
        jto = ncais3o.treeTreeView$ExplorerTree();
        iRC2 = jto.getRowCount();
        if (iRC2 != 6) {
            System.out.println("ERROR: unexpected Data Type count should be 6 but is: " + iRC2);
        }
        noxx = new Node(jto,"");
        noxxChild = noxx.getChildren();
        for (int i = noxxChild.length-1;i>0;i--) {
            System.out.println( i + ": Data Type: " + (noxxChild[i]));
            jto.selectRow(i);
            noxx = new Node(jto,jto.getSelectionPath());
            ncais3o.defineNewDataType();
            CreateNewDataType cndt = new CreateNewDataType();
            cndt.getSelectedDataType();
            if (cndt.cboDataType().getItemCount() == 1) {
                System.out.println("ERROR: In dialog is just on eitem available, should be 6.");
            }
            JTreeOperator jt;
            switch (i){
            case 5: // PolyStruct
                jt = cndt.treeTreeView$ExplorerTree();
                System.out.println(jt.getRowCount());
                cndt.addTemplate();
                cndt.addMember();
                cndt.delete();
                System.out.println(jt.getRowCount());
                break;
            case 4: // Exception
                // nothing to do here
                break;
            case 3: // Struct
                jt = cndt.treeTreeView$ExplorerTree();
                System.out.println(jt.getRowCount());
                cndt.addType();
                cndt.delete();
                System.out.println(jt.getRowCount());
                break;
            case 2: // Enumeration
                jt = cndt.treeTreeView$ExplorerTree();
                System.out.println(jt.getRowCount());
                cndt.addEnum();
                cndt.delete();
                System.out.println(jt.getRowCount());
                break;
            case 1: // Interface
                jt = cndt.treeTreeView$ExplorerTree();
                System.out.println(jt.getRowCount());
                cndt.newFunction();
                cndt.newParameter();
                cndt.delete();
                System.out.println(jt.getRowCount());
                break;
            case 0: // Service
                JTableOperator jtbo = cndt.tabPropertiesTable();
                break;
            }
            cndt.ok();
        }
        noxx = new Node(jto,"");
        noxxChild = noxx.getChildren();
        for (int i = 0;i<noxxChild.length;i++) {
            System.out.println( i + ": " + (noxxChild[i]));
        }
        
        ncais3o.finish();
        System.out.println("finished addin");
        // Opening Projects
        String openingProjectsTitle = Bundle.getString("org.netbeans.modules.project.ui.Bundle", "LBL_Opening_Projects_Progress");
        try  {
            // wait at most 60 second until progress dialog dismiss
            JemmyProperties.setCurrentTimeout("ComponentOperator.WaitStateTimeout", 60000);
            new NbDialogOperator(openingProjectsTitle).waitClosed();
        } catch (TimeoutExpiredException e)  {
            // ignore when progress dialog was closed before we started to wait for it
        }
        // ProjectSupport.waitScanFinished();
        waitScanFinished();
        
        // Compile
        build(sProjectName);
        
        // Move generated extension (.oxt) away for further testing
        copyOXT(sProjectFolder,sProjectName);
        
        // delete Project
        delete(sProjectName);
        
        log("finished");
    }
    
    private void build(String sProjectName) {
        // Compile
        ProjectsTabOperator pto = new ProjectsTabOperator();
        System.out.println("context menu open " + sProjectName);
        ProjectRootNode prn = pto.getProjectRootNode(sProjectName);
        System.out.println("got project root node " + prn.toString());
        
        //   MainWindowOperator.StatusTextTracer stt = MainWindowOperator.getDefault().getStatusTextTracer();
        //  stt.start();
        // "Run Project"
        //        new Action(null, "Build Project").perform(prn);
        // Deploy Office Extension
        // (uno-deploy)
        
        new Action(null, "Create OXT").perform(prn);
        //        OutputTabOperator oto = new OutputTabOperator(sProjectName + " (jar)");
        OutputTabOperator oto = new OutputTabOperator(sProjectName + " (uno-package)");
        //        oto.getTimeouts().setTimeout("ComponentOperator.WaitStateTimeout", 180000);
        System.out.println("Output Tab Name: "+oto.getName());
        try {
            oto.waitText("BUILD SUCCESSFUL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*long timeout = JemmyProperties.getCurrentTimeout("ComponentOperator.WaitComponentTimeout");
        JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", 3000);
        try {
            for (int i=0; i<10; i++) {
                OutputTabOperator output = new OutputTabOperator("",i);
                log("Output"+i+".log",output.getName()+"\n-------------\n\n"+output.getText());
            }
        } catch (TimeoutExpiredException e)  {}
        JemmyProperties.setCurrentTimeout("ComponentOperator.WaitComponentTimeout", timeout);
         */
        //   stt.waitText("finished building JEMMY",true);
        System.out.println("Output Tab content: "+oto.getText());
        System.out.println("Output Tab end -------------------------------------------- ");
        
        System.out.println("WorkDir: "+getWorkDirPath());
        // der ist ok 'Finished building Addin (jar).
        try {
            //            MainWindowOperator.getDefault().waitStatusText("Finished building "+sProjectName+" (jar).");
            //MainWindowOperator.getDefault().waitStatusText("Finished building "+sProjectName+" (add-package).");
            MainWindowOperator.getDefault().waitStatusText("Finished building "+sProjectName+" (uno-package).");
        } catch (Exception e) {
            System.out.println("Not SEEN: " + "Finished building "+sProjectName+" (uno-package).");
            //e.printStackTrace();
        }
        System.out.println("status Line Text: "+MainWindowOperator.getDefault().getStatusText());
        //  getLog("BUILD SUCCESSFUL").print("");
    }
    
    private void copyOXT(String sProjectFolder, String sProjectName) {
        // Move generated extension (.oxt) away for further testing
        File fOxt = new File(sProjectFolder + File.separator + "dist" + File.separator + sProjectName + ".oxt");
        if (fOxt.exists()) {
            java.io.FileReader in = null;
            java.io.FileWriter out = null;
            try {
                File outputFile = new java.io.File(fUserFolder + File.separator + sProjectName + ".oxt");
                in = new java.io.FileReader(fOxt);
                out = new java.io.FileWriter(outputFile);
                int c;
                while ((c = in.read()) != -1)
                    out.write(c);
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("ERROR: extension is not generated: " + fOxt.toString());
        }
        
    }
    
    private void delete(String sProjectName) {
        // delete Project
        ProjectsTabOperator pto = ProjectsTabOperator.invoke();
        ProjectRootNode prn = pto.getProjectRootNode(sProjectName);
        Integer i;
        DeleteAction deleteAction = new DeleteAction();
        deleteAction.perform(prn);
        NbDialogOperator nbdo = new NbDialogOperator("Delete Project");
        JCheckBoxOperator jcb = new JCheckBoxOperator(nbdo);
        System.out.println("Checkbox Name: "+jcb.getText());
        System.out.println("Is checkbox selected?: "+jcb.isSelected());
        jcb.setSelected(true);
        System.out.println(jcb.isSelected());
        nbdo.yes();
        nbdo.waitClosed();
        
        // ProjectSupport.waitScanFinished();
        waitScanFinished();
    }
    
}
