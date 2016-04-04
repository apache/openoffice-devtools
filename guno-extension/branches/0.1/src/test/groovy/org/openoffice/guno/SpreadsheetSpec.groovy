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
import com.sun.star.sheet.XSpreadsheet
import com.sun.star.sheet.XSpreadsheetDocument
import com.sun.star.sheet.XSpreadsheets
import com.sun.star.uno.RuntimeException
import com.sun.star.uno.UnoRuntime
import com.sun.star.uno.XComponentContext
import spock.lang.Shared
import spock.lang.Specification

import ooo.connector.BootstrapSocketConnector

/**
 *
 * @author Carl Marcum - CodeBuilders.net
 */

class SpreadsheetSpec extends Specification {

    @Shared XComponentContext mxRemoteContext
    @Shared XMultiComponentFactory mxRemoteServiceManager
    @Shared XSpreadsheetDocument xSpreadsheetDocument
    @Shared XComponent xComponent

    
    // fixture methods (setup, cleanup, setupSpec, cleanupSpec)

    // put our expensive operations here like connections
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
    def "get sheet by name"() {

        setup:
        XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets()
        xSpreadsheets.insertNewByName("MySheet", (short)0)
        

        when:   // stimulus
        XSpreadsheet xSpreadsheet = xSpreadsheetDocument.getSheetByName("MySheet")
        
        then:   // response
        xSpreadsheet != null

        cleanup:
        xSpreadsheets.removeByName("MySheet")
        
    }

    def "get sheet by index"() {

        when:   // stimulus
        XSpreadsheet xSpreadsheet = xSpreadsheetDocument.getSheetByIndex(1)

        then:   // response
        xSpreadsheet != null

    }


    
    // helper methods

    @Deprecated
    // Connect to a running office that is accepting connections.
    void connect() {
        if (mxRemoteContext == null && mxRemoteServiceManager == null) {
            try {
                // added to bootstrap by file path
                String oooExeFolder = "/opt/openoffice4/program/"

                // First step: get the remote office component context
                // mxRemoteContext = com.sun.star.comp.helper.Bootstrap.bootstrap()

                // we use the bootstrapconnector for tests
                mxRemoteContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                // mxRemoteContext = BootstrapPipeConnector.bootstrap(oooExeFolder);
                System.out.println("Connected to a running office ...")

                mxRemoteServiceManager = mxRemoteContext.getServiceManager()

            } catch( Exception e) {
                System.err.println("ERROR: can't get a component context from a running office ...")
                e.printStackTrace()
                System.exit(1)
            }
        }
    }

    @Deprecated
    /** Creates an empty spreadsheet document.
     @return  The XSpreadsheetDocument interface of the document. */
    XSpreadsheetDocument initDocument()
            throws RuntimeException, Exception {
        XComponentLoader aLoader = UnoRuntime.queryInterface(
                XComponentLoader.class,
                mxRemoteServiceManager.createInstanceWithContext(
                        "com.sun.star.frame.Desktop", mxRemoteContext))

        // changed to use class var xComponent
        xComponent = aLoader.loadComponentFromURL(
                "private:factory/scalc", "_default", 0, new com.sun.star.beans.PropertyValue[0] )

        XSpreadsheetDocument xSpreadsheetDocument = UnoRuntime.queryInterface(
                XSpreadsheetDocument.class, xComponent )

        return xSpreadsheetDocument
    }


}

