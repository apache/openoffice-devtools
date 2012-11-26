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

package org.openoffice.extensions.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sg128468
 */
public class ConfigurationSettingsTest extends TestCase {
    
    public ConfigurationSettingsTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ConfigurationSettingsTest.class);
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
     * Test of getSettings method, of class ConfigurationSettings.
     */
    public void testGetSettings() {
        System.out.println("getSettings");
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        assertNotNull(instance);
        String office = instance.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION);
        String sdk = instance.getValue(ConfigurationSettings.KEY_SDK_INSTALLATION);
        assertEquals("", sdk);
        assertEquals("", office);
    }

    /**
     * Test of store method, of class ConfigurationSettings.
     */
    public void testStore() {
        System.out.println("store");
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        instance.store();
        // how to test?
        
    }

    /**
     * Test of load method, of class ConfigurationSettings.
     */
    public void testLoad() {
        System.out.println("load");
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        instance.load();
        // how to test?
    }

    /**
     * Test of getValue method, of class ConfigurationSettings.
     */
    public void testGetValue() {
        System.out.println("getValue");
        String key = "OneKey";
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        String result = instance.getValue(key);
        assertNull(result);
        String expResult = "A Value";
        instance.setValue(key, "A Value");
        result = instance.getValue(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of setValue method, of class ConfigurationSettings.
     */
    public void testSetValue() {
        System.out.println("setValue");
        String key = "key";
        String value = "value";
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        instance.setValue(key, value);
    }

    /**
     * Test of getLibraryName method, of class ConfigurationSettings.
     */
    public void testGetLibraryName() {
        System.out.println("getLibraryName");
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        Properties props = TestProperties.getTestProperties();
        String office = props.getProperty("OfficeInstallation");
        String sdk = props.getProperty("SDKInstallation");
        instance.setValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION, office);
        instance.setValue(ConfigurationSettings.KEY_SDK_INSTALLATION, sdk);
        String result = instance.getLibraryName();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of getUser method, of class ConfigurationSettings.
     */
    public void testGetUser() {
        System.out.println("getUser");
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        String expResult = System.getProperty ("user.name");
        String result = instance.getUser();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimeStamp method, of class ConfigurationSettings.
     */
    public void testGetTimeStamp() {
        System.out.println("getTimeStamp");
        String result = ConfigurationSettings.getTimeStamp();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of registerPropertyChangeListener method, of class ConfigurationSettings.
     */
    public void testRegisterPropertyChangeListener() {
        System.out.println("registerPropertyChangeListener");
        String propertyName = "prop";
        PropertyChangeListener listener = new PropertyChangeListener() {
            String val;
            public void propertyChange(PropertyChangeEvent evt) {
                val = evt.getNewValue().toString();
            }
            @Override
            public String toString() {
                return val;
            }
        };
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        instance.registerPropertyChangeListener(propertyName, listener);
        instance.setValue(propertyName, "a value");
        assertEquals("a value", listener.toString());
    }

    /**
     * Test of removePropertyChangeListener method, of class ConfigurationSettings.
     */
    public void testRemovePropertyChangeListener() {
        System.out.println("removePropertyChangeListener");
        String propertyName = "prop";
        PropertyChangeListener listener = new PropertyChangeListener() {
            String val;
            public void propertyChange(PropertyChangeEvent evt) {
                val = evt.getNewValue().toString();
            }
            @Override
            public String toString() {
                return val;
            }
        };
        ConfigurationSettings instance = ConfigurationSettings.getSettings();
        instance.registerPropertyChangeListener(propertyName, listener);
        instance.removePropertyChangeListener(listener);
        instance.setValue(propertyName, "a value");
        assertNull(listener.toString());
    }

}
