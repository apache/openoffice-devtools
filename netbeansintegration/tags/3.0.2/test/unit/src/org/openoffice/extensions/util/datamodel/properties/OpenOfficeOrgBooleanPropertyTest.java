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

package org.openoffice.extensions.util.datamodel.properties;

import junit.framework.*;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Utilities;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public class OpenOfficeOrgBooleanPropertyTest extends TestCase {

    private ObjectHelperImplementations m_objectHelper;
    
    public OpenOfficeOrgBooleanPropertyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        m_objectHelper = new ObjectHelperImplementations();
    }

    protected void tearDown() throws Exception {
        m_objectHelper = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(OpenOfficeOrgBooleanPropertyTest.class);
        
        return suite;
    }

    /**
     * Test of isLocalized method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testIsLocalized() {
        System.out.println("isLocalized");
        
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        boolean expResult = false;
        boolean result = instance.isLocalized();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPropertyName method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetPropertyName() {
        System.out.println("getPropertyName");
        
        String expResult = "my name";
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            expResult, true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        String result = instance.getPropertyName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPropertyName method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testSetPropertyName() {
        System.out.println("setPropertyName");
        
        String name = "my other name";
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);

        instance.setPropertyName(name);
        
        String result = instance.getPropertyName();
        assertEquals(name, result);
    }

    /**
     * Test of getDescription method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetDescription() {
        System.out.println("getDescription");
        
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);

        String expResult = "the real description";
        
        instance.setDescription(expResult);
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDescription method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testSetDescription() {
        System.out.println("setDescription");
        
        String expResult = "the real description";
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setDescription(expResult);
        
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testSetValue() {
        System.out.println("setValue");
        
        boolean value = false;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setValue(value);

        boolean expResult = false;
        boolean result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValue method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetValue() {
        System.out.println("getValue");
        
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        boolean expResult = true;
        boolean result = instance.getValue();
        assertEquals(expResult, result);
    }

    /**
     * Test of getValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetValueForLanguage() throws Exception {
        System.out.println("getValueForLanguage");
        
        int langID = 0;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        boolean expResult = true;
        String result = instance.getValueForLanguage(langID);
        assertEquals(expResult, Boolean.parseBoolean(result));
    }

    /**
     * Test of removeValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testRemoveValueForLanguage() throws Exception {
        System.out.println("removeValueForLanguage");
        
        int langID = 0;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.removeValueForLanguage(langID);  // empty implemented!
    }

    /**
     * Test of setValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testSetValueForLanguage() throws Exception {
        System.out.println("setValueForLanguage");
        
        int langID = 0;
        String value = "false";
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setValueForLanguage(langID, value);
        boolean expResult = false;
        assertEquals(expResult, instance.getValue());

        value = "true";
        instance.setValueForLanguage(langID, value);
        expResult = true;
        assertEquals(expResult, instance.getValue());
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        NbNodeObject parent = m_objectHelper.getMockNbNodeObject();
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", parent, null);
        
        NbNodeObject expResult = parent;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        String expResult = "my name";
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            expResult, true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        String result = instance.getDisplayName(); // does not really have a display name, equals name
        assertEquals(expResult, result); 
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        NbNodeObject[] result = instance.getAllSubObjects();
        assertNull(result);  // no sub objects: returns null
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        Sheet sheet = null;
        PropertyChangeListener listener = null;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        // does not compile?
//        Node.Property[] expResult = new LocalizedProperty(LanguageDefinition.LANGUAGE_ID_en);
        int expResult = 1;
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(expResult, result.length);
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean context = true;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        assertNull(instance.getActions(true));
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction actions = null;
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setActions(actions);  // empty
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty.
     */
    public void testGetType() {
        System.out.println("getType");
        
        OpenOfficeOrgBooleanProperty instance = new OpenOfficeOrgBooleanProperty(
            "my name", true, "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        int expResult = NbNodeObject.PROPERTY_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
    }

}
