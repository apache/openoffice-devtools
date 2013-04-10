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
public class MacLocator extends PlatformLocator {

    private final static String[] STARTING_PATHS = 
            new String[] {"/Applications"};
    private final static String OPENOFFICEORG3_NAME = "OpenOffice.org";
    private final static String STAROFFICE9_NAME = "StarOffice";
    // all names in an array for providing some sort of hierarchy
    private final static String[] OFFICE_NAMES = new String[] {
         OPENOFFICEORG3_NAME,
         STAROFFICE9_NAME,
    };
    
    public MacLocator(String office, String sdk, boolean guessOfficePaths) {
        super(office, sdk, guessOfficePaths);
    }

    /**
     * determeine all paths variables: classpath and program/lib path
     */
    protected void locateOfficePaths() {
        assert mOfficePath != null;
        assert mSdkPath != null;
        mOfficePathsSet = false;
        try {
            File startPath = new File(mOfficePath.getCanonicalPath().concat("/Contents"));
            File[] baseLink = startPath.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    String path = pathname.getPath();
                    if (path != null && path.endsWith("basis-link")) {
                        return true;
                    }
                    return false;
                }
            });
            if (baseLink != null && baseLink.length > 0) {
                try {
                    String path = baseLink[0].getCanonicalPath();
                    mJutUnoilPath = path.concat("/program/classes");
                    mJuhJurtRidlPath = path.concat("/ure-link/share/java");
                    mUnorcPath = path.concat("/ure-link/lib");
                    mUreBinPath = path.concat("/ure-link/bin");
                    mPathVariable = mOfficePath.getCanonicalPath().concat(
                            "/Contents/MacOS:").concat(
                            mSdkPath.getCanonicalPath()).concat("/bin:/usr/lib:").concat(
                            path).concat("/program:").concat(path).concat(
                            "/ure-link/lib:").concat(mUreBinPath); // // NOI18N
                    mTypesPath = new String[]{path.concat("/ure-link/share/misc/types.rdb"), path.concat("/program/offapi.rdb")};
                    mThreeLayerOffice = true;
                    mOfficePathsSet = true;
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
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
                File[] officeAndSdkCands = f.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        if (name.startsWith(officeName))
                            return true;
                        return false;
                    }
                });
                if (officeAndSdkCands != null && officeAndSdkCands.length > 0) {
                    for (int j = 0; j < officeAndSdkCands.length; j++) {
                        String name = officeAndSdkCands[j].getName();
                        if (foundIndex > k && name.equals(officeName.concat(".app"))) {
                            mOfficePath = officeAndSdkCands[j];
                            foundIndex = k;
                        }
                        // search for SDK in same way
                        if (mSdkPath == null && (name.endsWith("sdk.app") || name.endsWith("SDK.app"))) {
                            mSdkPath = officeAndSdkCands[j];
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
                else {
                    File parent = mOfficePath.getParentFile();
                    if (parent != null) {
                        for (int k = 0; k < OFFICE_NAMES.length; k++) {
                            final String officeName = OFFICE_NAMES[k];
                            File[] sdkCands = parent.listFiles(new FilenameFilter() {
                                public boolean accept(File dir, String name) {
                                    if (name.startsWith(officeName) && (name.endsWith("sdk") || name.endsWith("SDK")))
                                        return true;
                                    return false;
                                }
                            });
                        }
                    }
                }
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
    }
}
