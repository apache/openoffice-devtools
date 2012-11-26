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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Vector;
import org.openide.util.NbBundle;
import org.openoffice.extensions.config.ConfigurationSettings;

/**
 *
 * @author sg128468
 */
public class LogWriter {
    
    static LogWriter lWriter;

    public static final int LEVEL_INFO = 0;
    public static final int LEVEL_WARNING = 4;
    public static final int LEVEL_CRITICAL = 8;
    public static final int LEVEL_EXCEPTION = 12;
    
    private static final String LEVEL_INFO_NAME_LOCALIZED = 
        NbBundle.getMessage(LogWriter.class, "LOG_Level_info"); // NOI18N
    private static final String LEVEL_WARNING_NAME_LOCALIZED = 
        NbBundle.getMessage(LogWriter.class, "LOG_Level_warning"); // NOI18N
    private static final String LEVEL_CRITICAL_NAME_LOCALIZED = 
        NbBundle.getMessage(LogWriter.class, "LOG_Level_critical"); // NOI18N
    private static final String LEVEL_EXCEPTION_NAME_LOCALIZED = 
        NbBundle.getMessage(LogWriter.class, "LOG_Level_exception"); // NOI18N

    private static final String LEVEL_INFO_NAME = "info";
    private static final String LEVEL_WARNING_NAME = "warning";
    private static final String LEVEL_CRITICAL_NAME = "critical";
    private static final String LEVEL_EXCEPTION_NAME = "exception";
    private static final String LEVEL_ALL_NAME = "all"; // NOI18N
    private static final String LEVEL_INACTIVE_NAME = "inactive"; // NOI18N

    // convenience for displaying the levels: only three for easier handling
    public static final String[] getLoglevels() { 
        return new String[] {
            LEVEL_INFO_NAME_LOCALIZED,
            LEVEL_WARNING_NAME_LOCALIZED,
            LEVEL_CRITICAL_NAME_LOCALIZED,
        };
    };

    // convenience abbreviation
    private static final String m_ls = System.getProperty("line.separator"); // NOI18N
    
    // for internal use: log all
    public static final int LEVEL_ALL = -1;
    // for internal use: set log inactive
    public static final int LEVEL_INACTIVE = 128468;
    // for internal use to not confuse with exceptions
    public static final int LEVEL_STACKTRACE = 11;
    // level and path
    public static final String LOG_LEVEL = "org.openoffice.nb.integration.log.level"; // NOI18N
    public static final String LOG_PATH = "org.openoffice.nb.integration.log.path"; // NOI18N
    
    public static LogWriter getLogWriter() {
        if (lWriter == null) { // should not happen, but to be save
            String[] vals = getDefaultValues();
            lWriter = new LogWriter(vals[0], vals[1]);
        }
        return lWriter;
    }
    
    public static LogWriter createLogWriter(String level, String path) {
        lWriter = new LogWriter(level, path);
        return lWriter;
    }
    
    public static String[] getDefaultValues() {
        String level = System.getProperty(LOG_LEVEL);
        if (level == null || level.length() == 0) {  // get environment variable as fallback
            Map<String,String> envVars = System.getenv();
            level = envVars.get(LOG_LEVEL.replace('.', '_')); // '.' not allowed in *ix environment vars
            if (level == null  || level.length() == 0) {
                level = LEVEL_ALL_NAME; // fallback to all when no level found
            }
        }
        String path = System.getProperty(LOG_PATH);
        if (path == null || level.length() == 0) {  // get environment variable as fallback
            Map<String,String> envVars = System.getenv();
            path = envVars.get(LOG_PATH.replace('.', '_')); // '.' not allowed in *ix environment vars
            if (path == null || level.length() == 0) {  // fallback to system temp
                path = System.getProperty("java.io.tmpdir");
            }
        }
        return new String[] {level, path};
    }
    
    private BufferedWriter m_Writer;
    private File m_LogFile;
    private int m_iLogLevel = 0;
    
    /** Creates a new instance of LogWriter 
     * There are three possibilties: set in UI, system variable or environment variable.
     * Possibilties are called in that order, UI settings win, then system, then environment#
     * UI settings are persistent: once set, they are kept.
     * Only exception: the UI settings say log is off, but system or environment say log is on - then it is on.
     */
    private LogWriter(String level, String path) {
        // set level
        setLogLevel(level);
        // get a unique log file in path or else in temp dir
        createLogFile(path);
    }

