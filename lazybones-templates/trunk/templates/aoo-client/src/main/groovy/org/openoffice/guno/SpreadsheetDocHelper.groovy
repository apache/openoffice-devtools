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

// based on SpreadsheetDocHelper.java provide with SDK examples
// added getModel

package org.openoffice.guno

import com.sun.star.comp.servicemanager.ServiceManager
import com.sun.star.container.XIndexAccess
import com.sun.star.container.XNamed
import com.sun.star.beans.XPropertySet
import com.sun.star.bridge.XUnoUrlResolver
import com.sun.star.uno.XNamingService
import com.sun.star.frame.XDesktop
import com.sun.star.frame.XModel
import com.sun.star.frame.XComponentLoader
import com.sun.star.frame.XController
import com.sun.star.lang.XMultiServiceFactory
import com.sun.star.sheet.XCellAddressable
import com.sun.star.sheet.XCellRangeAddressable
import com.sun.star.sheet.XSheetCellRange
import com.sun.star.sheet.XSpreadsheet
import com.sun.star.sheet.XSpreadsheetDocument
import com.sun.star.sheet.XSpreadsheetView
import com.sun.star.sheet.XSpreadsheets
import com.sun.star.table.BorderLine
import com.sun.star.table.CellAddress
import com.sun.star.table.CellRangeAddress
import com.sun.star.table.TableBorder
import com.sun.star.table.XCell
import com.sun.star.table.XCellRange
import com.sun.star.lang.XComponent
import com.sun.star.lang.XMultiComponentFactory

import com.sun.star.uno.UnoRuntime
import com.sun.star.uno.XComponentContext
import com.sun.star.uno.RuntimeException


/** This is a helper class for the spreadsheet and table samples.
It connects to a running office and creates a spreadsheet document.
Additionally it contains various helper functions.
 */
class SpreadsheetDocHelper {

    private final String  msDataSheetName  = "Data"

    private XComponentContext  mxRemoteContext
    private XMultiComponentFactory  mxRemoteServiceManager
    //    private com.sun.star.lang.XMultiServiceFactory  mxMSFactory
    private XSpreadsheetDocument mxDocument
    
    //added
    XComponent xComponent



    SpreadsheetDocHelper( String[] args ) {
        
        // Connect to a running office and get the service manager
        connect()

        // Create a new spreadsheet document
        try
        {
            mxDocument = initDocument()
        }
        catch (Exception ex)
        {
            System.err.println( "Couldn't create document: " + ex )
            System.err.println( "Error: Couldn't create Document\nException Message = "
                + ex.getMessage())
            ex.printStackTrace()
            System.exit( 1 )
        }
    }

    // __  helper methods  ____________________________________________

    /** Returns the service manager of the connected office.
    @return  XMultiComponentFactory interface of the service manager. */
    XMultiComponentFactory getServiceManager() {
        return mxRemoteServiceManager
    }

    /** Returns the component context of the connected office
    @return  XComponentContext interface of the context. */
    XComponentContext getContext() {
        return mxRemoteContext
    }

    /** Returns the whole spreadsheet document.
    @return  XSpreadsheetDocument interface of the document. */
    XSpreadsheetDocument getDocument() {
        return mxDocument
    }
    
    // added
    // loads a component specified by an URL into the specified new or existing frame. 
    /** Returns the component.
    @return  XComponent interface of the document. */
    XComponent getComponent() {
        
        return xComponent
    }

    /** Returns the spreadsheet with the specified index (0-based).
    @param nIndex  The index of the sheet.
    @return  XSpreadsheet interface of the sheet. */
    XSpreadsheet getSpreadsheet( int nIndex ) {
        // Collection of sheets
        XSpreadsheets xSheets = mxDocument.getSheets()
        XSpreadsheet xSheet = null
        try {
            XIndexAccess xSheetsIA = UnoRuntime.queryInterface(
                XIndexAccess.class, xSheets )
            xSheet = UnoRuntime.queryInterface(
                XSpreadsheet.class, xSheetsIA.getByIndex(nIndex))
        } catch (Exception ex) {
            System.err.println( "Error: caught exception in getSpreadsheet()!\nException Message = "
                + ex.getMessage())
            ex.printStackTrace()
        }
        return xSheet
    }

