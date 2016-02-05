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
import java.util.Arrays;
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
public class FunctionExceptionTest extends TestCase {
    
    public FunctionExceptionTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(FunctionExceptionTest.class);
        
        return suite;
    }
    
    /**
     * Test of getDisplayName method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testGetDisplayName() {
        System.out.println("getDisplayName");
        
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        String expResult = "sample.package.execptionName";
        String result = instance.getDisplayName();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getProperty method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testGetProperty() {
        System.out.println("getProperty");
        
        int propertyIndex = 0;
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        String expResult = "execptionName";
        OpenOfficeOrgProperty result = instance.getProperty(propertyIndex);
        try {
            assertEquals(expResult, result.getValueForLanguage(-1));
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            fail(ex.getMessage());
        }
    }
    
    /**
     * Test of getAllSubObjects method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testGetAllSubObjects() {
        System.out.println("getAllSubObjects");
        
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        int expResult = 3;
        NbNodeObject[] result = instance.getAllSubObjects();
        assertEquals(expResult, result.length);
    }
    
    /**
     * Test of createProperties method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testCreateProperties() {
        System.out.println("createProperties");
        
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        int expResult = 3;
        Node.Property[] result = instance.createProperties(null, null);
        assertEquals(expResult, result.length);
    }
    
    /**
     * Test of setActions method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testSetActions() {
        System.out.println("setActions");
        
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        instance.setActions(null); // empty implementation
    }
    
    /**
     * Test of getActions method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testGetActions() {
        System.out.println("getActions");
        
        boolean b = true;
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
//        Action[] expResult = null;
        Action[] result = instance.getActions(b);
        assertNull(result);
    }
    
    /**
     * Test of hasActions method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testHasActions() {
        System.out.println("hasActions");
        
        int type = 0;
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        boolean expResult = false;
        boolean result = instance.hasActions(type);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getParent method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testGetParent() {
        System.out.println("getParent");
        
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        NbNodeObject expResult = null;
        NbNodeObject result = instance.getParent();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getType method, of class org.openoffice.extensions.util.datamodel.FunctionException.
     */
    public void testGetType() {
        System.out.println("getType");
        
        FunctionException instance = new FunctionException("execptionName", "sample.package");
        
        int expResult = NbNodeObject.EXCEPTION_TYPE;
        int result = instance.getType();
        assertEquals(expResult, result);
        
    }
    
}
