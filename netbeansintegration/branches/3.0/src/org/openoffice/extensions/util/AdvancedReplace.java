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

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openoffice.extensions.projecttemplates.addon.datamodel.AddOn;
import org.openoffice.extensions.projecttemplates.addon.datamodel.Command;
import org.openoffice.extensions.projecttemplates.addon.datamodel.SeparatorElement;
import org.openoffice.extensions.projecttemplates.addon.datamodel.SubMenuElement;
import org.openoffice.extensions.projecttemplates.calcaddin.datamodel.AddIn;
import org.openoffice.extensions.util.datamodel.Function;
import org.openoffice.extensions.util.datamodel.FunctionException;
import org.openoffice.extensions.util.datamodel.IdlEnum;
import org.openoffice.extensions.util.datamodel.IdlEnumeration;
import org.openoffice.extensions.util.datamodel.Interface;
import org.openoffice.extensions.util.datamodel.NbNodeObject;
import org.openoffice.extensions.util.datamodel.Parameter;
import org.openoffice.extensions.util.datamodel.PolyStruct;
import org.openoffice.extensions.util.datamodel.PropertyContainer;
import org.openoffice.extensions.util.datamodel.Service;
import org.openoffice.extensions.util.datamodel.Struct;
import org.openoffice.extensions.util.datamodel.TemplateType;
import org.openoffice.extensions.util.datamodel.localization.LanguageDefinition;
import org.openoffice.extensions.util.datamodel.properties.LocalizedOpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgBooleanProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgIconProperty;
import org.openoffice.extensions.util.datamodel.properties.OpenOfficeOrgProperty;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgLanguageIDException;
import org.openoffice.extensions.util.datamodel.properties.UnknownOpenOfficeOrgPropertyException;

/**
 *
 */
public class AdvancedReplace {
    
    private static final String cr = System.getProperty("line.separator"); // NOI18N

    private static final int SIMPLE_REPLACE = 0;
    private static final int XCU_REPLACE = 1;
    private static final int INTERFACE_REPLACE = 2;
    private static final int SERVICE_REPLACE = 3;
    private static final int ADDIN_REPLACE = 4;
    private static final int ENUMERATION_REPLACE = 5;
    private static final int STRUCT_REPLACE = 6;
    private static final int EXCEPTION_REPLACE = 7;
    private static final int POLY_STRUCT_REPLACE = 8;
    private static final int ADDON_REPLACE = 9;
    private static final String EMPTY_VALUE = "<value/>"; // NOI18N

    private int replaceType;
    
    private int functionCount = 0;
    private int parameterCount = 0;
    private String[] function_template_xcu;
    private String[] parameter_template_xcu;
    
    private int menuOrderNumber;
    private int toolbarOrderNumber;
    
    private WizardDescriptor wiz;

    private Interface ifc;
    private Service srv;
    private AddIn addin;
    private IdlEnumeration enm;
    private Struct str;
    private FunctionException ex;
    private PolyStruct pstr;

    
    /** Creates a new instance of AdvancedReplace */
    public AdvancedReplace(
            WizardDescriptor wiz,
            String[] function_template_xcu, 
            String[] parameter_template_xcu) {
        this.function_template_xcu = function_template_xcu;
        this.parameter_template_xcu = parameter_template_xcu;
        if (function_template_xcu == null && parameter_template_xcu == null)
            replaceType = SIMPLE_REPLACE;
        else
            replaceType = XCU_REPLACE;
        this.wiz = wiz;
    }

    /** Creates a new instance of AdvancedReplace */
    public AdvancedReplace(WizardDescriptor wiz, AddOn addon) {
        replaceType = ADDON_REPLACE;
        this.wiz = wiz;
    }

    /** Creates a new instance of AdvancedReplace */
    public AdvancedReplace(WizardDescriptor wiz, SubMenuElement topElement) {
        replaceType = ADDON_REPLACE;
        this.wiz = wiz;
    }

    public AdvancedReplace(WizardDescriptor wiz, Interface ifc) {
        this.wiz = wiz;
        this.ifc = ifc;
        replaceType = INTERFACE_REPLACE;
    }

    public AdvancedReplace(WizardDescriptor wiz, Service srv) {
        this.wiz = wiz;
        this.srv = srv;
        replaceType = SERVICE_REPLACE;
    }

    public AdvancedReplace(WizardDescriptor wiz, IdlEnumeration enm) {
        this.wiz = wiz;
        this.enm = enm;
        replaceType = ENUMERATION_REPLACE;
    }

    public AdvancedReplace(WizardDescriptor wiz, FunctionException ex) {
        this.wiz = wiz;
        this.ex = ex;
        replaceType = EXCEPTION_REPLACE;
    }

    public AdvancedReplace(WizardDescriptor wiz, Struct str) {
        this.wiz = wiz;
        this.str = str;
        replaceType = STRUCT_REPLACE;
    }

    public AdvancedReplace(WizardDescriptor wiz, PolyStruct pstr) {
        this.wiz = wiz;
        this.pstr = pstr;
        replaceType = POLY_STRUCT_REPLACE;
    }

    public AdvancedReplace(WizardDescriptor wiz, AddIn addin) {
        this.wiz = wiz;
        this.addin = addin;
        replaceType = ADDIN_REPLACE;
    }

    /** Creates a new instance of AdvancedReplace */
    public AdvancedReplace(
            WizardDescriptor wiz) {
        this(wiz, null, null);
    }