    /** Inserts a new empty spreadsheet with the specified name.
    @param aName  The name of the new sheet.
    @param nIndex  The insertion index.
    @return  The XSpreadsheet interface of the new sheet. */
    XSpreadsheet insertSpreadsheet(
        String aName, short nIndex ) {
        // Collection of sheets
        XSpreadsheets xSheets = mxDocument.getSheets()
        XSpreadsheet xSheet = null
        try {
            xSheets.insertNewByName( aName, nIndex )
            xSheet = UnoRuntime.queryInterface(XSpreadsheet.class,
                xSheets.getByName( aName ))
        } catch (Exception ex) {
            System.err.println( "Error: caught exception in insertSpreadsheet()!\nException Message = "
                + ex.getMessage())
            ex.printStackTrace()
        }
        return xSheet
    }

    XModel getModel() {
        
        XModel xModel = null
        // println xComponent
        try {
            xModel = UnoRuntime.queryInterface(XModel.class, xComponent)
            
        } catch(Exception e){            
            System.err.println(" Exception " + e)
            e.printStackTrace(System.err)
        }        
            
        return xModel
    }
    
    XSpreadsheetView getSpreadsheetView() {
        XController xSpreadsheetController = this.getModel().getCurrentController()
        XSpreadsheetView xSpreadsheetView = UnoRuntime.queryInterface(XSpreadsheetView.class,
            xSpreadsheetController)
        return xSpreadsheetView
    }
    
    
    // ________________________________________________________________
    // Methods to fill values into cells.

    /** Writes a double value into a spreadsheet.
    @param xSheet  The XSpreadsheet interface of the spreadsheet.
    @param aCellName  The address of the cell (or a named range).
    @param fValue  The value to write into the cell. */
    void setValue(
        XSpreadsheet xSheet, String aCellName,
        double fValue ) throws RuntimeException, Exception {
        xSheet.getCellRangeByName( aCellName ).getCellByPosition( 0, 0 ).setValue( fValue )
    }

    /** Writes a formula into a spreadsheet.
    @param xSheet  The XSpreadsheet interface of the spreadsheet.
    @param aCellName  The address of the cell (or a named range).
    @param aFormula  The formula to write into the cell. */
    void setFormula(
        com.sun.star.sheet.XSpreadsheet xSheet,
        String aCellName,
        String aFormula ) throws RuntimeException, Exception {
        xSheet.getCellRangeByName( aCellName ).getCellByPosition( 0, 0 ).setFormula( aFormula )
    }

    /** Writes a date with standard date format into a spreadsheet.
    @param xSheet  The XSpreadsheet interface of the spreadsheet.
    @param aCellName  The address of the cell (or a named range).
    @param nDay  The day of the date.
    @param nMonth  The month of the date.
    @param nYear  The year of the date. */
    void setDate(
        XSpreadsheet xSheet, String aCellName,
        int nDay, int nMonth, int nYear ) throws RuntimeException, Exception {
        // Set the date value.
        com.sun.star.table.XCell xCell = xSheet.getCellRangeByName( aCellName ).getCellByPosition( 0, 0 )
        String aDateStr = nMonth + "/" + nDay + "/" + nYear
        xCell.setFormula( aDateStr )

        // Set standard date format.
        com.sun.star.util.XNumberFormatsSupplier xFormatsSupplier = UnoRuntime.queryInterface(
            com.sun.star.util.XNumberFormatsSupplier.class, getDocument() )
        com.sun.star.util.XNumberFormatTypes xFormatTypes = UnoRuntime.queryInterface(
            com.sun.star.util.XNumberFormatTypes.class, xFormatsSupplier.getNumberFormats() )
        int nFormat = xFormatTypes.getStandardFormat(
            com.sun.star.util.NumberFormat.DATE, new com.sun.star.lang.Locale() )

        XPropertySet xPropSet = UnoRuntime.queryInterface(XPropertySet.class, xCell )
        xPropSet.setPropertyValue( "NumberFormat", new Integer( nFormat ) )
    }

