/*
 * *************************************************************
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
 * ************************************************************
 */

package ooo.connector;

import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.XComponentContext;
import java.util.Random;
import ooo.connector.server.OOoServer;

//removed &highlight=named+pipe from end of url below for javadoc
/**
 * A bootstrap connector which uses a named pipe to connect to an OOo server.
 * 
 * Very helpful in getting the named pipe connection working has been the posts
 * of mnasato in the thread "Correct FilterName to open RTF from bytestream?" at
 * http://www.oooforum.org/forum/viewtopic.phtml?t=40263 
 */
public class BootstrapPipeConnector extends BootstrapConnector {

    /**
     * Constructs a bootstrap pipe connector which uses the specified folder of
     * the OOo installation containing the soffice executable.
     * 
     * @param   oooExecFolder   The folder of the OOo installation containing the soffice executable
     */
    public BootstrapPipeConnector(String oooExecFolder) {

        super(oooExecFolder);
    }

    /**
     * Constructs a bootstrap pipe connector which connects to the specified
     * OOo server.
     * 
     * @param   oooServer   The OOo server
     */
    public BootstrapPipeConnector(OOoServer oooServer) {

        super(oooServer);
    }

    /**
     * Connects to an OOo server using a random pipe name and returns a
     * component context for using the connection to the OOo server.
     * 
     * @return             The component context
     */
    public XComponentContext connect() throws BootstrapException {

        // create random pipe name
        String sPipeName = "uno" + Long.toString((new Random()).nextLong() & 0x7fffffffffffffffL);

        return connect(sPipeName);
    }

    /**
     * Connects to an OOo server using the specified pipe name and returns a
     * component context for using the connection to the OOo server.
     * 
     * @param   pipeName   The pipe name
     * @return             The component context
     */
    public XComponentContext connect(String pipeName) throws BootstrapException {

        // accept option
        String oooAcceptOption = "-accept=pipe,name=" + pipeName + ";urp;";

        // connection string
        String unoConnectString = "uno:pipe,name=" + pipeName + ";urp;StarOffice.ComponentContext";

        return connect(oooAcceptOption, unoConnectString);
    }

    /**
     * Bootstraps a connection to an OOo server in the specified soffice
     * executable folder of the OOo installation using a random pipe name and
     * returns a component context for using the connection to the OOo server.
     * 
     * @param   oooExecFolder      The folder of the OOo installation containing the soffice executable
     * @return                     The component context
     */
    public static final XComponentContext bootstrap(String oooExecFolder) throws BootstrapException {

        BootstrapPipeConnector bootstrapPipeConnector = new BootstrapPipeConnector(oooExecFolder);
        return bootstrapPipeConnector.connect();
    }

    /**
     * Bootstraps a connection to an OOo server in the specified soffice
     * executable folder of the OOo installation using the specified pipe name
     * and returns a component context for using the connection to OOo server.
     * 
     * @param   oooExecFolder      The folder of the OOo installation containing the soffice executable
     * @param   pipeName           The pipe name
     * @return                     The component context
     */
    public static final XComponentContext bootstrap(String oooExecFolder, String pipeName) throws BootstrapException {

        BootstrapPipeConnector bootstrapPipeConnector = new BootstrapPipeConnector(oooExecFolder);
        return bootstrapPipeConnector.connect(pipeName);
    }
}
