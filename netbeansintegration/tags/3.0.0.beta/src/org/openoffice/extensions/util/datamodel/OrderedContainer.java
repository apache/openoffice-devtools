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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.openoffice.extensions.util.LogWriter;

public class OrderedContainer<T> {
    private Hashtable<String,T> objects;
    private Vector<String> order;
    public OrderedContainer() {
        objects = new Hashtable<String,T>();
        order = new Vector<String>();
    }

    public void put(String key, T o) {
        order.remove(key);
        order.add(key);
        objects.put(key, o);
    }

    public void insertElementAt(String key, T o, int position) {
        order.remove(key);
        order.insertElementAt(key, position);
        objects.put(key, o);
    }

    public T get(Object name) {
        return objects.get(name);
    }

    public T get(int position) {
        return objects.get(order.get(position));
    }

    public String getKeyFromObject(T o) {
        int pos = getPositionFromObject(o);
        return order.get(pos);
    }
    
    public Enumeration<String> keys() {
        return order.elements();
    }

    public Collection<T> values() {
        return valuesInOrder();
    }
    
    public T remove(String key) {
        T o = objects.remove(key);
        order.remove(key);
        return o;
    }

    public int getPositionFromKey(String key) {
        return order.indexOf(key);
    }

    public int size() {
        return order.size();
    }
    
    public int getPositionFromObject(T o) {
        Enumeration<String> keys = objects.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (objects.get(key).equals(o)) {
                return getPositionFromKey(key);
            }
        }
        return -1;  // analog to Vector.indexof() which also returns -1
    }

    public String[] getKeysInOrder() {
        String[] keys = new String[order.size()];
        return order.toArray(keys);
    }
    
    public Collection<T> valuesInOrder() {
        Vector<T> v = new Vector<T>();
        for (Enumeration<String> e = order.elements(); e.hasMoreElements();) {
            v.add(objects.get(e.nextElement()));
        }
        return v;
    } 
    
    public String getNextNumber(String prefix) {
        int number = 0;
        Vector<String> names = new Vector<String>();
        int namePrefixLength = prefix.length();
        for (Iterator<String> it = order.iterator(); it.hasNext();) {
            try {
                int n = Integer.parseInt(it.next().substring(namePrefixLength));
                if (number <= n) {
                    number = n + 1;
                }
            }
            catch (NumberFormatException ex) {
                // uncritical exception
                LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
            }
        }
        return String.valueOf(number);
    }
}
