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

package org.openoffice.extensions.filetypes.unoidl.actions;

import java.io.File;
import org.apache.tools.ant.module.api.support.ActionUtils;

import org.netbeans.api.project.FileOwnerQuery;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;
import org.openoffice.extensions.config.ConfigurationSettings;

import org.openoffice.extensions.filetypes.unoidl.UnoIdlTypeDataObject;
import java.util.Properties;
import org.openoffice.extensions.util.LogWriter;
import org.openoffice.extensions.config.office.PlatformInfo;

//~--- classes ----------------------------------------------------------------

public final class CompileFileAction extends CookieAction {
    protected boolean asynchronous() {
        return false;
    }

    protected Class[] cookieClasses() {
        return new Class[] { DataObject.class };
    }

    protected String iconResource() {
        return "org/openoffice/extensions/filetypes/unoidl/idlfile.png"; // NOI18N
    }

    protected int mode() {
        return CookieAction.MODE_ANY;
    }

    protected void performAction(Node[] activatedNodes) {

        try {
            Properties props = new Properties();

            // TODO: probably these properties are not necessary because they are already set globally 
//            props.setProperty("office.home.dir", soPath); // NOI18N
//            props.setProperty("office.program.dir", soPath + "/" + PlatformInfo.getOfficeProgramDir()); // NOI18N
//            props.setProperty("oo.sdk.dir", sdkPath); // NOI18N

            for (int i = 0; i < activatedNodes.length; i++) {
                UnoIdlTypeDataObject d =
                    (UnoIdlTypeDataObject) activatedNodes[i].getCookie(
                        UnoIdlTypeDataObject.class);
                FileObject f          = d.getPrimaryFile();
                FileObject projectDir =
                    FileOwnerQuery.getOwner(f).getProjectDirectory();
                String path = FileUtil.toFile(projectDir).getCanonicalPath();
                props.setProperty("project.home", path); // NOI18N

                FileObject xmlFile = FileUtil.toFileObject(
                            new File(path + File.separator
                                           + "build.xml")); // NOI18N


                props.setProperty("compile-file", f.getPath()); // NOI18N
                ActionUtils.runTarget(xmlFile,
                                      new String[] { "uno-idl-compile" },
                                      props); // NOI18N

                // this is the alternative to the previous line
                //                    DataObject dataObject = DataObject.find(xmlFile);
                //                    AntTargetExecutor.Env env = new AntTargetExecutor.Env();
                //                    AntTargetExecutor exec = AntTargetExecutor.createTargetExecutor(env);
                //                    exec.execute(null, null); 
            }
        } catch (Exception e) {    // show exception in output window
            String tabName = NbBundle.getMessage(UnoIdlTypeDataObject.class,
                                 "OutputWindowName"); // NOI18N
            InputOutput io = IOProvider.getDefault().getIO(tabName, false);

            io.select();

            OutputWriter writer = io.getOut();

            try {
                writer.reset();
                e.printStackTrace(writer);
            } catch (java.io.IOException ioe) {
                LogWriter.getLogWriter().printStackTrace(ioe);
            }

            writer.close();
        }

/*        String tabName = NbBundle.getMessage(CompileFileAction.class, "OutputWindowName"); // NOI18N

        InputOutput io = IOProvider.getDefault().getIO(tabName, false);
        io.select();

        OutputWriter writer = io.getOut();
        try {
            writer.reset();
        }
        catch (java.io.IOException e) {
            // ignore; just the output tab is not empty.
        }

        CompileIdlFile cif = CompileIdlFile.getCompileIdlFileInstance(activatedNodes, writer);
        cif.start(); */
    }

    //~--- get methods --------------------------------------------------------

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    public String getName() {
        return NbBundle.getMessage(UnoIdlTypeDataObject.class,
                                   "CTL_CompileFileAction"); // NOI18N
    }
}
