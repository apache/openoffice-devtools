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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openoffice.extensions.filetypes.odf;

import java.io.File;
import java.io.IOException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openoffice.extensions.config.ConfigurationSettings;
import org.openoffice.extensions.config.ConfigurationValidator;
import org.openoffice.extensions.config.office.PlatformInfo;
import org.openoffice.extensions.util.LogWriter;

public final class OpenWithOpenOffice extends CookieAction {

    protected void performAction(Node[] activatedNodes) {
        OdfDataObject odtDataObject = activatedNodes[0].getLookup().lookup(OdfDataObject.class);
        // check for correct office and open dialog if office not found
        ConfigurationValidator.validateSettings();
        String officePath = ConfigurationSettings.getSettings().getValue(ConfigurationSettings.KEY_OFFICE_INSTALLATION);
        officePath = officePath.concat("/").concat(PlatformInfo.getOfficeProgramDir()).concat("/soffice");
        FileObject fileObject = odtDataObject.getLookup().lookup(FileObject.class);
        File file = FileUtil.toFile(fileObject);
        try {
            Runtime.getRuntime().exec(new String[]{officePath, file.getPath()});
        } catch (IOException ex) {
            LogWriter.getLogWriter().printStackTrace(ex);
        }
    }

    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }

    public String getName() {
        return NbBundle.getMessage(OpenWithOpenOffice.class, "CTL_OpenWithOpenOffice");
    }

    protected Class[] cookieClasses() {
        return new Class[]{OdfDataObject.class};
    }

    @Override
    protected String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}

