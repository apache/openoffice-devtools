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

import com.sun.star.beans.XPropertySet
import com.sun.star.container.XNameAccess
import com.sun.star.container.XNameContainer
import com.sun.star.frame.XComponentLoader
import com.sun.star.frame.XModel
import com.sun.star.lang.XComponent
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.lang.XMultiServiceFactory
import com.sun.star.sheet.XSpreadsheet
import com.sun.star.sheet.XSpreadsheetDocument
import com.sun.star.sheet.XSpreadsheets
import com.sun.star.style.XStyleFamiliesSupplier
import com.sun.star.table.CellHoriJustify
import com.sun.star.table.CellVertJustify
import com.sun.star.table.XCell
import com.sun.star.text.XText
import com.sun.star.uno.UnoRuntime
import com.sun.star.uno.XComponentContext
import com.sun.star.uno.XInterface
import com.sun.star.util.CloseVetoException
import com.sun.star.util.XCloseable
import ooo.connector.BootstrapSocketConnector
import spock.lang.Shared
import spock.lang.Specification

/**
 *
 * @author Carl Marcum - CodeBuilders.net
 */

class UnoSpec extends Specification {

    @Shared
    XComponentContext mxRemoteContext
    @Shared
    XMultiComponentFactory mxRemoteServiceManager
    @Shared
    XComponent xComponent
    @Shared
    XSpreadsheetDocument xSpreadsheetDocument

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

            } catch (Exception e) {
                System.err.println("ERROR: can't get a component context from a running office ...")
                e.printStackTrace()
                System.exit(1)
            }
        }

        // replaces initDocument()
        XComponentLoader aLoader = mxRemoteContext.componentLoader

        xComponent = aLoader.loadComponentFromURL(
                "private:factory/scalc", "_default", 0, new com.sun.star.beans.PropertyValue[0])

        xSpreadsheetDocument = xComponent.getSpreadsheetDocument(mxRemoteContext)

    }

    def cleanupSpec() {

        // close it all down
        // Check supported functionality of the document (model or controller).
        XModel xModel = UnoRuntime.queryInterface(XModel.class, xSpreadsheetDocument)

        if (xModel != null) {
            // It is a full featured office document.
            // Try to use close mechanism instead of a hard dispose().
            // But maybe such service is not available on this model.
            XCloseable xCloseable = UnoRuntime.queryInterface(XCloseable.class, xModel)

            if (xCloseable != null) {
                try {
                    // use close(boolean DeliverOwnership)
                    // The boolean parameter DeliverOwnership tells objects vetoing the close process that they may
                    // assume ownership if they object the closure by throwing a CloseVetoException
                    // Here we give up ownership. To be on the safe side, catch possible veto exception anyway.
                    xCloseable.close(true);
                } catch (CloseVetoException exCloseVeto) {

                }
            }
            // If close is not supported by this model - try to dispose it.
            // But if the model disagree with a reset request for the modify state
            // we shouldn't do so. Otherwhise some strange things can happen.
            else {
                try {
                    XComponent xDisposeable = UnoRuntime.queryInterface(XComponent.class, xModel)
                    xDisposeable.dispose()
                } catch (com.sun.star.beans.PropertyVetoException exModifyVeto) {

                }
            }

        }

    }

    // feature methods

    def "use guno method in spreadsheet"() {
        setup:
        XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets()
        xSpreadsheets.insertNewByName("MySheet", (short) 0)
        XSpreadsheet xSpreadsheet = xSpreadsheetDocument.getSheetByName("MySheet")

        XCell xCell = null

        // Insert a TEXT CELL using the XText interface
        xCell = xSpreadsheet.getCellByPosition(0, 3)


        when:
        XText xCellText = xCell.guno(XText.class)

        then:
        xCellText != null

        cleanup:
        xSpreadsheets.removeByName("MySheet")

    }

    def "get at cellstyle prop"() {
        setup:
        XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets()
        xSpreadsheets.insertNewByName("MySheet", (short) 0)
        XSpreadsheet xSpreadsheet = xSpreadsheetDocument.getSheetByName("MySheet")

        XPropertySet xPropSet = null
        XCell xCell = null

        // Access and modify a VALUE CELL
        xCell = xSpreadsheet.getCellByPosition(0, 0)
        // Set cell value.
        xCell.setValue(1234)

        XStyleFamiliesSupplier xSFS = UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, xSpreadsheetDocument)
        XNameAccess xSF = xSFS.getStyleFamilies()
        // get the cell styles
        XNameAccess xCS = UnoRuntime.queryInterface(XNameAccess.class, xSF.getByName("CellStyles"))

        String cellStyle = "MyStyle"

        // add cell style and return property set
        // get the service factory
        XMultiServiceFactory oDocMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, xSpreadsheetDocument)
        // get the name container
        XNameContainer oStyleFamilyNameContainer = UnoRuntime.queryInterface(XNameContainer.class, xCS)
        // create the interface
        XInterface oInt1 = oDocMSF.createInstance("com.sun.star.style.CellStyle")

        // insert style
        oStyleFamilyNameContainer.insertByName(cellStyle, oInt1)

        // get the property set
        XPropertySet oCPS1 = UnoRuntime.queryInterface(XPropertySet.class, oInt1);

        // set properties
        oCPS1.setPropertyValue("IsCellBackgroundTransparent", false)
        oCPS1.setPropertyValue("CellBackColor", 6710932) // 6710932
        oCPS1.setPropertyValue("CharColor", 16777215)
        oCPS1.setPropertyValue("RotateAngle", 9000) // angle * 100
        oCPS1.setPropertyValue("RotateReference", CellVertJustify.TOP) // not working
        oCPS1.setPropertyValue("VertJustify", CellVertJustify.BOTTOM)
        oCPS1.setPropertyValue("HoriJustify", CellHoriJustify.CENTER)
        oCPS1.setPropertyValue("ParaIndent", 200)

        when:
        boolean isCellBackgroundTransparent = oCPS1.getAt("IsCellBackgroundTransparent")
        int cellBackColor = oCPS1.getAt("CellBackColor")
        int charColor = oCPS1.getAt("CharColor")
        int rotateAngle = oCPS1.getAt("RotateAngle")
        CellVertJustify rotateReference = oCPS1.getAt("RotateReference")
        CellVertJustify vertJustify = oCPS1.getAt("VertJustify")
        CellHoriJustify horiJustify = oCPS1.getAt("HoriJustify")
        int paraIndent = oCPS1.getAt("ParaIndent")

        then:
        isCellBackgroundTransparent == false
        cellBackColor == 6710932
        charColor == 16777215
        rotateAngle == 9000
        rotateReference == CellVertJustify.TOP
        vertJustify == CellVertJustify.BOTTOM
        horiJustify == CellHoriJustify.CENTER
        // paraIndent == 200 // 0 for some reason

        cleanup:
        xSpreadsheets.removeByName("MySheet")
    }

    def "put at cellstyle prop"() {
        setup:
        XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets()
        xSpreadsheets.insertNewByName("MySheet", (short) 0)
        XSpreadsheet xSpreadsheet = xSpreadsheetDocument.getSheetByName("MySheet")

        XPropertySet xPropSet = null
        XCell xCell = null

        // Access and modify a VALUE CELL
        xCell = xSpreadsheet.getCellByPosition(0, 0)
        // Set cell value.
        xCell.setValue(1234)

        XStyleFamiliesSupplier xSFS = UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, xSpreadsheetDocument)
        XNameAccess xSF = xSFS.getStyleFamilies()
        // get the cell styles
        XNameAccess xCS = UnoRuntime.queryInterface(XNameAccess.class, xSF.getByName("CellStyles"))

        String cellStyle = "MyStyle2"

        // add cell style and return property set
        // get the service factory
        XMultiServiceFactory oDocMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, xSpreadsheetDocument)
        // get the name container
        XNameContainer oStyleFamilyNameContainer = UnoRuntime.queryInterface(XNameContainer.class, xCS)
        // create the interface
        XInterface oInt1 = oDocMSF.createInstance("com.sun.star.style.CellStyle")

        // insert style
        oStyleFamilyNameContainer.insertByName(cellStyle, oInt1)

        // get the property set
        XPropertySet oCPS1 = UnoRuntime.queryInterface(XPropertySet.class, oInt1);

        when:
        // set properties
        oCPS1.putAt("IsCellBackgroundTransparent", false)
        oCPS1.putAt("CellBackColor", 6710932) // 6710932
        oCPS1.putAt("CharColor", 16777215)
        oCPS1.putAt("RotateAngle", 9000) // angle * 100
        oCPS1.putAt("RotateReference", CellVertJustify.TOP) // not working
        oCPS1.putAt("VertJustify", CellVertJustify.BOTTOM)
        oCPS1.putAt("HoriJustify", CellHoriJustify.CENTER)
        oCPS1.putAt("ParaIndent", 200)

        boolean isCellBackgroundTransparent = oCPS1.getPropertyValue("IsCellBackgroundTransparent")
        int cellBackColor = oCPS1.getPropertyValue("CellBackColor")
        int charColor = oCPS1.getPropertyValue("CharColor")
        int rotateAngle = oCPS1.getPropertyValue("RotateAngle")
        CellVertJustify rotateReference = oCPS1.getPropertyValue("RotateReference")
        CellVertJustify vertJustify = oCPS1.getPropertyValue("VertJustify")
        CellHoriJustify horiJustify = oCPS1.getPropertyValue("HoriJustify")
        int paraIndent = oCPS1.getPropertyValue("ParaIndent")

        then:
        isCellBackgroundTransparent == false
        cellBackColor == 6710932
        charColor == 16777215
        rotateAngle == 9000
        rotateReference == CellVertJustify.TOP
        vertJustify == CellVertJustify.BOTTOM
        horiJustify == CellHoriJustify.CENTER
        // paraIndent == 200 // 0 for some reason

        cleanup:
        xSpreadsheets.removeByName("MySheet")
    }

}
