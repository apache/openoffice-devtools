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
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openoffice.extensions.config.office.PlatformInfo;

/**
 *
 * @author sg128468
 */
public class ScriptExecutorTest extends TestCase {
    
    private String executeCommand;
            
    public ScriptExecutorTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        if (PlatformInfo.isWindows()) {
            executeCommand = "dir";
        }
        else {
            executeCommand = "ls";
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ScriptExecutorTest.class);
        
        return suite;
    }

    /**
     * Test of setEnv method, of class ScriptExecutor.
     */
    public void testSetEnv() {
        System.out.println("setEnv");
        Map<String,String> _envVars = null;
        ScriptExecutor.setEnv(_envVars);
    }

    /**
     * Test of executeScript method, of class ScriptExecutor.
     */
    public void testExecuteScript_3args() {
        System.out.println("executeScript");
        String[] script = new String[]{executeCommand};
        Map<String,String> _envVars = null;
        File workDir = new File(System.getProperty("java.io.tmpdir"));
        int expResult = 0;
        int result = ScriptExecutor.executeScript(script, _envVars, workDir);
        assertEquals(expResult, result);
    }

    /**
     * Test of executeScript method, of class ScriptExecutor.
     */
    public void testExecuteScript_StringArr_StringArr() {
        System.out.println("executeScript");
        String[] script = new String[]{executeCommand};
        Map<String,String> envVars = null;
        int expResult = 0;
        int result = ScriptExecutor.executeScript(script, envVars);
        assertEquals(expResult, result);
    }

    /**
     * Test of executeScript method, of class ScriptExecutor.
     */
    public void testExecuteScript_StringArr() {
        System.out.println("executeScript");
        String[] script = new String[]{executeCommand};
        int expResult = 0;
        int result = ScriptExecutor.executeScript(script);
        assertEquals(expResult, result);
    }

    /**
     * Test of executeScript method, of class ScriptExecutor.
     */
    public void testExecuteScript_StringArr_File() {
        System.out.println("executeScript");
        String[] script = new String[]{executeCommand};
        File workDir = new File(System.getProperty("java.io.tmpdir"));
        int expResult = 0;
        int result = ScriptExecutor.executeScript(script, workDir);
        assertEquals(expResult, result);
    }

    /**
     * Test of getErrors method, of class ScriptExecutor.
     */
    public void testGetErrors() {
        System.out.println("getErrors");
        String expResult = "";
        String result = ScriptExecutor.getErrors();
        assertEquals(expResult, result);
    }

    /**
     * Test of hasErrors method, of class ScriptExecutor.
     */
    public void testHasErrors() {
        System.out.println("hasErrors");
        boolean expResult = false;
        boolean result = ScriptExecutor.hasErrors();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutput method, of class ScriptExecutor.
     */
    public void testGetOutput() {
        System.out.println("getOutput");
        String[] script = new String[]{executeCommand};
        ScriptExecutor.executeScript(script);
        String result = ScriptExecutor.getOutput();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of hasOutput method, of class ScriptExecutor.
     */
    public void testHasOutput() {
        System.out.println("hasOutput");
        boolean expResult = true;
        String[] script = new String[]{executeCommand};
        ScriptExecutor.executeScript(script);
        boolean result = ScriptExecutor.hasOutput();
        assertEquals(expResult, result);
    }
}
