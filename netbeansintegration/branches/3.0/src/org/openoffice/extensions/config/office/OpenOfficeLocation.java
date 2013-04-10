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
package org.openoffice.extensions.config.office;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author steffen
 */
public class OpenOfficeLocation {

    // static declarations and static functions
    private static OpenOfficeLocation sLocateOpenOffice;
    
    public static OpenOfficeLocation getOpenOfficeLocation() {
        if (sLocateOpenOffice == null) {
            sLocateOpenOffice = new OpenOfficeLocation();
            if (!sLocateOpenOffice.validate()) {
                sLocateOpenOffice = null;
            }
        }
        return sLocateOpenOffice;
    }
    
    public static OpenOfficeLocation getOpenOfficeLocation(String office, String sdk, boolean guessOfficePaths) {
        if (sLocateOpenOffice == null) {
            sLocateOpenOffice = new OpenOfficeLocation(office, sdk, guessOfficePaths);
            if (!sLocateOpenOffice.validate()) {
                sLocateOpenOffice = null;
            }
        }
        return sLocateOpenOffice;
    }

    public static void clearOpenOfficeLocation() {
        sLocateOpenOffice = null;
    }
    
    private static PlatformLocator createPlatformLocator(String office, String sdk, boolean guessOfficePaths) {
        if (PlatformInfo.isWindows()) {
            return new WinLocator(office, sdk, guessOfficePaths);
        }
        else if (PlatformInfo.isMacOS()) {
            return new MacLocator(office, sdk, guessOfficePaths);
        }
        return new IxLocator(office, sdk, guessOfficePaths);
    }
    
    private PlatformLocator mPlatformLocator;
    private String mOfficeVersion;

    // local declarations and local functions
    private OpenOfficeLocation() {
        this(null, null, true);
    }

    private OpenOfficeLocation(String office, String sdk, boolean guessOfficePaths) {
        mPlatformLocator = createPlatformLocator(office, sdk, guessOfficePaths);
        findOfficeVersion();
    }
    
    /**
     * Get the path of bootstraprc (or bootstrap.ini); bootstraprc is the last 
     * entry of the path. Returns empty string if bootstraprc is not found.
     * @param officepath
     * @return the path of bootstraprc (bootstrap.ini)
     */
    public String getBootstrapPath() {
        String bootstrapiniPath = "";
        try
        {
            String bootstrapini = PlatformInfo.getBootstrapIni();
            File f = new File(mPlatformLocator.getOfficePath() + File.separator 
                    + PlatformInfo.getOfficeProgramDir() + File.separator + bootstrapini ); // NOI18N
            if (f.exists()) {
                bootstrapiniPath = f.getCanonicalPath();
            }
        }
        catch (IOException ex)
        {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return bootstrapiniPath;
    }

//    public PlatformLocator getPlatformLocator() {
//        return mPlatformLocator;
//    }
    
    public String getOfficePath() {
        return mPlatformLocator.getOfficePath();
    }
    
    public String getSdkPath() {
        return mPlatformLocator.getSdkPath();
    }
    
    public String getUnorcPath() {
        return mPlatformLocator.getUnorcPath();
    }
    
    public String getUreBinPath() {
        return mPlatformLocator.getUreBinPath();
    }
    
    /**
     * Get the version of the Office currently used.
     * @return the Office version
     */
    public String getOfficeVersion() {
        return mOfficeVersion;
    }

    public String getFullPathForJar(String jarName) {
        return mPlatformLocator.getFullPathForJar(jarName);
    }
    
    public String[] getUnoTypesPath() {
        return mPlatformLocator.getTypesPath();
    }
    
    public String getPathVariable() {
        return mPlatformLocator.getPathVariable();
    }
    
    private void findOfficeVersion() {
        FileInputStream inStream = null; // NOI18N
        try {
            File bootstrap = new File(getBootstrapPath());
            inStream = new FileInputStream(bootstrap);
            java.util.PropertyResourceBundle props = new java.util.PropertyResourceBundle(inStream);
            mOfficeVersion = (String) props.handleGetObject("ProductKey"); // NOI18N
        } catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } finally {
            try {
                if (inStream != null)
                    inStream.close();
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
    
    public static boolean validateOffice(String officePath) {
        String postfix = PlatformInfo.getPostfix();  // NOI18N
        
        String checkdir = PlatformInfo.getOfficeProgramDir();

        File f = new File(officePath + File.separator + checkdir + File.separator + "soffice" + postfix);
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "Looking for file: " + f.getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return f.exists();
    }
    
    public static boolean validateSDK(String sdkPath) {
        String sdkPostfix = PlatformInfo.getSDKPostfix(); // NOI18N
        
        File f = new File(sdkPath + File.separator + "setsdkenv_" + sdkPostfix); // NOI18N
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "Looking for file: " + f.getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return f.exists();
    }
    
    public boolean validate() {
        if (mPlatformLocator.mOfficePathsSet) {
            return validateOffice(getOfficePath()) & validateSDK(getSdkPath());
        }
        return false;
    }
 
    public static String getSdk(String officePath) {
        PlatformLocator locator = createPlatformLocator(officePath, null, true);
        String path = locator.getSdkPath();
        return path; // may be null
    }
    
    public boolean isThreeLayerOffice() {
        return mPlatformLocator.isThreeLayerOffice();
    }
    
    /**
     * Be optimistic about the office: if path is null or "", we assume it is
     * a three layer office.
     * @param officePath
     * @return true, if officePath denotes a three layer office, also true if 
     * officePath is empty
     */
    public static boolean isThreeLayerOffice(String officePath) {
        if (officePath == null || officePath.length() == 0) return true;
        PlatformLocator locator = createPlatformLocator(officePath, null, true);
        return locator.isThreeLayerOffice();
    }
}
