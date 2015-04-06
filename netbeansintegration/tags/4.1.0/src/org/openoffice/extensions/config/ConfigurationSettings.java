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

package org.openoffice.extensions.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openoffice.extensions.config.office.OpenOfficeLocation;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.config.office.PlatformInfo;

/**
 *
 * @author jsc
 */
public class ConfigurationSettings {
    
    private FileObject settingsFolder;
    private FileObject settingsFile;
    private FileLock lock;
    private Properties settings;
    private LibraryManager libraryManager;
    private String libraryName; 
    private String userName;
    private ListenerContainer listenerContainer;
    
//    private static boolean firstInit = true;
    
    /** Configuration file base name*/
    public static final String PROPERTIES_BASE = "org-openoffice-extensions"; // NOI18N
    /** Configuration file extension*/
    public static final String PROPERTIES_EXTENSION = "xml"; // NOI18N
    /** Key under which the office installtion path is stored in the properties file*/
    public static final String KEY_OFFICE_INSTALLATION="office"; // NOI18N
    /** Key under which the SDK installtion path is stored in the properties file*/
    public static final String KEY_SDK_INSTALLATION="sdk"; // NOI18N
    /** Log file for all logging all operations and errors */
    public static final String KEY_LOG_FILE="log"; // NOI18N
    /** Log level for logging */
    public static final String KEY_LOG_LEVEL="log_level"; // NOI18N
    
    /** There can only be one!*/
    private static ConfigurationSettings SINGLETON = null;
    
    private static File fileChooserStartingDirectory = null;
    
    private ConfigurationSettings()
    {
//        this.firstInit = firstInit;
        listenerContainer = new ListenerContainer();
        libraryManager = new LibraryManager();
        settings = new Properties();
        // get platform settings: not really needed, but this is the first that's executed
        // and it should be assured that the settings are there early.
        PlatformInfo.getPlatformBinDir();

        // register listeners to set the Java system properties when office and sdk
        // are set
//        this.registerPropertyChangeListener(KEY_OFFICE_INSTALLATION, 
//                new OfficePropertyChangeListener(KEY_OFFICE_INSTALLATION, "office.home")); // NOI18N
//        this.registerPropertyChangeListener(KEY_OFFICE_INSTALLATION, 
//                new OfficePropertyChangeListener(KEY_OFFICE_INSTALLATION, "office.program.path", "/".concat(PlatformInfo.getOfficeProgramDir()))); // NOI18N
//        this.registerPropertyChangeListener(KEY_SDK_INSTALLATION, 
//                new OfficePropertyChangeListener(KEY_SDK_INSTALLATION, "oo.sdk.home")); // NOI18N

        setDefaultValues();
        settingsFolder= FileUtil.getConfigRoot().getFileObject("Settings"); // NOI18N
        if (settingsFolder==null)
        {
            try
            {
                OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation();
                if (loc != null) {
                    setValue(KEY_OFFICE_INSTALLATION, loc.getOfficePath());
                    setValue(KEY_SDK_INSTALLATION, loc.getSdkPath());
                }
                settingsFolder=FileUtil.getConfigRoot().createFolder("Settings"); // NOI18N
                store();
            }
            catch (IOException ex)
            {
                ex.printStackTrace(); // no log available yet
            }
        }
        else
        {
            load();
            String office = getValue(KEY_OFFICE_INSTALLATION);
            String sdk = getValue(KEY_SDK_INSTALLATION);
            OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation(office, sdk, false);
            if (loc == null) {
                // no office set: search for one
                loc = OpenOfficeLocation.getOpenOfficeLocation();
                if (loc != null) {
                    // found office, so store it.
                    setValue(KEY_OFFICE_INSTALLATION, loc.getOfficePath());
                    setValue(KEY_SDK_INSTALLATION, loc.getSdkPath());
                    store();
                }
            }
            else {
                setOfficeLocation();
            }
        }
        
        LogWriter lwr = LogWriter.createLogWriter(getValue(KEY_LOG_LEVEL), getValue(KEY_LOG_FILE));
        // register listeners for log settings
        this.registerPropertyChangeListener(KEY_LOG_FILE, 
                new OfficePropertyChangeListener(KEY_LOG_FILE, LogWriter.LOG_PATH)); // NOI18N
        this.registerPropertyChangeListener(KEY_LOG_LEVEL, 
                new OfficePropertyChangeListener(KEY_LOG_LEVEL, LogWriter.LOG_LEVEL)); // NOI18N
        LogWriter.LogSettingsChangeListener logListener = new LogWriter.LogSettingsChangeListener(lwr);
        registerPropertyChangeListener(KEY_LOG_FILE, logListener);
        registerPropertyChangeListener(KEY_LOG_LEVEL, logListener);

        userName = System.getProperty ("user.name"); // NOI18N
        if (userName == null) {
               userName = "SomeBody"; // NOI18N
        }
    }
    