    /**
     * Set the log leve to a sensible value. Return the value if it needs to
     * be checked.
     * @param level the log level
     **/
    private void setLogLevel(String level) {
        // set level: use english names as fallback.
        if (level == null) {
            m_iLogLevel = LEVEL_INACTIVE;
        } else if (level.trim().equalsIgnoreCase(LEVEL_INFO_NAME_LOCALIZED)) {
            m_iLogLevel = LEVEL_INFO;
        } else if (level.trim().equalsIgnoreCase(LEVEL_INFO_NAME)) { 
            m_iLogLevel = LEVEL_INFO;
        } else if (level.trim().equalsIgnoreCase(LEVEL_WARNING_NAME_LOCALIZED)) {
            m_iLogLevel = LEVEL_WARNING;
        } else if(level.trim().equalsIgnoreCase(LEVEL_WARNING_NAME)) {
            m_iLogLevel = LEVEL_WARNING;
        } else if (level.trim().equalsIgnoreCase(LEVEL_CRITICAL_NAME_LOCALIZED)) {
            m_iLogLevel = LEVEL_CRITICAL;
        } else if (level.trim().equalsIgnoreCase(LEVEL_CRITICAL_NAME)) { 
            m_iLogLevel = LEVEL_CRITICAL;
        } else if (level.trim().equalsIgnoreCase(LEVEL_EXCEPTION_NAME_LOCALIZED)) {
            m_iLogLevel = LEVEL_EXCEPTION;
        } else if (level.trim().equalsIgnoreCase(LEVEL_EXCEPTION_NAME)) {
            m_iLogLevel = LEVEL_EXCEPTION;
        } else if (level.trim().equalsIgnoreCase(LEVEL_INACTIVE_NAME)) { 
            m_iLogLevel = LEVEL_INACTIVE;
        } else if (level.trim().equalsIgnoreCase(LEVEL_ALL_NAME)) { 
            m_iLogLevel = LEVEL_ALL;
        } else {
            m_iLogLevel = LEVEL_INACTIVE;
        }
    }
    
    /**
     * Return true, if the log is active
     **/
    public boolean isActive() {
        return m_iLogLevel >= LEVEL_INACTIVE;
    }
    
