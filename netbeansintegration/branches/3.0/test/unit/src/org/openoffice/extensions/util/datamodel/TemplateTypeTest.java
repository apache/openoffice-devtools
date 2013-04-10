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

package org.openoffice.extensions.util.datamodel;

import junit.framework.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentTypePropertyEditor;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class TemplateTypeTest extends TestCase {
    
    public TemplateTypeTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(TemplateTypeTest.class);
        
        return suite;
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        TemplateType instance = null;
        
        String expResult = "";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testGetProperty() {
        System.out.println("getProperty");
        
        int propertyIndex = 0;
        TemplateType instance = null;
        
        OpenOfficeOrgProperty expResult = null;
        OpenOfficeOrgProperty result = instance.getProperty(propertyIndex);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        TemplateType instance = null;
        
        NbNodeObject[] expResult = null;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Sheet sheet = null;
        PropertyChangeListener listener = null;
        TemplateType instance = null;
        
        Node.Property[] expResult = null;
        Node.Property[] result = instance.createProperties(sheet, listener);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction baseAction = null;
        TemplateType instance = null;
        
        instance.setActions(baseAction);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        TemplateType instance = null;
        
        Action[] expResult = null;
        Action[] result = instance.getActions(b);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        TemplateType instance = null;
        
        boolean expResult = true;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        TemplateType instance = null;
        
        NbNodeObject expResult = null;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.TemplateType.
     */
    public void testGetType() {
        System.out.println("getType");
        
        TemplateType instance = null;
        
        int expResult = 0;
        int result = instance.getType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
