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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sg128468
 */
public class TestProperties {

    public static Properties getTestProperties() {
        Properties props = new Properties();
        TestProperties testProps = new TestProperties();
        InputStream inStream = testProps.getClass().getClassLoader().getResourceAsStream(
            "org/openoffice/extensions/config/Test.properties");
        System.out.println("Stream " + inStream);
        try {
            props.load(inStream);
            if (inStream != null) {
                inStream.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(TestProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
    
}
