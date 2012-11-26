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
package org.openoffice.extensions.util;

/**
 *
 * @author sg128468
 */
public class PathExchanger {
    
    /** Creates a new instance of PathExchanger:
     * not allowed to use.
     */
    private PathExchanger() {
    }
    
    public static String pathToOfficeFileUrl(String path) {
        String retString = path.replace('\\', '/').replaceAll(" ", "%20"); // NOI18N
        if (!retString.startsWith("file:")) { // NOI18N
            if (retString.charAt(0) == '/') {
                retString = "file://".concat(retString); // NOI18N
            }
            else {
                retString = "file:///".concat(retString); // NOI18N
            }
        }
        return retString;
    }
}
