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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.StringTokenizer;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.util.ScriptExecutor;

/**
 *
 * @author steffen
 */
public class WinLocator extends PlatformLocator {

    public WinLocator(String officePath, String sdkPath, boolean guessOfficePaths) {
        super(officePath, sdkPath, guessOfficePaths);
    }

    protected void locateOfficePaths() {
        assert mOfficePath != null;
        assert mSdkPath != null;
        mOfficePathsSet = false;
        File[] baseLink = mOfficePath.listFiles(new FilenameFilter() {

            public boolean accept(File path, String name) {
                if (name.endsWith("basis-link")) { // NOI18N
                    return true;
                }
                return false;
            }
        });
        if (baseLink == null || baseLink.length == 0) {
            try {
                String path = mOfficePath.getCanonicalPath();
                mJuhJurtRidlPath = path.concat("\\program\\classes"); // NOI18N
                mJutUnoilPath = mJuhJurtRidlPath;
                mUreBinPath = path.concat("\\program");
                mPathVariable = path.concat("\\program").concat(";").concat( // NOI18N
                        mUreBinPath);
                mUnorcPath = path.concat("\\program");
                mJutUnoilPath = mJuhJurtRidlPath;
                mTypesPath = new String[]{path.concat("\\program\\types.rdb")}; // NOI18N
                mThreeLayerOffice = false;
                mOfficePathsSet = true;
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        } else {
            BufferedReader buf1 = null;
            String basisPath = null;
            String urePath = null;
            File f = null;
            try {
                buf1 = new BufferedReader(new FileReader(baseLink[0]));
                String append1 = buf1.readLine();
                f = new File(mOfficePath.getCanonicalPath().concat("\\").concat(append1)); // NOI18N
                basisPath = f.getCanonicalPath();
            } catch (FileNotFoundException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } catch (IOException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            } finally {
                try {
                    if (buf1 != null) {
                        buf1.close();
                    }
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }

            File[] ureLink = f.listFiles(new FilenameFilter() {

                public boolean accept(File path, String name) {
                    if (name.endsWith("ure-link")) { // NOI18N
                        return true;
                    }
                    return false;
                }
            });
            if (ureLink != null && ureLink.length != 0) {
                BufferedReader buf2 = null;
                try {
                    buf2 = new BufferedReader(new FileReader(ureLink[0]));
                    String append2 = buf2.readLine();
                    f = new File(basisPath.concat("\\").concat(append2)); // NOI18N
                    urePath = f.getCanonicalPath();
                } catch (FileNotFoundException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } finally {
                    try {
                        if (buf2 != null) {
                            buf2.close();
                        }
                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
            }

            if (basisPath != null && urePath != null) {
                try {
                    mJutUnoilPath = basisPath.concat("\\program\\classes"); // NOI18N
                    mJuhJurtRidlPath = urePath.concat("\\java"); // NOI18N
                    // the following are not the same paths on other platforms!
                    mUnorcPath = urePath.concat("\\bin"); // NOI18N
                    mUreBinPath = urePath.concat("\\bin"); // NOI18N
                    mPathVariable = mOfficePath.getCanonicalPath().concat("\\program;").concat(
                            mSdkPath.getCanonicalPath()).concat("\\bin;").concat(
                            basisPath).concat("\\program;").concat(mUreBinPath); // // NOI18N
                    mTypesPath = new String[]{urePath.concat("\\misc\\types.rdb"), basisPath.concat("\\program\\offapi.rdb")}; // NOI18N
                    mThreeLayerOffice = true;
                    mOfficePathsSet = true;
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
        }
    }

    protected void locateOffice() {
        final String KEY = "HKLM\\SOFTWARE\\OpenOffice.org\\UNO\\InstallPath"; // NOI18N
        final String[] VALUE = new String[]{"StarOffice 9", "OpenOffice.org 3", "StarOffice", "OpenOffice.org"}; // NOI18N
        final String REG_SZ = "REG_SZ";
        
        // do not execute with new path variables: keep the ones of the user
        ScriptExecutor.setEnv(null);
        ScriptExecutor.executeScript(new String[]{"reg", "query", "\"".concat(KEY).concat("\"")}); // NOI18N
        
        if (!ScriptExecutor.hasErrors()) {
            String output = ScriptExecutor.getOutput();
            // parse output
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, output);
            StringTokenizer tokenizer = new StringTokenizer(output, "\r\n"); // NOI18N
            boolean lookForPath = false;
            int foundIndex = VALUE.length; // prefer StarOffice 9 before OOo 3 before StarOffice and so on
            while (tokenizer.hasMoreTokens()) {
                String next = tokenizer.nextToken().trim();
                for (int i = 0; i < VALUE.length; i++) {
                    if (next.startsWith(VALUE[i])) {
                        if (foundIndex > i) {
                            lookForPath = true;
                            foundIndex = i;
                        }
                    }
                }
                if (lookForPath) {
                    // take end of string after REG_SZ
                    int index = next.indexOf(REG_SZ) + REG_SZ.length(); // NOI18N
                    if (index <= 0) index = 0;
                    // this is best candidate
                    mOfficePath = new File(next.substring(index).trim()).getParentFile();
                    lookForPath = false;
                }
            }
        }
        else {
            // TODO: do something here?
        }
    }

    protected void locateSDK() {
        if (mOfficePath != null) {
            File[] baseLink = mOfficePath.listFiles(new FilenameFilter() {

                public boolean accept(File path, String name) {
                    if (name.endsWith("basis-link")) { // NOI18N
                        return true;
                    }

                    return false;
                }
            });
            if (baseLink != null && baseLink.length > 0) {
                BufferedReader buf1 = null;
                try {
                    buf1 = new BufferedReader(new FileReader(baseLink[0]));
                    String append1 = buf1.readLine();
                    File f = new File(mOfficePath.getCanonicalPath().concat("\\").concat(append1)); // NOI18N
                    File[] paths = f.listFiles(new FilenameFilter() {

                        public boolean accept(File dir, String name) {
                            if (name.endsWith("sdk")) { // NOI18N
                                return true;
                            }

                            return false;
                        }
                    });
                    if (paths != null && paths.length > 0) {
                        mSdkPath = paths[0].getCanonicalFile();
                    }

                } catch (FileNotFoundException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } catch (IOException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                } finally {
                    try {
                        if (buf1 != null) {
                            buf1.close();
                        }

                    } catch (IOException ex) {
                        LogWriter.getLogWriter().printStackTrace(ex);
                    }
                }
            }
        }
    }
}