    /** Draws a colored border around the range and writes the headline in the
    first cell.
    @param xSheet  The XSpreadsheet interface of the spreadsheet.
    @param aRange  The address of the cell range (or a named range).
    @param aHeadline  The headline text. */
    void prepareRange(XSpreadsheet xSheet,
        String aRange, String aHeadline ) throws RuntimeException, Exception {
        XPropertySet xPropSet = null
        XCellRange xCellRange = null

        // draw border
        xCellRange = xSheet.getCellRangeByName( aRange )
        xPropSet = UnoRuntime.queryInterface(XPropertySet.class, xCellRange )
        BorderLine aLine = new BorderLine()
        aLine.Color = 0x99CCFF
        aLine.InnerLineWidth = aLine.LineDistance = 0
        aLine.OuterLineWidth = 100
        TableBorder aBorder = new TableBorder()
        aBorder.TopLine = aBorder.BottomLine = aBorder.LeftLine = aBorder.RightLine = aLine
        aBorder.IsTopLineValid = aBorder.IsBottomLineValid = true
        aBorder.IsLeftLineValid = aBorder.IsRightLineValid = true
        xPropSet.setPropertyValue( "TableBorder", aBorder )

        // draw headline
        XCellRangeAddressable xAddr = UnoRuntime.queryInterface(
            XCellRangeAddressable.class, xCellRange )
        CellRangeAddress aAddr = xAddr.getRangeAddress();

        xCellRange = xSheet.getCellRangeByPosition(
            aAddr.StartColumn, aAddr.StartRow, aAddr.EndColumn, aAddr.StartRow )
        xPropSet = UnoRuntime.queryInterface( com.sun.star.beans.XPropertySet.class, xCellRange )
        xPropSet.setPropertyValue( "CellBackColor", new Integer( 0x99CCFF ) )
        // write headline
        XCell xCell = xCellRange.getCellByPosition( 0, 0 )
        xCell.setFormula( aHeadline )
        xPropSet = (com.sun.star.beans.XPropertySet)
        UnoRuntime.queryInterface(XPropertySet.class, xCell )
        xPropSet.setPropertyValue( "CharColor", new Integer( 0x003399 ) )
        xPropSet.setPropertyValue( "CharWeight", new Float( com.sun.star.awt.FontWeight.BOLD ) )
    }
    
    /** Inserts a cell range address into a cell range container and prints a message.
    @param xContainer The com.sun.star.sheet.XSheetCellRangeContainer interface of the container.
    @param nSheet Index of sheet of the range.
    @param nStartCol Index of first column of the range.
    @param nStartRow Index of first row of the range.
    @param nEndCol Index of last column of the range.
    @param nEndRow Index of last row of the range.
    @param bMerge Determines whether the new range should be merged with the existing ranges.
     */
    static void insertRange(
        com.sun.star.sheet.XSheetCellRangeContainer xContainer,
        int nSheet, int nStartCol, int nStartRow, int nEndCol, int nEndRow,
        boolean bMerge) throws RuntimeException, Exception {
        com.sun.star.table.CellRangeAddress aAddress = new com.sun.star.table.CellRangeAddress();
        aAddress.Sheet = (short)nSheet;
        aAddress.StartColumn = nStartCol;
        aAddress.StartRow = nStartRow;
        aAddress.EndColumn = nEndCol;
        aAddress.EndRow = nEndRow;
        xContainer.addRangeAddress(aAddress, bMerge);
        System.out.println(
         "Inserting " + (bMerge ? " with" : "without") + " merge,"
            + " result list: " + xContainer.getRangeAddressesAsString());
    }

    // ________________________________________________________________
    // Methods to create cell addresses and range addresses.

    /** Creates a com.sun.star.table.CellAddress and initializes it
    with the given range.
    @param xSheet  The XSpreadsheet interface of the spreadsheet.
    @param aCell  The address of the cell (or a named cell). */
    CellAddress createCellAddress(
        XSpreadsheet xSheet,
        String aCell ) throws RuntimeException, Exception {
        XCellAddressable xAddr = UnoRuntime.queryInterface(XCellAddressable.class,
            xSheet.getCellRangeByName( aCell ).getCellByPosition( 0, 0 ) )
        return xAddr.getCellAddress()
    }

