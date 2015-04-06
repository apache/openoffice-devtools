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

package org.openoffice.extensions.util.datamodel;

import junit.framework.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class OrderedContainerTest extends TestCase {
    
    public OrderedContainerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(OrderedContainerTest.class);
        
        return suite;
    }

    /**
     * Test of put method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testPut() {
        System.out.println("put");
        
        String key = "key";
        String expResult = "a value";
        OrderedContainer<String> instance = new OrderedContainer<String>();
        
        instance.put(key, expResult);

        String result = instance.get(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of insertElementAt method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testInsertElementAt() {
        System.out.println("insertElementAt");
        
        String key1 = "key1";
        String key2 = "key2";
        String value1 = "value1";
        String value2 = "value2";
        int position = 0;
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        
        instance.insertElementAt(key1, value1, position);
        instance.insertElementAt(key2, value2, position);
        
        int result = instance.getPositionFromKey(key2);
        assertEquals(position, result);

        result = instance.getPositionFromKey(key1);
        assertEquals(position + 1, result);
    }

    /**
     * Test of get method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testGet() {
        System.out.println("get");
        
        String key = "key";
        String expResult = "a value";
        OrderedContainer<String> instance = new OrderedContainer<String>();
        
        instance.put(key, expResult);

        String result = instance.get(key);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNameFromObject method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testGetNameFromObject() {
        System.out.println("getNameFromObject");
        
        String expResult = "key";
        String value = "a value";
        OrderedContainer<String> instance = new OrderedContainer<String>();
        
        instance.put(expResult, value);
        String result = instance.getKeyFromObject(value);

        assertEquals(expResult, result);
    }

    /**
     * Test of keys method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testKeys() {
        System.out.println("keys");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        
        final String[] keys = new String[]{"key1", "key2"};
        instance.put(keys[0], "value1");
        instance.put(keys[1], "value2");

        Enumeration<String> result = instance.keys();

        int index = 0;
        while (result.hasMoreElements()) {
            assertEquals(keys[index++], result.nextElement());
        }
    }

    /**
     * Test of values method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testValues() {
        System.out.println("values");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();

        final String[] value = new String[]{"value1", "value2"};
        instance.put("key0", value[0]);
        instance.put("key1", value[1]);
        
        Collection<String> result = instance.values();
        int index = 0;
        for (Iterator<String> it = result.iterator(); it.hasNext();) {
            String resultElem = it.next();
            assertEquals(value[index++], resultElem);
        }
    }

    /**
     * Test of remove method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testRemove() {
        System.out.println("remove");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        instance.put("key0", "value0");
        instance.put("key1", "value1");
        
        String expResult = null;
        String result = instance.remove("key0");
        assertEquals("value0", result);
        result = instance.get("key0");
        assertNull(result);
        result = instance.get("key1");
        assertEquals("value1", result);
    }

    /**
     * Test of getPositionFromKey method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testGetPositionFromKey() {
        System.out.println("getPositionFromKey");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        instance.put("key1", "value0");
        instance.put("key3", "value1");

        instance.insertElementAt("key2", "value2", 1);
        for (int expResult = 1; expResult < 4; expResult++) {
            int result = instance.getPositionFromKey("key" + expResult);
            assertEquals(expResult - 1, result);
        }
        instance.insertElementAt("key0", "value3", 0);
        for (int expResult = 0; expResult < 4; expResult++) {
            int result = instance.getPositionFromKey("key" + expResult);
            assertEquals(expResult, result);
        }
    }

    /**
     * Test of size method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testSize() {
        System.out.println("size");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        for (int i = 0; i < 255; i++) {
            instance.put("key" + i, "value" + i);
        }
        
        int expResult = 255;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPositionFromObject method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testGetPositionFromObject() {
        System.out.println("getPositionFromObject");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        instance.put("key0", "value0");
        instance.put("key1", "value1");
        
        int expResult = 0;
        int result = instance.getPositionFromObject("value0");
        assertEquals(expResult, result);

        expResult = 1;
        result = instance.getPositionFromObject("value1");
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeysInOrder method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testGetKeysInOrder() {
        System.out.println("getKeysInOrder");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        String[] keys = new String[]{"key0", "key1", "key2"};
        instance.put(keys[0], "value0");
        instance.put(keys[1], "value1");
        instance.put(keys[2], "value2");
        
        String[] result = instance.getKeysInOrder();
        for (int i = 0; i < result.length; i++) {
            assertEquals(keys[i], result[i]);
        }
    }

    /**
     * Test of valuesInOrder method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testValuesInOrder() {
        System.out.println("valuesInOrder");
        
        OrderedContainer<String> instance = new OrderedContainer<String>();
        String[] values = new String[]{"value0", "value0", "value0"};
        instance.put("key0", values[0]);
        instance.put("key1", values[0]);
        instance.put("key2", values[0]);
        
        Collection<String> result = instance.valuesInOrder();
        int index = 0;
        for (Iterator<String> it = result.iterator(); it.hasNext();) {
            String elem = it.next();
            assertEquals(elem, values[index++]);
        }
    }

    /**
     * Test of getNextNumber method, of class org.openoffice.extensions.util.datamodel.OrderedContainer.
     */
    public void testGetNextNumber() {
        System.out.println("getNextNumber");
        
        String prefix = "key";
        OrderedContainer<String> instance = new OrderedContainer<String>();
        String[] keys = new String[]{"key0", "key1", "key2"};
        instance.put(keys[0], "value0");
        instance.put(keys[1], "value1");
        instance.put(keys[2], "value2");
        
        String expResult = "3";
        String result = instance.getNextNumber(prefix);
        assertEquals(expResult, result);
    }
    
}