    public static ConfigurationSettings getSettings() {
        if (SINGLETON==null) {
            SINGLETON=new ConfigurationSettings();           
        }
        return SINGLETON;
    } 
   
    public void setDefaultValues ()
    {
        this.clear();
        this.setValue(KEY_OFFICE_INSTALLATION, ""); // NOI18N
        this.setValue(KEY_SDK_INSTALLATION, ""); // NOI18N
        String[] defVals = LogWriter.getDefaultValues();
        this.setValue(KEY_LOG_LEVEL, defVals[0]);
        this.setValue(KEY_LOG_FILE, defVals[1]);
    }       
    
    public void store ()
    {
        try
        {    
//            if ( firstInit ) {
//                InitialSettingsDialog initDialog = new InitialSettingsDialog(null, true, this);
//                initDialog.setAlwaysOnTop(true);
//                initDialog.setVisible(true);
//
//                String officePath = getValue(KEY_OFFICE_INSTALLATION);
//                String sdkPath = getValue(KEY_SDK_INSTALLATION);
//                if (officePath.length() > 0 && sdkPath.length() > 0) {
//                    firstInit = false;
//                }
//            }

            
            settingsFile = settingsFolder.getFileObject(PROPERTIES_BASE,PROPERTIES_EXTENSION);
            if (settingsFile==null)
            {
                settingsFile = settingsFolder.createData(PROPERTIES_BASE,PROPERTIES_EXTENSION);
            }

            lock = settingsFile.lock();
            OutputStream out = settingsFile.getOutputStream (lock);
            settings.storeToXML(out, NbBundle.getMessage(ConfigurationSettings.class, "ConfigurationSettingsPropertiesComment"));
            out.close ();
            lock.releaseLock();

            setOfficeLocation();
        }
        catch (IOException ex)
        {
            // TODO file can not be created , do something about it
            ex.printStackTrace(); // no log available yet
        }
    }
   
    protected void load()
    {
        settingsFile = settingsFolder.getFileObject(PROPERTIES_BASE,PROPERTIES_EXTENSION);
        if (settingsFile != null)
        {
            // settings already exists. we don't need an initial configation step
            try       
            {
                InputStream in = settingsFile.getInputStream();
                settings.loadFromXML(in);
                in.close();
                // call listeners
                Enumeration keys = settings.keys();
                while (keys.hasMoreElements()) {
                    String key = (String)keys.nextElement();
                    PropertyChangeListener[] listeners = listenerContainer.getListenerForProperty(key);
                    for (int i=0; i<listeners.length; i++) {
                        listeners[i].propertyChange(new PropertyChangeEvent(this, key, null, settings.getProperty(key)));
                    }
                }
            }
            catch (IOException ex)
            {
                ex.printStackTrace(); // no log available yet
            }
        }
    }
    
    public String getValue(String key)
    {
        if (key == null) return "";
        return settings.getProperty (key);
    }
    
