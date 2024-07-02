package net.prismclient.document.type.database.excel

import org.apache.poi.xssf.usermodel.XSSFSheet

class ExcelSheet(internal val _sheet: XSSFSheet) {
//    val workbook: Workbook
//        get() = _sheet.workbook

    val sheetName: String get() = _sheet.sheetName

    val lastRowNum: Int get() = _sheet.lastRowNum

    val firstRowNum: Int get() = _sheet.firstRowNum

    val physicalNumberOfRows: Int get() = _sheet.physicalNumberOfRows

    fun getRow(index: Int): ExcelRow = ExcelRow(_sheet.getRow(index))

//    fun cellIterator(): Iterator<Row> = _sheet.rowIterator()

//    fun findCellInRange(range: CellRangeAddress, value: String): ExcelCell? {
//        for (rowIndex in range.firstRow..range.lastRow) {
//            val row = getRow(rowIndex) ?: continue
//            for (colIndex in range.firstColumn..range.lastColumn) {
//                val cell = row.getCell(colIndex)
//                if (cell.content == value) return cell
//            }
//        }
//        return null
//    }

    override fun toString() = _sheet.toString()
}
