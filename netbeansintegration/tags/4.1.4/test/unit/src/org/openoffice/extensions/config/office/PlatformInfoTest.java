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
public class PlatformInfoTest extends TestCase {
    
    public PlatformInfoTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(PlatformInfoTest.class);
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
     * Test of getPlatformBinDir method, of class PlatformInfo.
     */
    public void testGetPlatformBinDir() {
        System.out.println("getPlatformBinDir");
        String expResult = "";
        String result = PlatformInfo.getPlatformBinDir();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPlatformPackageDir method, of class PlatformInfo.
     */
    public void testGetPlatformPackageDir() {
        System.out.println("getPlatformPackageDir");
        String expResult = "";
        String result = PlatformInfo.getPlatformPackageDir();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isWindows method, of class PlatformInfo.
     */
    public void testIsWindows() {
        System.out.println("isWindows");
        boolean expResult = false;
        boolean result = PlatformInfo.isWindows();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isMacOS method, of class PlatformInfo.
     */
    public void testIsMacOS() {
        System.out.println("isMacOS");
        boolean expResult = false;
        boolean result = PlatformInfo.isMacOS();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOfficeProgramDir method, of class PlatformInfo.
     */
    public void testGetOfficeProgramDir() {
        System.out.println("getOfficeProgramDir");
        String expResult = "";
        String result = PlatformInfo.getOfficeProgramDir();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBootstrapIni method, of class PlatformInfo.
     */
    public void testGetBootstrapIni() {
        System.out.println("getBootstrapIni");
        String expResult = "";
        String result = PlatformInfo.getBootstrapIni();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUnorcName method, of class PlatformInfo.
     */
    public void testGetUnorcName() {
        System.out.println("getUnorcName");
        String expResult = "";
        String result = PlatformInfo.getUnorcName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSDKPostfix method, of class PlatformInfo.
     */
    public void testGetSDKPostfix() {
        System.out.println("getSDKPostfix");
        String expResult = "";
        String result = PlatformInfo.getSDKPostfix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPostfix method, of class PlatformInfo.
     */
    public void testGetPostfix() {
        System.out.println("getPostfix");
        String expResult = "";
        String result = PlatformInfo.getPostfix();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
