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
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;

/**
 *
 * @author sg128468
 */
public class ParameterTest extends TestCase {
    
    public ParameterTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ParameterTest.class);
        
        return suite;
    }

    /**
     * Test of printLanguages method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testPrintLanguages() {
        System.out.println("printLanguages");
        
        Parameter instance = null;
        
        instance.printLanguages();
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        Parameter instance = null;
        
        String expResult = "";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testGetProperty() {
        System.out.println("getProperty");
        
        int propertyIndex = 0;
        Parameter instance = null;
        
        OpenOfficeOrgProperty expResult = null;
        OpenOfficeOrgProperty result = instance.getProperty(propertyIndex);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        Parameter instance = null;
        
        NbNodeObject[] expResult = null;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Sheet sheet = null;
        PropertyChangeListener listener = null;
        Parameter instance = null;
        
        Node.Property[] expResult = null;
        Node.Property[] result = instance.createProperties(sheet, listener);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction baseAction = null;
        Parameter instance = null;
        
        instance.setActions(baseAction);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        Parameter instance = null;
        
        Action[] expResult = null;
        Action[] result = instance.getActions(b);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        Parameter instance = null;
        
        boolean expResult = true;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        Parameter instance = null;
        
        NbNodeObject expResult = null;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.Parameter.
     */
    public void testGetType() {
        System.out.println("getType");
        
        Parameter instance = null;
        
        int expResult = 0;
        int result = instance.getType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
