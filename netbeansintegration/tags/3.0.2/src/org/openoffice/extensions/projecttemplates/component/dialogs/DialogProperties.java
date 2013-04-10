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

package org.openoffice.extensions.projecttemplates.component.dialogs;

import java.util.HashMap;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author sg128468
 */
public class DialogProperties {

    public final static String EDIT = "EDIT";  // edit or new // NOI18N
    public final static String NODE = "NODE";  // the node for editing; null when new // NOI18N
    public final static String PKG = "PKG";    // the initial package suggestion // NOI18N
    public final static String SRV = "SRV";    // the initial service name suggestion // NOI18N
    public final static String IFC = "IFC";    // the initial interface suggestion // NOI18N
    public final static String NAME = "NAME";    // the initial name suggestion // NOI18N
    public final static String ALLOW_SELECTION = "ALLOW_SELECTION";  // allow selection of the type: no for wizard // NOI18N
    public final static String SELECTION = "SELECTION";  // selected item as String[]: if no selection allowed, only item that's created or edited // NOI18N
    public final static String PANEL = "PANEL";          // the panel to trigger updates // NOI18N
    public final static String DATA_TYPE = "TYPE";  // a DataType integer type: mandatory // NOI18N
    
    private HashMap<String, OneDataType> properties;
    
    /** Creates a new instance of DialogProperties */
    public DialogProperties() {
        this(new OneDataType[0]);
    }

    /** Creates a new instance of DialogProperties with properties*/
    public DialogProperties(OneDataType[]types) {
        properties = new HashMap<String, OneDataType>();
        // default props, may be overwritten
        setDefaultProps();
        for (int i=0; i<types.length; i++) {
            properties.put(types[i].n, types[i]);
        }
        // plausibility check for dependent variables
        if (getProperty(NODE) != null) {
            // whith a node, we edit
            properties.put(EDIT, new OneDataType<Boolean>(EDIT, Boolean.TRUE));
            properties.put(ALLOW_SELECTION, new OneDataType<Boolean>(ALLOW_SELECTION, Boolean.FALSE));
        }
        // consistency: if edit, then no selection allowed!
        else if (getBooleanProperty(EDIT)) {
            properties.put(ALLOW_SELECTION, new OneDataType<Boolean>(ALLOW_SELECTION, Boolean.FALSE));
            if (getProperty(NODE) == null) {  // this is critical
                LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "NODE is not set for editing it."); // NOI18N
            }
        }
        if (getProperty(DATA_TYPE) == null) {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "No data type set for the plugged panel in NewDataTypeBaseDialog."); // NOI18N
        }
    }

    public Object getProperty(String key) {
        OneDataType type = properties.get(key);
        if (type == null) return null;
        return type.o;
    }
    
    public boolean getBooleanProperty(String key) {
        @SuppressWarnings( value="unchecked" )
        OneDataType<Boolean> b = properties.get(key);
        return b.o.booleanValue();
    }
    
    public int getIntProperty(String key) {
        @SuppressWarnings( value="unchecked" )
        OneDataType<Integer> b = properties.get(key);
        return b.o.intValue();
    }
    
    public String getStringProperty(String key) {
        @SuppressWarnings( value="unchecked" )
        OneDataType<String> s = properties.get(key);
        if (s == null) return null;
        return s.o;
    }
    
    public void setProperty(OneDataType value) {
        properties.put(value.n, value);
    }
    
    private void setDefaultProps() {
        // no selection allowed
        properties.put(ALLOW_SELECTION, new OneDataType<Boolean>(ALLOW_SELECTION, Boolean.FALSE));
        // set string as "null pointer exception" precaution: should be overwritten
        properties.put(SELECTION, new OneDataType<String[]>(SELECTION, new String[]{""})); // NOI18N
        // default is create, not edit
        properties.put(EDIT, new OneDataType<Boolean>(EDIT, Boolean.FALSE));
    }
    
    public static class OneDataType<T> {
        public OneDataType(String name, T object) {
            n = name;
            o = object;
        }
        protected T o;
        protected String n;
    }
}
