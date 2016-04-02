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

/**
 *
 * @author Carl Marcum - CodeBuilders.net
 */

import com.sun.star.beans.XPropertySet
import com.sun.star.container.ElementExistException
import com.sun.star.container.XEnumeration
import com.sun.star.container.XEnumerationAccess
import com.sun.star.container.XIndexAccess
import com.sun.star.container.XNameAccess
import com.sun.star.container.XNameContainer
import com.sun.star.frame.XComponentLoader
import com.sun.star.lang.XComponent
import com.sun.star.lang.XMultiServiceFactory
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.sheet.XCellAddressable
import com.sun.star.sheet.XCellRangesQuery
import com.sun.star.sheet.XSheetCellRangeContainer
import com.sun.star.sheet.XSheetCellRanges
import com.sun.star.sheet.XSpreadsheet
import com.sun.star.sheet.XSpreadsheetDocument
import com.sun.star.sheet.XSpreadsheets
import com.sun.star.style.XStyleFamiliesSupplier
import com.sun.star.table.CellAddress
import com.sun.star.table.CellVertJustify
import com.sun.star.table.XCell
import com.sun.star.uno.UnoRuntime
import com.sun.star.uno.XComponentContext
import com.sun.star.uno.XInterface


class SpreadsheetExtension {



    /** Returns the spreadsheet document with the specified index component context
     * @param mxRemoteContext the remote context.
     * @return XSpreadsheetDocument interface of the spreadsheet document.
     */
    static XSpreadsheetDocument getSpreadsheetDocument(final XComponent self, XComponentContext mxRemoteContext) {

        XSpreadsheetDocument xSpreadsheetDocument = null

        xSpreadsheetDocument = UnoRuntime.queryInterface(
                XSpreadsheetDocument.class, self)

        return xSpreadsheetDocument
    }

    /* XSpreadsheetDocument methods *******************************************/

    /** Returns the spreadsheet with the specified index (0-based).
     * @param nIndex The index of the sheet.
     * @return XSpreadsheet interface of the sheet.
     */
    static XSpreadsheet getSheetByIndex(final XSpreadsheetDocument self, Integer nIndex) {
        // Collection of sheets
        XSpreadsheets xSheets = self.getSheets()
        XSpreadsheet xSheet = null

        XIndexAccess xSheetsIA = UnoRuntime.queryInterface(
                XIndexAccess.class, xSheets)
        xSheet = UnoRuntime.queryInterface(
                XSpreadsheet.class, xSheetsIA.getByIndex(nIndex))

        return xSheet

    }

    /** Returns the spreadsheet with the specified name.
     * @param name The name of the sheet.
     * @return XSpreadsheet interface of the sheet.
     */
    static XSpreadsheet getSheetByName(final XSpreadsheetDocument self, String name) {
        // Collection of sheets
        XSpreadsheets xSheets = self.getSheets()
        XSpreadsheet xSpreadsheet = null

        Object sheet = xSheets.getByName(name)
        // removed cast from right side
        xSpreadsheet = UnoRuntime.queryInterface(XSpreadsheet.class, sheet)

        return xSpreadsheet

    }

    /** Returns a sheet cell range container.
     * @return XSheetCellRangeContainer a sheet cell range container of the sheet.
     */
    static XSheetCellRangeContainer getRangeContainer(final XSpreadsheetDocument self) {
        XMultiServiceFactory xDocFactory = UnoRuntime.queryInterface(
                XMultiServiceFactory.class, self)
        XSheetCellRangeContainer result = UnoRuntime.queryInterface(
                XSheetCellRangeContainer.class, xDocFactory.createInstance("com.sun.star.sheet.SheetCellRanges"))
        return result
    }

