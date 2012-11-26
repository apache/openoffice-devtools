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
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author steffen
 */
public class IxLocator extends PlatformLocator {

    private final static String[] STARTING_PATHS = 
            new String[] {"/opt", "/usr/lib", "/usr"};
    private final static String OPENOFFICEORG3_NAME = "openoffice.org3";
    private final static String STAROFFICE9_NAME = "staroffice9";
    private final static String OPENOFFICEORG_NAME = "openoffice.org";
    private final static String STAROFFICE_NAME = "staroffice";
    private final static String STARSUITE_NAME = "starsuite";
    // all names in an array for providing some sort of hierarchy
    private final static String[] OFFICE_NAMES = new String[] {
         STAROFFICE9_NAME,
         OPENOFFICEORG3_NAME,
         STAROFFICE_NAME,
         OPENOFFICEORG_NAME,
         STARSUITE_NAME,
    };
    
    public IxLocator(String officePath, String sdkPath, boolean guessOfficePaths) {
        super(officePath, sdkPath, guessOfficePaths);
    }

    /**
     * determeine all paths variables: classpath and program/lib path
     */
    protected void locateOfficePaths() {
        assert mOfficePath != null;
        assert mSdkPath != null;
        mOfficePathsSet = false;
        File[] baseLink = mOfficePath.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String path = pathname.getPath();
                if (path != null && path.endsWith("basis-link")) {
                    return true;
                }
                return false;
            }
        });
        if (baseLink == null || baseLink.length == 0) {
            try {
                String path = mOfficePath.getCanonicalPath();
                mJuhJurtRidlPath = path.concat("/program/classes"); // NOI18N
                mUreBinPath = mSdkPath.getCanonicalPath().concat("/").concat(
                        PlatformInfo.getPlatformBinDir()).concat("/bin");
                // add "/usr/lib" to path
                mPathVariable = path.concat("/program").concat(":").concat(
                        mUreBinPath).concat(
                        ":/usr/bin"); // // NOI18N
                mJutUnoilPath = mJuhJurtRidlPath;
                mTypesPath = new String[]{path.concat("/program/types.rdb")}; // NOI18N
                mThreeLayerOffice = false;
                mUnorcPath = path.concat("/program");
                mOfficePathsSet = true;
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        else {
            try {
                String path = baseLink[0].getCanonicalPath();
                mJutUnoilPath = path.concat("/program/classes");
                mJuhJurtRidlPath = path.concat("/ure-link/share/java");
                mUnorcPath = path.concat("/ure-link/lib");
                mPathVariable = 
                        mOfficePath.getCanonicalPath().concat("/program").concat(
                        ":").concat(
                            mSdkPath.getCanonicalPath()).concat("/bin").concat(
                        ":/usr/lib").concat(
                        ":").concat(path).concat("/program").concat(
                        ":").concat(path).concat("/ure-link/lib").concat(
                        ":").concat(path).concat("/ure-link/bin"); // // NOI18N
                mUreBinPath = path.concat("/ure-link/bin");
                mTypesPath = new String[]{
                    path.concat("/ure-link/share/misc/types.rdb"),
                    path.concat("/program/offapi.rdb"),
                };
                mThreeLayerOffice = true;
                mOfficePathsSet = true;
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }

    /**
     * Find OOo candidate installations, select one of those
     * @return one selected OpenOffice.org installation directory 
     */
    protected void locateOffice() {
        int foundIndex = OFFICE_NAMES.length; // prefer StarOffice 9 before OOo 3 before StarOffice and so on
        for (int i = 0; i < STARTING_PATHS.length; i++) {
            File f = new File(STARTING_PATHS[i]);
            // iterate over names in order: first ones are preferred
            for (int k = 0; k < OFFICE_NAMES.length; k++) {
                final String officeName = OFFICE_NAMES[k];
                File[] cands = f.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        if (name.startsWith(officeName))
                            return true;
                        return false;
                    }
                });
                if (cands != null && cands.length > 0) {
                    for (int j = 0; j < cands.length; j++) {
                        String name = cands[j].getName();
                        // prefer SO before OOo 3/9 before all else
                        if (foundIndex > k && !name.endsWith("sdk")) {
                            mOfficePath = cands[j];
                            foundIndex = k;
                        }
                        // sdk must match office (this is for OOo 2 etc) - this may be dangerous
                        if (mSdkPath == null && name.endsWith("_sdk")) { // NOI18N
                            mSdkPath = cands[j];
                        }
                    }
                }
            }
        }
    }
    
    protected void locateSDK() {
        if (mSdkPath == null && mOfficePath != null) {  // office, but no sdk yet found...
            try {
                String path = mOfficePath.getCanonicalPath();
                String sdkPath = path.concat("/basis-link/sdk"); // NOI18N
                File f = new File(sdkPath);
                if (f.exists()) {
                    mSdkPath = f.getCanonicalFile();
                }
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
}
