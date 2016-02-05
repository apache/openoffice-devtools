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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import junit.framework.TestCase;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openoffice.extensions.util.datamodel.NbNodeObject;

/**
 *
 * @author sg128468
 */
public class IdlFileHelperTest extends TestCase {
    
    public IdlFileHelperTest(String testName) {
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
     * Test of getAllIdlFiles method, of class IdlFileHelper.
     */
    public void testGetAllIdlFiles() {
        System.out.println("getAllIdlFiles");
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
//        System.out.println("Tempdir" + tmpDir);
//        FileObject o;
//        try {
//            o = FileUtil.createFolder(tmpDir);
//        } catch (IOException ex) {
//            fail(ex.toString());
//        }
//        System.out.println("FO " + o);
//        IdlFileHelper instance = new IdlFileHelper(o);
//        File[] list = tmpDir.listFiles(new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".idl");
//            }
//        });
//        NbNodeObject[] result = instance.getAllIdlFiles();
//        assertEquals(list.length, result.length);
    }

}