    /** Returns the style families supplier
     * @return XStyleFamiliesSupplier the style families supplier of the spreadsheet document.
     */
    static XStyleFamiliesSupplier getStyleFamiliesSupplier(final XSpreadsheetDocument self) {
        XStyleFamiliesSupplier result = UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, self)
        return result
    }

    /** Returns the property set of the cell style if it exists. If not the the cell style is
     * created and it's property set is returned.
     * @param cellStyle the name of the cell style to return the property set of.
     * @return XPropertySet the property set of the cell style.
     */
    static XPropertySet getCellStylePropertySet(final XSpreadsheetDocument self, String cellStyle) {

        XStyleFamiliesSupplier xSFS = UnoRuntime.queryInterface(XStyleFamiliesSupplier.class, self)
        XNameAccess xSF = xSFS.getStyleFamilies()
        // get the cell styles
        XNameAccess xCS = UnoRuntime.queryInterface(XNameAccess.class, xSF.getByName("CellStyles"))

        // see if cellStyle exists
        if (xCS.hasByName(cellStyle)) {
            // get the property set
            Object oCS = xCS.getByName(cellStyle)

            XPropertySet result = UnoRuntime.queryInterface(XPropertySet.class, oCS);
            return result

        } else {
            // add cell style and return property set
            // get the service factory
            XMultiServiceFactory oDocMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, self)
            // get the name container
            XNameContainer oStyleFamilyNameContainer = UnoRuntime.queryInterface(XNameContainer.class, xCS)
            // create the interface
            XInterface oInt1 = oDocMSF.createInstance("com.sun.star.style.CellStyle")

            // insert style
            oStyleFamilyNameContainer.insertByName(cellStyle, oInt1)

            // get the property set
            XPropertySet result = UnoRuntime.queryInterface(XPropertySet.class, oInt1);
            return result
        }

    }

    /* XSpreadsheet methods *************************************************/

    /**
     * Returns the cell ranges matching the specified type.
     * @param type a combination of CellFlags flags.
     * @return Object all cells of the current cell range(s) with the specified content type(s).
     */
    static XSheetCellRanges getCellRanges(final XSpreadsheet self, Object type) {
        XCellRangesQuery xCellQuery = UnoRuntime.queryInterface(XCellRangesQuery.class, self)
        XSheetCellRanges result = xCellQuery.queryContentCells((short) type)
        return result
    }

    /**
     * Inserts a formula (string) value into the cell specified by column and row.
     * @param column zero based column position.
     * @param row zero based row position.
     * @param value the string value to insert.
     */
    static void insertFormulaIntoCell(final XSpreadsheet self, int column, int row, String value) {

        XCell xCell = null
        xCell = self.getCellByPosition(column, row)
        xCell.setFormula(value)
    }

    /**
     * Inserts a float value into the cell specified by column and row.
     * @param column zero based column position.
     * @param row zero based row position.
     * @param value the float value to insert.
     */
    static void insertValueIntoCell(final XSpreadsheet self, int column, int row, float value) {

        XCell xCell = null
        xCell = self.getCellByPosition(column, row)
        xCell.setValue((new Float(value)).floatValue())
    }

    /* XSheetCellRanges methods **********************************************/

    /** Returns a list of XCells contained in the range.
     * @return List < XCell >  list of XCells contained in the range.
     */
    static List<XCell> getCellList(final XSheetCellRanges self) {
        def result = []
        XEnumerationAccess xFormulas = self.cells
        XEnumeration xFormulaEnum = xFormulas.createEnumeration()
        while (xFormulaEnum.hasMoreElements()) {
            Object obj = xFormulaEnum.nextElement()
            XCell xCell = UnoRuntime.queryInterface(XCell.class, obj)
            result << xCell
        }
        return result
    }

    /* XSheetCellRangeContainer methods **************************************/

    /** Returns a list of XCells contained in the range container.
     * @return List < XCell >  list of XCells contained in the range container.
     */
    static List<XCell> getCellList(final XSheetCellRangeContainer self) {
        def result = []
        XEnumerationAccess xFormulas = self.cells
        XEnumeration xFormulaEnum = xFormulas.createEnumeration()
        while (xFormulaEnum.hasMoreElements()) {
            Object obj = xFormulaEnum.nextElement()
            XCell xCell = UnoRuntime.queryInterface(XCell.class, obj)
            result << xCell
        }
        return result
    }



    // XCellRange method for getPropertySet
    // maybe try XPropertySet.getPropertySet (class self)

    // TEST



    /* XCell methods *********************************************************/

    /** Sets the specified property with the value.
     * @param prop The name of the property to set.
     * @param value The value to set.
     */
    static void setPropertyValue(final XCell self, String prop, Object value) {
        XPropertySet xCellProps = UnoRuntime.queryInterface(
                XPropertySet.class, self)
        xCellProps.setPropertyValue(prop, value)
    }

    /** Returns the value of the property.
     * @param prop The name of the property to get.
     * @return Object value of a type determined by the property.
     */
    static Object getPropertyValue(final XCell self, String prop) {
        XPropertySet xCellProps = UnoRuntime.queryInterface(
                XPropertySet.class, self)
        Object result = xCellProps.getPropertyValue(prop)
        return result
    }

    /** Sets the CellStyle property with the value.
     * @param value The value to set.
     */
    static void setCellStyle(final XCell self, Object value) {
        self.setPropertyValue("CellStyle", value)
    }

    /** Sets the VertJustify property with the value.
     * @param value The value to set.
     */
    static void setVertJustify(final XCell self, Object value) {
        self.setPropertyValue("VertJustify", value)
    }

    /** Returns the value of the VertJustify property.
     * @return Integer value of a type detemined by the property.
     */
    static Integer getVertJustify(final XCell self) {
        int result = self.getPropertyValue("VertJustify").value
        return result
    }

    /** Returns the cell address of the cell.
     * @return CellAddress the cell address within the spreadsheet document.
     */
    static CellAddress getAddress(final XCell self) {
        XCellAddressable xCellAddressable = UnoRuntime.queryInterface(XCellAddressable.class, self)
        CellAddress result = xCellAddressable.getCellAddress()
        return result
    }


}

