package net.prismclient.document.type.database.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFRow

class ExcelRow(internal val _row: XSSFRow) {
    val lastCellNumber: Int get() = _row.lastCellNum.toInt()

    val sheet: Sheet get() = _row.sheet

    val firstCellNum: Short get() = _row.firstCellNum

    val physicalNumberOfCells: Int get() = _row.physicalNumberOfCells

    var rowNum: Int
        get() = _row.rowNum
        set(value) { _row.rowNum = value }

    fun getCell(index: Int): ExcelCell = ExcelCell(_row.getCell(index))


//    fun cellIterator(): Iterator<Cell> = _row.cellIterator()
//
//    fun spliterator(): Spliterator<Cell> = _row.spliterator()
}
