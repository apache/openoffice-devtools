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
package org.openoffice.extensions.projecttemplates.actions.panel;

import java.util.Vector;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;

/**
 *
 * @author sg128468
 */
public class GenericDescriptionProperty<T> {

    private Vector<T> m_props;
    private Vector<String> m_locales;
    private String m_name;
    
    public GenericDescriptionProperty(String name) {
        m_props = new Vector<T>();
        m_locales = new Vector<String>();
        m_name = name;
    }
    
    public String getName() {
        return m_name;
    }
    
    public int getSize() {
        return m_locales.size();
    }

    public T getProperty(int index) throws ArrayIndexOutOfBoundsException {
        return m_props.get(index);
    }
    
    public void setProperty(int index, T element) throws ArrayIndexOutOfBoundsException {
        m_props.set(index, element);
    }
    
    public T getPropertyForLocale(String locale) {
        // make sure locale is in short form
        locale = LanguageDefinition.getLanguageShortNameForName(locale);
        for (int i = 0; i<m_locales.size(); i++) {
            String string = m_locales.get(i);
            if (string != null && string.equals(locale)) {
               return m_props.get(i); 
            }
        }
        return null;
    }
    
    public void setPropertyAndLocale(String locale, T property) {
        int index = -1;
        // make sure locale is in short form
        locale = LanguageDefinition.getLanguageShortNameForName(locale);
        if ((index = m_locales.indexOf(locale)) != -1) {
            m_props.setElementAt(property, index);
        }
        else {
            synchronized(this) {
                m_locales.add(locale);
                m_props.add(property);
            }
        }
    }
    
    public void deletePropertyAndLocale(int index) {
        synchronized(this) {
            m_locales.remove(index);
            m_props.remove(index);
        }
    }
    
    public void deletePropertyAndLocale(String locale) {
        // make sure locale is in short form
        locale = LanguageDefinition.getLanguageShortNameForName(locale);
        int index = m_locales.indexOf(locale);
        if(index != -1) {
            synchronized(this) {
                m_locales.remove(index);
                m_props.remove(index);
            }
        }
    }
    
    public String getShortLocale(int index) throws ArrayIndexOutOfBoundsException {
        return m_locales.get(index);
    }
    
    public String getLocale(int index) throws ArrayIndexOutOfBoundsException {
        int id = LanguageDefinition.getLanguageIdForName(m_locales.get(index));
        return LanguageDefinition.getLanguageNameForId(id);
    }
    
    public String getDefaultShortLocale() {
        if (m_locales.size() > 0) {
            return m_locales.get(0);
        }
        return null;
    }

    public String[] getAllLocales() {
        return m_locales.toArray(new String[m_locales.size()]);
    }
    
    public T getDefaultProperty() {
        if (m_locales.size() > 0) {
            return m_props.get(0);
        }
        return null;
    }
    
    public void setDefaultLocale(String localeShortName) {
        if (m_locales.size() == 0) return; // fast exit
        String oldDefaultLocale = m_locales.get(0);
        if (!localeShortName.equals(oldDefaultLocale)) {
            int index = m_locales.indexOf(localeShortName);
            if (index >= 0) {
                T oldDefaultProp = m_props.get(0);
                T newDefaultProp = getPropertyForLocale(localeShortName);
                synchronized(this) {
                    m_locales.set(index, oldDefaultLocale);
                    m_props.set(index, oldDefaultProp);
                    m_locales.set(0, localeShortName);
                    m_props.set(0, newDefaultProp);
                }
            }
        }
    }

    public void clear() {
        m_locales.clear();
        m_props.clear();
    }
}
