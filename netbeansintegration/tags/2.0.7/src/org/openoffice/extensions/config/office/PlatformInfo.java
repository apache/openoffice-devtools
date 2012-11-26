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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 * @author js93811
 */
public class PlatformInfo {
    static public final String UNKNOWN_PLATFORM = "unknown_platform";  // NOI18N
    
    static String m_PlatformBinDir = null;
    static String m_PlatformPackageDir = null;
    // unix on all, on Windows: windows.bat
    static String m_ProgramSDKPostfix = null;
    // ".exe" on windows
    static String m_ProgramPostfix = null;
    // program on all, but Contents/MacOS on Mac
    static String m_OfficeProgramDir = null;
    static String m_UnorcName = null;
    static String m_BootstrapIni;
    
    public static String getPlatformBinDir() {
        if (m_PlatformBinDir == null) {
            preparePlatformInfo();
        }
        
        return m_PlatformBinDir;
    }
    
    public static String getPlatformPackageDir() {
        if (m_PlatformBinDir == null) {
            preparePlatformInfo();
        }
        return m_PlatformPackageDir;
    }    
    
    public static boolean isWindows() {
        if (m_PlatformBinDir == null) {
            preparePlatformInfo();
        }

        return m_PlatformBinDir.equals("windows"); // NOI18N
    }

    public static boolean isMacOS() {
        if (m_PlatformBinDir == null) {
            preparePlatformInfo();
        }

        return m_PlatformBinDir.equals("macosx"); // NOI18N
    }
    
    public static String getOfficeProgramDir() {
        if (m_OfficeProgramDir == null) {
            preparePlatformInfo();
        }

        return m_OfficeProgramDir;
    }

    public static String getBootstrapIni() {
        if (m_BootstrapIni == null) {
            preparePlatformInfo();
        }
        
        return m_BootstrapIni;
    }
    
    public static String getUnorcName() {
        if (m_UnorcName == null) {
            preparePlatformInfo();
        }
        
        return m_UnorcName;
    }
    
    public static String getSDKPostfix() {
        return m_ProgramSDKPostfix == null?"unix":m_ProgramSDKPostfix;
    }
    
    public static String getPostfix() {
        return m_ProgramPostfix == null?"":m_ProgramPostfix;
    }
    
    private static void preparePlatformInfo() {
        boolean knownPlatform = false;
        if (m_PlatformBinDir == null || System.getProperty("os.dir") == null) { // NOI18N
            String osName = System.getProperty("os.name"); // NOI18N
            String osArch = System.getProperty("os.arch"); // NOI18N
            
            m_OfficeProgramDir = "program"; //NOI18N
            m_BootstrapIni = "bootstraprc";
            m_UnorcName = "unorc";
            
            if (osName.indexOf("Windows") >= 0) { // NOI18N
                m_PlatformBinDir = "windows"; // NOI18N
                m_PlatformPackageDir = "Windows"; // NOI18N
                m_BootstrapIni = "bootstrap.ini";
                m_UnorcName = "uno.ini";
                m_ProgramPostfix = ".exe";
                m_ProgramSDKPostfix = "windows.bat";
                knownPlatform = true;
            } 
            else if (osName.indexOf("Linux") >= 0) { // NOI18N
                m_PlatformBinDir = "linux"; // NOI18N
                knownPlatform = true;
                if (osArch.indexOf("i386") >= 0) { // NOI18N
                    m_PlatformPackageDir = "Linux_x86"; // NOI18N
                }
                else {
                    // TODO: handle unkown architecture
                }
            }
            else if (osName.indexOf("SunOS") >= 0) { // NOI18N
                if (osArch.indexOf("sparc") >= 0) { // NOI18N
                    m_PlatformBinDir = "solsparc"; // NOI18N
                    m_PlatformPackageDir = "Solaris_SPARC"; // NOI18N
                    knownPlatform = true;
                }
                else if (osArch.indexOf("x86") >= 0) { // NOI18N
                    m_PlatformBinDir = "solintel";                 // NOI18N    
                    m_PlatformPackageDir = "Solaris_x86"; // NOI18N
                    knownPlatform = true;
                }
                else {
                    // TODO: handle unkown architecture
                }
            }
            else if (osName.indexOf("Mac OS X") >= 0) { // NOI18N
                m_OfficeProgramDir = "Contents/MacOS";
                if (osArch.indexOf("x86_64") >= 0) { // NOI18N
                    m_PlatformBinDir = "macosx"; // NOI18N
                    m_PlatformPackageDir = "MacOSX_Intel"; // NOI18N
                    knownPlatform = true;
                }
                else if (osArch.indexOf("i386") >= 0) { // NOI18N
                    m_PlatformBinDir = "macosx"; // NOI18N
                    m_PlatformPackageDir = "MacOSX_Intel"; // NOI18N
                    knownPlatform = true;
                }
                else if (osArch.indexOf("PPC") >= 0) { // NOI18N
                    m_PlatformBinDir = "macosx";                 // NOI18N    
                    m_PlatformPackageDir = "MacOSX_PowerPC"; // NOI18N
                    knownPlatform = true;
                }
                else {
                    // TODO: handle unkown architecture
                }
            }
            else {
                // TODO: handle unkown platform i.e. mac
            }
        }
        if (!knownPlatform) {
            Properties platformProps = new Properties();
            File f = new File(System.getProperty("user.home").concat(
                File.separator).concat(".OOo_plugin.properties"));
            if (f.exists() && f.isFile() && f.canRead()) {
                FileObject props = FileUtil.toFileObject(f);
                try {
                    platformProps.load(props.getInputStream());
                    m_PlatformBinDir = platformProps.getProperty("platform.bin.dir");
                    m_PlatformPackageDir = platformProps.getProperty("platform.package.dir");
                    m_ProgramSDKPostfix = platformProps.getProperty("platform.sdk.postfix");
                    m_ProgramPostfix = platformProps.getProperty("platform.postfix");
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, 
                        "Read Platform Info:  1. platform bin " + m_PlatformBinDir + 
                        "  2. platform package " + m_PlatformPackageDir + 
                        "  3. postfix " + m_ProgramPostfix + 
                        "  4. sdk postfix " + m_ProgramSDKPostfix);
                    knownPlatform = true;
                } catch (FileNotFoundException ex) {
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                } catch (IOException ex) {
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ex.getMessage());
                }
            }
            if (m_PlatformBinDir == null || m_PlatformPackageDir == null || !knownPlatform) {
                m_PlatformBinDir = UNKNOWN_PLATFORM;
            }
        }
        System.setProperty("sdk.os.dir", m_PlatformBinDir); // NOI18N
    }
    
}
