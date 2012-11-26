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
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import java.util.Vector;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.projecttemplates.calcaddin.datamodel.AddIn;
import org.openoffice.extensions.util.datamodel.FunctionException;
import org.openoffice.extensions.util.datamodel.IdlEnumeration;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.PolyStruct;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.Struct;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 */
public class IdlFileCreator {

    private WizardDescriptor wiz;
    private Class templateBaseClass;
    private final String INTERFACE_TEMPLATE_NAME = "InterfaceTemplate"; // NOI18N
    private final String ENUMERATION_TEMPLATE_NAME = "EnumerationTemplate"; // NOI18N
    private final String STRUCT_TEMPLATE_NAME = "StructTemplate"; // NOI18N
    private final String POLY_STRUCT_TEMPLATE_NAME = "PolyStructTemplate"; // NOI18N
    private final String SERVICE_TEMPLATE_NAME = "ServiceTemplate"; // NOI18N
    private final String PROPS_FILE = "Bundle.properties"; // NOI18N
    private final String EXCEPTION_TEMPLATE_NAME = "ExceptionTemplate"; // NOI18N
    
    // define relation between template and data structure
    
    
    public static final String[][] IDL_JAVA_TYPE_MAPPING = new String[][] {
        {"void", "void"},
        {"long", "int"},
        {"double", "double"},
        {"string", "String"},
//        {"sequence < sequence < long > >", "int[][]"},
//        {"sequence < sequence < double > >", "double[][]"},
//        {"sequence < sequence < string > >", "String[][]"},
        {"any", "Object"},
//        {"sequence < any >", "Object[]"},
//        {"sequence < sequence < any > >", "Object[][]"},
        // just convenience for AddIns
        {"com::sun::star::table::XCellRange", "XCellRange"},
        {"com::sun::star::beans::XPropertySet", "XPropertySet"},
        {"com::sun::star::sheet::XVolatileResult", "XVolatileResult"},
    }; // NOI18N

    public static String getIdlTypeForJavaType(String javaType) {
        // replace dots with ::
        if (javaType.indexOf(".") != -1) {  // NOI18N
            javaType = javaType.replaceAll("\\.", "::"); // NOI18N
            return javaType;
        }
        for (int i=0; i<IDL_JAVA_TYPE_MAPPING.length; i++) {
            if (javaType.indexOf(IDL_JAVA_TYPE_MAPPING[i][1]) != -1) {
                javaType = javaType.replaceAll(IDL_JAVA_TYPE_MAPPING[i][1], IDL_JAVA_TYPE_MAPPING[i][0]);
            }
        }
        return javaType;
    }

    
    private String[] template_idl;
    private String baseDir;
    
    /** Creates a new instance of IdlFileCreator */
    public IdlFileCreator(WizardDescriptor wiz, 
            Class templateBaseClass, String baseDir) {
        this.wiz = wiz;
        this.templateBaseClass = templateBaseClass;
        this.baseDir = baseDir;
    }
    
    public FileObject[] createAllIdlFiles(NbNodeObject baseObject) {
        try {
            switch(baseObject.getType()) {
                case NbNodeObject.SERVICE_TYPE:
                    return createService((Service)baseObject);
                case NbNodeObject.STRUCT_TYPE:
                    return createStruct((Struct)baseObject);
                case NbNodeObject.POLY_STRUCT_TYPE:
                    return createPolyStruct((PolyStruct)baseObject);
                case NbNodeObject.INTERFACE_TYPE:
                    return createInterface((Interface)baseObject);
                case NbNodeObject.ENUMERATION_TYPE:
                    return createEnumeration((IdlEnumeration)baseObject);
                case NbNodeObject.EXCEPTION_TYPE:
                    return createException((FunctionException)baseObject);
                case NbNodeObject.ADDIN_TYPE:
                    return createAddIn((AddIn)baseObject);
                default:  // what to do in this case? -> bug in own implementation, real bad.
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Unkown type."); // NOI18N
            }
        }
        catch(java.lang.ClassCastException e) {
            LogWriter.getLogWriter().printStackTrace(e);  // this must not happen!
        }
        return null;
    }
    