    public String[] replaceVariables(String sourceString) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        if (sourceString == null || sourceString.length() == 0) {
            return new String[0];
        }
        switch(replaceType) {
            case INTERFACE_REPLACE:
                return doInterfaceReplace(sourceString);
            case SERVICE_REPLACE:
                return doServiceReplace(sourceString);
            case ADDIN_REPLACE:
                return doAddInReplace(sourceString);
            case ENUMERATION_REPLACE:
                return doEnumerationReplace(sourceString);
            case STRUCT_REPLACE:
                return doStructReplace(sourceString);
            case POLY_STRUCT_REPLACE:
                return doPolyStructReplace(sourceString);
            case EXCEPTION_REPLACE:
                return doExceptionReplace(sourceString);
            case ADDON_REPLACE:
                return doAddOnReplace(sourceString);
            default:
                return doSimpleReplace(sourceString);
        }
    }
    
    private String[] doSimpleReplace(String sourceString) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException {
        String output = sourceString.replaceAll("\\$\\!ProjectName\\!", 
                        (String)wiz.getProperty("name")); // NOI18N
        output = output.replaceAll("\\$\\!PackageName\\!", 
                                (String)wiz.getProperty("UnoPackage")); // NOI18N
        output = output.replaceAll("\\$\\!CR\\!", cr); // NOI18N
        output = output.replaceAll("\\$\\!PackageNameUnderscore\\!", 
                                (String)wiz.getProperty("packageNameUnderscore")); // NOI18N
        output = output.replaceAll("\\$\\!PackageNameModule\\!", 
                                (String)wiz.getProperty("packageNameModule")); // NOI18N
        output = output.replaceAll("\\$\\!TimeStamp\\!", 
                                (String)wiz.getProperty("TimeStamp")); // NOI18N
        output = output.replaceAll("\\$\\!CloseModule\\!", 
                                (String)wiz.getProperty("closeModule")); // NOI18N
        output = output.replaceAll("\\$\\!MainClass\\!", 
                                (String)wiz.getProperty("mainClassName")); // NOI18N
        output = output.replaceAll("\\$\\!MainClassSmall\\!", 
                                (String)wiz.getProperty("mainClassNameSmall")); // NOI18N
        output = output.replaceAll("\\$\\!AddonContext\\!", 
                                (String)wiz.getProperty("Context")); // NOI18N
        output = output.replaceAll("\\$\\!Protocol\\!", 
                                (String)wiz.getProperty("Protocol")); // NOI18N
        output = output.replaceAll("\\$\\!StructName\\!", 
                                (String)wiz.getProperty("StructName")); // NOI18N
        output = output.replaceAll("\\$\\!PolyStructName\\!", 
                                (String)wiz.getProperty("PolyStructName")); // NOI18N
        output = output.replaceAll("\\$\\!PolyStructNameTemplate\\!", 
                                (String)wiz.getProperty("PolyStructNameTemplate")); // NOI18N
        output = output.replaceAll("\\$\\!ExceptionName\\!", 
                                (String)wiz.getProperty("ExceptionName")); // NOI18N
        output = output.replaceAll("\\$\\!ParentExceptionName\\!", 
                                (String)wiz.getProperty("ParentExceptionName")); // NOI18N
        output = output.replaceAll("\\$\\!Service\\!", 
                                (String)wiz.getProperty("Service")); // NOI18N
        output = output.replaceAll("\\$\\!XMultiInheritanceInterface\\!", 
                                (String)wiz.getProperty("XMultiInheritanceInterface")); // NOI18N
        output = output.replaceAll("\\$\\!EnumName\\!", 
                                (String)wiz.getProperty("EnumName")); // NOI18N
        if (output.indexOf("$!Os!") != -1) {
            output = output.replaceAll("\\$\\!Os\\!",  // NOI18N
                                "os.dir=".concat((String)wiz.getProperty("PlatformBinDir"))); // NOI18N
        }
        if (output.indexOf("$!Sdk!") != -1) { // NOI18N
            output = output.replaceAll("\\$\\!Sdk\\!", 
                                "sdk=".concat(((String)wiz.getProperty("SdkPath")).replaceAll("\\\\", "\\\\\\\\\\\\\\\\"))); // NOI18N
        }
        if (output.indexOf("$!Soffice!") != -1) {
            output = output.replaceAll("\\$\\!Soffice\\!", 
                                "soffice.program=".concat(((String)wiz.getProperty("OfficePath")).replaceAll("\\\\", "\\\\\\\\\\\\\\\\"))); // NOI18N
        }
        if (output.indexOf("$!ProjectDir!") != -1) {
            output = output.replaceAll("\\$\\!ProjectDir\\!", 
                                "project.dir=".concat(((String)wiz.getProperty("ProjectDir")).replaceAll("\\\\", "\\\\\\\\\\\\\\\\"))); // NOI18N
        }

        // special stuff with more interaction
        if (output.indexOf("$!Import!") != -1) { // NOI18N
            Vector<String> importContainer = new Vector<String>();
            importContainer.add("\n#include <com/sun/star/uno/XInterface.idl>"); // NOI18N
            int funcCount = 0;
            if (wiz.getProperty("XInterface") == null) { // NOI18N
                funcCount = ((Integer)wiz.getProperty("functionCount")).intValue(); // NOI18N
            }
            else {
                String ifc = (String)wiz.getProperty("XInterface"); // NOI18N
                funcCount = ((Integer)wiz.getProperty(
                        ifc.concat("functionCount"))).intValue(); // NOI18N
            }
            for (int i=0; i<funcCount; i++) {
                String[] function = null;
                if (wiz.getProperty("XInterface") == null) { // NOI18N
                    function = (String[])wiz.getProperty(new StringBuffer("function").append(i).toString());  // NOI18N
                }
                else {
                    function = (String[])wiz.getProperty(new StringBuffer((String)wiz.getProperty("XInterface")).append("function").append(i).toString()); // NOI18N
                }
                if (function[0].equals("XVolatileResult")) { // NOI18N
                    importContainer.add("\n#include <com/sun/star/sheet/XVolatileResult.idl>"); // NOI18N
                }
                boolean[] imported = new boolean[2];
                int param = 2;
                while (param<function.length) {
                    if (function[param].equals("XCellRange") && !imported[0]) { // NOI18N
                        importContainer.add("\n#include <com/sun/star/table/XCellRange.idl>"); // NOI18N
                        imported[0] = true;
                    }
                    if (function[param].equals("XPropertySet") && !imported[1]) { // NOI18N
                        importContainer.add("\n#include <com/sun/star/beans/XPropertySet.idl>"); // NOI18N
                        imported[1] = true;
                    }
                    param += 2;
                }
            }
            importContainer.add(getExceptionImports(
                    (NbNodeObject)wiz.getProperty("ServiceObject"))); // NOI18N

            if (output.length() > 0) {
                importContainer.add("\n"); // NOI18N
            }
            return importContainer.toArray(new String[importContainer.size()]);
        } 
        else if (output.indexOf("$!Methods!") != -1) { // NOI18N
            LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "######### OOOOOPSS ###########"); // NOI18N
        }
        else if (output.indexOf("$!xcuFunctions!") != -1) { // NOI18N
            int funcCount = ((Integer)wiz.getProperty("functionCount")).intValue(); // NOI18N
            if (functionCount < funcCount) {
                boolean lastFunction = functionCount == funcCount - 1;
                parameterCount = 3;
                functionCount++;
                if (lastFunction) return function_template_xcu;
                String[] retString = new String[function_template_xcu.length + 1];
                for (int i=0; i<function_template_xcu.length; i++) {
                    retString[i] = function_template_xcu[i];
                }
                retString[function_template_xcu.length] = "$!xcuFunctions!"; // NOI18N
                return retString;
            }
        }
        else if (output.indexOf("$!xcuParameters!") != -1) { // NOI18N
            String[] function = (String[])wiz.getProperty(new StringBuffer("function").append(functionCount-1).toString()); // NOI18N
            if (parameterCount < function.length) {
                String parameter = function[parameterCount];
                boolean lastParameter = parameterCount == function.length - 1;
                parameterCount += 2;
                if (lastParameter) return parameter_template_xcu;
                String[] retString = new String[parameter_template_xcu.length + 1];
                for (int i=0; i<parameter_template_xcu.length; i++) {
                    retString[i] = parameter_template_xcu[i];
                }
                retString[parameter_template_xcu.length] = "$!xcuParameters!"; // NOI18N
                return retString;
            }
        }
        else if (output.indexOf("$!xcuFunctionName!") != -1) { // NOI18N
            String[] function = (String[])wiz.getProperty(new StringBuffer("function").append(functionCount-1).toString()); // NOI18N
            output = output.replaceAll("\\$\\!xcuFunctionName\\!", function[1]); // NOI18N
        }
        else if (output.indexOf("$!xcuParameterName!") != -1) { // NOI18N
            String[] function = (String[])wiz.getProperty(new StringBuffer("function").append(functionCount-1).toString()); // NOI18N
            output = output.replaceAll("\\$\\!xcuParameterName\\!", function[parameterCount - 2]); // NOI18N
        }
        else if(output.indexOf("$!xcuCategory!") != -1) { // NOI18N
            String[] function = (String[])wiz.getProperty(new StringBuffer("function").append(functionCount-1).toString()); // NOI18N
            output = output.replaceAll("\\$\\!xcuCategory\\!", (String)wiz.getProperty(function[1].concat("Category"))); // NOI18N
        }
        // multilingual stuff
        else if (output.indexOf("$!Lang!") != -1) {   // NOI18N
            String[] returnVal = null;
            String[] function = (String[])wiz.getProperty(new StringBuffer("function").append(functionCount-1).toString()); // NOI18N
            String functionName = function[1];
            String parameterName = function[parameterCount - 2]; // not always needed but easy to get here
            String[][] paramPropNames = new String[][] {
                {"$!xcuFunctionDisplayName!", functionName.concat("DisplayName")},
                {"$!xcuFunctionDescription!", functionName.concat("Description")},
                {"$!xcuFunctionCategoryDisplayName!", functionName.concat("Description")},
                {"$!xcuFunctionCompatibilityName!", functionName.concat("CompatibilityName")},
                {"$!xcuParameterDisplayName!", functionName.concat(parameterName).concat("DisplayName")},
                {"$!xcuParameterDescription!", functionName.concat(parameterName).concat("Description")},
            }; // NOI18N
            return replaceXcuParameters(paramPropNames, output);
        }
        return new String[]{output};
    }

    private String[] replaceXcuParameters(String[][] paramPropNames, String output) {
        Vector<String> returnVal = new Vector<String>();
        for (int i=0; i<paramPropNames.length; i++) {
            if (output.indexOf(paramPropNames[i][0]) != -1) {
                String[][]props = (String[][])wiz.getProperty(paramPropNames[i][1]);
                for (int j=0; j<props[0].length; j++) {
                    if (props[1][j] != null && props[1][j].length() != 0) {
                        String searchString = paramPropNames[i][0].replaceAll("\\$", "\\\\\\$").replaceAll("\\!", "\\\\\\!"); // NOI18N
                        returnVal.add(output.replaceAll(
                            "\\$\\!Lang\\!", props[0][j]).replaceAll( // NOI18N
                            searchString, props[1][j]));
                    }
                }
                if (returnVal.size() == 0) {
                    // replace to keep the indent
                    String emptyValue = output.replaceAll("^( *).*$", "$1<value/>"); // NOI18N
                    returnVal.add(emptyValue);
                }
            }
        }
        return returnVal.toArray(new String[returnVal.size()]);
    }
    
    private String[] doInterfaceReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        if(output.indexOf("$!Import!") != -1) { // NOI18N
            String[] usedTypes = getAllUsedTypes(ifc); // used types must be imported
            Interface[] addIfcs = ifc.getAllAggregatedInterfaces();
            Vector<String>importLines = new Vector<String>();
            // first imported interfaces
            for (int i=0; i<usedTypes.length; i++) {
                importLines.add(usedTypes[i]);
            } 
            // then exceptions
            importLines.add(getExceptionImports(ifc));
            // XInterface -> will be removed aome time later
            importLines.add("\n#include <com/sun/star/uno/XInterface.idl>"); // NOI18N
            // rest are included interfaces
            for(int i=0; i<addIfcs.length; i++) {
                StringBuffer oneOutputLine = new StringBuffer("\n"); // NOI18N
                String pkg = addIfcs[i - 1].getSimpleProperty(ifc.PROPERTY_CONTAINER_PACKAGE).replaceAll("\\.", "/"); // NOI18N
                String name = addIfcs[i - 1].getSimpleProperty(ifc.PROPERTY_CONTAINER_NAME);
                oneOutputLine.append("\n#include <").append(pkg).append("/").append(name).append(".idl>"); // NOI18N
                importLines.add(oneOutputLine.toString());
            }
            String[] lines = importLines.toArray(new String[importLines.size()]);
            lines[lines.length - 1] = lines[lines.length - 1].concat("\n"); // NOI18N
            return lines;
        }
        else if(output.indexOf("$!Import2!") != -1) { // NOI18N
            String whitespace = output.substring(0, output.indexOf("$!Import2!")); // NOI18N
            Interface[] addIfcs = ifc.getAllAggregatedInterfaces();
            String[] lines = new String[addIfcs.length];
            for(int i=0; i<lines.length; i++) {
                StringBuffer oneOutputLine = new StringBuffer("\n"); // NOI18N
                String pkg = addIfcs[i].getSimpleProperty(ifc.PROPERTY_CONTAINER_PACKAGE).replaceAll("\\.", "::"); // NOI18N
                String name = addIfcs[i].getSimpleProperty(ifc.PROPERTY_CONTAINER_NAME);
                oneOutputLine.append(whitespace).append(" ::".concat(pkg).concat("::").concat(name).concat(";\n")); // NOI18N
                lines[i] = oneOutputLine.toString();
            }
            
            return lines;
        }
        else if(output.indexOf("$!XMultiInheritanceInterface!") != -1) { // NOI18N
            wiz.putProperty("XMultiInheritanceInterface", ifc.getSimpleProperty(ifc.PROPERTY_CONTAINER_NAME)); // NOI18N
        }
        else if (output.indexOf("$!Methods!") != -1) { // NOI18N
            String whitespace = output.substring(0, output.indexOf("$!Methods!")); // NOI18N
            String[] funcNames = this.ifc.getAllSetObjectNames();
            String[] returnVal = new String[funcNames.length];
            for (int i=0; i<funcNames.length; i++) {
                StringBuffer oneOutputLine = new StringBuffer("\n").append(whitespace); // NOI18N
                Function function = (Function)ifc.getSetObject(funcNames[i]);
                String funcName = (String)function.getProperty(function.PROPERTY_Name).getValueForLanguage(-1);
                String funcType = (String)function.getProperty(function.PROPERTY_Type).getValueForLanguage(-1);
                funcType = getArrays(funcType);
                oneOutputLine.append(IdlFileCreator.getIdlTypeForJavaType(funcType)).append(" ").append(funcName).append("("); // NOI18N
                String[] paramNames = function.getAllSetObjectNames();
                for (int j=0; j<paramNames.length; j++) {
                    Parameter param = (Parameter)function.getSetObject(paramNames[j]);
                    String paramName = (String)param.getProperty(param.PROPERTY_Name).getValueForLanguage(-1);
                    String paramType = (String)param.getProperty(param.PROPERTY_Type).getValueForLanguage(-1);
                    paramType = getArrays(paramType);
                    oneOutputLine.append("[in] ").append(IdlFileCreator.getIdlTypeForJavaType(paramType)).append(" ").append(paramName); // NOI18N
                    if (j < paramNames.length - 1) {
                        oneOutputLine.append(", "); // NOI18N
                    }
                }
                String exceptions = function.getProperty(function.PROPERTY_Exceptions).getValueForLanguage(-1);
                if (exceptions.length() == 0)
                    oneOutputLine.append(");"); // NOI18N
                else {
                    oneOutputLine.append(")\n"); // NOI18N
                    whitespace = "    ".concat(whitespace); // NOI18N
                    String indent = "\n".concat(whitespace).concat("         "); // NOI18N
                    exceptions = exceptions.replace(".", "::"); // NOI18N
                    exceptions = exceptions.replace(" ", indent); // NOI18N
                    oneOutputLine.append(whitespace).append("raises ( ").append(exceptions).append(" );"); // NOI18N
                }
                returnVal[i] = oneOutputLine.toString();
            }
            return returnVal;
        }
        return doSimpleReplace(output);
    }

    private String[] doEnumerationReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        if(output.indexOf("$!EnumName!") != -1) { // NOI18N
            wiz.putProperty("EnumName", enm.getSimpleProperty(enm.PROPERTY_CONTAINER_NAME)); // NOI18N
        }
        else if (output.indexOf("$!Enums!") != -1) { // NOI18N
            Vector<String> lines = new Vector<String>();
            String whitespace = output.substring(0, output.indexOf("$!Enums!")); // NOI18N
            String[] enumNames = this.enm.getAllSetObjectNames();
            for (int i=0; i<enumNames.length; i++) {
                IdlEnum oneEnum = (IdlEnum)this.enm.getSetObject(enumNames[i]);
                String name = oneEnum.getProperty(oneEnum.PROPERTY_Name).getValueForLanguage(-1);
                String oneLine = "\n".concat(whitespace).concat(name); // NOI18N
                if (i == enumNames.length - 1) {
                    oneLine = oneLine.concat("\n"); // NOI18N
                }
                else {
                    oneLine = oneLine.concat(",\n"); // NOI18N
                }
                lines.add(oneLine);
            }
            return lines.toArray(new String[lines.size()]);
        }
        return doSimpleReplace(output);
    }

    private String[] doStructReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        if(output.indexOf("$!StructName!") != -1) { // NOI18N
            wiz.putProperty("StructName", str.getSimpleProperty(str.PROPERTY_CONTAINER_NAME)); // NOI18N
        }
        else if (output.indexOf("$!Structs!") != -1) { // NOI18N
            Vector<String> lines = new Vector<String>();
            String whitespace = output.substring(0, output.indexOf("$!Structs!")); // NOI18N
            String[] structNames = this.str.getAllSetObjectNames();
            for (int i=0; i<structNames.length; i++) {
                Parameter param = (Parameter)this.str.getSetObject(structNames[i]);
                String name = param.getProperty(param.PROPERTY_Name).getValueForLanguage(-1);
                String type = param.getProperty(param.PROPERTY_Type).getValueForLanguage(-1);
                type = getArrays(type);
                lines.add("\n"); // NOI18N
                lines.add(whitespace.concat(IdlFileCreator.getIdlTypeForJavaType(
                        type)).concat(" ").concat(name).concat(";\n")); // NOI18N
            }
            return lines.toArray(new String[lines.size()]);
        }
        return doSimpleReplace(output);
    }

    private String[] doPolyStructReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException, UnknownOpenOfficeOrgPropertyException {
        if(output.indexOf("$!PolyStructName!") != -1) { // NOI18N
            wiz.putProperty("PolyStructName", pstr.getSimpleProperty(pstr.PROPERTY_CONTAINER_NAME)); // NOI18N
        }
        else if(output.indexOf("$!PolyStructNameTemplate!") != -1) { // NOI18N
            StringBuffer templateStructName = new StringBuffer(pstr.getSimpleProperty(srv.PROPERTY_CONTAINER_NAME));
            templateStructName.append("<"); // NOI18N
            String[] typeNames = pstr.getTemplateTypeNames();
            for (int i=0; i<typeNames.length; i++) {
                if (i != 0) {
                    templateStructName.append(","); // NOI18N
                }
                // get type and append
                templateStructName.append(pstr.getTemplateType(
                        typeNames[i]).getSimpleProperty(TemplateType.PROPERTY_CONTAINER_TYPE));
                templateStructName.append(">"); // NOI18N
            }
            wiz.putProperty("PolyStructNameTemplate", templateStructName.toString()); // NOI18N
        }
        else if (output.indexOf("$!Import!") != -1) { // NOI18N
            String[] propNames = pstr.getAllSetObjectNames();
            Vector<String>importLines = new Vector<String>();
            for (int i=0; i<propNames.length; i++) {
                Parameter param = (Parameter)pstr.getSetObject(propNames[i]);
                String type = param.getSimpleProperty(param.PROPERTY_CONTAINER_TYPE);
                // type may be a template in itself: this returns the type if no template was found
                String[] imports = getTemplateImportsFromType(type);
                for (int j=0; j<imports.length; j++) {
                    importLines.add(imports[j]);
                }
            }
            // rest are included interfaces
            String[] outputLines = new String[importLines.size()];
            for(int i=0; i<outputLines.length; i++) {
                String line = importLines.get(i);
                StringBuffer oneOutputLine = new StringBuffer("\n"); // NOI18N
                oneOutputLine.append("\n#ifndef __").append(line.replaceAll("\\.", "_")).append("_idl__"); // NOI18N
                oneOutputLine.append("\n#include <").append(line.replaceAll("\\.", "/")).append(".idl>"); // NOI18N
                oneOutputLine.append("\n#endif"); // NOI18N
                outputLines[i] = oneOutputLine.toString();
            }
            return outputLines;
        }
        else if (output.indexOf("$!Templates!") != -1) { // NOI18N
            String whitespace = output.substring(0, output.indexOf("$!Templates!")); // NOI18N
            String[] typeNames = pstr.getTemplateTypeNames();
            StringBuffer outputLines = new StringBuffer("\n"); // NOI18N
            for (int i=0; i<typeNames.length; i++) {
                TemplateType templType = pstr.getTemplateType(typeNames[i]);
                String name = templType.getSimpleProperty(templType.PROPERTY_CONTAINER_NAME);
                String type = templType.getSimpleProperty(templType.PROPERTY_CONTAINER_TYPE);
                outputLines.append("\n").append(whitespace).append(type).append(" ").append(name).append(";\n"); // NOI18N
            }
            return new String[]{outputLines.toString()};
        }
        else if (output.indexOf("$!Properties!") != -1) { // NOI18N
            String whitespace = output.substring(0, output.indexOf("$!Properties!")); // NOI18N
            String[] propNames = pstr.getAllSetObjectNames();
            StringBuffer outputLines = new StringBuffer("\n"); // NOI18N
            for (int i=0; i<propNames.length; i++) {
                Parameter propType = (Parameter)pstr.getSetObject(propNames[i]);
                String name = propType.getSimpleProperty(propType.PROPERTY_CONTAINER_NAME);
                String type = propType.getSimpleProperty(propType.PROPERTY_CONTAINER_TYPE);
                outputLines.append("\n").append(whitespace).append(
                        IdlFileCreator.getIdlTypeForJavaType(type)).append(" ").append(name).append(";\n"); // NOI18N
            }
            return new String[]{outputLines.toString()};
        }
        return doSimpleReplace(output);
    }
    
    private String[] doExceptionReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgPropertyException, UnknownOpenOfficeOrgLanguageIDException {
            
        if(output.indexOf("$!ExceptionName!") != -1) { // NOI18N
            wiz.putProperty("ExceptionName", ex.getSimpleProperty(ex.PROPERTY_CONTAINER_NAME)); // NOI18N
        }
        else if(output.indexOf("$!ParentExceptionName!") != -1) { // NOI18N
            wiz.putProperty("ParentExceptionName", ex.getSimpleProperty(ex.PROPERTY_CONTAINER_PARENT_EXCPTION).replaceAll("\\.", "::")); // NOI18N
        }
        else if(output.indexOf("$!Import!") != -1) { // NOI18N
            String parentException = ex.getSimpleProperty(ex.PROPERTY_CONTAINER_PARENT_EXCPTION);
            String ifndef = "\n#ifndef __".concat(parentException.replace('.', '_')).concat("_idl__"); // NOI18N
            String imp = "\n#import <".concat(parentException.replace('.', '/').concat(".idl>")); // NOI18N
            String endif = "\n#endif\n"; // NOI18N
            return new String[]{ifndef, imp, endif};
        }
        return doSimpleReplace(output);
    }
    
    private String[] doServiceReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgPropertyException, UnknownOpenOfficeOrgLanguageIDException {
        if (output.indexOf("$!Import!") != -1) { // NOI18N
            String[] types = (String[])wiz.getProperty("Import"); // NOI18N
            return getImportFromTypes(types);
        }
        else if(output.indexOf("$!Service!") != -1) { // NOI18N
            wiz.putProperty("Service", srv.getSimpleProperty(srv.PROPERTY_CONTAINER_NAME)); // NOI18N
        }
        return doSimpleReplace(output);
    }

    private String[] doAddOnReplace(String output) 
                throws IOException, UnknownOpenOfficeOrgLanguageIDException {
        // first iteration step: either menu bar or tool bar may be missing (but not both!)
        Vector<String> retValue = null;
        if (output.indexOf("$!OfficeMenuBar!") != -1) { // NOI18N
            Boolean menu = (Boolean)wiz.getProperty("GotMenu"); // NOI18N
            if (menu.booleanValue()) {
                retValue = new Vector<String>();
                String[] lines = (String[])wiz.getProperty("OfficeMenuBar"); // NOI18N
                for (int i = 0; i < lines.length; i++) {
                    String[] ret = doAddOnReplaceInternal(lines[i]);
                    for (int j = 0; j < ret.length; j++) {
                        retValue.add(ret[j]);
                    }
                }
            }
            else {
                return new String[]{""}; // NOI18N
            }
        }
        if (output.indexOf("$!OfficeToolBar!") != -1) { // NOI18N
            Boolean toolbar = (Boolean)wiz.getProperty("GotToolbar"); // NOI18N
            if (toolbar.booleanValue()) {
                retValue = new Vector<String>();
                String[] lines = (String[])wiz.getProperty("OfficeToolBar"); // NOI18N
                for (int i = 0; i < lines.length; i++) {
                    String[] ret = doAddOnReplaceInternal(lines[i]);
                    for (int j = 0; j < ret.length; j++) {
                        retValue.add(ret[j]);
                    }
                }
            }
            else {
                return new String[]{""}; // NOI18N
            }
        }
        if (retValue != null) {
            return retValue.toArray(new String[retValue.size()]);
        }
        else {
            return doAddOnReplaceInternal(output);
        }
    }
    
    private String[] doAddOnReplaceInternal(String output) 
                throws IOException, UnknownOpenOfficeOrgLanguageIDException {
        if (output.indexOf("$!MenuTitle!") != -1) { // NOI18N
            AddOn addOn = (AddOn)wiz.getProperty("AddOnMenu"); // NOI18N
            LocalizedOpenOfficeOrgProperty nameProp = (LocalizedOpenOfficeOrgProperty)
                        addOn.getProperty(addOn.PROPERTY_DisplayName);
            Integer[] indexes = nameProp.getUsedLanguageIndexes();
            String[] returnValues = new String[indexes.length];
            for (int j=0; j<indexes.length; j++) {
                int langId = indexes[j].intValue();
                String localizedName = stripName(nameProp.getValueForLanguage(langId));
                returnValues[j] = "          <value xml:lang=\"".concat( // NOI18N
                        LanguageDefinition.getLanguageShortNameForId(langId)).concat("\">").concat(
                        localizedName).concat("</value>"); // NOI18N
            }
            return returnValues;
        }
        else if (output.indexOf("$!ToolbarTitle!") != -1) { // NOI18N
            AddOn addOn = (AddOn)wiz.getProperty("AddOnToolbar"); // NOI18N
            LocalizedOpenOfficeOrgProperty nameProp = (LocalizedOpenOfficeOrgProperty)
                        addOn.getProperty(addOn.PROPERTY_DisplayName);
            Integer[] indexes = nameProp.getUsedLanguageIndexes();
            String[] returnValues = new String[indexes.length];
            for (int j=0; j<indexes.length; j++) {
                int langId = indexes[j].intValue();
                String localizedName = stripName(nameProp.getValueForLanguage(langId));
                returnValues[j] = "          <value xml:lang=\"".concat(
                        LanguageDefinition.getLanguageShortNameForId(langId)).concat("\">").concat(
                        localizedName).concat("</value>"); // NOI18N
            }
            return returnValues;
        }
        else if (output.indexOf("$!ProtocolHandlerFunctionTemplate!") != -1) { // NOI18N
            String protocol = "        <value>".concat((String)wiz.getProperty(
                "Protocol")).concat(":*</value>"); // NOI18N
            return new String[]{protocol};
        }
        else if (output.indexOf("$!Images!") != -1) { // NOI18N
            return getAddOnImages();
        }
        else if (output.indexOf("$!AddonMenuFunctionTemplate!") != -1) { // NOI18N
            // Addon.xcu special replace
            return createAddonMenuXcu();
        }
        else if (output.indexOf("$!AddonToolbarFunctionTemplate!") != -1) { // NOI18N
            // Addon.xcu special replace
            return createAddonToolbarXcu();
        }
        return doSimpleReplace(output);
    }

    private String[] createAddonMenuXcu() {
        AddOn topElementMenu = (AddOn)wiz.getProperty("AddOnMenu"); // NOI18N
        Vector<String>v = new Vector<String>();
        createAddonMenuEntries("        ", (String)wiz.getProperty("Protocol"), topElementMenu, v); // NOI18N
        return v.toArray(new String[v.size()]);
    }
    
    private String[] createAddonToolbarXcu() {
        AddOn topElementToolbar = (AddOn)wiz.getProperty("AddOnToolbar"); // NOI18N
        AddOn subMeun = (AddOn)topElementToolbar.getAllMenus()[0];
        Vector<String>v = new Vector<String>();
        createAddonToolbarEntries("      ", (String)wiz.getProperty("Protocol"), topElementToolbar, v); // NOI18N
        return v.toArray(new String[v.size()]);
    }
    
    private void createAddonMenuEntries(String whitespace, String namePrefix, AddOn subElement, Vector<String> subMenu) {
        subMenu.add(whitespace.concat("<node oor:name=\"Submenu\">")); // NOI18N
        String[] subObjectNamesInOrder = subElement.getAllSetObjectNames();
        for (int i=0; i<subObjectNamesInOrder.length; i++) {
            NbNodeObject object = (NbNodeObject)subElement.getSetObject(subObjectNamesInOrder[i]);
            switch(object.getType()) {
                case NbNodeObject.UI_MENU_TYPE:
                    String menuName = "m" + (++menuOrderNumber);  // NOI18N
                    subMenu.add(whitespace.concat("  <node oor:name=\"").concat(
                            menuName).concat("\" oor:op=\"replace\">")); // NOI18N
                    subMenu.add(whitespace.concat("    <prop oor:name=\"URL\" oor:type=\"xs:string\">")); // NOI18N
                    subMenu.add(whitespace.concat("      <value/>")); // NOI18N
                    subMenu.add(whitespace.concat("    </prop>")); // NOI18N
                    subMenu.add(whitespace.concat("    <prop oor:name=\"ImageIdentifier\" oor:type=\"xs:string\">")); // NOI18N
                    subMenu.add(whitespace.concat("      <value/>")); // NOI18N
                    subMenu.add(whitespace.concat("    </prop>")); // NOI18N
                    subMenu.add(whitespace.concat("    <prop oor:name=\"Target\" oor:type=\"xs:string\">")); // NOI18N
                    subMenu.add(whitespace.concat("      <value>_self</value>")); // NOI18N
                    subMenu.add(whitespace.concat("    </prop>")); // NOI18N
                    subMenu.add(whitespace.concat("    <prop oor:name=\"Title\" oor:type=\"xs:string\">")); // NOI18N
                    subMenu.add(whitespace.concat("      <value/>")); // NOI18N
                    PropertyContainer pCont = (PropertyContainer)object;
                    LocalizedOpenOfficeOrgProperty nameProp = (LocalizedOpenOfficeOrgProperty)pCont.getProperty(pCont.PROPERTY_DisplayName);
                    Integer[] indexes = nameProp.getUsedLanguageIndexes();
                    for (int j=0; j<indexes.length; j++) {
                        int langId = indexes[j].intValue();
                        String languageName = stripName(nameProp.getValueForLanguage(langId));
                        subMenu.add(whitespace.concat("      <value xml:lang=\"").concat(
                                LanguageDefinition.getLanguageShortNameForId(langId)).concat("\">").concat(
                                languageName).concat("</value>")); // NOI18N
                    }
                    subMenu.add(whitespace.concat("    </prop>")); // NOI18N
                    createAddonMenuEntries(whitespace.concat("      "), namePrefix, (SubMenuElement)object, subMenu); // NOI18N
                    subMenu.add(whitespace.concat("  </node>")); // NOI18N
                    break;
                case NbNodeObject.UI_SEPARATOR_TYPE:
                    handleSeparator(whitespace.concat("  "), namePrefix, (SeparatorElement)object, subMenu, true); // NOI18N
                    break;
                case NbNodeObject.FUNCTION_TYPE:
                    handleCommandType(whitespace.concat("  "), namePrefix, (Command)object, subMenu, true); // NOI18N
                    break;
                default:
                    // this is real bad
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Illegal NbNodeObject"); // NOI18N
            }
        }
        subMenu.add(whitespace.concat("</node>")); // NOI18N
    }

    private void createAddonToolbarEntries(String whitespace, String namePrefix, AddOn subElement, Vector<String> subMenu) {
        String[] subObjectNamesInOrder = subElement.getAllSetObjectNames();
        for (int i=0; i<subObjectNamesInOrder.length; i++) {
            NbNodeObject object = (NbNodeObject)subElement.getSetObject(subObjectNamesInOrder[i]);
            switch(object.getType()) {
                case NbNodeObject.UI_MENU_TYPE:
                    createAddonToolbarEntries(whitespace, namePrefix, (SubMenuElement)object, subMenu);
                    break;
                case NbNodeObject.UI_SEPARATOR_TYPE:
                    handleSeparator(whitespace, namePrefix, (SeparatorElement)object, subMenu, false);
                    break;
                case NbNodeObject.FUNCTION_TYPE:
                    handleCommandType(whitespace, namePrefix, (Command)object, subMenu, false);
                    break;
                default:
                    // this is real bad
                    LogWriter.getLogWriter().log(LogWriter.LEVEL_CRITICAL, "Illegal NbNodeObject"); // NOI18N
            }
        }
    }
    
    private void handleSeparator(String whitespace, String namePrefix, SeparatorElement separator, Vector<String> subMenu, boolean menu) {
        String menuName = "m" + (menu?++menuOrderNumber:++toolbarOrderNumber);  // NOI18N
        subMenu.add(whitespace.concat("<node oor:name=\"").concat(menuName).concat("\" oor:op=\"replace\">")); // NOI18N
        subMenu.add(whitespace.concat("  <prop oor:name=\"URL\" oor:type=\"xs:string\">")); // NOI18N
        subMenu.add(whitespace.concat("    <value>private:separator</value>")); // NOI18N
        subMenu.add(whitespace.concat("  </prop>")); // NOI18N
        subMenu.add(whitespace.concat("</node>")); // NOI18N
    }

    private void handleCommandType(String whitespace, String namePrefix, Command object, Vector<String> subMenu, boolean menu) {
        try {
            String menuName = "m" + (menu?++menuOrderNumber:++toolbarOrderNumber);  // NOI18N
            PropertyContainer propCont = (PropertyContainer) object;
            String name = propCont.getSimpleProperty(PropertyContainer.PROPERTY_CONTAINER_NAME);
            Vector<String> v = new Vector<String>();
            for (int i=0; i<propCont.PROPERTY_Context.length; i++) {
                OpenOfficeOrgBooleanProperty prop = (OpenOfficeOrgBooleanProperty)propCont.getProperty(propCont.PROPERTY_Context[i]);
                if (prop.getValue()) {
                    v.add(prop.getPropertyName());
                }
            }
            String context = XcuFileCreator.getXcuContext(v.toArray(new String[v.size()]));
            LocalizedOpenOfficeOrgProperty nameProp = (LocalizedOpenOfficeOrgProperty)propCont.getProperty(propCont.PROPERTY_DisplayName);
            subMenu.add(whitespace.concat("<node oor:name=\"").concat(menuName).concat("\" oor:op=\"replace\">")); // NOI18N
            subMenu.add(whitespace.concat("  <prop oor:name=\"URL\" oor:type=\"xs:string\">")); // NOI18N
            subMenu.add(whitespace.concat("    <value>").concat(namePrefix).concat(":").concat(name).concat("</value>")); // NOI18N
            subMenu.add(whitespace.concat("  </prop>")); // NOI18N
            subMenu.add(whitespace.concat("  <prop oor:name=\"ImageIdentifier\" oor:type=\"xs:string\">")); // NOI18N
            subMenu.add(whitespace.concat("    <value/>")); // NOI18N
            subMenu.add(whitespace.concat("  </prop>")); // NOI18N
            subMenu.add(whitespace.concat("  <prop oor:name=\"Target\" oor:type=\"xs:string\">")); // NOI18N
            subMenu.add(whitespace.concat("    <value>_self</value>")); // NOI18N
            subMenu.add(whitespace.concat("  </prop>")); // NOI18N
            subMenu.add(whitespace.concat("  <prop oor:name=\"Context\" oor:type=\"xs:string\">")); // NOI18N
            subMenu.add(whitespace.concat("    ").concat(context)); // NOI18N
            subMenu.add(whitespace.concat("  </prop>")); // NOI18N
            subMenu.add(whitespace.concat("  <prop oor:name=\"Title\" oor:type=\"xs:string\">")); // NOI18N
            subMenu.add(whitespace.concat("    <value/>")); // NOI18N
            Integer[] indexes = nameProp.getUsedLanguageIndexes();
            for (int j=0; j<indexes.length; j++) {
                int langId = indexes[j].intValue();
                String languageName = stripName(nameProp.getValueForLanguage(langId));
                subMenu.add(whitespace.concat("    <value xml:lang=\"").concat( // NOI18N
                        LanguageDefinition.getLanguageShortNameForId(langId)).concat("\">").concat( // NOI18N
                        languageName).concat("</value>")); // NOI18N
            }
            subMenu.add(whitespace.concat("  </prop>")); // NOI18N
            subMenu.add(whitespace.concat("</node>")); // NOI18N
        } catch (UnknownOpenOfficeOrgPropertyException ex) {
            ex.printStackTrace();
        }
    }
    
    private String[] getAddOnImages() {
        AddOn topElement = (AddOn)wiz.getProperty("AddOn"); // NOI18N
        String protocol = (String)wiz.getProperty("Protocol"); // NOI18N
        String[][] commandAndImages = collectCommandsAndImages(topElement);
        Vector<String> retValue = new Vector<String>();
        retValue.add("    <node oor:name=\"Images\">"); // NOI18N
        for (int i=0; i<commandAndImages.length; i++) {
            retValue.add("      <node oor:name=\"".concat(protocol).concat(".").concat(commandAndImages[i][0].toLowerCase()).concat(".images\" oor:op=\"replace\">")); // NOI18N
            retValue.add("        <prop oor:name=\"URL\" oor:type=\"xs:string\">"); // NOI18N
            retValue.add("          <value>".concat(protocol).concat(":").concat(commandAndImages[i][0]).concat("</value>")); // NOI18N
            retValue.add("        </prop>"); // NOI18N
            retValue.add("        <node oor:name=\"UserDefinedImages\">"); // NOI18N
            retValue.add("          <prop oor:name=\"ImageSmallURL\">"); // NOI18N
            retValue.add("            ".concat(commandAndImages[i][1])); // NOI18N
            retValue.add("          </prop>"); // NOI18N
            retValue.add("          <prop oor:name=\"ImageBigURL\">"); // NOI18N
            retValue.add("            ".concat(commandAndImages[i][2])); // NOI18N
            retValue.add("          </prop>"); // NOI18N
            retValue.add("          <prop oor:name=\"ImageSmallHCURL\">"); // NOI18N
            retValue.add("            ".concat(commandAndImages[i][3])); // NOI18N
            retValue.add("          </prop>"); // NOI18N
            retValue.add("          <prop oor:name=\"ImageBigHCURL\">"); // NOI18N
            retValue.add("            ".concat(commandAndImages[i][4])); // NOI18N
            retValue.add("          </prop>"); // NOI18N
            retValue.add("        </node>"); // NOI18N
            retValue.add("      </node>"); // NOI18N
        }
        retValue.add("      </node>"); // NOI18N
        return retValue.toArray(new String[retValue.size()]);
    }
    
    private String[][] collectCommandsAndImages(AddOn topElement) {
        NbNodeObject[] commands = topElement.getAllCommands();
        String[][]commandsAndImages = new String[commands.length][5];
        for (int i=0; i<commands.length; i++) {
            Command command = (Command)commands[i];
            try {
                String commandName = command.getSimpleProperty(command.PROPERTY_CONTAINER_NAME);
                PropertyContainer propCont = (PropertyContainer)command;
                String iconSmall = getIconEntry(propCont.getProperty(propCont.PROPERTY_Icon_Lowres_Small));
                String iconBig = getIconEntry(propCont.getProperty(propCont.PROPERTY_Icon_Lowres_Big)); 
                String iconHiresSmall = getIconEntry(propCont.getProperty(propCont.PROPERTY_Icon_Hires_Small)); 
                String iconHiresBig = getIconEntry(propCont.getProperty(propCont.PROPERTY_Icon_Hires_Big)); 
                commandsAndImages[i][0] = commandName;
                commandsAndImages[i][1] = iconSmall;
                commandsAndImages[i][2] = iconBig;
                commandsAndImages[i][3] = iconHiresSmall;
                commandsAndImages[i][4] = iconHiresBig;
            } catch (UnknownOpenOfficeOrgPropertyException ex) {
                LogWriter.getLogWriter().printStackTrace(ex);
            }
        }
        return commandsAndImages;
    }
    
    private String stripName(String name) {
        String result = name.trim();
        if (result.startsWith("<")) { // NOI18N
                result = result.substring(1);
            if (result.endsWith(">")) { // NOI18N
                result = result.substring(0, result.length() - 1);
            }
        }
        return result.trim();
    }
    
    private String[] doAddInReplace(String output) 
            throws IOException, UnknownOpenOfficeOrgLanguageIDException {
        if (output.indexOf("$!Methods!") != -1) { // NOI18N
            String whitespace = output.substring(0, output.indexOf("$!Methods!")); // NOI18N
            String[] funcNames = this.addin.getAllSetObjectNames();
            String[] returnVal = new String[funcNames.length];
            for (int i=0; i<funcNames.length; i++) {
                StringBuffer functionOutputLine = new StringBuffer("\n").append(whitespace); // NOI18N
                Function function = (Function)addin.getSetObject(funcNames[i]);
                String funcName = (String)function.getProperty(function.PROPERTY_Name).getValueForLanguage(-1);
                String funcType = (String)function.getProperty(function.PROPERTY_Type).getValueForLanguage(-1);
                funcType = getArrays(funcType);
                functionOutputLine.append(IdlFileCreator.getIdlTypeForJavaType(funcType)).append(" ").append(funcName).append("("); // NOI18N
                String[] paramNames = function.getAllSetObjectNames();
                for (int j=0; j<paramNames.length; j++) {
                    Parameter param = (Parameter)function.getSetObject(paramNames[j]);
                    String paramName = (String)param.getProperty(param.PROPERTY_Name).getValueForLanguage(-1);
                    String paramType = (String)param.getProperty(param.PROPERTY_Type).getValueForLanguage(-1);
                    paramType = getArrays(paramType);
                    functionOutputLine.append("[in] ").append(IdlFileCreator.getIdlTypeForJavaType(paramType)).append(" ").append(paramName); // NOI18N
                    if (j < paramNames.length - 1) {
                        functionOutputLine.append(", "); // NOI18N
                    }
                }
                String exceptions = function.getProperty(function.PROPERTY_Exceptions).getValueForLanguage(-1);
                if (exceptions.length() == 0)
                    functionOutputLine.append(");"); // NOI18N
                else {
                    functionOutputLine.append(")\n"); // NOI18N
                    whitespace = "    ".concat(whitespace); // NOI18N
                    String indent = "\n".concat(whitespace).concat("         "); // NOI18N
                    exceptions = exceptions.replace(".", "::"); // NOI18N
                    exceptions = exceptions.replace(" ", indent); // NOI18N
                    functionOutputLine.append(whitespace).append("raises ( ").append(exceptions).append(" );"); // NOI18N
                }
                returnVal[i] = functionOutputLine.toString();
            }
            return returnVal;
        }
        return doSimpleReplace(output);
    }
    
    /**
     * take a idl type, e.g. long[][] and remodel it into 
     * sequence< sequence< long > >
     * @param paramType the type
     * @return a type with correct arrays: returns the same type if no arrays are found
     */
    private String getArrays(String paramType) {
       int index = 0;
       while((index = paramType.lastIndexOf("[]")) != -1) { // NOI18N
           paramType = paramType.substring(0, index).concat(paramType.substring(index + 2));
           paramType = "sequence< ".concat(paramType).concat(" >"); // NOI18N
       } 
       return paramType;
    }
    
    private String[] getAllUsedTypes(Interface ifc) 
            throws UnknownOpenOfficeOrgPropertyException, UnknownOpenOfficeOrgLanguageIDException{
        Vector<String> usedTypes = new Vector<String>();
        String[] funcNames = this.ifc.getAllSetObjectNames();
        for (int i=0; i<funcNames.length; i++) {
            Function function = (Function)ifc.getSetObject(funcNames[i]);
            String funcType = (String) function.getProperty(function.PROPERTY_Type).getValueForLanguage(-1);
            String[] addReturnType = getTemplateImportsFromType(funcType);
            for (int k=0; k<addReturnType.length; k++) {
                if (!usedTypes.contains(addReturnType[k]))
                    usedTypes.add(addReturnType[k]);
            }
            String[] paramNames = function.getAllSetObjectNames();
            for (int j=0; j<paramNames.length; j++) {
                Parameter param = (Parameter)function.getSetObject(paramNames[j]);
                String paramType = (String)param.getProperty(param.PROPERTY_Type).getValueForLanguage(-1);
                String[] addParamType = getTemplateImportsFromType(paramType);
                for (int k=0; k<addParamType.length; k++) {
                    if (!usedTypes.contains(addParamType[k]))
                        usedTypes.add(addParamType[k]);
                }
            }
        }
        String[] types = usedTypes.toArray(new String[usedTypes.size()]);
        return getImportFromTypes(types);
    }

    private String[] getImportFromTypes(String[] types) {
        // build import structure
        for (int i=0; i<types.length; i++) {
            String oneType = types[i];
            types[i] = "\n#ifndef __".concat(oneType.replaceAll("\\.", "_")).concat("_idl__\n")
                    .concat("#import <").concat(oneType.replaceAll("\\.", "/")).concat(".idl>\n")
                    .concat("#endif\n"); // NOI18N
        }
        return types;
    }
    
    private String getExceptionImports(NbNodeObject object) 
            throws UnknownOpenOfficeOrgLanguageIDException {
        Vector<String> result = new Vector<String>();
        if (object == null)
            return ""; // NOI18N
        if (object instanceof AddIn) {
            String[] funcNames = ((AddIn)object).getAllSetObjectNames();
            for (int i=0; i<funcNames.length; i++) {
                Function function = (Function)addin.getSetObject(funcNames[i]);
                String[] exceptions = getExceptionsFromFunction(function);
                for (int k=0; k<exceptions.length; k++) {
                    if (!result.contains(exceptions[k]))
                        result.add(exceptions[k]);
                }
            }
        } 
        else {
            if (object instanceof Interface) { // what else?
                Interface ifcTemp = (Interface)object;
                String[] funcNames = ifcTemp.getAllSetObjectNames();
                for (int j=0; j<funcNames.length; j++) {
                    Function function = (Function)ifcTemp.getSetObject(funcNames[j]);
                    String[] exceptions = getExceptionsFromFunction(function);
                    for (int k=0; k<exceptions.length; k++) {
                        if (!result.contains(exceptions[k]))
                            result.add(exceptions[k]);
                    }
                }
            }
        }
        StringBuffer retValue = new StringBuffer();
        for (Enumeration<String> en = result.elements(); en.hasMoreElements(); retValue.append(en.nextElement())){
            // empty by design
        }
        return retValue.toString();
    }
    
    private String[] getExceptionsFromFunction(Function function) 
            throws UnknownOpenOfficeOrgLanguageIDException {
        Vector<String>functionExceptions = new Vector<String>();
        StringTokenizer t = null;
        String exceptions = function.getProperty(function.PROPERTY_Exceptions).getValueForLanguage(-1);
        t = new StringTokenizer(exceptions, " ,"); // NOI18N
        
        while(t.hasMoreTokens()) {
            String oneException = t.nextToken();
            String ifndef = "\n#ifndef __".concat(oneException.replace('.', '_')).concat("_idl__"); // NOI18N
            String imp = "\n#import <".concat(oneException.replace(".", "/").concat(".idl>")); // NOI18N
            String endif = "\n#endif\n"; // NOI18N
            functionExceptions.add(ifndef.concat(imp).concat(endif));
        }
        return functionExceptions.toArray(new String[functionExceptions.size()]);
    }
    
    private String[] getTemplateImportsFromType(String type) {
        Vector<String>additionalImports = new Vector<String>();
        type = type.trim();
        int index = 0;
        while ((index = type.indexOf('<')) != -1) {
            String oneStruct = type.substring(0, index);
            checkForComplexTemplates(oneStruct, additionalImports);
            int lastIndex = type.lastIndexOf('>');
            type = type.substring(index + 1, lastIndex);
        }
        checkForComplexTemplates(type, additionalImports);
        return additionalImports.toArray(new String[additionalImports.size()]);
    }
    
    
    private void checkForComplexTemplates(String candidate, Vector<String> additionalImports) {
        if (candidate.trim().indexOf(',') != -1) {
            StringTokenizer t = new StringTokenizer(candidate, ","); // NOI18N
            while(t.hasMoreTokens()) {
                candidate = t.nextToken();
                if (candidate.trim().indexOf('.') != -1)
                    additionalImports.add(candidate.trim());
            }
        }
        else {
            if (candidate.trim().indexOf('.') != -1)
                additionalImports.add(candidate.trim());
        }
    }
    
    /**
     * function  is only used for AddOns. When using with other wizards, check 
     * correct %origin%/../../../../../images/ directory
     * @param prop the icon property to create xcu entries from
     * @return entry complete as string or empty entry for missing icons
     */
    private String getIconEntry(OpenOfficeOrgProperty prop) {
        String retValue = EMPTY_VALUE;
        try {
            OpenOfficeOrgIconProperty iconProp = (OpenOfficeOrgIconProperty)prop;
            String imgPath = iconProp.getValueForLanguage(-1);
            if (imgPath != null && imgPath.length() != 0) {
                File imgFile = new File(imgPath);
                FileObject sourceImage = FileUtil.toFileObject(imgFile);
                String name = imgFile.getName();
                // prepare for images
                File projDir = (File) wiz.getProperty("projdir"); // NOI18N
                FileObject imageFolder = FileUtil.createFolder(
                        FileUtil.toFileObject(projDir), "images"); // NOI18N
                // copy images
                int index = name.indexOf('.');
                if (index < 0) index = name.length();
                FileUtil.copyFile(sourceImage, imageFolder, name.substring(0, index));
                retValue = "<value>%origin%/../../../../../images/".concat(name).concat("</value>");  // NOI18N
            }
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (NullPointerException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        catch (IllegalArgumentException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        } catch (UnknownOpenOfficeOrgLanguageIDException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
        return retValue;
    }
}
