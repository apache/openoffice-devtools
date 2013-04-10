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
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.ObjectHelperImplementations;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public class FunctionTest extends TestCase {

    private ObjectHelperImplementations m_objectHelper;
    
    public FunctionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        m_objectHelper = new ObjectHelperImplementations();
    }

    protected void tearDown() throws Exception {
        m_objectHelper = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(FunctionTest.class);
        
        return suite;
    }

    /**
     * Test of addLanguage method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testAddLanguage() {
        System.out.println("addLanguage");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        OpenOfficeOrgProperty prop = instance.getProperty(instance.PROPERTY_DisplayName);
        try {
            prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
        
        try {
            String val = prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_de);
            assertNull(val);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
        
        String expValue = "dummy text";
        instance.addLanguage(LanguageDefinition.LANGUAGE_ID_de, expValue);
        try {
            prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
            String value = prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_de);
            assertEquals(expValue, value);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of printLanguages method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testPrintLanguages() {
        System.out.println("printLanguages");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        instance.printLanguages();  // just internal testing for languages
    }

    /**
     * Test of removeLanguage method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testRemoveLanguage() {
        System.out.println("removeLanguage");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );

        String expValue = "dummy text";
        instance.addLanguage(LanguageDefinition.LANGUAGE_ID_de, expValue);
        
        instance.removeLanguage(LanguageDefinition.LANGUAGE_ID_en);
        
        OpenOfficeOrgProperty prop = instance.getProperty(instance.PROPERTY_DisplayName);
        try {
            prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_de);
            String value = prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_de);
            assertEquals(expValue, value);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }

        try {
            String val = prop.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
            assertNull(val);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of getParameter method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetParameter() {
        System.out.println("getParameter");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        Parameter newParam = instance.addParameter();
        
        Parameter expResult = newParam;
        Parameter result = null;
        try {
            String name = newParam.getProperty(newParam.PROPERTY_Name).getValueForLanguage(-1);
            result = instance.getParameter(name);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
        assertEquals(expResult, result);
    }

    /**
     * Test of addParameter method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testAddParameter() {
        System.out.println("addParameter");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        Parameter result = instance.addParameter();
        assertNotNull(result);  // tested in getParameter
    }

    /**
     * Test of removeParameter method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testRemoveParameter() {
        System.out.println("removeParameter");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        Parameter newParam1 = instance.addParameter();
        Parameter newParam2 = instance.addParameter();
        
        Parameter expResult = null;
        Parameter result = null;
        try {
            expResult = newParam1;
            String name = expResult.getProperty(expResult.PROPERTY_Name).getValueForLanguage(-1);
            result = instance.getParameter(name);
            assertEquals(expResult, result);

            instance.removeParameter(name);
            assertNull(instance.getParameter(name));
            
            expResult = newParam2;
            name = expResult.getProperty(expResult.PROPERTY_Name).getValueForLanguage(-1);
            result = instance.getParameter(name);
            assertEquals(expResult, result);
            
            instance.removeParameter(name);
            assertNull(instance.getParameter(name));
            
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
        
    }

    /**
     * Test of getAllSetObjectNames method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetAllSetObjectNames() {
        System.out.println("getAllSetObjectNames");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        instance.addParameter();
        instance.addParameter();
        
        String[] expResult = new String[]{"parameter0", "parameter1"};
        String[] result = instance.getAllSetObjectNames();
        assertEquals(expResult.length, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
        
    }

    /**
     * Test of getSetObject method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetSetObject() {
        System.out.println("getSetObject");
        
        String internalName = "parameter0";
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );

        Parameter expResult = instance.addParameter();
        
        Object result = instance.getSetObject(internalName);
        assertTrue(result instanceof Parameter);
        assertEquals(expResult, result);
    }

    /**
     * Test of addSetObject method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testAddSetObject() {
        System.out.println("addSetObject");
        
        String internalName = "bumblebee";
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getAddinNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );

        Object setObject = new Parameter("a name", "int", instance, 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)});
        
        instance.addSetObject(internalName, setObject);
        
        Object result = instance.getSetObject(internalName);
        assertTrue(result instanceof Parameter);
        assertEquals(setObject, result);
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        String expResult = "int functionName();";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        int expResult = 3;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(expResult, result.length);
        
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        int expResult = 3;
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(expResult, result.length);
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );

        instance.setActions(m_objectHelper.getMyLanguageAction());
        assertTrue(instance.hasActions(0));
        
//        Action[] expResult = null;
        Action[] result = instance.getActions(b);
        assertNull(result);  // wrong parent type for actions
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );

        instance.setActions(m_objectHelper.getMyLanguageAction());
        assertTrue(instance.hasActions(0));
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        NbNodeObject parent = m_objectHelper.getMockNbNodeObject();
        Function instance = new Function("functionName", "int", 
            parent, 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );

        
        NbNodeObject expResult = parent;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.Function.
     */
    public void testGetType() {
        System.out.println("getType");
        
        Function instance = new Function("functionName", "int", 
            m_objectHelper.getMockNbNodeObject(), 
            new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)}
            );
        
        int expResult = NbNodeObject.FUNCTION_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
    }
    
}
