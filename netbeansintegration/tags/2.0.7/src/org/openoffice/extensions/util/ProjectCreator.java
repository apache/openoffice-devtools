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

import org.openoffice.extensions.config.office.PlatformInfo;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import org.openide.filesystems.FileLock;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.config.office.OpenOfficeLocation;
import org.openoffice.extensions.projecttemplates.actions.ProjectVersion;
import org.openoffice.extensions.projecttemplates.addon.AddOnWizardIterator;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.projecttemplates.calcaddin.AddinWizardIterator;
import org.openoffice.extensions.projecttemplates.component.ComponentWizardIterator;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;
import sun.tools.jar.Main;

/**
 * Create a project from wizard, e.g. an Calc AddIn
 */
public class ProjectCreator {
    
    private File m_RootDir;
    private WizardDescriptor wiz;

    private FileObject m_RootFile;
    private static final String CR = System.getProperty("line.separator"); // NOI18N
    private static final int UNDEFINED_TYPE = 0;
    private static final int ADDIN_TYPE = 1;
    private static final int CREATE_INTERFACE_TYPE = 2;
    private static final int COMPONENT_TYPE = 3;
    private static final int ADDON_TYPE = 4;
    private int m_ProjectType;  // for just using skeletonmaker, use undefined type

    // addin templates
    private String[] addin_template_xcu;
    private String[] function_template_xcu;
    private String[] parameter_template_xcu;
    
    private String[] service_template_idl;
    private String[] interface_template_idl;
    
    private String[] addin_schema_xcs;
    
    /** Creates a new instance of ProjectCreator */
    public ProjectCreator(File rootDir, WizardDescriptor wiz) {
        this.wiz = wiz;
        this.m_RootDir = rootDir;
        this.m_RootFile = FileUtil.toFileObject(rootDir);
    }
    
/*    public String createIdlFileAndMerge(NbNodeObject idlType) {
        projectType = CREATE_INTERFACE_TYPE;
        Class c = ComponentWizardIterator.class;
        withoutSkeletonMaker = true;
        String rdbName = TypeBrowser.getTempRdbFileName();
        if (rdbName == null) return null;  // 2do: concide exception handling
        String propsFile = "Bundle.properties"; // NOI18N
        interface_template_idl = readFileLinesInArray(c, propsFile, "InterfaceTemplate"); // NOI18N
        
        updateWizardDescriptor();
        if (idlType instanceof Interface) {
            prepareWizardDataFromInterface((Interface)idlType);
        }

        FileObject[] createdIdlFiles = null;
        try {
            IdlFileCreator ifcr = new IdlFileCreator(wiz, ComponentWizardIterator.class,
                    rootDir.getCanonicalPath());
            createdIdlFiles = ifcr.createAllIdlFiles(idlType);
/*            String idlNme = (String)wiz.getProperty("idlName"); // NOI18N
            interf = FileUtil.createData(rootFile, idlNme);
            fillFile(interf, interface_template_idl); */
/*            skeletonmaker(createdIdlFiles);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
            return null;
        }
        return createdIdlFiles==null?null:createdIdlFiles[0].getPath();
    } */
    
    public void createComponent() {
        m_ProjectType = COMPONENT_TYPE;
        Class c = ComponentWizardIterator.class;
        
        // read template files in String arrays
        String propsFile = "Bundle.properties"; // NOI18N
        service_template_idl = readFileLinesInArray(c, propsFile, "ServiceTemplate"); // NOI18N
        interface_template_idl = readFileLinesInArray(c, propsFile, "InterfaceTemplate"); // NOI18N
        
        FileObject template = Templates.getTemplate(wiz);
        updateWizardDescriptor();
        try {
            unZipFile(template.getName(), template.getInputStream());
            createProjectStructure();
        } catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgLanguageIDException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        } catch (UnknownOpenOfficeOrgPropertyException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        
    }
    
