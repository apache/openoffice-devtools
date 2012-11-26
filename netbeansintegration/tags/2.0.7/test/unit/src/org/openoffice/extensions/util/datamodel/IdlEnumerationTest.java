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
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class IdlEnumerationTest extends TestCase {
    
    public IdlEnumerationTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IdlEnumerationTest.class);
        
        return suite;
    }

    /**
     * Test of getEnum method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetEnum() {
        System.out.println("getEnum");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        IdlEnum expResult = instance.addEnum();
        assertNotNull(expResult);
        String name = null;
        try {
            name = expResult.getSimpleProperty(expResult.PROPERTY_CONTAINER_NAME);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            fail(ex.getMessage());
        }
        IdlEnum result = instance.getEnum(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of addEnum method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testAddEnum() {
        System.out.println("addEnum");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        IdlEnum result = instance.addEnum();
        assertNotNull(result);
    }

    /**
     * Test of removeEnum method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testRemoveEnum() {
        System.out.println("removeEnum");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        IdlEnum expResult = instance.addEnum();
        assertNotNull(expResult);
        String name = null;
        try {
            name = expResult.getSimpleProperty(expResult.PROPERTY_CONTAINER_NAME);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            fail(ex.getMessage());
        }
        IdlEnum result = instance.getEnum(name);
        assertEquals(expResult, result);

        instance.removeEnum(name);
        
        result = instance.getEnum(name);
        assertNull(result);
    }

    /**
     * Test of getAllSetObjectNames method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetAllSetObjectNames() {
        System.out.println("getAllSetObjectNames");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        IdlEnum aResult1 = instance.addEnum();
        IdlEnum aResult2 = instance.addEnum();
        assertNotNull(aResult1);
        assertNotNull(aResult2);
        String name1 = null;
        String name2 = null;
        try {
            name1 = aResult1.getSimpleProperty(aResult1.PROPERTY_CONTAINER_NAME);
            name2 = aResult2.getSimpleProperty(aResult2.PROPERTY_CONTAINER_NAME);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            fail(ex.getMessage());
        }
        
        String[] expResult = new String[]{name1, name2};
        String[] result = instance.getAllSetObjectNames();
        assertEquals(expResult.length, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result[i]); // maybe not the same order?
        }
    }

    /**
     * Test of getSetObject method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetSetObject() {
        System.out.println("getSetObject");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        IdlEnum expResult = instance.addEnum();
        assertNotNull(expResult);

        String[] names = instance.getAllSetObjectNames();
        assertEquals(1, names.length);
        
        IdlEnum result = (IdlEnum) instance.getSetObject(names[0]);
        assertEquals(expResult, result);
    }

    /**
     * Test of addSetObject method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testAddSetObject() {
        System.out.println("addSetObject");
        
        String internalName = "internalName";
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        IdlEnum expResult = new IdlEnum("name", instance);
        
        instance.addSetObject(internalName, expResult);
        
        IdlEnum result = (IdlEnum) instance.getSetObject(internalName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        String expResult = "a.package.name";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        IdlEnum aResult1 = instance.addEnum();
        IdlEnum aResult2 = instance.addEnum();
        assertNotNull(aResult1);
        assertNotNull(aResult2);
        
        NbNodeObject[] expResult = new NbNodeObject[]{aResult1, aResult2};
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(4, result.length);
        boolean firstFound = false;
        boolean secondFound = false;
        for (int i = 0; i < result.length; i++) {
            if (result[i].equals(expResult [0])) firstFound = true;
            if (result[i].equals(expResult [1])) secondFound = true;
        }
        assertTrue(firstFound && secondFound);
    }

    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetProperty() {
        System.out.println("getProperty");
        
        int propertyIndex = 0;
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        String expResult = "name";
        OpenOfficeOrgProperty result = instance.getProperty(propertyIndex);
        try {
            assertEquals(expResult, result.getValueForLanguage(-1));
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(2, result.length);
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        Action[] result = instance.getActions(b);
        assertNull(result);
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        instance.setActions(null); // empty implementation
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        NbNodeObject result = instance.getParent();
        assertNull(result);  // not implemented: class is top in chain
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.IdlEnumeration.
     */
    public void testGetType() {
        System.out.println("getType");
        
        IdlEnumeration instance = new IdlEnumeration("name", "a.package");
        
        int expResult = NbNodeObject.ENUMERATION_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
    }
    
}
