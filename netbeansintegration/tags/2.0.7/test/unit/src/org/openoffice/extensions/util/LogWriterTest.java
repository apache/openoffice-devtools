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

import java.io.OutputStream;
import junit.framework.TestCase;

/**
 *
 * @author sg128468
 */
public class LogWriterTest extends TestCase {
    
    public LogWriterTest(String testName) {
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
     * Test of getLoglevels method, of class LogWriter.
     */
    public void testGetLoglevels() {
        System.out.println("getLoglevels");
        String[] expResult = new String[]{"Info", "Warning", "Critical"};
        String[] result = LogWriter.getLoglevels();
        assertEquals(expResult.length, result.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of getLogWriter method, of class LogWriter.
     */
    public void testGetLogWriter() {
        System.out.println("getLogWriter");
        LogWriter result = LogWriter.getLogWriter();
        assertNotNull(result);
    }

    /**
     * Test of createLogWriter method, of class LogWriter.
     */
    public void testCreateLogWriter() {
        System.out.println("createLogWriter");
        String level = "info";
        String path = System.getProperty("java.io.tmpdir");
        LogWriter result = LogWriter.createLogWriter(level, path);
        assertNotNull(result);
    }

    /**
     * Test of getDefaultValues method, of class LogWriter.
     */
    public void testGetDefaultValues() {
        System.out.println("getDefaultValues");
        String level = "info";
        String path = System.getProperty("java.io.tmpdir");
        System.setProperty(LogWriter.LOG_LEVEL, level);
        System.setProperty(LogWriter.LOG_PATH, path);
        String[] expResult = new String[]{level, path};
        String[] result = LogWriter.getDefaultValues();
        assertEquals(expResult.length, result.length);
        for (int i = 0; i < result.length; i++) {
            assertEquals(expResult[i], result[i]);
        }
    }

    /**
     * Test of isActive method, of class LogWriter.
     */
    public void testIsActive() {
        System.out.println("isActive");
        LogWriter instance = LogWriter.createLogWriter("inactive", System.getProperty("java.io.tmpdir"));
        boolean expResult = true;
        boolean result = instance.isActive();
        assertEquals(expResult, result);
        instance = LogWriter.createLogWriter("info", System.getProperty("java.io.tmpdir"));
        expResult = false;
        result = instance.isActive();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLogFile method, of class LogWriter.
     */
    public void testGetLogFile() {
        System.out.println("getLogFile");
        LogWriter instance = LogWriter.getLogWriter();
        String result = instance.getLogFile();
        assertNotNull(result);
    }

    /**
     * Test of getLogStream method, of class LogWriter.
     */
    public void testGetLogStream() {
        System.out.println("getLogStream");
        int logLevel = LogWriter.LEVEL_ALL;
        LogWriter instance = LogWriter.getLogWriter();
        OutputStream result = instance.getLogStream(logLevel);
        assertNotNull(result);
    }

    /**
     * Test of clearLogFile method, of class LogWriter.
     */
    public void testClearLogFile() {
        System.out.println("clearLogFile");
        LogWriter instance = LogWriter.getLogWriter();
        instance.clearLogFile();
        // how to test?
    }

    /**
     * Test of log method, of class LogWriter.
     */
    public void testLog() {
        System.out.println("log");
        int level = 0;
        String message = "This is a message";
        LogWriter instance = LogWriter.getLogWriter();
        instance.log(level, message);
        // how to test?
    }

    /**
     * Test of printStackTrace method, of class LogWriter.
     */
    public void testPrintStackTrace_Throwable() {
        System.out.println("printStackTrace");
        Throwable t = new Throwable("A test Throwable");
        LogWriter instance = LogWriter.getLogWriter();
        instance.printStackTrace(t);
        // how to test?
    }

    /**
     * Test of printStackTrace method, of class LogWriter.
     */
    public void testPrintStackTrace_String_int() {
        System.out.println("printStackTrace");
        String message = "Hello World.";
        int depth = 0;
        LogWriter instance = LogWriter.getLogWriter();
        instance.printStackTrace(message, depth);
    }

}
