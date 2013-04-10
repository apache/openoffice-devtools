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

package org.openoffice.extensions.util.typebrowser.logic;

/**
 * Just a container to hold some static names.
 */
// TODO: rename and make an interface?
public interface UnoTypes {
    
    public static final String[] SIMPLE_TYPE_TAGS = new String[] {
        "void", "boolean", "byte", "char", "short", "int", 
        "long", "float", "double", "String", "Object"
    }; // NOI18N

    public static final int SIMPLE_TYPE = 0;
    public static final int ANY_TYPE = 1;
    public static final int ENUM_TYPE = 2;
    public static final int STRUCT_TYPE = 3;
    public static final int EXCEPTION_TYPE = 4;
    public static final int INTERFACE_TYPE = 5;
    public static final int SERVICE_TYPE = 6;
    public static final int MODULE_TYPE = 7;
    public static final int CONSTANTS_TYPE = 8;
    public static final int SINGLETON_TYPE = 9;
    public static final int ERROR_TYPE = 10;
    public static final int POLY_STRUCT_TYPE = 11;
    public static final int OWN_DESIGN_TYPE = 128;
    
    /** reflecting a simple type: this is no UNO type!!
    */
	public static final String SIMPLE = "simple"; // NOI18N
    /** reflecting the any type; anys can carry any UNO value except of any values
    */
	public static final String ANY = "ANY"; // NOI18N
    /** reflecting enum types
    */
	public static final String ENUM = "ENUM"; // NOI18N
    /** reflecting compound types
    */
	public static final String STRUCT = "STRUCT"; // NOI18N
    /** reflecting exception types
    */
	public static final String EXCEPTION = "EXCEPTION"; // NOI18N
    /** reflecting interface types
    */
	public static final String INTERFACE = "INTERFACE"; // NOI18N
    /** reflecting services
    */
	public static final String SERVICE = "SERVICE"; // NOI18N
    /** reflecting modules
    */
	public static final String MODULE = "MODULE"; // NOI18N
    /** reflecting constants groups
    */
    public static final String CONSTANTS = "CONSTANTS"; // NOI18N
    /** reflecting singletons
    */
    public static final String SINGLETON = "SINGLETON"; // NOI18N
    
}
