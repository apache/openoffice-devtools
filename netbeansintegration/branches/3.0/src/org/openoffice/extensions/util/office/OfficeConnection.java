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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.config.LibraryManager;
import org.openoffice.extensions.config.office.OpenOfficeLocation;
import org.openoffice.extensions.config.office.PlatformInfo;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.typebrowser.logic.UnoTypes;

/**
 *
 * @author sg128468
 */
public class OfficeConnection {
    
    // TODO: this class was conceived with storing the Office and SDK location
    // inside of a project - but it's stored in NB settings now. Review the class.
    
    private static OfficeConnection theOfficeConnection = null;
    
    public static Object getComponentContext() {
        if (theOfficeConnection == null) {
            theOfficeConnection = new OfficeConnection();
        }
        else {  // acess a different office?
            ConfigurationSettings settings = ConfigurationSettings.getSettings();
            String path = theOfficeConnection.mOfficeProgramPath;
            String settingsPath = settings.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION);
            if (!path.equals(settingsPath)) {
                theOfficeConnection.connect();
            }
        }
        return theOfficeConnection.getContext();
    }

    public static void disposeContext() {
        if (theOfficeConnection != null) {
            theOfficeConnection.disconnect();
        }
    }
    
    public static ReflectionWrapper getReflectionWrapper() {
        if (theOfficeConnection == null) {
            getComponentContext();
        }
        return theOfficeConnection.mWrapper;
    }
    
    String mUnorcName;
    String mUnorcPath;
    Object mxCtx;
    String mOfficeProgramPath;
    ReflectionWrapper mWrapper;
    boolean mLocalConnection;    
    boolean mFirstConnection;    
    
    /** Creates a new instance of OfficeConnection */
    private OfficeConnection() {
        mUnorcName = PlatformInfo.getUnorcName();
        mFirstConnection = true;
        connect();
    }
    
    private Object getContext() {
        if (mxCtx == null) {
            connect();
        }
        else {  // check if Office still alive!
            Object sManager = null;
            Exception e = null;
            try {
                sManager = mWrapper.executeMethod(
                    mxCtx, "getServiceManager", new Object[0]); // NOI18N
            } catch (InvocationTargetException ex) {
                e = ex;
            } catch (NoSuchMethodException ex) {
                e = ex;
            } catch (IllegalAccessException ex) {
                e = ex;
            }
            finally {
                if (e != null) {
                    String message = e.getMessage()!=null?e.getMessage():""; // NOI18N
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, 
                        "Office not alive: ".concat(message)); // NOI18N
                }
                if (sManager == null) {
                    setLocalVariablesNull();
                    connect();
                }
            }
        }
        return mxCtx;
    }
    
    private void connect() {
        disconnect();  // avoid reconecting the office: dispose first
        Vector<File> repository = createRepository();
        mWrapper = new ReflectionWrapper(repository);
        if (mLocalConnection || mFirstConnection) {
            mFirstConnection = false;
            java.util.Hashtable<String,String> htBootParams = new java.util.Hashtable<String,String>();

            // bootstrap simple uno if that's possible
            htBootParams.put("SYSBINDIR", mOfficeProgramPath); // NOI18N
            try {
                // unused code.
//                String targetUnorcPath = System.getProperty("java.io.tmpdir") + File.separator + mUnorcName; // NOI18N
//                if (TypeBrowser.hasTempRdbFileName()) {
//                    // copy unorc and add the own rdb file
//                    File source = new File(mUnorcPath);
//                    File target = new File(targetUnorcPath);
//                    try {
//                        BufferedReader srcReader = new BufferedReader(new FileReader(source));
//                        FileWriter trgtWriter = new FileWriter(target);
//                        try {
//                            char[] charBuff = new char[256];
//                            while(srcReader.ready()) {
//                                srcReader.read(charBuff);
//                                trgtWriter.write(charBuff, 0, charBuff.length);
//                            }
//                        }
//                        catch (java.io.IOException e) {
//                            LogWriter.getLogWriter().printStackTrace(e);   
//                        }
//                        finally {
//                            srcReader.close();
//                            trgtWriter.close();
//                        }
//                    }
//                    catch (java.io.IOException e) {
//                        LogWriter.getLogWriter().printStackTrace(e);   
//                    }
//                }
                mLocalConnection = false;
                mxCtx = mWrapper.executeStaticMethod(
                        "com.sun.star.comp.helper.Bootstrap", 
                        "defaultBootstrap_InitialComponentContext", 
                        new Object[]{mUnorcPath, htBootParams}); // NOI18N
                if (mxCtx != null) {
                    mLocalConnection = true;
                }
            } catch (NoSuchMethodException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }    
        for (int i=0; mxCtx == null && i<10; i++) { // if bootstrapping of a single uno does not work we boot an Office
            try {
                mxCtx = mWrapper.executeStaticMethod(
                        "com.sun.star.comp.helper.Bootstrap", "bootstrap", null); // NOI18N
            } catch (NoSuchMethodException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "### incvocation target"); // NOI18N
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    LogWriter.getLogWriter().printStackTrace(e);
                }
            } catch (IllegalAccessException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    private Vector<File> createRepository() {
        ConfigurationSettings settings = ConfigurationSettings.getSettings();
        String officeInstallation = settings.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION);
        mOfficeProgramPath = officeInstallation.concat(File.separator).concat(PlatformInfo.getOfficeProgramDir());
                
        Vector<File> repository = new Vector<File>();
        // add program path, so resource for soffice binary is found
        repository.add(new File(mOfficeProgramPath));
        OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation();
        if (loc != null) {
            mUnorcPath = loc.getUnorcPath().concat(File.separator).concat(mUnorcName);
            // add juh, jurt, java_uno, jut, unoil, ridl
            for (int i = 0; i < LibraryManager.JAR_LIBRARIES_ESSENTIAL.length; i++) {
                String jarName = LibraryManager.JAR_LIBRARIES_ESSENTIAL[i];
                repository.add(new File(loc.getFullPathForJar(jarName)));
            }
        }
        return repository;
    }

    private void setLocalVariablesNull() {
        mxCtx = null;
        mOfficeProgramPath = ""; // NOI18N
        mWrapper = null;
    }
    
    private void disconnect() {
        if (mLocalConnection) {
            setLocalVariablesNull();
        }
        else {
            if (mxCtx != null) {
                try {
                    Object xMgr = mWrapper.executeMethod(mxCtx, "getServiceManager", new Object[0]); // NOI18N
                    Class classType = mWrapper.forName("com.sun.star.uno.XComponentContext"); // NOI18N
                    Object desk = mWrapper.executeMethod(xMgr, "createInstanceWithContext",  // NOI18N
                            new Object[]{"com.sun.star.frame.Desktop", mxCtx}, // NOI18N
                            new Class[]{String.class, classType});

                    classType = mWrapper.forName("com.sun.star.frame.XDesktop"); // NOI18N
                    Object aDesktop = mWrapper.executeStaticMethod("com.sun.star.uno.AnyConverter", "toObject", new Object[]{classType, desk}); // NOI18N
                    mWrapper.executeMethod(aDesktop, "terminate", null); // NOI18N
                } catch (InvocationTargetException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } catch (IllegalAccessException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } catch (NoSuchMethodException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } catch (ClassNotFoundException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
                finally {  // reset all variables
                    setLocalVariablesNull();
                }
            }
        }
    }
}
