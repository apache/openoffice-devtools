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
 *************************************************************/

package org.openoffice.guno

import com.sun.star.frame.XComponentLoader
import com.sun.star.lang.XComponent
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.sheet.XSpreadsheetDocument
import com.sun.star.uno.UnoRuntime
import com.sun.star.uno.XComponentContext
import ooo.connector.BootstrapSocketConnector
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author Carl Marcum - CodeBuilders.net
 */

class UnoSpec extends Specification {

    @Shared XComponentContext mxRemoteContext
    @Shared XMultiComponentFactory mxRemoteServiceManager
    @Shared XComponent xComponent
    @Shared XSpreadsheetDocument xSpreadsheetDocument


    // fixture methods (setup, cleanup, setupSpec, cleanupSpec)

    def setupSpec() {

        // connect to the office and get a component context
        if (mxRemoteContext == null) {
            try {
                // bootstrap by file path
                String oooExeFolder = "/opt/openoffice4/program/"
                mxRemoteContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                System.out.println("Connected to a running office ...")

                // mxRemoteServiceManager = mxRemoteContext.getServiceManager()

            } catch( Exception e) {
                System.err.println("ERROR: can't get a component context from a running office ...")
                e.printStackTrace()
                System.exit(1)
            }
        }

        // replaces initDocument()
        XComponentLoader aLoader = mxRemoteContext.componentLoader

        xComponent = aLoader.loadComponentFromURL(
                "private:factory/scalc", "_default", 0, new com.sun.star.beans.PropertyValue[0] )

        xSpreadsheetDocument = xComponent.getSpreadsheetDocument(mxRemoteContext)

    }

    def cleanupSpec() {

        // close it all down
        // Check supported functionality of the document (model or controller).
        com.sun.star.frame.XModel xModel = UnoRuntime.queryInterface(
                com.sun.star.frame.XModel.class, xSpreadsheetDocument)

        if (xModel != null) {
            // It is a full featured office document.
            // Try to use close mechanism instead of a hard dispose().
            // But maybe such service is not available on this model.
            com.sun.star.util.XCloseable xCloseable = UnoRuntime.queryInterface(
                    com.sun.star.util.XCloseable.class, xModel)

            if (xCloseable != null) {
                try {
                    // use close(boolean DeliverOwnership)
                    // The boolean parameter DeliverOwnership tells objects vetoing the close process that they may
                    // assume ownership if they object the closure by throwing a CloseVetoException
                    // Here we give up ownership. To be on the safe side, catch possible veto exception anyway.
                    xCloseable.close(true);
                } catch (com.sun.star.util.CloseVetoException exCloseVeto) {

                }
            }
            // If close is not supported by this model - try to dispose it.
            // But if the model disagree with a reset request for the modify state
            // we shouldn't do so. Otherwhise some strange things can happen.
            else {
                try {
                    com.sun.star.lang.XComponent xDisposeable = UnoRuntime.queryInterface(
                            com.sun.star.lang.XComponent.class, xModel)
                    xDisposeable.dispose()
                } catch (com.sun.star.beans.PropertyVetoException exModifyVeto) {

                }
            }

        }

    }


    // feature methods


}
