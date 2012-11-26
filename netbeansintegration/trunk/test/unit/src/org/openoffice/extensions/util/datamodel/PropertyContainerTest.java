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
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;

/**
 *
 * @author sg128468
 */
public class PropertyContainerTest extends TestCase {
    
    public PropertyContainerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PropertyContainerTest.class);
        
        return suite;
    }

    /**
     * Test of getLocalizedProperty method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testGetLocalizedProperty() throws Exception {
        System.out.println("getLocalizedProperty");
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_DISPLAY_NAME;
        int languageID = LanguageDefinition.LANGUAGE_ID_en;
        PropertyContainer instance = new PropertyContainerImpl();
        
        String expResult = "localizedvalue";
        String result = instance.getLocalizedProperty(propertyName, languageID);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setLocalizedProperty method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testSetLocalizedProperty() throws Exception {
        System.out.println("setLocalizedProperty");
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_DISPLAY_NAME;
        int languageID = LanguageDefinition.LANGUAGE_ID_en;
        String value = "newValue";
        PropertyContainer instance = new PropertyContainerImpl();
        
        instance.setLocalizedProperty(propertyName, languageID, value);
        
        String expResult = value;
        String result = instance.getLocalizedProperty(propertyName, languageID);
        assertEquals(expResult, result);
    }

    /**
     * Test of getSimpleProperty method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testGetSimpleProperty() throws Exception {
        System.out.println("getSimpleProperty");
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_NAME;
        PropertyContainer instance = new PropertyContainerImpl();
        
        String expResult = "value";
        String result = instance.getSimpleProperty(propertyName);
        assertEquals(expResult, result);
    }

    /**
     * Test of setSimpleProperty method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testSetSimpleProperty() throws Exception {
        System.out.println("setSimpleProperty");
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_NAME;
        String value = "newValue";
        PropertyContainer instance = new PropertyContainerImpl();
        
        instance.setSimpleProperty(propertyName, value);
        
        String expResult = value;
        String result = instance.getSimpleProperty(propertyName);
        assertEquals(expResult, result);
    }

    /**
     * Test of isPropertyLocalized method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testIsPropertyLocalized() throws Exception {
        System.out.println("isPropertyLocalized");
        
        PropertyContainer instance = new PropertyContainerImpl();
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_DISPLAY_NAME;
        boolean expResult = true;
        boolean result = instance.isPropertyLocalized(propertyName);
        assertEquals(expResult, result);
        
        propertyName = PropertyContainer.PROPERTY_CONTAINER_NAME;
        expResult = false;
        result = instance.isPropertyLocalized(propertyName);
        assertEquals(expResult, result);
    }

    /**
     * Test of setLanguageWithDefaultText method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testSetLanguageWithDefaultText() throws Exception {
        System.out.println("setLanguageWithDefaultText");
        
        int languageID = LanguageDefinition.LANGUAGE_ID_ar;
        String text = "default";
        PropertyContainer instance = new PropertyContainerImpl();
        
        instance.setLanguageWithDefaultText(languageID, text);
        
        String expResult = text;
        String result = instance.getLocalizedProperty(
            PropertyContainer.PROPERTY_CONTAINER_DISPLAY_NAME, languageID);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeLanguageAndText method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testRemoveLanguageAndText() throws Exception {
        System.out.println("removeLanguageAndText");
        
        int languageID = LanguageDefinition.LANGUAGE_ID_ar;
        PropertyContainer instance = new PropertyContainerImpl();

        instance.setLanguageWithDefaultText(languageID, "dummy");
        
        instance.removeLanguageAndText(languageID);

        String result = instance.getLocalizedProperty(PropertyContainer.PROPERTY_CONTAINER_DISPLAY_NAME, languageID);
        assertNull(result);
    }

    /**
     * Test of getAllPropertyNames method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testGetAllPropertyNames() {
        System.out.println("getAllPropertyNames");
        
        PropertyContainer instance = null;
        
        String[] expResult = new String[] {
            PropertyContainer.PROPERTY_CONTAINER_NAME,
            PropertyContainer.PROPERTY_CONTAINER_PACKAGE,
            PropertyContainer.PROPERTY_CONTAINER_DISPLAY_NAME
        };
        String[] result = instance.getAllPropertyNames();
        assertEquals(expResult.length, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testGetProperty() throws Exception {
        System.out.println("getProperty");
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_PACKAGE;
        PropertyContainer instance = new PropertyContainerImpl();
        
//        OpenOfficeOrgProperty expResult = null;
        OpenOfficeOrgProperty result = instance.getProperty(propertyName);
        assertNotNull(result);
    }

    /**
     * Test of setProperty method, of class org.openoffice.extensions.util.datamodel.PropertyContainer.
     */
    public void testSetProperty() throws Exception {
        System.out.println("setProperty");
        
        String propertyName = PropertyContainer.PROPERTY_CONTAINER_NAME;
        OpenOfficeOrgProperty value = new SimpleOpenOfficeOrgProperty("newName", "value", "description", null, null);
        PropertyContainer instance = new PropertyContainerImpl();
        
        instance.setProperty(propertyName, value);
        OpenOfficeOrgProperty result = instance.getProperty(propertyName);

        assertEquals(value, result);
    }
    
    private class PropertyContainerImpl extends PropertyContainer {
        public PropertyContainerImpl() {
            super(new String[]{PROPERTY_CONTAINER_NAME, PROPERTY_CONTAINER_PACKAGE, PROPERTY_CONTAINER_DISPLAY_NAME});
            properties[0] = new SimpleOpenOfficeOrgProperty("name", "value", "description", null, null);
            properties[1] = new SimpleOpenOfficeOrgProperty("package", "a.package", "description", null, null);
            properties[2] = new LocalizedOpenOfficeOrgProperty("displayName", "localizedvalue", "description", 
                null, LanguageDefinition.LANGUAGE_ID_en, null);
        }
    }
}
