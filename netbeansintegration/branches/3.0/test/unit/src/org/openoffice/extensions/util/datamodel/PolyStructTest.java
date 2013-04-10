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
public class PolyStructTest extends TestCase {
    
    public PolyStructTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PolyStructTest.class);
        
        return suite;
    }

    /**
     * Test of getPropertyType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetPropertyType() {
        System.out.println("getPropertyType");
        
        String name = "";
        PolyStruct instance = null;
        
        Parameter expResult = null;
        Parameter result = instance.getPropertyType(name);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addPropertyType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testAddPropertyType() {
        System.out.println("addPropertyType");
        
        PolyStruct instance = null;
        
        Parameter expResult = null;
        Parameter result = instance.addPropertyType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removePropertyType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testRemovePropertyType() {
        System.out.println("removePropertyType");
        
        String name = "";
        PolyStruct instance = null;
        
        instance.removePropertyType(name);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTemplateType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetTemplateType() {
        System.out.println("getTemplateType");
        
        String name = "";
        PolyStruct instance = null;
        
        TemplateType expResult = null;
        TemplateType result = instance.getTemplateType(name);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addTemplateType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testAddTemplateType() {
        System.out.println("addTemplateType");
        
        PolyStruct instance = null;
        
        TemplateType expResult = null;
        TemplateType result = instance.addTemplateType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeTemplateType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testRemoveTemplateType() {
        System.out.println("removeTemplateType");
        
        String name = "";
        PolyStruct instance = null;
        
        instance.removeTemplateType(name);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTemplateTypeNames method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetTemplateTypeNames() {
        System.out.println("getTemplateTypeNames");
        
        PolyStruct instance = null;
        
        String[] expResult = null;
        String[] result = instance.getTemplateTypeNames();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSetObjectNames method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetAllSetObjectNames() {
        System.out.println("getAllSetObjectNames");
        
        PolyStruct instance = null;
        
        String[] expResult = null;
        String[] result = instance.getAllSetObjectNames();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSetObject method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetSetObject() {
        System.out.println("getSetObject");
        
        String internalName = "";
        PolyStruct instance = null;
        
        Object expResult = null;
        Object result = instance.getSetObject(internalName);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addSetObject method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testAddSetObject() {
        System.out.println("addSetObject");
        
        String internalName = "";
        Object setObject = null;
        PolyStruct instance = null;
        
        instance.addSetObject(internalName, setObject);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        PolyStruct instance = null;
        
        String expResult = "";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        PolyStruct instance = null;
        
        NbNodeObject[] expResult = null;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetProperty() {
        System.out.println("getProperty");
        
        int propertyIndex = 0;
        PolyStruct instance = null;
        
        OpenOfficeOrgProperty expResult = null;
        OpenOfficeOrgProperty result = instance.getProperty(propertyIndex);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Sheet sheet = null;
        PropertyChangeListener listener = null;
        PolyStruct instance = null;
        
        Node.Property[] expResult = null;
        Node.Property[] result = instance.createProperties(sheet, listener);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        PolyStruct instance = null;
        
        boolean expResult = true;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        PolyStruct instance = null;
        
        Action[] expResult = null;
        Action[] result = instance.getActions(b);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction baseAction = null;
        PolyStruct instance = null;
        
        instance.setActions(baseAction);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        PolyStruct instance = null;
        
        NbNodeObject expResult = null;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.PolyStruct.
     */
    public void testGetType() {
        System.out.println("getType");
        
        PolyStruct instance = null;
        
        int expResult = 0;
        int result = instance.getType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
