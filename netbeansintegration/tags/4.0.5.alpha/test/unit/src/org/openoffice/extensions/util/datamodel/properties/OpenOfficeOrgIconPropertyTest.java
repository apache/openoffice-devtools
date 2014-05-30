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
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;

/**
 *
 * @author sg128468
 */
public class OpenOfficeOrgIconPropertyTest extends TestCase {

    private ObjectHelperImplementations m_objectHelper;
    
    public OpenOfficeOrgIconPropertyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        m_objectHelper = new ObjectHelperImplementations();
    }

    protected void tearDown() throws Exception {
        m_objectHelper = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(OpenOfficeOrgIconPropertyTest.class);
        
        return suite;
    }

    /**
     * Test of isLocalized method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testIsLocalized() {
        System.out.println("isLocalized");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        boolean expResult = false;
        boolean result = instance.isLocalized();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPropertyName method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetPropertyName() {
        System.out.println("getPropertyName");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        String expResult = "a name";
        String result = instance.getPropertyName();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setPropertyName method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testSetPropertyName() {
        System.out.println("setPropertyName");
        
        String name = "new name";
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setPropertyName(name);
        
        String expResult = name;
        String result = instance.getPropertyName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescription method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetDescription() {
        System.out.println("getDescription");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        String expResult = "a description";
        String result = instance.getDescription();
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setDescription method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testSetDescription() {
        System.out.println("setDescription");
        
        String description = "new description";
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setDescription(description);
        
        String expResult = description;
        String result = instance.getDescription();
        assertEquals(expResult, result);
    }

    /**
     * Test of getImage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetImage() {
        System.out.println("getImage");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
//        Image expResult = null;
        Image result = instance.getImage();
        assertNotNull(result);  // not a valid image with the constructor
        
    }

    /**
     * Test of getValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetValueForLanguage() throws Exception {
        System.out.println("getValueForLanguage");
        
        int langID = 0;
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        String expResult = "a path";
        String result = instance.getValueForLanguage(langID);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of removeValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testRemoveValueForLanguage() throws Exception {
        System.out.println("removeValueForLanguage");
        
        int langID = 0;
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.removeValueForLanguage(langID);  // not easily testable
    }

    /**
     * Test of setValueForLanguage method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testSetValueForLanguage() throws Exception {
        System.out.println("setValueForLanguage");
        
        int langID = 0;
        String value = "";
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setValueForLanguage(langID, value);// not easily testable
        
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        NbNodeObject parent = m_objectHelper.getMockNbNodeObject();
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", parent, null);
        
        NbNodeObject expResult = parent;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        String expResult = "a name";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
//        NbNodeObject[] expResult = null;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertNull(result);
        
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        int expResult = 1;
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(expResult, result.length);
        
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean context = true;
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
//        Action[] expResult = null;
        Action[] result = instance.getActions(context);
        assertNull(result);
        
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction actions = null;
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        instance.setActions(actions); // empty implemented
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty.
     */
    public void testGetType() {
        System.out.println("getType");
        
        OpenOfficeOrgIconProperty instance = new OpenOfficeOrgIconProperty(
            "a name", "a path", "a description", m_objectHelper.getMockNbNodeObject(), null);
        
        int expResult = NbNodeObject.PROPERTY_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
    }
    
}
