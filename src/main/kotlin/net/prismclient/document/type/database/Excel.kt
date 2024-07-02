package net.prismclient.document.type.database

import net.prismclient.document.Document
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class Excel(file: File) : Document(file) {
    private var _workbook: XSSFWorkbook? = null
    private val workbook: XSSFWorkbook
        get() = _workbook ?: XSSFWorkbook(file).also { if (cache) _workbook = it }

    init {
        Runtime.getRuntime().addShutdownHook(Thread { _workbook?.close() })
    }

    fun retrieveSheet(page: Int = 0, lambda: (sheet: XSSFSheet) -> Unit) {
        workbook.dispose { lambda(it.getSheetAt(page)) }
    }
}