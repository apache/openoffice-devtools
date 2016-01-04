/*
 * ************************************************************
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
 ************************************************************
 */
/*
 * ${project_class_name}.groovy
 *
 * Created on 2015.12.05 - 08:01:43
 *
 */
package ${project_package}

import com.sun.star.beans.PropertyValue as PV
import com.sun.star.beans.XPropertySet
import com.sun.star.uno.XComponentContext
import com.sun.star.comp.helper.Bootstrap
import com.sun.star.container.XEnumeration
import com.sun.star.container.XEnumerationAccess
import com.sun.star.frame.XComponentLoader
import com.sun.star.frame.XController
import com.sun.star.frame.XModel
import com.sun.star.lang.XComponent
import com.sun.star.lang.XMultiComponentFactory
import com.sun.star.sheet.CellFlags
import com.sun.star.sheet.XCellAddressable
import com.sun.star.sheet.XCellRangesQuery
import com.sun.star.sheet.XSheetCellRangeContainer
import com.sun.star.sheet.XSheetCellRanges
import com.sun.star.sheet.XSpreadsheet
import com.sun.star.sheet.XSpreadsheetDocument
import com.sun.star.sheet.XSpreadsheetView
import com.sun.star.sheet.XSpreadsheets
import com.sun.star.table.XCell
import com.sun.star.uno.UnoRuntime

import org.openoffice.guno.SpreadsheetDocHelper

/**
 *
 * @author Carl Marcum
 */
class ${project_class_name} {

    /**
     * Creates a new instance of GroovyUnoClient
     */
    public ${project_class_name}() {
    }

    /**
     * @param args the command line arguments
     */
    static void main(String[] args) {
        try {

            SpreadsheetDocHelper sdHelper = new SpreadsheetDocHelper()

            XSpreadsheetDocument xSpreadsheetDocument = sdHelper.getDocument()

            XSpreadsheets xSpreadsheets = xSpreadsheetDocument.getSheets()
            xSpreadsheets.insertNewByName("MySheet", (short)0)
            
            com.sun.star.uno.Type elemType = xSpreadsheets.getElementType()
            println(elemType.getTypeName())

            XSpreadsheet xSpreadsheet = xSpreadsheetDocument.getSheetByName("MySheet")
 
            XCell xCell = xSpreadsheet.getCellByPosition(0, 0)
            xCell.value = 21
            xCell = xSpreadsheet.getCellByPosition(0, 1)
            xCell.value = 21
            xCell = xSpreadsheet.getCellByPosition(0, 2)
            
            // looks like property access but calls setFormula
            xCell.formula = "=sum(A1:A2)"

            // looks like property access but calls setCellStyle
            xCell.cellStyle = "Result"
            
            // example for use of enum types
            xCell.vertJustify = com.sun.star.table.CellVertJustify.TOP
            // xCell.vertJustify = 1 // also works

            XSpreadsheetView xSpreadsheetView = sdHelper.getSpreadsheetView()

            xSpreadsheetView.setActiveSheet(xSpreadsheet)

            // *********************************************************
            // example for getting cell ranges
            XSheetCellRanges formulaCells = xSpreadsheet.getCellRanges(CellFlags.FORMULA)
            
            // example for using a closure to iterate through list of cells
            XCell[] cellList = formulaCells.cellList
            cellList.each() {println("Formula cell in column " + it.address.Column  + 
                ", row " + it.address.Row + " contains " + it.formula)
            }

            // Create a new cell range container
            XSheetCellRangeContainer xRangeCont = xSpreadsheetDocument.rangeContainer
         
            // query addresses of all cells containing anything
            XSheetCellRanges xCellRanges = xSpreadsheet.getCellRanges(1023)
            
            println("Cells containing anything: " + xCellRanges.rangeAddressesAsString)
            
            // add xCellRanges to xRangeCont
            xRangeCont.addRangeAddresses(xCellRanges.rangeAddresses, false)

            // Get the list of cells from a range container and 
            // use a closure to iterate through the list
            XCell[] cellList2 = xRangeCont.cellList
            print("All filled cells: ")
            cellList2.each() {println("Formula cell in column " + it.address.Column  + 
                ", row " + it.address.Row + " contains " + it.formula)
            }


        }
        catch (Exception e){
            e.printStackTrace()
        }
        finally {
            System.exit( 0 )
        }
    }
    

    


}
