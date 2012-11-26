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
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.ObjectHelperImplementations;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class InterfaceTest extends TestCase {

    private ObjectHelperImplementations m_objectHelper;
    
    public InterfaceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        m_objectHelper = new ObjectHelperImplementations();
    }

    protected void tearDown() throws Exception {
        m_objectHelper = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(InterfaceTest.class);
        
        return suite;
    }

    /**
     * Test of addAggregatedInterface method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testAddAggregatedInterface() {
        System.out.println("addAggregatedInterface");
        
        Interface ifc = new Interface("XAggregatedTestInterface", "a.package");
        Interface instance = new Interface("XTestInterface", "a.package");
        
        instance.addAggregatedInterface(ifc);
    }

    /**
     * Test of getAllAggregatedInterfaces method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetAllAggregatedInterfaces() {
        System.out.println("getAllAggregatedInterfaces");
        
        Interface ifc = new Interface("XAggregatedTestInterface", "a.package");
        Interface instance = new Interface("XTestInterface", "a.package");
        
        instance.addAggregatedInterface(ifc);
        
        Interface[] expResult = new Interface[]{ifc};
        Interface[] result = instance.getAllAggregatedInterfaces();
        assertEquals(1, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of removeAggregatedInterface method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testRemoveAggregatedInterface() {
        System.out.println("removeAggregatedInterface");
        
        String name = "XAggregatedTestInterface";
        Interface ifc = new Interface("XAggregatedTestInterface", "a.package");
        Interface instance = new Interface("XTestInterface", "a.package");
        
        instance.addAggregatedInterface(ifc);
        
        Interface[] results = instance.getAllAggregatedInterfaces();
        assertEquals(1, results.length);
        
        Interface expResult = ifc;
        Interface result = instance.removeAggregatedInterface(name);
        assertEquals(expResult, result);
        
        results = instance.getAllAggregatedInterfaces();
        assertEquals(0, results.length);
    }

    /**
     * Test of addFunction method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testAddFunction() {
        System.out.println("addFunction");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        Function result = instance.addFunction();
        assertNotNull(result);
    }

    /**
     * Test of removeFunction method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testRemoveFunction() {
        System.out.println("removeFunction");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        Function function = instance.addFunction();
        
        instance.removeFunction(function);
    }

    /**
     * Test of setType method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testSetType() {
        System.out.println("setType");
        
        int type = NbNodeObject.OFFICE_INTERFACE_TYPE;
        Interface instance = new Interface("XTestInterface", "a.package");

        instance.setType(type);
        int result = instance.getType();
        assertEquals(type, result);

        instance.addAggregatedInterface(new Interface("XAggregated", "a.package"));
        result = instance.getType();
        assertEquals(NbNodeObject.MULTI_INHERITANCE_INTERFACE_TYPE, result);
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetType() {
        System.out.println("getType");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        int expResult = NbNodeObject.INTERFACE_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllSetObjectNames method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetAllSetObjectNames() {
        System.out.println("getAllSetObjectNames");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        instance.addFunction();
        instance.addFunction();
        
        String[] expResult = new String[]{"function0", "function1"};
        String[] result = instance.getAllSetObjectNames();
        assertEquals(2, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of getSetObject method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetSetObject() {
        System.out.println("getSetObject");
        
        String internalName = "function1";
        Interface instance = new Interface("XTestInterface", "a.package");
        Function f0 = instance.addFunction();
        Function f1 = instance.addFunction();
        
        Object expResult = f1;
        Object result = instance.getSetObject(internalName);
        assertEquals(expResult, result);
    }

    /**
     * Test of addSetObject method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testAddSetObject() {
        System.out.println("addSetObject");
        
        String internalName = "dideldum";
        Interface instance = new Interface("XTestInterface", "a.package");
        Function setObject = new Function("dideldum", "int", instance, null);
        Function f0 = instance.addFunction();
        Function f1 = instance.addFunction();
        
        instance.addSetObject(internalName, setObject);
        
        Function expResult = setObject;
        Function result = (Function)instance.getSetObject(internalName);
        assertEquals(expResult, result);
    }

    /**
     * Test of addLanguage method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testAddLanguage() {
        System.out.println("addLanguage");
        
        int languageID = LanguageDefinition.LANGUAGE_ID_de;
        String defaultText = "default text";
        Interface instance = new Interface("XTestInterface", "a.package");

        Function f0 = instance.addFunction();
        instance.addLanguage(languageID, defaultText);  // not really supported on interfaces
    }

    /**
     * Test of removeLanguage method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testRemoveLanguage() {
        System.out.println("removeLanguage");
        
        int languageID = LanguageDefinition.LANGUAGE_ID_de;
        String defaultText = "default text";
        Interface instance = new Interface("XTestInterface", "a.package");

        Function f0 = instance.addFunction();
        instance.addLanguage(languageID, defaultText);
        instance.removeLanguage(LanguageDefinition.LANGUAGE_ID_en); // not really supported on interfaces
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        NbNodeObject result = instance.getParent();
        assertNull(result);  // top class
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        String expResult = "a.package.XTestInterface";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        Function f0 = instance.addFunction();
        
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(4, result.length);
        boolean foundFunction = false;
        for (int i = 0; i < result.length; i++) {
            if (result[i].equals(f0)) foundFunction = true;
        }
        assertTrue(foundFunction);
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(3, result.length);
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        Interface instance = new Interface("XTestInterface", "a.package");
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean context = true;
        Interface instance = new Interface("XTestInterface", "a.package");
        
        Action[] result = instance.getActions(context);
        assertNull(result);
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.Interface.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        Interface instance = new Interface("XTestInterface", "a.package");
        
        instance.setActions(null); // empty implemented
    }
    
}
