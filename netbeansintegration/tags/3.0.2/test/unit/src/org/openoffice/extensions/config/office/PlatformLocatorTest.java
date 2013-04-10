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
public class PlatformLocatorTest extends TestCase {
    
    public PlatformLocatorTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PlatformLocatorTest.class);
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
     * Test of locateOffice method, of class PlatformLocator.
     */
    public void testLocateOffice() {
        System.out.println("locateOffice");
        PlatformLocator instance = null;
        instance.locateOffice();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of locateSDK method, of class PlatformLocator.
     */
    public void testLocateSDK() {
        System.out.println("locateSDK");
        PlatformLocator instance = null;
        instance.locateSDK();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of locateOfficePaths method, of class PlatformLocator.
     */
    public void testLocateOfficePaths() {
        System.out.println("locateOfficePaths");
        PlatformLocator instance = null;
        instance.locateOfficePaths();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFullPathForJar method, of class PlatformLocator.
     */
    public void testGetFullPathForJar() {
        System.out.println("getFullPathForJar");
        String jarName = "";
        PlatformLocator instance = null;
        String expResult = "";
        String result = instance.getFullPathForJar(jarName);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOfficePath method, of class PlatformLocator.
     */
    public void testGetOfficePath() {
        System.out.println("getOfficePath");
        PlatformLocator instance = null;
        String expResult = "";
        String result = instance.getOfficePath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSdkPath method, of class PlatformLocator.
     */
    public void testGetSdkPath() {
        System.out.println("getSdkPath");
        PlatformLocator instance = null;
        String expResult = "";
        String result = instance.getSdkPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPathVariable method, of class PlatformLocator.
     */
    public void testGetPathVariable() {
        System.out.println("getPathVariable");
        PlatformLocator instance = null;
        String expResult = "";
        String result = instance.getPathVariable();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTypesPath method, of class PlatformLocator.
     */
    public void testGetTypesPath() {
        System.out.println("getTypesPath");
        PlatformLocator instance = null;
        String[] expResult = null;
        String[] result = instance.getTypesPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isThreeLayerOffice method, of class PlatformLocator.
     */
    public void testIsThreeLayerOffice() {
        System.out.println("isThreeLayerOffice");
        PlatformLocator instance = null;
        boolean expResult = false;
        boolean result = instance.isThreeLayerOffice();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnorcPath method, of class PlatformLocator.
     */
    public void testGetUnorcPath() {
        System.out.println("getUnorcPath");
        PlatformLocator instance = null;
        String expResult = "";
        String result = instance.getUnorcPath();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
