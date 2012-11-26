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
import java.io.IOException;
import org.openide.util.Exceptions;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author steffen
 */
public abstract class PlatformLocator {

    /** derived classes have to fill this variable in locateOffice method **/
    protected File mOfficePath;
    /** derived classes have to fill this variable in locateSdk method **/
    protected File mSdkPath;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected String mJutUnoilPath;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected String mJuhJurtRidlPath;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected String mPathVariable;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected String mUnorcPath;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected String mUreBinPath;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected String[] mTypesPath;
    /** derived classes have to fill this variable in locateOfficePaths method **/
    protected boolean mThreeLayerOffice;
    /** derived clases can set this to show that something went wrong with office paths **/
    protected boolean mOfficePathsSet;
    
    /**
     * C'tor for this class: only called from derived classes
     * @param office path to the office, can be null
     * @param sdk path to the sdk, can be null
     */
    protected PlatformLocator(String officePath, String sdkPath, boolean guessOfficePaths) {
        if (guessOfficePaths && (officePath == null || officePath.length() == 0)) {
            // sdk path may be found, too
            locateOffice();
        }
        else {
            if (officePath != null && officePath.length() > 0)
                mOfficePath = new File(officePath);
        }
        if (guessOfficePaths && (sdkPath == null || sdkPath.length() == 0)) {
            locateSDK();
        }
        else {
            if (sdkPath != null && sdkPath.length() > 0)
                mSdkPath = new File(sdkPath);
        }
        if (mOfficePath != null && mSdkPath != null && mOfficePath.exists() && mSdkPath.exists()) {
            locateOfficePaths(); // produce path variables
        }
        else {
            mOfficePathsSet = false; // gets also set in locateOfficePaths
            mOfficePath = null;
            mSdkPath = null;
        }
        logAllVariables();
    }

    private void logAllVariables() {
        try {
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mOfficePath: " + (mOfficePath == null ? "" : mOfficePath.getCanonicalPath()));
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mSdkPath: " + (mSdkPath == null ? "" : mSdkPath.getCanonicalPath()));
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mJutUnoilPath: " + mJutUnoilPath);
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mJuhJurtRidlPath: " + mJuhJurtRidlPath);
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mPathVariable: " + mPathVariable);
            String typesPath = "";
            if (mTypesPath != null) {
                for (int i = 0; i < mTypesPath.length; i++) {
                    typesPath = typesPath.concat(mTypesPath[i]).concat(":");
                }
            }
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mTypesPath: " + typesPath);
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "mThreeLayerOffice: " + mThreeLayerOffice);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    // derived classes must implement the following functions:
    /**
     * Locate the office directory for this platform: after executing this 
     * function, mOfficePath should denote the path to the office, null 
     * otherwise
     */
    protected abstract void locateOffice();

    /**
     * Locate the sdk directory for this platform: after executing this 
     * function, mSdkPath should denote the path to the sdk, null 
     * otherwise
     */
    protected abstract void locateSDK();

    /**
     * Fill all other variables with meaningful values, when officePath and 
     * sdkPath are known.
     */
    protected abstract void locateOfficePaths();
    
    /**
     * Get the full path for a special jar. Note: WinLocator overrides this,
     * because of stupid Java problems when writing \\
     * @param jarName the name of the jar
     * @return the full path including the jar
     */
    public String getFullPathForJar(String jarName) {
        if (jarName.equals("juh.jar") || jarName.equals("jurt.jar") || 
                jarName.equals("ridl.jar") || jarName.equals("java_uno.jar") || jarName.equals("unoloader.jar")) {
            return mJuhJurtRidlPath.concat(File.separator).concat(jarName);
        }
        return mJutUnoilPath.concat(File.separator).concat(jarName);
    }

    /**
     *  Get the path to the Office
     * @return the path to the Office
     */
    public String getOfficePath() {
        try {
            if (mOfficePath != null) {
                return mOfficePath.getCanonicalPath();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }
    
    /**
     * Get the path to the SDK
     * @return the path to the sdk
     */
    public String getSdkPath() {
        try {
            if (mSdkPath != null) {
                return mSdkPath.getCanonicalPath();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "";
    }
    
    /**
     *  Get the path variable
     * @return the path variable
     */
    public String getPathVariable() {
        if (mPathVariable == null) {
            return "";
        }
        return mPathVariable;
    }

    /**
     *  Get the ure bin path for tools
     * @return the path variable
     */
    public String getUreBinPath() {
        if (mUreBinPath == null) {
            return "";
        }
        return mUreBinPath;
    }

    /**
     * get the types path
     * @return the types path
     */
    public String[] getTypesPath() {
        if (mTypesPath == null) {
            return new String[]{""};
        }
        return mTypesPath;
    }
    
    /**
     * Is this a three layer office?
     * @return true, when it's athree layer office, false else.
     */
    public boolean isThreeLayerOffice() {
        return mThreeLayerOffice;
    }
    
    /**
     * Get the path to uno(.exe) 
     *
     */
    public String getUnorcPath() {
        if (mUnorcPath == null) {
            return "";
        }
        return mUnorcPath;
    }
}
