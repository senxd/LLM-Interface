package net.prismclient.document.type

import net.prismclient.document.Document
import java.io.File

/**
 * Used for parsing Microsoft Word documents (file extension docx).
 */
class Docx(fileLocation: File) : Document(fileLocation, "docx") {
 //import org.apache.poi.xwpf.usermodel.XWPFDocument
    //import java.io.FileInputStream
    //
    //fun readDocxFile(filePath: String) {
    //    // Open the DOCX file
    //    FileInputStream(filePath).use { fis ->
    //        // Create a document object
    //        val document = XWPFDocument(fis)
    //
    //        // Iterate through the paragraphs in the document
    //        for (paragraph in document.paragraphs) {
    //            println(paragraph.text)
    //        }
    //
    //        // Iterate through the tables in the document
    //        for (table in document.tables) {
    //            for (row in table.rows) {
    //                for (cell in row.tableCells) {
    //                    println(cell.text)
    //                }
    //            }
    //        }
    //    }
    //}
    //
    //fun main() {
    //    val filePath = "path/to/your/document.docx"
    //    readDocxFile(filePath)
    //}
}