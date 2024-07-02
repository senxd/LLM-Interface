package net.prismclient.document.type.database.excel

import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.FormulaError
import org.apache.poi.xssf.usermodel.XSSFCell
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class ExcelCell(internal val _cell: XSSFCell) {
    var content: String
        get() = _cell.stringCellValue
        set(value) { _cell.setCellValue(value) }

    var booleanValue: Boolean
        get() = _cell.booleanCellValue
        set(value) { _cell.setCellValue(value) }

    var numericValue: Double
        get() = _cell.numericCellValue
        set(value) { _cell.setCellValue(value) }

    var stringValue: String
        get() = _cell.richStringCellValue.string
        set(value) { _cell.setCellValue(value) }

    var formula: String
        get() = _cell.cellFormula
        set(value) { _cell.cellFormula = value }

    var errorValue: Byte
        get() = _cell.errorCellValue
        set(value) { _cell.setCellErrorValue(value) }

    val isArrayFormula: Boolean
        get() = _cell.isPartOfArrayFormulaGroup

    var dateValue: Date?
        get() = _cell.dateCellValue
        set(value) { _cell.setCellValue(value) }

    var localDateTimeValue: LocalDateTime?
        get() = _cell.localDateTimeCellValue
        set(value) { _cell.setCellValue(value) }

    val columnIndex: Int
        get() = _cell.columnIndex

    val rowIndex: Int
        get() = _cell.rowIndex

    val reference: String
        get() = _cell.reference

    val rawValue: String?
        get() = _cell.rawValue

    fun toFormattedString(): String {
        return when (_cell.cellType) {
            CellType.NUMERIC -> {
                if (DateUtil.isCellDateFormatted(_cell)) {
                    val sdf = SimpleDateFormat("dd-MMM-yyyy")
                    sdf.format(_cell.dateCellValue)
                } else {
                    _cell.numericCellValue.toString()
                }
            }
            CellType.STRING -> _cell.richStringCellValue.toString()
            CellType.FORMULA -> _cell.cellFormula
            CellType.BLANK -> ""
            CellType.BOOLEAN -> _cell.booleanCellValue.toString()
            CellType.ERROR -> FormulaError.forInt(_cell.errorCellValue).string
            else -> "Unknown Cell Type"
        }
    }
}
