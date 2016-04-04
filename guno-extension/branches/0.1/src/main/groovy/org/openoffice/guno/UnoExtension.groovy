/*
 * *************************************************************
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

package org.openoffice.guno

/**
 *
 * @author Carl Marcum - CodeBuilders.net
 */

import com.sun.star.beans.XPropertySet
import com.sun.star.container.XIndexAccess
import com.sun.star.frame.XComponentLoader
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.uno.UnoRuntime
import com.sun.star.uno.XComponentContext

class UnoExtension {

    /**
     * Returns the component loader.
     * @return XComponentLoader interface.
     */
    static XComponentLoader getComponentLoader(final XComponentContext self) {

        XMultiComponentFactory mxRemoteServiceManager = null
        XComponentLoader aLoader = null

        mxRemoteServiceManager = self.getServiceManager()
        aLoader = UnoRuntime.queryInterface(
                XComponentLoader.class, mxRemoteServiceManager.createInstanceWithContext(
                "com.sun.star.frame.Desktop", self))

        return aLoader
    }

    /**
     * Returns the queried object.
     * @param clazz the object type to return.
     * @return Object the requested object.
     */
    static Object guno(final Object self, Class clazz) {
        UnoRuntime.queryInterface(clazz, self)
    }

    /**
     * Gets the value of a property.
     * @param name the property name to return the value of.
     * @return Object the property value.
     */
    static Object getAt(final XPropertySet self, String name) {
        self.getPropertyValue(name)
    }

    /**
     * Sets the value of a property.
     * @param name the property name.
     * @param value the value to set.
     */
    static void putAt(final XPropertySet self, String name, Object value) {
        self.setPropertyValue(name, value)
    }

    /**
     * Provides access to the elements of a collection through an index.
     * @param index specifies the position in the array. The first index is 0.
     * @return Object the element at the specified index.
     */
    static Object getAt(final XIndexAccess self, int index) {
        self.getByIndex(index)
    }

}
