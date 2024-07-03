package net.prismclient.document.type.database.excel

import net.prismclient.document.Document
import java.io.File

class Excel(file: File) : Document(file) {
    val excelFile: ExcelFile = ExcelFile(file)

    override var cache: Boolean = true
        set(value) {
            excelFile.cache = value
            field = value
        }

    fun retrieveSheet(page: Int = 0) = excelFile.getSheetAt(page)
}