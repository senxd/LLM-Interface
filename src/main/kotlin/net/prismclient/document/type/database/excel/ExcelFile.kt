package net.prismclient.document.type.database.excel

import org.apache.poi.xssf.usermodel.*
import java.io.Closeable
import java.io.File

class ExcelFile(private val file: File) : Closeable {
    internal var cache: Boolean = true

    private var _workbook: XSSFWorkbook? = null
    private val workbook: XSSFWorkbook
        get() = _workbook ?: XSSFWorkbook(file).also { if (cache) _workbook = it }

    val numberOfSheets: Int get() = workbook.numberOfSheets

    init { Runtime.getRuntime().addShutdownHook(Thread { close() }) }

    fun getSheetAt(index: Int): ExcelSheet = ExcelSheet(workbook.getSheetAt(index))

    fun getSheet(sheetName: String): ExcelSheet? = workbook.getSheet(sheetName)?.let { ExcelSheet(it) }

    fun getRow(sheetIndex: Int, rowIndex: Int): ExcelRow = getSheetAt(sheetIndex).getRow(rowIndex)

    fun getCell(sheetIndex: Int, rowIndex: Int, cellIndex: Int): ExcelCell =
        getRow(sheetIndex, rowIndex).getCell(cellIndex)

    fun getActiveSheetIndex() = workbook.activeSheetIndex

    fun getSheetName(sheetIndex: Int) = workbook.getSheetName(sheetIndex)

    fun getSheetIndex(name: String) = workbook.getSheetIndex(name)

    fun getSheetIndex(sheet: ExcelSheet) = workbook.getSheetIndex(sheet._sheet)

    fun getName(name: String): String = workbook.getName(name).nameName

    fun getAllNames(): List<String> = workbook.allNames.map { it.nameName }

    override fun close() {
        _workbook?.close()
        _workbook = null
    }
}