    public void createAddon() {
        m_ProjectType = ADDON_TYPE;

        FileObject template = Templates.getTemplate(wiz);
        updateWizardDescriptor();
        try {
            unZipFile(template.getName(), template.getInputStream());
            createProjectStructure();
        } catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgLanguageIDException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        } catch (UnknownOpenOfficeOrgPropertyException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        
    }
    
    public void createCalcAddIn() {
        m_ProjectType = ADDIN_TYPE;
        Class c = AddinWizardIterator.class;
        
        // read template files in String arrays
        String propsFile = "Bundle.properties"; // NOI18N
        addin_template_xcu = readFileLinesInArray(c, propsFile, "AddInTemplate"); // NOI18N
        function_template_xcu = readFileLinesInArray(c, propsFile, "FunctionTemplate"); // NOI18N
        parameter_template_xcu = readFileLinesInArray(c, propsFile, "ParameterTemplate"); // NOI18N
        service_template_idl = readFileLinesInArray(c, propsFile, "ServiceTemplate"); // NOI18N
        interface_template_idl = readFileLinesInArray(c, propsFile, "InterfaceTemplate"); // NOI18N
        addin_schema_xcs = readFileLinesInArray(c, propsFile, "AddInSchema"); // NOI18N
        
        try {
            FileObject template = Templates.getTemplate(wiz);
            updateWizardDescriptor();
            unZipFile(template.getName(), template.getInputStream());
            createProjectStructure();
        } catch (FileNotFoundException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    private void updateWizardDescriptor() {
        ConfigurationSettings settings = ConfigurationSettings.getSettings();
        wiz.putProperty("PlatformBinDir", PlatformInfo.getPlatformBinDir()); // NOI18N
        wiz.putProperty("PlatformPackageDir", PlatformInfo.getPlatformPackageDir()); // NOI18N
        wiz.putProperty("OfficeLibrary", settings.getLibraryName()); // NOI18N
        wiz.putProperty("OfficePath", Utilities.replaceString(settings.getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION), "\\", "/")); // NOI18N
        wiz.putProperty("SdkPath", Utilities.replaceString(settings.getValue(ConfigurationSettings.KEY_SDK_INSTALLATION), "\\", "/")); // NOI18N
        wiz.putProperty("TimeStamp", ConfigurationSettings.getTimeStamp()); // NOI18N
        wiz.putProperty("UserName", settings.getUser()); // NOI18N
        String pkg = (String)wiz.getProperty("UnoPackage"); // NOI18N
        StringBuffer targetFileName = new StringBuffer("src"); // NOI18Nm
        targetFileName.append(File.separatorChar);
        targetFileName.append(pkg.replace('.', '/'));
        wiz.putProperty("CompleteSourcePath", targetFileName.toString()); // NOI18N
        wiz.putProperty("CentralRegistrationClass", 
            pkg.concat(".").concat("CentralRegistrationClass")); // NOI18N
        wiz.putProperty("BuildUnoVersion", ProjectVersion.getProjectVersion()); // NOI18N
    }
    
    private void unZipFile(String templateName, InputStream source) throws IOException {
        final String[] PATH_PROPERTIES = {};
        final String[] CONTENT_PROPERTIES = { 
            "ProjectName", 
            "OfficePath", 
            "SdkPath", 
            "OfficeLibrary", 
            "PlatformBinDir",
            "RegistrationClass", 
            "BuildUnoVersion", 
            "UnoPackage",
            "TimeStamp",
            "UserName",
            "CentralRegistrationClass",
            "StartupOptions",
        }; // NOI18N
        
        Class c = null;
        switch (m_ProjectType) {
            case ADDIN_TYPE:
                c = AddinWizardIterator.class;
                break;
            case ADDON_TYPE:
                c = AddOnWizardIterator.class;
                break;
            default:
                c = ComponentWizardIterator.class;
        }
        Substitutions substitutions = new Substitutions(wiz, new BufferedInputStream(
                c.getResourceAsStream(templateName.concat(".properties"))), // NOI18N
                PATH_PROPERTIES, CONTENT_PROPERTIES);
        try {
            ZipInputStream str = new ZipInputStream(source);
            ZipEntry entry;
            while ((entry = str.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    // in this template we don't have to change any paths
                    continue;
                } else {
                    String name = entry.getName();
                    FileObject fo = null;
                    fo = FileUtil.createData(m_RootFile, substitutions.substitutePath(name));
                    if (fo != null) {
                        FileLock lock = fo.lock();
                        try {
                            OutputStream out = fo.getOutputStream(lock);
                            try {
                                InputStream substituted = substitutions.substituteContent(entry.getSize(),
                                        str, entry.getName());
                                FileUtil.copy(substituted, out);
                            } finally {
                                out.close();
                            }
                        } finally {
                            lock.releaseLock();
                        }
                    }
                }
            }
        } finally {
            source.close();
        }
    }
    
    public static String[] readFileLinesInArray(Class baseClass, String propsFile, String key) {
        InputStream in = new BufferedInputStream(baseClass.getResourceAsStream(propsFile));
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        String s = props.getProperty(key);
        StringTokenizer t = new StringTokenizer(s, "\n"); // NOI18N
        Vector<String> lines = new Vector<String>();
        while(t.hasMoreTokens()) {
            String line = t.nextToken();
            lines.add(line);
        }
        return lines.toArray(new String[lines.size()]);
    }
    
    private void createProjectStructure() 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, 
                UnknownOpenOfficeOrgPropertyException {
        FileUtil.createFolder(m_RootFile, "test"); // NOI18N
        
        String projectPath = FileUtil.toFile(m_RootFile).getCanonicalPath();
        FileUtil.createFolder(m_RootFile, "build"); // NOI18N
        FileUtil.createFolder(m_RootFile, "dist"); // NOI18N
        
        String pkg = (String)wiz.getProperty("UnoPackage"); // NOI18N
        String pkgPath = pkg.replace('.', File.separatorChar);

        File packageTree = new File(
                new StringBuffer(projectPath).append(File.separator).append("src").append(
                File.separator).append(pkgPath).toString()); // NOI18N
        
        packageTree.mkdirs();
        
        wiz.putProperty("javaOutPath", packageTree.getCanonicalPath()); // NOI18N
        
        String projectSourceDir = new StringBuffer(projectPath).append(File.separator).append("src").toString(); // NOI18N
        
        if (this.m_ProjectType == ADDIN_TYPE) {
            IdlFileCreator ifcr = new IdlFileCreator(wiz, AddinWizardIterator.class,
                    projectSourceDir);

            FileObject[] createdIdlFiles = ifcr.createAllIdlFiles((NbNodeObject)wiz.getProperty("ServiceObject")); // NOI18N

            File f = new File(projectPath.concat(File.separator).concat("src")); // NOI18N
            FileObject sourceFo = FileUtil.toFileObject(f);
            FileObject xcuFile = FileUtil.createData(m_RootFile, "registry/data/org/openoffice/Office/CalcAddins.xcu"); // NOI18N
            fillFile(xcuFile, addin_template_xcu);
            ManifestCreator mc = new ManifestCreator(m_RootFile);
            mc.add(ManifestCreator.configuration_data_MediaType, "registry/data/org/openoffice/Office/CalcAddins.xcu"); // NOI18N
            mc.add(ManifestCreator.uno_typelibrary_rdb_MediaType, "types.rdb"); // NOI18N
            mc.add(ManifestCreator.uno_component_java_MediaType, new StringBuffer((String)
            wiz.getProperty("name")).append(".jar").toString()); // NOI18N
            
            mc.flush();
            
            skeletonmaker(createdIdlFiles, null);
        } else if (this.m_ProjectType == COMPONENT_TYPE) {
            
            IdlFileCreator ifcr = new IdlFileCreator(wiz, ComponentWizardIterator.class,
                    projectSourceDir);

            Hashtable types = (Hashtable)wiz.getProperty("DesignedTypes"); // NOI18N
            Enumeration keys = types.keys();
            Vector<FileObject> idlFiles = new Vector<FileObject>();
            while(keys.hasMoreElements()) {
                FileObject[] createdIdlFiles = ifcr.createAllIdlFiles((NbNodeObject)types.get(keys.nextElement()));
                for (int i=0; i<createdIdlFiles.length; i++) {
                    idlFiles.add(createdIdlFiles[i]); 
                }
            }
            @SuppressWarnings(value = "unchecked") // NOI18N
            Vector<String> v = (Vector<String>)wiz.getProperty("DumpTypes"); // NOI18N

            ManifestCreator mc = new ManifestCreator(m_RootFile);
            if (!types.isEmpty()) // with own data types, we have a types.rdb
                mc.add(ManifestCreator.uno_typelibrary_rdb_MediaType, "types.rdb"); // NOI18N
            mc.add(ManifestCreator.uno_component_java_MediaType, new StringBuffer((String)
            wiz.getProperty("name")).append(".jar").toString()); // NOI18N
            mc.flush();
            
            skeletonmaker(idlFiles.toArray(new FileObject[idlFiles.size()]),
                    v.toArray(new String[v.size()]));
        }
        else if (this.m_ProjectType == ADDON_TYPE) {

            wiz.putProperty("mainClassNameSmall", ((String)wiz.getProperty("mainClassName")).toLowerCase()); // NOI18N
            
            XcuFileCreator xcucr = new XcuFileCreator(wiz, AddOnWizardIterator.class);
//                    projectSourceDir);

            xcucr.createXcuFile(XcuFileCreator.XCU_ADDON_TYPE);
            xcucr.createXcuFile(XcuFileCreator.XCU_PROTOCOL_HANDLER_TYPE);
            
            ManifestCreator mc = new ManifestCreator(m_RootFile);
            mc.add(ManifestCreator.configuration_data_MediaType, "registry/data/org/openoffice/Office/ProtocolHandler.xcu"); // NOI18N
            mc.add(ManifestCreator.configuration_data_MediaType, "registry/data/org/openoffice/Office/Addons.xcu"); // NOI18N
            mc.add(ManifestCreator.uno_component_java_MediaType, new StringBuffer((String)
            wiz.getProperty("name")).append(".jar").toString()); // NOI18N
            mc.flush();
            
            skeletonmaker((AddOn)wiz.getProperty("AddOn")); // NOI18N
        }
    }
    
    private void fillFile(FileObject fo, String[] data)
    throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        FileLock lock = fo.lock();
        try {
            OutputStream out = fo.getOutputStream(lock);
            OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8"); // NOI18N
            AdvancedReplace aReplace = new AdvancedReplace(
                    wiz, function_template_xcu, parameter_template_xcu);
            try {
                writeStringArrayInFile(outWriter, aReplace, data);
            } finally {
                outWriter.close();
            }
        } finally {
            lock.releaseLock();
        }
    }
    
