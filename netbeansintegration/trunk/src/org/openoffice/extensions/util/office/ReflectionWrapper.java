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

package org.openoffice.extensions.util.office;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 *
 */
public class ReflectionWrapper {
    ClassLoader classLoader;
    
    
    protected ReflectionWrapper(Vector<File> repository) {
        classLoader = JarClassLoader.getClassLoader(repository);
    }
    
    public Class forName(String className) throws ClassNotFoundException {
        return classLoader.loadClass(className);
    }

    public Object callConstructor(String className, Object[]param) 
            throws ClassNotFoundException, NoSuchMethodException, 
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Object constructedObject = null;
        Class<?> newClass = classLoader.loadClass(className);
        Class[]ctorParamTypes = getClassTypesFromParameters(param);
        Constructor ctor = newClass.getConstructor(ctorParamTypes);
        constructedObject = ctor.newInstance(param);
        return constructedObject;
    }
    
    public Object executeMethod(Object callObject, 
            String methodName, Object[]param, Class[]paramTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object methodResult = null;
        Class<?> clazz = callObject.getClass();
        Method method = clazz.getMethod(methodName, paramTypes);
        methodResult = method.invoke(callObject, param);
        return methodResult;
    }

    public Object executeMethod(Object callObject, 
            String methodName, Object[]param) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class[]paramTypes = getClassTypesFromParameters(param);
        return executeMethod(callObject, methodName, param, paramTypes);
    }

    public boolean executeBooleanMethod(Object callObject, 
            String methodName, Object[]param) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object retValue = executeMethod(callObject, methodName, param);
        return ((Boolean)retValue).booleanValue();
    }
    
    public Object getArray(String className, 
            int[] dimensions) throws ClassNotFoundException {
        Object arrayObject = null;
        arrayObject = Array.newInstance(classLoader.loadClass(className), dimensions);
        return arrayObject;
    }
    
    public void setArrayValue(Object arrayType, int index, Object value) {
        Array.set(arrayType, index, value);
    }

    public Object getArrayValue(Object arrayType, int index) {
        return Array.get(arrayType, index);
    }

    public Object executeStaticMethod(String className, 
            String methodName, Object[]param) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object methodResult = null;
        Class<?> clazz = classLoader.loadClass(className);
        Class[]methodParamTypes = getClassTypesFromParameters(param);
        Method method = clazz.getMethod(methodName, methodParamTypes);
        methodResult = method.invoke(clazz, param);
        return methodResult;
    }

    public Object getPublicField(Object callObject, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Object fieldResult = null;
        Class clazz = callObject.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        fieldResult = field.get(callObject);
        return fieldResult;
    }
    
    public Object getStaticField(String className, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Object fieldResult = null;
        Class clazz = classLoader.loadClass(className);
        Field field = clazz.getDeclaredField(fieldName);
        fieldResult = field.get(null);
        return fieldResult;
    }
    
    private Class[] getClassTypesFromParameters(Object[] param) {
        if (param == null) return new Class[0];
        Class[]classParamTypes = new Class[param.length];
        for (int i=0; i<param.length; i++) {
            Class c = param[i].getClass();
            // for proxy objects we take the object class
            if (c.getName().indexOf("$Proxy") != -1) { // NOI18N
                c = Object.class;
            }
            classParamTypes[i] = c;
        }
        return classParamTypes;
    }
}
