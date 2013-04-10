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

package org.openoffice.extensions.util;

import junit.framework.TestCase;
import org.openide.filesystems.FileObject;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Service;

/**
 *
 * @author sg128468
 */
public class IdlFileCreatorTest extends TestCase {
    
    public IdlFileCreatorTest(String testName) {
        super(testName);
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
     * Test of getIdlTypeForJavaType method, of class IdlFileCreator.
     */
    public void testGetIdlTypeForJavaType() {
        System.out.println("getIdlTypeForJavaType");
        for (int i = 0; i < IdlFileCreator.IDL_JAVA_TYPE_MAPPING.length; i++) {
            String result = IdlFileCreator.getIdlTypeForJavaType(IdlFileCreator.IDL_JAVA_TYPE_MAPPING[i][1]);
            assertEquals(IdlFileCreator.IDL_JAVA_TYPE_MAPPING[i][0], result);
        }
        String someType = "com.sun.star.test.TestType";
        String expResult = "com::sun::star::test::TestType";
        assertEquals(expResult, IdlFileCreator.getIdlTypeForJavaType(someType));
    }

    /**
     * Test of createAllIdlFiles method, of class IdlFileCreator.
     *
    public void testCreateAllIdlFiles() {
        NbNodeObject baseObject = new Service("Service", "com.example.package");
        IdlFileCreator instance = new IdlFileCreator();
        FileObject[] expResult = null;
        FileObject[] result = instance.createAllIdlFiles(baseObject);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getModuleFromPackage method, of class IdlFileCreator.
     *
    public void testGetModuleFromPackage() {
        System.out.println("getModuleFromPackage");
        String pkg = "";
        IdlFileCreator instance = null;
        instance.getModuleFromPackage(pkg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    } */

}