    private void writeStringArrayInFile(
            OutputStreamWriter outWriter, AdvancedReplace aReplace, String[]data)
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        for (int i=0; i<data.length; i++) {
            String[] nextData = aReplace.replaceVariables(data[i]);
            if (nextData.length == 1) {
                outWriter.write(nextData[0], 0, nextData[0].length());
                outWriter.write(CR, 0, CR.length());
            } else if (nextData.length > 0){ // just to avoid endless recursion
                outWriter.flush();
                writeStringArrayInFile(outWriter, aReplace, (String[])nextData);
            }
        }
        outWriter.flush();
    }

    private void skeletonmaker(AddOn addon) {
        String platform = PlatformInfo.getPlatformBinDir();
        String sdkPath = (String)wiz.getProperty("SdkPath"); // NOI18N
        String sdkBinPath = sdkPath.concat(File.separator).concat(platform).concat(File.separator).concat("bin"); // NOI18N
        // np exception? should not happen this far in the code
        if (OpenOfficeLocation.getOpenOfficeLocation().isThreeLayerOffice()) {
            sdkBinPath = sdkPath.concat(File.separator).concat("bin"); // NOI18N
        }
        String soPath = "";
        String[] typesRdbPath = null;
        OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation();
        if (loc != null) {
            soPath = loc.getPathVariable();
            if (!PlatformInfo.isWindows()) {
                soPath = soPath.concat(File.pathSeparator).concat("/usr/bin"); // NOI18N
            }
            typesRdbPath = loc.getUnoTypesPath();
        }
        else {
            typesRdbPath = new String[0];
        }

        // tempdir is needed for idlc
        Map<String,String> p = new HashMap<String,String>(5);
        p.put(ScriptExecutor.PATH, soPath);
        p.put(ScriptExecutor.LD_LIBRARY_PATH, soPath);
        p.put(ScriptExecutor.DYLD_LIBRARY_PATH, soPath);
        p.put(ScriptExecutor.TEMP, System.getProperty("java.io.tmpdir"));
        p.put(ScriptExecutor.TMP, System.getProperty("java.io.tmpdir"));
        ScriptExecutor.setEnv(p); // NOI18N
        
        String outFileWithPackage = ((String)wiz.getProperty("packageName")).concat(".").concat((String)wiz.getProperty("mainClassName")); // NOI18N

        try {
            String namePrefix = ((String)wiz.getProperty("Protocol")).concat(":"); // NOI18N
            NbNodeObject[] commands = addon.getAllCommands();
            for (int i=0; i<commands.length; i++) {
                if (i != 0)
                    namePrefix = namePrefix.concat(","); // NOI18N
                namePrefix = namePrefix.concat(
                        ((PropertyContainer)commands[i]).getSimpleProperty(PropertyContainer.PROPERTY_CONTAINER_NAME));
            }
        
            Vector<String> commandVector = new Vector<String>();
            commandVector.add(sdkBinPath.concat(File.separator).concat("uno-skeletonmaker")); // NOI18N
            
            if (typesRdbPath.length > 0) {
                String typesURLList = prepareTypesURLList(typesRdbPath);
                commandVector.add("-env:UNO_TYPES=".concat(typesURLList)); // NOI18N
            }          
            
            commandVector.add("add-on"); // NOI18N
            commandVector.add("--java5"); // NOI18N

            commandVector.add("-o".concat(((File)wiz.getProperty("projdir")).getCanonicalPath())
                .concat(File.separator).concat("src")); // NOI18N
            commandVector.add("-n".concat(outFileWithPackage)); // NOI18N

            commandVector.add("-p".concat(namePrefix)); // NOI18N
            
            String[] command = commandVector.toArray(new String[commandVector.size()]);
            StringBuffer c = new StringBuffer();
            for (int i=0; i<command.length; i++) {
                c.append(command[i]).append(" "); // NOI18N
            }
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, c.toString());
            ScriptExecutor.executeScript(command);
            if (ScriptExecutor.hasErrors()) {  // message box with errors...
                String message = NbBundle.getMessage(ProjectCreator.class, "ERROR_CommandExecute").concat(
                    "\n").concat(ScriptExecutor.getErrors()); // NOI18N
                JOptionPane.showMessageDialog(null, message, 
                    NbBundle.getMessage(ProjectCreator.class, "LOG_Level_critical"), JOptionPane.ERROR_MESSAGE); // NOI18N
            }
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }
    
    // TODO: show errors on output tab!
    private boolean skeletonmaker(FileObject[] idlFileObjects, String[] dumpTypes) throws IOException {
        boolean executeWorked = true; // return variable; set to false in case of error
        String projDir = ((File)wiz.getProperty("projdir")).getCanonicalPath(); // NOI18N
        String platform = PlatformInfo.getPlatformBinDir();
        String sdkPath = (String)wiz.getProperty("SdkPath"); // NOI18N
        String sdkBinPath = sdkPath.concat(File.separator).concat(platform).concat(File.separator).concat("bin"); // NOI18N
        String ureBinPath = sdkBinPath;
        // np exception? should not happen this far in the code
        if (OpenOfficeLocation.getOpenOfficeLocation().isThreeLayerOffice()) {
            sdkBinPath = sdkPath.concat(File.separator).concat("bin"); // NOI18N
            ureBinPath = OpenOfficeLocation.getOpenOfficeLocation().getUreBinPath();
        }               
        
        // adapt path for Ubuntu script execution of sdk tools
//        String soProgramPath = ((String)wiz.getProperty("OfficePath")).concat(File.separator).concat(PlatformInfo.getOfficeProgramDir()); // NOI18N
//        String soUrl = (soProgramPath.startsWith("/") ? "file://" : "file:///").concat(soProgramPath.replace('\\', '/')); // NOI18N
        String soPath = "";
        String[] typesRdbPath = null;
        OpenOfficeLocation loc = OpenOfficeLocation.getOpenOfficeLocation();
        if (loc != null) {
            soPath = loc.getPathVariable();
            if (!PlatformInfo.isWindows()) {
                soPath = soPath.concat(File.pathSeparator).concat("/usr/bin"); // NOI18N
            }
            typesRdbPath = loc.getUnoTypesPath();
        }
        else {
            typesRdbPath = new String[0];
        }

        
        // tempdir is needed for idlc
        Map<String,String> p = new HashMap<String,String>(5);
        p.put(ScriptExecutor.PATH, soPath);
        p.put(ScriptExecutor.LD_LIBRARY_PATH, soPath);
        p.put(ScriptExecutor.DYLD_LIBRARY_PATH, soPath);
        p.put(ScriptExecutor.TEMP, System.getProperty("java.io.tmpdir"));
        p.put(ScriptExecutor.TMP, System.getProperty("java.io.tmpdir"));
        ScriptExecutor.setEnv(p); // NOI18N
        
        // don't do all this if no own idls are defined.
        String rdbPath = null;
        if (idlFileObjects.length != 0) {
            // idlc
            String srcIdlPath = projDir.concat(File.separator).concat("src"); // NOI18N
            String sdkIdlIncludes =  sdkPath.concat(File.separator).concat("idl").concat(";").concat(srcIdlPath); // NOI18N

            String urdOutPath = projDir.concat(File.separator).concat("build").concat(File.separator)
            .concat("idl").concat(File.separator).concat("urd"); // NOI18N

            String[] command = new String[3 + idlFileObjects.length];
            command[0] = sdkBinPath.concat(File.separator).concat("idlc"); // NOI18N
            command[1] = "-I".concat(sdkIdlIncludes); // NOI18N
            command[2] = "-O".concat(urdOutPath); // NOI18N
            for (int i=0; i<idlFileObjects.length; i++) {
                command[3+i] = FileUtil.toFile(idlFileObjects[i]).getCanonicalPath();
            }

            ScriptExecutor.executeScript(command, (File)wiz.getProperty("projdir")); // NOI18N
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());

            // regmerge
            File f = new File(projDir.concat(File.separator).concat("build").concat(File.separator)
            .concat("idl").concat(File.separator).concat("rdb")); // NOI18N
            f.mkdirs();
            rdbPath = projDir.concat(File.separator).concat("build").concat(File.separator)
            .concat("idl").concat(File.separator).concat("rdb").concat(File.separator).concat("types.rdb"); // NOI18N

            command = new String[3 + idlFileObjects.length];
            command[0] = ureBinPath.concat(File.separator).concat("regmerge"); // NOI18N
            command[1] = rdbPath;
            command[2] = "/UCR"; // NOI18N
            for (int i=0; i<idlFileObjects.length; i++) {
                command[3+i] = urdOutPath.concat(File.separator)
                .concat(FileUtil.toFile(idlFileObjects[i]).getName().replaceAll("\\.idl", ".urd")); // NOI18N
            }

            ScriptExecutor.executeScript(command);
            if (ScriptExecutor.hasErrors()) {  // message box with errors...
                String message = NbBundle.getMessage(ProjectCreator.class, "ERROR_CommandExecute").concat(
                    "\n").concat(ScriptExecutor.getErrors()); // NOI18N
                JOptionPane.showMessageDialog(null, message,
                    NbBundle.getMessage(ProjectCreator.class, "LOG_Level_critical"), JOptionPane.ERROR_MESSAGE); // NOI18N
                executeWorked = false;
            }
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());
            
            // new part for initially creating a IDL_types.rdb
            // javamaker
            f = new File(projDir.concat(File.separator).concat("build").concat(File.separator)
            .concat("classes")); // NOI18N
            String classesOutPath = f.getCanonicalPath();
            f.mkdirs();
            command = new String[5 + typesRdbPath.length];
            command[0] = sdkBinPath.concat(File.separator).concat("javamaker"); // NOI18N
            command[1] = "-BUCR"; // NOI18N
            command[2] = "-O";
            command[3] = classesOutPath;
            command[4] = rdbPath;
            for (int i = 0; i < typesRdbPath.length; i++) {
                command[5 + i] = "-X".concat(typesRdbPath[i]);
            }
            
            ScriptExecutor.executeScript(command);
            if (ScriptExecutor.hasErrors()) {  // message box with errors...
                String message = NbBundle.getMessage(ProjectCreator.class, "ERROR_CommandExecute").concat(
                    "\n").concat(ScriptExecutor.getErrors()); // NOI18N
                JOptionPane.showMessageDialog(null, message,
                    NbBundle.getMessage(ProjectCreator.class, "LOG_Level_critical"), JOptionPane.ERROR_MESSAGE); // NOI18N
                executeWorked = false;
            }
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
            LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());

            // make a jar
            PrintStream outStream = new PrintStream(LogWriter.getLogWriter().getLogStream(LogWriter.LEVEL_INFO));
            PrintStream errStream = new PrintStream(LogWriter.getLogWriter().getLogStream(LogWriter.LEVEL_CRITICAL));
            Main jartool = new Main(outStream, errStream, "jar");
            
            f = new File(projDir.concat(File.separator).concat("dist"));
            f.mkdirs();
            command = new String[2  + 3 * idlFileObjects.length];
            final String IDL_JAR_NAME = (String)ProjectTypeHelper.getObjectFromUnoProperties(
                m_RootFile, "idl_types.jar"); // NOI18N
            String jarFile = f.getCanonicalPath().concat(File.separator).concat(IDL_JAR_NAME);
            f = new File(jarFile);
            if (f.exists()) {
                command[0] = "uf";
            }
            else {
                command[0] = "cf";
            }
            command[1] = jarFile;
            for (int i = 0; i < idlFileObjects.length; i++) {
                // remove first part of path to build correct directory structure in jar file
                String fileName = FileUtil.toFile(idlFileObjects[i]).getCanonicalPath().replace(srcIdlPath, ""); 
                command[2 + i * 3] = "-C";
                command[3 + i * 3] = classesOutPath; // build/classes
                command[4 + i * 3] = fileName.substring(1).replace(".idl", ".class"); // remove preceding "/"
                LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "File: " + command[4 + i * 3]);
            }

            jartool.run(command);
        }

        // skeletonmaker
        String outFileWithPackage = ((String)wiz.getProperty("packageName")).concat(".").concat((String)wiz.getProperty("mainClassName")); // NOI18N

        Vector<String> commandVector = new Vector<String>();
        commandVector.add(sdkBinPath.concat(File.separator).concat("uno-skeletonmaker")); // NOI18N
        
        if (typesRdbPath.length > 0) {
            String typesURLList = prepareTypesURLList(typesRdbPath);
            commandVector.add("-env:UNO_TYPES=".concat(typesURLList)); // NOI18N
        }
        
        commandVector.add(this.m_ProjectType==ADDIN_TYPE?"calc-add-in":"component"); // NOI18N

        commandVector.add("--java5"); // NOI18N
        if (idlFileObjects.length!=0)
            commandVector.add("-l".concat(PathExchanger.pathToOfficeFileUrl(rdbPath))); // NOI18N

        // this is different for components
        if (this.m_ProjectType==ADDIN_TYPE) {
            commandVector.add("-t".concat(outFileWithPackage)); // NOI18N
        }
        else {
            // add all the types.
            for (int i=0; i<dumpTypes.length; i++) {
                commandVector.add("-t".concat(dumpTypes[i])); // NOI18N
            }
        }
        commandVector.add("-o".concat(projDir)
        .concat(File.separator).concat("src")); // NOI18N
        if (this.m_ProjectType==ADDIN_TYPE) {
            commandVector.add("-n".concat(outFileWithPackage).concat("Impl")); // NOI18N
        }
        else {
            commandVector.add("-n".concat(outFileWithPackage)); // NOI18N
        }
                   
        String[] command = commandVector.toArray(new String[commandVector.size()]);
        StringBuffer c = new StringBuffer();
        for (int i=0; i<command.length; i++) {
            c.append(command[i]).append(" "); // NOI18N
        }
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, c.toString());
        ScriptExecutor.executeScript(command);
        if (ScriptExecutor.hasErrors()) {  // message box with errors...
            String message = NbBundle.getMessage(ProjectCreator.class, "ERROR_CommandExecute").concat(
                "\n").concat(ScriptExecutor.getErrors()); // NOI18N
            JOptionPane.showMessageDialog(null, message, 
                NbBundle.getMessage(ProjectCreator.class, "LOG_Level_critical"), JOptionPane.ERROR_MESSAGE); // NOI18N
            executeWorked = false;
        }
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, ScriptExecutor.getOutput());
        LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, ScriptExecutor.getErrors());
        return executeWorked;
    }
    
    /**
     * Call this for creating an implementation object for one or more  new idl file(s)
     * @param files the idl files for creating the implementation object
     */
    public boolean skeletonmaker(FileObject[] files) throws IOException {
        updateWizardDescriptor();
        String[] localIdlFiles = new String[files.length];
        FileObject sourceFO = m_RootFile.getFileObject("src"); // NOI18N
        for (int i = 0; i < files.length; i++) {
            int startPos = FileUtil.toFile(sourceFO).getCanonicalPath().length();
            String name = FileUtil.toFile(files[i]).getCanonicalPath().substring(startPos + 1);
            name = name.substring(0, name.lastIndexOf('.'));
            localIdlFiles[i] = name.replace(File.separatorChar, '.');
        }
        return skeletonmaker(files, localIdlFiles);
    }
    
    /**
     * Call this for creating an implementation object for one or more new idl file(s)
     * from OpenOffice.org
     * @param idlNames the idl files for creating the implementation object
     */
    public boolean skeletonmaker(String[] idlNames) throws IOException {
        updateWizardDescriptor();
        Vector<FileObject> localIdlFiles = new Vector<FileObject>();
        FileObject sourceFO = m_RootFile.getFileObject("src");
        for (int i = 0; i < idlNames.length; i++) {
            String name = idlNames[i].replace('.', '/').concat(".idl"); // NOI18N
            FileObject idlCandidate = sourceFO.getFileObject(name);
            if (idlCandidate != null && !idlCandidate.isVirtual()) {
                localIdlFiles.add(idlCandidate); // file exists in local path
            }
        }
        return skeletonmaker(localIdlFiles.toArray(new FileObject[localIdlFiles.size()]), idlNames);
    }
    
    private String prepareTypesURLList(String[] typesRdbPath) {
        String[] typeUrl = new String[typesRdbPath.length];
        String typesURLList = "";
        // make URL from path to types rdb
        for (int i = 0; i < typesRdbPath.length; i++) {
            String types = typesRdbPath[i].replace(" ", "%20"); // NOI18N
            types = (types.startsWith("/")?"file://":"file:///").concat(types); // NOI18N
            typeUrl[i] = types.replace("\\", "/"); // NOI18N
            if(i > 0) {
                typesURLList = typesURLList.concat(" ");
            }
            typesURLList = typesURLList.concat(typeUrl[i]);
        } 
        return typesURLList;
    }
    
    // debug purpose
    private String printCommand(String[] command) {
        String c = ""; // NOI18N
        for (int i=0; i<command.length; i++)
            c = c.concat(command[i]).concat(" "); // NOI18N
        return c;
    }
}
