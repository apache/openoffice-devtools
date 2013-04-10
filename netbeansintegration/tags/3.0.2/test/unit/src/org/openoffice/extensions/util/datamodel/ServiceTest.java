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
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.projecttemplates.component.datamodel.types.node.ComponentInterfaceTypePropertyEditor;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class ServiceTest extends TestCase {
    
    public ServiceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ServiceTest.class);
        
        return suite;
    }

    /**
     * Test of addInterface method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testAddInterface() {
        System.out.println("addInterface");
        
        Service instance = null;
        
        Interface expResult = null;
        Interface result = instance.addInterface();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeInterface method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testRemoveInterface() {
        System.out.println("removeInterface");
        
        Interface ifc = null;
        Service instance = null;
        
        instance.removeInterface(ifc);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetType() {
        System.out.println("getType");
        
        Service instance = null;
        
        int expResult = 0;
        int result = instance.getType();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSetObjectNames method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetAllSetObjectNames() {
        System.out.println("getAllSetObjectNames");
        
        Service instance = null;
        
        String[] expResult = null;
        String[] result = instance.getAllSetObjectNames();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSetObject method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetSetObject() {
        System.out.println("getSetObject");
        
        String name = "";
        Service instance = null;
        
        Object expResult = null;
        Object result = instance.getSetObject(name);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addSetObject method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testAddSetObject() {
        System.out.println("addSetObject");
        
        String name = "";
        Object setObject = null;
        Service instance = null;
        
        instance.addSetObject(name, setObject);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addLanguage method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testAddLanguage() {
        System.out.println("addLanguage");
        
        int languageID = 0;
        String defaultText = "";
        Service instance = null;
        
        instance.addLanguage(languageID, defaultText);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeLanguage method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testRemoveLanguage() {
        System.out.println("removeLanguage");
        
        int languageID = 0;
        Service instance = null;
        
        instance.removeLanguage(languageID);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        Service instance = null;
        
        NbNodeObject expResult = null;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        Service instance = null;
        
        String expResult = "";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        Service instance = null;
        
        NbNodeObject[] expResult = null;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Sheet sheet = null;
        PropertyChangeListener listener = null;
        Service instance = null;
        
        Node.Property[] expResult = null;
        Node.Property[] result = instance.createProperties(sheet, listener);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        Service instance = null;
        
        boolean expResult = true;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean context = true;
        Service instance = null;
        
        Action[] expResult = null;
        Action[] result = instance.getActions(context);
        assertEquals(expResult, result);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.Service.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction actions = null;
        Service instance = null;
        
        instance.setActions(actions);
        
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
