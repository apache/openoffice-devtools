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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openoffice.extensions.config.office;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sg128468
 */
public class OpenOfficeLocationTest extends TestCase {
    
    public OpenOfficeLocationTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(OpenOfficeLocationTest.class);
        return suite;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getOpenOfficeLocation method, of class OpenOfficeLocation.
     */
    public void testGetOpenOfficeLocation_0args() {
        System.out.println("getOpenOfficeLocation");
        OpenOfficeLocation expResult = null;
        OpenOfficeLocation result = OpenOfficeLocation.getOpenOfficeLocation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOpenOfficeLocation method, of class OpenOfficeLocation.
     */
    public void testGetOpenOfficeLocation_String_String() {
        System.out.println("getOpenOfficeLocation");
        String office = "";
        String sdk = "";
        OpenOfficeLocation expResult = null;
        OpenOfficeLocation result = OpenOfficeLocation.getOpenOfficeLocation(office, sdk, false);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearOpenOfficeLocation method, of class OpenOfficeLocation.
     */
    public void testClearOpenOfficeLocation() {
        System.out.println("clearOpenOfficeLocation");
        OpenOfficeLocation.clearOpenOfficeLocation();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBootstrapPath method, of class OpenOfficeLocation.
     */
    public void testGetBootstrapPath() {
        System.out.println("getBootstrapPath");
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getBootstrapPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOfficePath method, of class OpenOfficeLocation.
     */
    public void testGetOfficePath() {
        System.out.println("getOfficePath");
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getOfficePath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSdkPath method, of class OpenOfficeLocation.
     */
    public void testGetSdkPath() {
        System.out.println("getSdkPath");
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getSdkPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnorcPath method, of class OpenOfficeLocation.
     */
    public void testGetUnorcPath() {
        System.out.println("getUnorcPath");
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getUnorcPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOfficeVersion method, of class OpenOfficeLocation.
     */
    public void testGetOfficeVersion() {
        System.out.println("getOfficeVersion");
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getOfficeVersion();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFullPathForJar method, of class OpenOfficeLocation.
     */
    public void testGetFullPathForJar() {
        System.out.println("getFullPathForJar");
        String jarName = "";
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getFullPathForJar(jarName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnoTypesPath method, of class OpenOfficeLocation.
     */
    public void testGetUnoTypesPath() {
        System.out.println("getUnoTypesPath");
        OpenOfficeLocation instance = null;
        String[] expResult = null;
        String[] result = instance.getUnoTypesPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPathVariable method, of class OpenOfficeLocation.
     */
    public void testGetPathVariable() {
        System.out.println("getPathVariable");
        OpenOfficeLocation instance = null;
        String expResult = "";
        String result = instance.getPathVariable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validateOffice method, of class OpenOfficeLocation.
     */
    public void testValidateOffice() {
        System.out.println("validateOffice");
        String officePath = "";
        boolean expResult = false;
        boolean result = OpenOfficeLocation.validateOffice(officePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validateSDK method, of class OpenOfficeLocation.
     */
    public void testValidateSDK() {
        System.out.println("validateSDK");
        String sdkPath = "";
        boolean expResult = false;
        boolean result = OpenOfficeLocation.validateSDK(sdkPath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of validate method, of class OpenOfficeLocation.
     */
    public void testValidate() {
        System.out.println("validate");
        OpenOfficeLocation instance = null;
        boolean expResult = false;
        boolean result = instance.validate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSdk method, of class OpenOfficeLocation.
     */
    public void testGetSdk() {
        System.out.println("getSdk");
        String officePath = "";
        String expResult = "";
        String result = OpenOfficeLocation.getSdk(officePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isThreeLayerOffice method, of class OpenOfficeLocation.
     */
    public void testIsThreeLayerOffice_0args() {
        System.out.println("isThreeLayerOffice");
        OpenOfficeLocation instance = null;
        boolean expResult = false;
        boolean result = instance.isThreeLayerOffice();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isThreeLayerOffice method, of class OpenOfficeLocation.
     */
    public void testIsThreeLayerOffice_String() {
        System.out.println("isThreeLayerOffice");
        String officePath = "";
        boolean expResult = false;
        boolean result = OpenOfficeLocation.isThreeLayerOffice(officePath);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
