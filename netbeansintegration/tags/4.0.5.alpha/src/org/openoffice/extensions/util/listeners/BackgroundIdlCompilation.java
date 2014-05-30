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
package org.openoffice.extensions.util.listeners;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.config.office.OpenOfficeLocation;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.config.office.PlatformInfo;
import org.openoffice.extensions.util.ScriptExecutor;
import sun.tools.jar.Main;

/**
 * Compile Idl files in the background and add the compiled jar file to
 * the classpath of the project.
 * @author sg128468
 */
public class BackgroundIdlCompilation extends Thread {

    //sleep time in milliseconds
    private static final int INITIAL_SLEEP_TIME = 500;
    // number of intervals
    private static final int SLEEP_INTERVALS = 10;
    // static reference to compile class
    static BackgroundIdlCompilation sm_idlCompile;

    public static void compileInBackground(String[] allIdlFiles) {
        // do nothing when no files exist
        if (allIdlFiles == null || allIdlFiles.length == 0) return; // TODO: remove eventually created dirs?
        if (sm_idlCompile == null || !sm_idlCompile.isAlive()) {
            sm_idlCompile = new BackgroundIdlCompilation(allIdlFiles);
        } else {
            sm_idlCompile.halt();
            // wait for thread to die
            // TODO: evaluate if this is a bottleneck...
            while (sm_idlCompile.isAlive()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    LogWriter.getLogWriter().printStackTrace(ex);
                }
            }
            sm_idlCompile = new BackgroundIdlCompilation(allIdlFiles);
        }
        sm_idlCompile.start();
    }
    private String[] m_idlFiles;
    private boolean m_Halt;

    public BackgroundIdlCompilation(String[] idlFiles) {
        m_Halt = false;
        m_idlFiles = idlFiles;
    }

    @Override
    public void run() {
        // simple idea: to avoid compiling too often, wait for five seconds; if
        // idl file is changed again, start new
        for (int i = 0; !m_Halt && i < SLEEP_INTERVALS; i++) {
            try {
                sleep(BackgroundIdlCompilation.INITIAL_SLEEP_TIME);
            } catch (InterruptedException e) {
                LogWriter.getLogWriter().printStackTrace(e);
            }
        }

        try {
            // ok, five seconds passed and nothing happened, start compilation.
            ConfigurationSettings settings = ConfigurationSettings.getSettings();
            String sdkPath = settings.getValue(ConfigurationSettings.KEY_SDK_INSTALLATION);
            String sdkBinPath = sdkPath.concat(File.separator).concat(PlatformInfo.getPlatformBinDir()).concat(File.separator).concat("bin"); // NOI18N
            String soProgram = settings.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION).concat(File.separator).concat(PlatformInfo.getOfficeProgramDir());
            String outPath = System.getProperty("java.io.tmpdir");
            String buildDir = "build".concat(OOoListenerHelper.getProjectName());

            String soPath = "";
            String[] typesRdbPath = null;
            OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation();
            if (loc != null) {
                soPath = loc.getPathVariable();
                typesRdbPath = loc.getUnoTypesPath();
            }
            else {
                typesRdbPath = new String[0];
            }
            
            Map<String,String> p = new HashMap<String,String>(5);
            p.put(ScriptExecutor.PATH, soPath);
            p.put(ScriptExecutor.LD_LIBRARY_PATH, soPath);
            p.put(ScriptExecutor.DYLD_LIBRARY_PATH, soPath);
            p.put(ScriptExecutor.TEMP, System.getProperty("java.io.tmpdir"));
            p.put(ScriptExecutor.TMP, System.getProperty("java.io.tmpdir"));
            // tempdir is needed for idlc
            ScriptExecutor.setEnv(p);

            // idlc
            File projectDir = FileUtil.toFile(OOoListenerHelper.getProjectDir());
            String projDir = projectDir.getCanonicalPath();
            String srcIdlPath = projDir.concat(File.separator).concat("src"); // NOI18N
            String sdkIdlIncludes = sdkPath.concat(File.separator).concat("idl").concat(";").concat(srcIdlPath); // NOI18N
            String urdOutPath = outPath.concat(File.separator).concat(buildDir).concat(File.separator).concat("urd"); // NOI18N
            String[] command = new String[3 + m_idlFiles.length];
            command[0] = sdkBinPath.concat(File.separator).concat("idlc"); // NOI18N
            command[1] = "-I".concat(sdkIdlIncludes); // NOI18N
            command[2] = "-O".concat(urdOutPath); // NOI18N
            for (int i = 0; i < m_idlFiles.length; i++) {
                command[3 + i] = m_idlFiles[i];
                // afterwards, path to idl file is not needed anymore
                m_idlFiles[i] = m_idlFiles[i].substring(m_idlFiles[i].lastIndexOf(File.separatorChar) + 1);
            }

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            ScriptExecutor.executeScript(command, projectDir); // NOI18N
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            // regmerge
            File f = new File(outPath.concat(File.separator).concat(buildDir).concat(File.separator).concat("rdb")); // NOI18N
            f.mkdirs();
            String rdbPath = outPath.concat(File.separator).concat("build").concat(File.separator).concat("idl").concat(File.separator).concat("rdb").concat(File.separator).concat("types.rdb"); // NOI18N
            command = new String[3 + m_idlFiles.length];
            command[0] = sdkBinPath.concat(File.separator).concat("regmerge"); // NOI18N
            command[1] = rdbPath;
            command[2] = "/UCR"; // NOI18N
            for (int i = 0; i < m_idlFiles.length; i++) {
                command[3 + i] = urdOutPath.concat(File.separator).concat(m_idlFiles[i]).replaceAll("\\.idl", ".urd"); // NOI18N
            }

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            ScriptExecutor.executeScript(command);
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            // javamaker
            String classOutPath = outPath.concat(File.separator).concat(buildDir).concat(File.separator).concat("class");
            f = new File(classOutPath);
            f.mkdirs();
            command = new String[4 + typesRdbPath.length];
            command[0] = sdkBinPath.concat(File.separator).concat("javamaker"); // NOI18N
            command[1] = "-BUCR"; // NOI18N
            command[2] = "-O".concat(projDir).concat(File.separator).concat(classOutPath);
            command[3] = rdbPath;
            for (int i = 0; i < typesRdbPath.length; i++) {
                command[4] = "-X ".concat(typesRdbPath[i]);
            }

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            ScriptExecutor.executeScript(command);
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            // make a jar
            PrintStream outStream = new PrintStream(LogWriter.getLogWriter().getLogStream(LogWriter.LEVEL_INFO));
            PrintStream errStream = new PrintStream(LogWriter.getLogWriter().getLogStream(LogWriter.LEVEL_CRITICAL));
            Main jartool = new Main(outStream, errStream, "jar");
            
            File tmpJarFile = File.createTempFile("types", "jar", new File(classOutPath));
            tmpJarFile.deleteOnExit();
            command = new String[4];
            command[0] = "cf";
            command[1] = tmpJarFile.getCanonicalPath();
            command[2] = "-C ".concat(projDir).concat(File.separator).concat("build").concat(File.separator).concat("class");
            command[3] = ".";

            // halt now and exit if idl file was changed
            if (m_Halt) {
                return;
            }  // not sure if this is a good idea like this.

            jartool.run(command);

        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    /**
     * stop the thread
     */
    public void halt() {
        m_Halt = true;
    }
}