    private void clear() {
        Enumeration keys = settings.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            PropertyChangeListener[] listeners = listenerContainer.getListenerForProperty(key);
            for (int i=0; i<listeners.length; i++) {
                listeners[i].propertyChange(new PropertyChangeEvent(this, key, null, null));
            }
        }
        settings.clear();
    }
    
    public void setValue(String key, String value) {
        if (key == null) return;
        settings.setProperty(key, value == null?"":value.trim ());
        PropertyChangeListener[] listeners = listenerContainer.getListenerForProperty(key);
        for (int i=0; i<listeners.length; i++) {
            listeners[i].propertyChange(new PropertyChangeEvent(this, key, null, value));
        }
    }  
    
    public String getLibraryName() {
        if (libraryName == null) {
            libraryName = LibraryManager.getLibraryName(
                    OpenOfficeLocation.getOpenOfficeLocation(
                        getValue(KEY_OFFICE_INSTALLATION), 
                        getValue(KEY_SDK_INSTALLATION), false));
        }
        return libraryName;
    }
    
    public String getUser() {
        return userName;
    }

    public static String getTimeStamp() {
        Calendar cal = new GregorianCalendar();
        DecimalFormat dfmt = new DecimalFormat("00"); // NOI18N

        // Calendar.Month starts with numbering 0 for January
        return dfmt.format(cal.get(Calendar.YEAR)) + "." +
                dfmt.format(cal.get(Calendar.MONTH) + 1) + "." +
                dfmt.format(cal.get(Calendar.DAY_OF_MONTH)) + " - " +
                dfmt.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" +
                dfmt.format(cal.get(Calendar.MINUTE)) + ":" +
                dfmt.format(cal.get(Calendar.SECOND));  // NOI18N
    }
    
    public static void storeDefaultFileChooserStartingDir(File dir) {
        if (dir == null ) {
            fileChooserStartingDirectory = null;
        }
        else if (dir.isDirectory()) {
            fileChooserStartingDirectory = dir;
        }
    }
    
    public static File getDefaultFileChooserStartingDir() {
        return fileChooserStartingDirectory;
    }
    
    public void registerPropertyChangeListener(
            // when propertyName is null, listener is registered for all.
            String propertyName, PropertyChangeListener listener) {
        listenerContainer.addListener(propertyName, listener);
    }

    public void removePropertyChangeListener(
            PropertyChangeListener listener) {
        listenerContainer.removeListener(listener);
    }

    private void setJavaSystemProperties(OpenOfficeLocation loc) {
        // TODO: move all that three layer stuff and building of paths into OpenOfficeLocation class.
        String officePath = loc.getOfficePath();
        String sdkPath = loc.getSdkPath();
        System.setProperty("office.home.dir", officePath); // NOI18N
        System.setProperty("office.program.dir", officePath.concat("/").concat(PlatformInfo.getOfficeProgramDir())); // NOI18N
        System.setProperty("ure.bin.dir", System.getProperty("office.program.dir")); // NOI18N
        
        System.setProperty("oo.version.number", loc.isThreeLayerOffice()?"three":"older"); // NOI18N
        System.setProperty("oo.sdk.dir", sdkPath); // NOI18N
        String[] types = loc.getUnoTypesPath();
        System.setProperty("oo.unotypes.rdb", "-X".concat(types[0])); // NOI18N
        if (types.length > 1) {
            System.setProperty("oo.offapi.rdb", "-X".concat(types[1])); // NOI18N
        }
        else {
            // workaround for i92911: works on Windows, but not on *ix
            System.setProperty("oo.offapi.rdb", "-X".concat(types[0])); // NOI18N
        }
        if (loc.isThreeLayerOffice()) {
            System.setProperty("sdk.bin.dir", sdkPath.concat(File.separator).concat("bin")); // NOI18N
        }
        else {
            String platform = PlatformInfo.getPlatformBinDir();
//            System.setProperty("sdk.bin.dir", sdkPath.concat(File.separator).concat(platform).concat(File.separator).concat("bin")); // NOI18N
            System.setProperty("sdk.bin.dir", sdkPath.concat(File.separator).concat("bin")); // NOI18N
        }
        //System.setProperty("ure.bin.dir", loc.getUreBinPath()); // NOI18N
        String soPath = loc.getPathVariable();
        if (!PlatformInfo.isWindows()) {
            soPath = soPath.concat(File.pathSeparator).concat("/usr/bin"); // NOI18N
        
            if (PlatformInfo.isMacOS())
                soPath = new String("/usr/lib").concat(File.pathSeparator).concat(soPath); // NOI18N

        }
        System.setProperty("office.tool.path", soPath); // NOI18N
    }

    private void setOfficeLocation() {
        String officePath = getValue(KEY_OFFICE_INSTALLATION);
        String sdkPath = getValue(KEY_SDK_INSTALLATION);
        OpenOfficeLocation.clearOpenOfficeLocation();
        OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation(officePath, sdkPath, false);
        if (loc != null) {
            libraryName = libraryManager.createLibrary(loc);
            setJavaSystemProperties(loc);
        }
    }

    private class ListenerContainer {
        PropertyChangeListener[] listeners;
        String[] keys;
        ListenerContainer() {
            listeners = new PropertyChangeListener[0];
            keys = new String[0];
        }
        void addListener(String property, PropertyChangeListener listener) {
            if (listener == null) return;
            PropertyChangeListener[] newListeners = new PropertyChangeListener[listeners.length + 1];
            String[] newKeys = new String[keys.length + 1];
            int i=0;
            for (; i<listeners.length; i++) {
                newListeners[i] = listeners[i];
                newKeys[i] = keys[i];
            }
            newKeys[i] = property;
            newListeners[i] = listener;
            listeners = newListeners;
            keys = newKeys;
        }
        void removeListener(PropertyChangeListener listener) {
            if (listener == null) return;
            Vector<PropertyChangeListener> v = new Vector<PropertyChangeListener>();
            Vector<String> k = new Vector<String>();
            for (int i=0; i<listeners.length; i++) {
                if (!listeners[i].equals(listener)) {
                    v.add(listeners[i]);
                    k.add(keys[i]);
                }
            }
            listeners = (PropertyChangeListener[])v.toArray(new PropertyChangeListener[v.size()]);
            keys = (String[])k.toArray(new String[k.size()]);
        }
        PropertyChangeListener[] getListenerForProperty(String propertyName) {
            Vector<PropertyChangeListener> v = new Vector<PropertyChangeListener>();
            for (int i=0; i<listeners.length; i++) {
                if (keys[i] == null || keys[i].equals(propertyName)) {
                    v.add(listeners[i]);
                }
            }
            return v.toArray(new PropertyChangeListener[v.size()]);
        }
    }
    
    /**
     * Java properties office.home and oo.sdk.home have to be set, when the 
     * settings for these variables are changed. So use a listener for this.
     */
    private class OfficePropertyChangeListener implements PropertyChangeListener {
        protected String localPropName;
        protected String javaSysPropName;
        protected String postfixValue;
        public OfficePropertyChangeListener(String localPropName, String javaSysPropName) {
            this(localPropName, javaSysPropName, null);
        }
        public OfficePropertyChangeListener(String localPropName, String javaSysPropName, String postfixValue) {
            this.localPropName = localPropName;
            this.javaSysPropName = javaSysPropName;
            this.postfixValue = postfixValue;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(localPropName)) {
                if (postfixValue == null) {
                    System.setProperty(javaSysPropName, (String)evt.getNewValue());
                }
                else {
                    System.setProperty(javaSysPropName, ((String)evt.getNewValue()).concat(postfixValue));
                }
            }
        }
    }
}
