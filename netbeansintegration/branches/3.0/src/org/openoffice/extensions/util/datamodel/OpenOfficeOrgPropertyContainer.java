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

import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 * @author sg128468
 */
public interface OpenOfficeOrgPropertyContainer {

    String[] getAllPropertyNames();

    OpenOfficeOrgProperty getProperty(String propertyName) throws UnknownOpenOfficeOrgPropertyException;

    void setProperty(String propertyName, OpenOfficeOrgProperty value) throws UnknownOpenOfficeOrgPropertyException;

    String getSimpleProperty(String propertyName) throws UnknownOpenOfficeOrgPropertyException;

    void setSimpleProperty(String propertyName, String value) throws UnknownOpenOfficeOrgPropertyException;

    String getLocalizedProperty(String propertyName, int languageID) throws UnknownOpenOfficeOrgPropertyException, UnknownOpenOfficeOrgLanguageIDException;

    boolean isPropertyLocalized(String propertyName) throws UnknownOpenOfficeOrgPropertyException;

    void setLocalizedProperty(String propertyName, int languageID, String value) throws UnknownOpenOfficeOrgLanguageIDException;
    
}
