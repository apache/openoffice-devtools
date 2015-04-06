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
import org.openide.util.NbBundle;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.datamodel.actions.BaseAction;
import org.openoffice.extensions.util.datamodel.properties.ObjectHelperImplementations;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.SimpleOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;

/**
 *
 * @author sg128468
 */
public class IdlEnumTest extends TestCase {

    private ObjectHelperImplementations m_objectHelper;
    
    public IdlEnumTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        m_objectHelper = new ObjectHelperImplementations();
    }

    protected void tearDown() throws Exception {
        m_objectHelper = null;
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IdlEnumTest.class);
        
        return suite;
    }

    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        String expResult = "name";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testGetProperty() {
        System.out.println("getProperty");
        
        int propertyIndex = 0;
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        String expResult = "name";
        OpenOfficeOrgProperty result = instance.getProperty(propertyIndex);
        try {
            assertEquals(expResult, result.getValueForLanguage(-1));
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        String expResult = "name";
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(1, result.length);
        assertEquals(expResult, ((SimpleOpenOfficeOrgProperty)result[0]).getValueForLanguage(-1));
    }

    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(1, result.length);
    }

    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        BaseAction baseAction = null;
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        instance.setActions(baseAction);
    }

    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        Action[] result = instance.getActions(b);
        assertNull(result);
    }

    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        NbNodeObject expResult = m_objectHelper.getMockNbNodeObject();
        IdlEnum instance = new IdlEnum("name", expResult);
        
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.IdlEnum.
     */
    public void testGetType() {
        System.out.println("getType");
        
        IdlEnum instance = new IdlEnum("name", m_objectHelper.getMockNbNodeObject());
        
        int expResult = NbNodeObject.ENUM_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
    }
    
}
