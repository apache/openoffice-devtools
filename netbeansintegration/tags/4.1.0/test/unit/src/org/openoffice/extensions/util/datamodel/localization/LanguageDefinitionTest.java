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

package org.openoffice.extensions.util.datamodel.localization;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author sg128468
 */
public class LanguageDefinitionTest extends TestCase {
    
    public LanguageDefinitionTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getLanguages method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguages() {
        System.out.println("getLanguages");
        
        String[] result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguages();
        assertNotNull(result);

        String[] result2 = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguagesShortName();
        assertEquals(result2.length, result.length);
    }

    /**
     * Test of getLanguagesShortName method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguagesShortName() {
        System.out.println("getLanguagesShortName");
        
        String[] result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguagesShortName();
        assertNotNull(result);
    }

    /**
     * Test of getLanguageIdForName method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguageIdForName() {
        System.out.println("getLanguageIdForName");
        
        String languageName = "English(en)";
        int expResult = 15;
        int result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForName(languageName);
        assertEquals(expResult, result);

        languageName = "Albanian(sq)";
        expResult = 0;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForName(languageName);
        assertEquals(expResult, result);
        
        languageName = "Welsh(cy)";
        expResult = 66;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForName(languageName);
        assertEquals(expResult, result);

        languageName = "Blabla";
        expResult = -1;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForName(languageName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getLanguageIdForShortName method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguageIdForShortName() {
        System.out.println("getLanguageIdForShortName");
        
        String shortName = "en";
        int expResult = 15;
        int result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForShortName(shortName);
        assertEquals(expResult, result);
        
        shortName = "sq";
        expResult = 0;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForShortName(shortName);
        assertEquals(expResult, result);
        
        shortName = "cy";
        expResult = 66;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForShortName(shortName);
        assertEquals(expResult, result);
        
        shortName = "blabla";
        expResult = -1;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageIdForShortName(shortName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getLanguageNameForId method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguageNameForId() {
        System.out.println("getLanguageNameForId");
        
        String expResult = "English(en)";
        int id = 15;
        String result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForId(id);
        assertEquals(expResult, result);

        expResult = "Albanian(sq)";
        id = 0;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForId(id);
        assertEquals(expResult, result);
        
        expResult = "Welsh(cy)";
        id = 66;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForId(id);
        assertEquals(expResult, result);

        id = -1;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForId(id);
        assertNull(result);
    }

    /**
     * Test of getLanguageShortNameForId method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguageShortNameForId() {
        System.out.println("getLanguageShortNameForId");
        
        String expResult = "en";
        int id = 15;
        String result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageShortNameForId(id);
        assertEquals(expResult, result);

        expResult = "sq";
        id = 0;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageShortNameForId(id);
        assertEquals(expResult, result);
        
        expResult = "cy";
        id = 66;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageShortNameForId(id);
        assertEquals(expResult, result);

        id = -1;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageShortNameForId(id);
        assertNull(result);
        
    }

    /**
     * Test of hasLanguage method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testHasLanguage() {
        System.out.println("hasLanguage");
        
        int langID = 25;
        boolean expResult = true;
        boolean result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.hasLanguage(langID);
        assertEquals(expResult, result);
        
        langID = -15;
        expResult = false;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.hasLanguage(langID);
        assertEquals(expResult, result);
        
        langID = 678;
        expResult = false;
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.hasLanguage(langID);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of getLanguageNameForShortName method, of class org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.
     */
    public void testGetLanguageNameForShortName() {
        System.out.println("getLanguageNameForShortName");
        
        String languageShortName = "en";
        String expResult = "English(en)";
        String result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForShortName(languageShortName);
        assertEquals(expResult, result);

        languageShortName = "sq";
        expResult = "Albanian(sq)";
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForShortName(languageShortName);
        assertEquals(expResult, result);
        
        languageShortName = "cy";
        expResult = "Welsh(cy)";
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForShortName(languageShortName);
        assertEquals(expResult, result);

        languageShortName = "blabla";
        result = org.openoffice.extensions.util.datamodel.localization.LanguageDefinition.getLanguageNameForShortName(languageShortName);
        assertNull(result);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(LanguageDefinitionTest.class);
        
        return suite;
    }
    
}
