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
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openoffice.extensions.util.datamodel.*;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.ObjectHelperImplementations.MyLanguageAction;

/**
 *
 * @author sg128468
 */
public class LocalizedOpenOfficeOrgPropertyTest extends TestCase {
    
    private ObjectHelperImplementations m_objectHelper;
        
    public LocalizedOpenOfficeOrgPropertyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        m_objectHelper = new ObjectHelperImplementations();
    }

    protected void tearDown() throws Exception {
        m_objectHelper = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(LocalizedOpenOfficeOrgPropertyTest.class);
        
        return suite;
    }

    /**
     * Test of isLocalized method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testIsLocalized() {
        System.out.println("isLocalized");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        boolean expResult = true;
        boolean result = instance.isLocalized();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPropertyName method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetPropertyName() {
        System.out.println("getPropertyName");
        
        String expResult = "MyName";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            expResult, "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        String result = instance.getPropertyName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getUsedLanguageIndexes method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetUsedLanguageIndexes() {
        System.out.println("getUsedLanguageIndexes");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        Integer[] expResult = new Integer[]{new Integer(LanguageDefinition.LANGUAGE_ID_en)};
        Integer[] result = instance.getUsedLanguageIndexes();
        assertEquals(expResult.length, result.length);
        assertEquals(expResult[0], result[0]);
    }

    /**
     * Test of setPropertyName method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testSetPropertyName() {
        System.out.println("setPropertyName");
        
        String name = "A new Name";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        instance.setPropertyName(name);

        String expResult = instance.getPropertyName();
        assertEquals(expResult, name);
    }

    /**
     * Test of getValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetValueForLanguage() {
        System.out.println("getValueForLanguage");
        
        String expResult = "aValue";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", expResult, "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        String result = instance.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
        assertEquals(expResult, result);

        result = instance.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_de);
        assertNull(result);

        result = instance.getValueForLanguage(-2);
        assertNull(result);
    }

    /**
     * Test of setValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testSetValueForLanguage() {
        System.out.println("setValueForLanguage");
        
        String expResult = "a new Value";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        instance.setValueForLanguage(LanguageDefinition.LANGUAGE_ID_en, expResult);
        String result = instance.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
        assertEquals(expResult, result);

        expResult = "another new Value";
        instance.setValueForLanguage(LanguageDefinition.LANGUAGE_ID_de, expResult);
        result = instance.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_de);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testRemoveValueForLanguage() {
        System.out.println("removeValueForLanguage");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        instance.removeValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
        String result = instance.getValueForLanguage(LanguageDefinition.LANGUAGE_ID_en);
        assertNull(result);
    }

    /**
     * Test of getDescription method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetDescription() {
        System.out.println("getDescription");
        
        String expResult = "a really long description";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        instance.setDescription(expResult);
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDescription method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testSetDescription() {
        System.out.println("setDescription");
        
        String expResult = "a new description";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "MyName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        instance.setDescription(expResult);
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        String expResult = "myName";
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            expResult, "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        NbNodeObject[] result = instance.getAllSubObjects();
        assertNull(result);
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);

        // does not compile?
//        Node.Property[] expResult = new LocalizedProperty(LanguageDefinition.LANGUAGE_ID_en);
        int expResult = 1;
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(expResult, result.length);
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        NbNodeObject expResult = m_objectHelper.getMockNbNodeObject();
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", expResult,
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        boolean expResult = true;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        Action[] result = instance.getActions(true);
        assertEquals(result.length, 2); // got two actions
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        boolean expResult = true;
        MyLanguageAction langActions = new ObjectHelperImplementations().getMyLanguageAction();
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        instance.setActions(langActions);

        Action[] actions = instance.getActions(true);
        actions[0].actionPerformed(new ActionEvent(this, 0, "addLang"));
        actions[1].actionPerformed(new ActionEvent(this, 0, "delLang"));
        
        boolean result = langActions.addLangCalled & langActions.delLangCalled;
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty.
     */
    public void testGetType() {
        System.out.println("getType");
        
        LocalizedOpenOfficeOrgProperty instance = new LocalizedOpenOfficeOrgProperty(
            "myName", "aValue", "a description", m_objectHelper.getMockNbNodeObject(),
            LanguageDefinition.LANGUAGE_ID_en, null);
        
        int expResult = NbNodeObject.LOCALIZED_PROPERTY_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
        
    }
}