    /** Creates a com.sun.star.table.CellRangeAddress and initializes
    it with the given range.
    @param xSheet  The XSpreadsheet interface of the spreadsheet.
    @param aRange  The address of the cell range (or a named range). */
    CellRangeAddress createCellRangeAddress(
        XSpreadsheet xSheet, String aRange ) {
        
        XCellRangeAddressable xAddr = UnoRuntime.queryInterface(XCellRangeAddressable.class,
            xSheet.getCellRangeByName( aRange ) )
        return xAddr.getRangeAddress()
    }

    // ________________________________________________________________
    // Methods to convert cell addresses and range addresses to strings.

    /** Returns the text address of the cell.
    @param nColumn  The column index.
    @param nRow  The row index.
    @return  A string containing the cell address. */
    String getCellAddressString( int nColumn, int nRow ) {
        String aStr = ""
        if (nColumn > 25) {
            aStr += ('A' + nColumn / 26 - 1) // removed char cast
        } 
        aStr += ('A' + nColumn % 26)
        aStr += (nRow + 1)
        return aStr
    }

    /** Returns the text address of the cell range.
    @param aCellRange  The cell range address.
    @return  A string containing the cell range address. */
    String getCellRangeAddressString(
        CellRangeAddress aCellRange ) {
        return getCellAddressString( aCellRange.StartColumn, aCellRange.StartRow )
        + ":"
        + getCellAddressString( aCellRange.EndColumn, aCellRange.EndRow )
    }

    /** Returns the text address of the cell range.
    @param xCellRange  The XSheetCellRange interface of the cell range.
    @param bWithSheet  true = Include sheet name.
    @return  A string containing the cell range address. */
    String getCellRangeAddressString(XSheetCellRange xCellRange, boolean bWithSheet ) {
        String aStr = ""
        if (bWithSheet) {
            XSpreadsheet xSheet = xCellRange.getSpreadsheet()
            XNamed xNamed = UnoRuntime.queryInterface(XNamed.class, xSheet )
            aStr += xNamed.getName() + "."
        }
        XCellRangeAddressable xAddr = UnoRuntime.queryInterface(
            XCellRangeAddressable.class, xCellRange )
        aStr += getCellRangeAddressString( xAddr.getRangeAddress() )
        return aStr
    }

    /** Returns a list of addresses of all cell ranges contained in the collection.
    @param xRangesIA  The XIndexAccess interface of the collection.
    @return  A string containing the cell range address list. */
    String getCellRangeListString(
        XIndexAccess xRangesIA ) throws RuntimeException, Exception {
        String aStr = ""
        int nCount = xRangesIA.getCount()
        for (int nIndex = 0; nIndex < nCount; ++nIndex) {
            if (nIndex > 0)
            aStr += " "
            Object aRangeObj = xRangesIA.getByIndex( nIndex )
            XSheetCellRange xCellRange = (XSheetCellRange)
            UnoRuntime.queryInterface(XSheetCellRange.class, aRangeObj)
            aStr += getCellRangeAddressString( xCellRange, false )
        }
        return aStr
    }


    // Connect to a running office that is accepting connections.
    private void connect() {
        if (mxRemoteContext == null && mxRemoteServiceManager == null) {
            try {
                // First step: get the remote office component context
                mxRemoteContext = com.sun.star.comp.helper.Bootstrap.bootstrap()
                System.out.println("Connected to a running office ...")
                    
                mxRemoteServiceManager = mxRemoteContext.getServiceManager()
            } catch( Exception e) {
                System.err.println("ERROR: can't get a component context from a running office ...")
                e.printStackTrace()
                System.exit(1)
            }            
        }
    }

    /** Creates an empty spreadsheet document.
    @return  The XSpreadsheetDocument interface of the document. */
    private XSpreadsheetDocument initDocument()
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