    public String getLogFile() {
        if (m_LogFile != null) {
            try {
                return m_LogFile.getCanonicalPath();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return ""; // NOI18N
    }
    
    public OutputStream getLogStream(int logLevel) {
        return new LogStream(logLevel);
    }
    
    private void setLogFile(String file) {
        if (file.equals(getLogFile())) return;
        try {
            if (m_Writer != null)
                m_Writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // set all variables to null.
        m_LogFile = null;
        m_Writer = null;
        createLogFile(file);
    }
    
    public void clearLogFile() {
        try {
            if (m_Writer != null)
                m_Writer.close();
            if (m_LogFile != null) {
                m_LogFile.createNewFile();
                m_Writer = new BufferedWriter(new FileWriter(m_LogFile));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        // create time stamp for empty file.
        createTimeStamp(true);
    }
    
    public void log(int level, String message) {
        // do not log empty messages!
        if (level >= m_iLogLevel && m_Writer != null && message != null && message.length() != 0) {
            try {
                final String className = getLastCalledClassName();
                switch(level) {
                    case LEVEL_INFO:
                        m_Writer.write(m_ls.concat("INFO       # ").concat(className).concat(": ")); // NOI18N
                        break;
                    case LEVEL_WARNING:
                        m_Writer.write(m_ls.concat("WARNING    # ").concat(className).concat(": ")); // NOI18N
                        break;
                    case LEVEL_CRITICAL:
                        m_Writer.write(m_ls.concat("CRITICAL   # ").concat(className).concat(": ")); // NOI18N
                        break;
                    case LEVEL_STACKTRACE:
                        m_Writer.write(m_ls.concat("STACKTRACE # : ")); // NOI18N
                        break;
                    case LEVEL_EXCEPTION:
                        m_Writer.write(m_ls.concat("EXCEPTION  # : ")); // NOI18N
                        break;
                    default:
                }
                m_Writer.write(message.concat(m_ls));
                m_Writer.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * write exceptions to the log
     */
    public void printStackTrace(Throwable t) {
        if (t != null)
            log(LEVEL_EXCEPTION, t.getLocalizedMessage());
        else
            log(LEVEL_EXCEPTION, "Could not write Exception (was null): ".concat(getStackTrace(3))); // NOI18N
    }
    
    /**
     * print a stack trace of the last calleers until the cll of this function;
     * depth determines how many callers will be traced. Any number
     * is possible (e.g. MAX_INT), but of course there cannot be more than
     * <i>all</i> callers. This has a similar level as exceptions.
     * @param message a message printed with the stack trace
     * @param depth the count of the trace
     */
    public void printStackTrace(String message, int depth) {
        if (message != null) {
            String msg = message.concat(m_ls).concat(getStackTrace(depth));
            log(LEVEL_STACKTRACE, msg);
        }
        else {
            log(LEVEL_STACKTRACE, ""); // NOI18N
        }
    }
    
    /**
     * takes the last class name from the stack before this class is called,
     * so the debug output contains this information.
     * @returns the class name where a method from this class was called.
     */
    private String getLastCalledClassName() {
        Throwable t = new Throwable();
        t.fillInStackTrace();
        StackTraceElement[] elements = t.getStackTrace();
        boolean found = false;
        String classNamePrefix = null;
        for (int i=0; !found && i<elements.length; i++) {
            classNamePrefix = elements[i].getClassName();
            if (!classNamePrefix.equals(LogWriter.class.getName())) {
                int lineNumber = elements[i].getLineNumber();
                found = true;
                if (lineNumber >= 0) { // cannot determine line number
                    // classNamePrefix = classNamePrefix;
//                }
//                else {
                    classNamePrefix = new StringBuffer(classNamePrefix).append(",line ").append(lineNumber).toString();  // NOI18N
                }
            }
        }
        return classNamePrefix;
    }    

    /**
     * takes the stack trace until this class comes up,
     * so the debug output contains this information.
     * @returns the stack until the class  where a method from this class was called.
     */
    private String getStackTrace(int depth) {
        Throwable t = new Throwable();
        t.fillInStackTrace();
        StackTraceElement[] elements = t.getStackTrace();
        Vector<String> stackTrace = new Vector<String>();
        for (int i=0; i<elements.length; i++) {
            String classNamePrefix = elements[i].getClassName();
            if (!classNamePrefix.equals(LogWriter.class.getName())) { 
                if (classNamePrefix.startsWith("org.openoffice.extensions")) { // NOI18N
                    stackTrace.add(elements[i].toString().concat(m_ls));
                }
            }
        }
        StringBuffer result = new StringBuffer();
        int end = (stackTrace.size() - depth > 0)?
            (stackTrace.size() - depth) : 0;
        for (int i=stackTrace.size() - 1; i >= end; i--) {
            result.append(stackTrace.get(i)).append(m_ls);
        }
        return result.toString();
    } 
    
    /**
     * Create the log file or open an existing one if path points to
     * one. Only characteristic of a log file is, it has to be writeable.
     */
    private void createLogFile(String path) {
        if (m_iLogLevel == LEVEL_INACTIVE) // don't bother with logfile when inactive
            return;
        
        boolean createdNewFile = true;
        if (path != null) {
            File logCandidate = new File(path);
            if (logCandidate.exists()) {
                if (logCandidate.canWrite()) {  // no else for this one: create a new file 
                    if (logCandidate.isDirectory()) {
                        try {
                            m_LogFile = File.createTempFile("OOoNBIntegration", ".log", logCandidate); // NOI18N
                            m_Writer = new BufferedWriter(new FileWriter(m_LogFile));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    else {
                        m_LogFile = logCandidate;
                        try {
                            m_Writer = new BufferedWriter(new FileWriter(m_LogFile));
                            createdNewFile = false;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            m_LogFile = null;
                        }
                    }
                }
            }
            else {  // log file is new, so create it
                try {
                    if (logCandidate.createNewFile()) {
                        m_LogFile = logCandidate;
                        m_Writer = new BufferedWriter(new FileWriter(m_LogFile));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    m_LogFile = null;
                }
            }
        }
        // get a unique log file in path or else in temp dir
        if (m_LogFile == null) { // don't bother with logfile when inactive
            try {
                File p = new File(System.getProperty("java.io.tmpdir")); // NOI18N
                m_LogFile = File.createTempFile("OOoNBIntegration", ".log", p); // NOI18N
                m_Writer = new BufferedWriter(new FileWriter(m_LogFile));
            } catch (IOException ex) {
                ex.printStackTrace();
                m_LogFile = null;
            }
        }
        // create a time stamp in the log file to indicate the new log session
        if (m_LogFile != null)
            createTimeStamp(createdNewFile);
    }
    
    /** Create a time stamp in the log file: mark a new log session this way
     */
    private void createTimeStamp(boolean emptyFile) {
        if (m_Writer == null) return; // easy exit for empty writer
        String time = ConfigurationSettings.getTimeStamp();
        try {
            if (!emptyFile) {  // no need for returns when file is empty
                m_Writer.newLine();
                m_Writer.newLine();
            }
            for (int i = 0; i < 80; i++) {
                m_Writer.write("*");
            }
            m_Writer.newLine();
            m_Writer.write("* Log session started: ".concat(time).concat(
                "                                   *"));
            m_Writer.newLine();
            for (int i = 0; i < 80; i++) {
                m_Writer.write("*");
            }
            m_Writer.newLine();
            m_Writer.newLine();
            m_Writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Changes to the log settings have to be reflected in LogWriter class,
     * so to be notified of changes, add some listeners
     */
    public static class LogSettingsChangeListener implements PropertyChangeListener {
        private LogWriter mLwr;
        public LogSettingsChangeListener(LogWriter lwr) {
            mLwr = lwr;
        }
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(ConfigurationSettings.KEY_LOG_LEVEL)) {
                mLwr.setLogLevel((String)evt.getNewValue());
            }
            if (evt.getPropertyName().equals(ConfigurationSettings.KEY_LOG_FILE)) {
                mLwr.setLogFile((String)evt.getNewValue());
            }
        }
    }

    /**
     * Wrapper class around the log to give the logger away to others
     */
    private class LogStream extends OutputStream {
        private int m_logLevel;
        private StringWriter m_sWriter;
        public LogStream(int logLevel) {
            m_logLevel = logLevel;
            m_sWriter = new StringWriter();
        }
        
        @Override
        public void write(int b) throws IOException {
            m_sWriter.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            m_sWriter.write(new String(b));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            m_sWriter.write(new String(b, off, len));
        }

        @Override
        public void flush() throws IOException {
            LogWriter.getLogWriter().log(m_logLevel, m_sWriter.toString());
            m_sWriter.flush();
        }
    }
}