    private FileObject[] createInterface(Interface ifc) {
        // in case of office types, no files have to be generated
        if (ifc.getType() == NbNodeObject.OFFICE_INTERFACE_TYPE) {
            return new FileObject[0];
        }
        
        Vector<FileObject> fileObjects = new Vector<FileObject>();

        // craete this interface
        FileObject o = createOneInterface(ifc);
        if (o != null)
            fileObjects.add(o);
        
        // go through this type and create all sub-interfaces
        Interface[] aggIfcs = ifc.getAllAggregatedInterfaces();
        for (int i=0; i<aggIfcs.length; i++) {
            FileObject[] addObjects = createInterface(aggIfcs[i]);
            for (int j=0; j<addObjects.length; j++) {
                fileObjects.add(addObjects[j]);
            }
        }

        return fileObjects.toArray(new FileObject[fileObjects.size()]);
    }
    
    private FileObject createOneInterface(Interface ifc) {
        // get the template
        String[] interface_template_idl = ProjectCreator.readFileLinesInArray(
                templateBaseClass, PROPS_FILE, INTERFACE_TEMPLATE_NAME);
        try {
            String packg = ifc.getSimpleProperty(ifc.PROPERTY_CONTAINER_PACKAGE);
            getModuleFromPackage(packg);
            String pkgDir = packg.replace('.', File.separatorChar);
            File f = new File(baseDir.concat(File.separator).concat(pkgDir));
            f.mkdirs();
            // do interface
            FileObject ifcObject = FileUtil.createData(
                    FileUtil.toFileObject(f), 
                    ifc.getSimpleProperty(ifc.PROPERTY_CONTAINER_NAME).concat(".idl") // NOI18N
            );
            AdvancedReplace aReplace = new AdvancedReplace(wiz, ifc);
            fillFile(aReplace, ifcObject, interface_template_idl);
            return ifcObject;
        }
        catch(IOException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        catch (UnknownOpenOfficeOrgLanguageIDException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        catch (UnknownOpenOfficeOrgPropertyException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        return null;
    }

    private FileObject[] createService(Service service) {
        // in case of office types, no files have to be generated
        if (service.getType() == NbNodeObject.OFFICE_SERVICE_TYPE) {
            return new FileObject[0];
        }
        // get the template
        String[] service_template_idl = ProjectCreator.readFileLinesInArray(
                templateBaseClass, PROPS_FILE, SERVICE_TEMPLATE_NAME);
        
        // go through this type and create all sub-interfaces
        String [] ifcObjectNames = service.getAllSetObjectNames();
        wiz.putProperty("Import", ifcObjectNames); // NOI18N
        Vector<FileObject> fileObjects = new Vector<FileObject>();
        if (ifcObjectNames.length == 1) { // just one interface: can be anything
            wiz.putProperty("XMultiInheritanceInterface", getIdlTypeForJavaType(ifcObjectNames[0])); // NOI18N
        }
        // create the interface if it needs to be done
        for (int i=0; i<ifcObjectNames.length; i++) {
            Interface ifc = (Interface)service.getSetObject(ifcObjectNames[i]);
            if (ifc != null) {
                FileObject[] addObjects = createInterface(ifc);
                for (int j=0; j<addObjects.length; j++) {
                    fileObjects.add(addObjects[j]);
                }
            }
        }

        try {
            String packg = service.getSimpleProperty(Service.PROPERTY_CONTAINER_PACKAGE);
            getModuleFromPackage(packg);
            String pkg = packg.replace('.', File.separatorChar);
            File f = new File(baseDir.concat(File.separator).concat(pkg));
            f.mkdirs();
            String name = service.getSimpleProperty(Service.PROPERTY_CONTAINER_NAME);
            // do service
            FileObject serviceObject = FileUtil.createData(
                    FileUtil.toFileObject(f), 
                    name.concat(".idl") // NOI18N
            );
            AdvancedReplace aReplace = new AdvancedReplace(wiz, service);
            fillFile(aReplace, serviceObject, service_template_idl);
            fileObjects.add(serviceObject);
        }
        catch(IOException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        catch (UnknownOpenOfficeOrgLanguageIDException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        catch (UnknownOpenOfficeOrgPropertyException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        return (FileObject[])fileObjects.toArray(new FileObject[fileObjects.size()]);
    }
    
    private FileObject[] createEnumeration(IdlEnumeration idlEnumeration) {
        try {
            String packg = idlEnumeration.getSimpleProperty(idlEnumeration.PROPERTY_CONTAINER_PACKAGE);
            getModuleFromPackage(packg);
            String pkg = packg.replace('.', File.separatorChar);
            File f = new File(baseDir.concat(File.separator).concat(pkg));
            f.mkdirs();
            String enumName = idlEnumeration.getSimpleProperty(IdlEnumeration.PROPERTY_CONTAINER_NAME);
            String[] enumeration_template_idl = ProjectCreator.readFileLinesInArray(
                    templateBaseClass, PROPS_FILE, ENUMERATION_TEMPLATE_NAME);
            FileObject enumObject = FileUtil.createData(FileUtil.toFileObject(f), enumName.concat(".idl")); // NOI18N
            AdvancedReplace aReplace = new AdvancedReplace(wiz, idlEnumeration);
            fillFile(aReplace, enumObject, enumeration_template_idl);
            return new FileObject[]{enumObject};
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return new FileObject[0];
    }
    
    private FileObject[] createException(FunctionException exception) {
        try {
            String packg = exception.getSimpleProperty(exception.PROPERTY_CONTAINER_PACKAGE);
            getModuleFromPackage(packg);
            String pkg = packg.replace('.', File.separatorChar);
            File f = new File(baseDir.concat(File.separator).concat(pkg));
            f.mkdirs();
            String enumName = exception.getSimpleProperty(FunctionException.PROPERTY_CONTAINER_NAME);
            String[] enumeration_template_idl = ProjectCreator.readFileLinesInArray(
                    templateBaseClass, PROPS_FILE, EXCEPTION_TEMPLATE_NAME);
            FileObject enumObject = FileUtil.createData(FileUtil.toFileObject(f), enumName.concat(".idl")); // NOI18N
            AdvancedReplace aReplace = new AdvancedReplace(wiz, exception);
            fillFile(aReplace, enumObject, enumeration_template_idl);
            return new FileObject[]{enumObject};
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return new FileObject[0];
    }
    
    private FileObject[] createStruct(Struct struct) {
        try {
            String packg = struct.getSimpleProperty(struct.PROPERTY_CONTAINER_PACKAGE);
            getModuleFromPackage(packg);
            String pkg = packg.replace('.', File.separatorChar);
            File f = new File(baseDir.concat(File.separator).concat(pkg));
            f.mkdirs();
            String enumName = struct.getSimpleProperty(struct.PROPERTY_CONTAINER_NAME);
            String[] enumeration_template_idl = ProjectCreator.readFileLinesInArray(
                    templateBaseClass, PROPS_FILE, STRUCT_TEMPLATE_NAME);
            FileObject enumObject = FileUtil.createData(FileUtil.toFileObject(f), enumName.concat(".idl")); // NOI18N
            AdvancedReplace aReplace = new AdvancedReplace(wiz, struct);
            fillFile(aReplace, enumObject, enumeration_template_idl);
            return new FileObject[]{enumObject};
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return new FileObject[0];
    }
    
    private FileObject[] createPolyStruct(PolyStruct struct) {
        try {
            String packg = struct.getSimpleProperty(struct.PROPERTY_CONTAINER_PACKAGE);
            getModuleFromPackage(packg);
            String pkg = packg.replace('.', File.separatorChar);
            File f = new File(baseDir.concat(File.separator).concat(pkg));
            f.mkdirs();
            String enumName = struct.getSimpleProperty(struct.PROPERTY_CONTAINER_NAME);
            String[] enumeration_template_idl = ProjectCreator.readFileLinesInArray(
                    templateBaseClass, PROPS_FILE, POLY_STRUCT_TEMPLATE_NAME);
            FileObject enumObject = FileUtil.createData(FileUtil.toFileObject(f), enumName.concat(".idl")); // NOI18N
            AdvancedReplace aReplace = new AdvancedReplace(wiz, struct);
            fillFile(aReplace, enumObject, enumeration_template_idl);
            return new FileObject[]{enumObject};
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return new FileObject[0];
    }

    private FileObject[] createAddIn(AddIn object) {
        String packg = (String)wiz.getProperty("UnoPackage"); // NOI18N
        getModuleFromPackage(packg);
        String pkg = packg.replace('.', File.separatorChar);
        
        // get the template
        String[] service_template_idl = ProjectCreator.readFileLinesInArray(
                templateBaseClass, PROPS_FILE, SERVICE_TEMPLATE_NAME);
        // get the template
        String[] interface_template_idl = ProjectCreator.readFileLinesInArray(
                templateBaseClass, PROPS_FILE, INTERFACE_TEMPLATE_NAME);
        
        try {
            // do service
            FileObject service = FileUtil.createData(
                    FileUtil.toFileObject(new File(baseDir.concat(File.separator).concat(pkg))), 
                    ((String)wiz.getProperty("mainClassName")).concat(".idl") // NOI18N
            );
            AdvancedReplace aReplace = new AdvancedReplace(wiz, null, null);
            fillFile(aReplace, service, service_template_idl);
            
            // do interface
            FileObject intrface = FileUtil.createData(
                    FileUtil.toFileObject(new File(baseDir.concat(File.separator).concat(pkg))),
                    "X".concat((String)wiz.getProperty("mainClassName")).concat(".idl") // NOI18N
            );
            aReplace = new AdvancedReplace(wiz, object);
            fillFile(aReplace, intrface, interface_template_idl);
            
            return new FileObject[]{service, intrface};
        }
        catch(IOException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        catch (UnknownOpenOfficeOrgLanguageIDException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        catch (UnknownOpenOfficeOrgPropertyException e) {
            LogWriter.getLogWriter().printStackTrace(e);
        }
        return null;  // 2do here?
    }

    private void fillFile(AdvancedReplace replace, FileObject fo, String[] data) 
        throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        FileLock lock = fo.lock();
        try {
            OutputStream out = fo.getOutputStream(lock);
            OutputStreamWriter outWriter = new OutputStreamWriter(out);
            try {
                writeStringArrayInFile(outWriter, replace, data);
            }
            finally {
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
                outWriter.write("\n", 0, 1); // NOI18N
            }
            else if (nextData.length > 0){ // just to avoid endless recursion
                outWriter.flush();
                writeStringArrayInFile(outWriter, aReplace, (String[])nextData);
            }
        }
        outWriter.flush();
    }
    
    public void getModuleFromPackage(String pkg) {
        String packageNameUnderscore = pkg.replace('.', '_');
        
        StringTokenizer t = new StringTokenizer(pkg, "."); // NOI18N
        String packageNameModule = "module ".concat(t.nextToken()).concat(" {"); // NOI18N
        String closeModule = "};"; // NOI18N
        while(t.hasMoreTokens()) {
            packageNameModule = packageNameModule.concat(" module ").concat(t.nextToken()).concat(" {"); // NOI18N
            closeModule = closeModule.concat(" };"); // NOI18N
        }
        wiz.putProperty("packageNameModule", packageNameModule); // NOI18N
        wiz.putProperty("closeModule", closeModule); // NOI18N
        wiz.putProperty("packageNameUnderscore", packageNameUnderscore); // NOI18N
    }
}
