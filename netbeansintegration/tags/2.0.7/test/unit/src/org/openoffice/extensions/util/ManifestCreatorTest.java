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
import java.util.Properties;
import junit.framework.TestCase;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author sg128468
 */
public class ManifestCreatorTest extends TestCase {
    
    FileObject projectRoot;
    
    public ManifestCreatorTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        // TODO: create mock file object for tests
        projectRoot = FileUtil.toFileObject(new File(System.getProperty("java.io.tmpdir")));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of add method, of class ManifestCreator.
     */
    public void testAdd() {
        System.out.println("add");
        String mimeType = "some type";
        String fullPath = "some path";
        ManifestCreator instance = new ManifestCreator(projectRoot);
        instance.add(mimeType, fullPath);
    }

    /**
     * Test of remove method, of class ManifestCreator.
     */
    public void testRemove() {
        System.out.println("remove");
        String mimeType = "some type";
        String fullPath = "some path";
        ManifestCreator instance = new ManifestCreator(projectRoot);
        instance.remove(mimeType, fullPath);
    }

    /**
     * Test of flush method, of class ManifestCreator.
     */
    public void testFlush() throws Exception {
        System.out.println("flush");
        ManifestCreator instance = new ManifestCreator(projectRoot);
//        instance.flush();
//        FileObject man = projectRoot.getFileObject("src/uno-extension-manifest.xml"); // NOI18N
//        assertTrue(man.canRead());
//        assertTrue(man.isData());
    }

    /**
     * Test of updateManifest method, of class ManifestCreator.
     */
    public void testUpdateManifest() {
        System.out.println("updateManifest");
        Properties props = new Properties();
        ManifestCreator instance = new ManifestCreator(projectRoot);
//        instance.updateManifest(props);
    }

}
