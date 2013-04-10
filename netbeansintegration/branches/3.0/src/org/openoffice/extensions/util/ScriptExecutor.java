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

package org.openoffice.extensions.util;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.openoffice.extensions.config.office.PlatformInfo;

/**
 *
 */
public class ScriptExecutor {
    public static final String PATH = "PATH";
    public static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";
    public static final String DYLD_LIBRARY_PATH = "DYLD_LIBRARY_PATH";
    public static final String TEMP = "TEMP";
    public static final String TMP = "TMP";

    static Map<String,String> envVars;
    static String[] script;
    static File workDir;
    static String errorMsg;
    private static String outMsg;
    static int exitVal;

    
    /** 
     * set the environment variables
     */
    public static void setEnv(Map<String,String> _envVars) {
        envVars = correctEnvVarForWindwos(_envVars);
    }
    
    /**
     * correct env variables on Windows: especially on Windows xp, PATH 
     * variable has to be set as "Path"
     */
    private static Map<String,String> correctEnvVarForWindwos(Map<String,String> _envVars) {
        // fix Win XP bug: canntot set path as "PATH"
        if (_envVars != null && PlatformInfo.isWindows()) {
            String value = _envVars.remove(PATH);
            if (value != null) {
                _envVars.put("Path", value);
            }       
        }
        return _envVars;
    }
    
    private static void updateExistingEnvironment(Map<String,String> environment) {
        if (envVars != null && environment != null) {
            Set<String> keys = envVars.keySet();
            for (Iterator<String> it = keys.iterator(); it.hasNext();) {
                String key = it.next();
                if (environment.containsKey(key)) {
                    if (key.equalsIgnoreCase(PATH) || 
                            key.equalsIgnoreCase(LD_LIBRARY_PATH)|| 
                                key.equalsIgnoreCase(DYLD_LIBRARY_PATH)) {
                        // append on all paths
                        String val = environment.get(key);
                        val = val.concat(File.pathSeparator).concat(envVars.get(key));
                        environment.put(key, val);
                    }
                    // keep all tem/zmp variables as they are.
                } 
                else {
                    environment.put(key, envVars.get(key));
                }
                // for testing, log the stuff
                // LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, key.concat("=").concat(environment.get(key)));
            }
        }
    }
    
    /**
     * Look, if the momentarily executed program has already finished.
     * @param p The momentarily running process.
     * @return The exit value of the program.
     */
    private static int isRunning(Process p) {
        int ret = -305;
        try {
            ret = p.exitValue();
        }
        catch (java.lang.Exception e) {
            // empty by default: exception is thrown when p is still alive
        }
        return ret;
    }
    

    /**
     * Execute a program and wait for the result.
     * @param script The program including parameters.
     * @param envVars The environment variables for the program.
     * @param workDir The working directory of the command.
     * @return The exit value of the program.
     */
    public static int executeScript(String[] script, Map<String,String> _envVars, File workDir ) {
        StringBuffer buf = new StringBuffer();
        for (int i=0; i<script.length; i++) {
            buf.append(script[i]).append(" "); // NOI18N
        }
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, buf.toString());
        if (_envVars != null) {
            envVars = correctEnvVarForWindwos(_envVars);
        }
        errorMsg = ""; // NOI18N
        outMsg = ""; // NOI18N
        exitVal = -305;
        int available;
        byte[] in = null;
        byte[] err = null;
        String sIn = ""; // NOI18N
        String sErr = ""; // NOI18N
        try {
            ProcessBuilder pb = new ProcessBuilder(script);
            pb = pb.directory(workDir);
            updateExistingEnvironment(pb.environment());
            
            Process tool = pb.start();
//            Process tool = Runtime.getRuntime().exec(script, envVars, workDir);
            InputStream aIn = tool.getInputStream();
            InputStream aErr = tool.getErrorStream();
            
            while(exitVal == -305) {
                exitVal = isRunning(tool);
                // input stream
                available = aIn.available();
                if (available > 0) {
                    in = new byte[available];
                    aIn.read(in, 0, available);
                    outMsg += new String(in);
                }
                // error stream
                available = aErr.available();
                if (available > 0) {
                    err = new byte[available];
                    aErr.read(err, 0, available);
                    errorMsg += new String(err);
                } 
                Thread.sleep(500);
            }
        } catch (IOException ioe) {
            LogWriter.getLogWriter().printStackTrace(ioe);
        }
        catch (InterruptedException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        return exitVal;
    }
    
    /**
     * Execute a program and wait for the result.
     * @param script The program including parameters.
     * @param envVars The environment variables for the program.
     * @param workDir The working directory of the command.
     * @return The exit value of the program.
     */
    public static int executeScript(String[] script, Map<String,String> envVars) {
        if (workDir == null) {
            return executeScript(script, envVars, 
                        new File(System.getProperty("user.dir"))); // NOI18N
        }
        return executeScript(script, envVars, workDir);
    }

    /**
     * Execute a program and wait for the result. Use the
     * initial set environment variables from this class.
     * @param script The program including parameters.
     * @return True, if the program executed correctly.
     */
    public static int executeScript(String[] script) {
        return executeScript(script, envVars);
    }
    
    /**
     * Execute a program and wait for the result. Use the
     * initial set environment variables from this class.
     * @param script The program including parameters.
     * @return True, if the program executed correctly.
     */
    public static int executeScript(String[] script, File workDir) {
        return executeScript(script, envVars, workDir);
    }
    
    /**
     * Get the error messages of the last executed script.
     * @return The message
     */
    public static String getErrors() {
        return errorMsg;
    }

    /**
     * Get the error messages of the last executed script.
     * @return The message
     */
    public static boolean hasErrors() {
        return errorMsg != null && errorMsg.length() > 0;
    }
    
    /**
     * Get the output of the last executed script.
     * @return The message
     */
    public static String getOutput() {
        return outMsg;
    }

    /**
     * Get the error messages of the last executed script.
     * @return The message
     */
    public static boolean hasOutput() {
        return outMsg != null && outMsg.length() > 0;
    }
    
}