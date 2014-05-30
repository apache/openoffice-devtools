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
public class IxLocatorTest extends TestCase {
    
    public IxLocatorTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IxLocatorTest.class);
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
     * Test of locateOfficePaths method, of class IxLocator.
     */
    public void testLocateOfficePaths() {
        System.out.println("locateOfficePaths");
        IxLocator instance = null;
        instance.locateOfficePaths();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of locateOffice method, of class IxLocator.
     */
    public void testLocateOffice() {
        System.out.println("locateOffice");
        IxLocator instance = null;
        instance.locateOffice();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of locateSDK method, of class IxLocator.
     */
    public void testLocateSDK() {
        System.out.println("locateSDK");
        IxLocator instance = null;
        instance.locateSDK();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
