package net.prismclient.document.type

import net.prismclient.document.Document
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream

/**
 * Used for parsing Microsoft Word documents (file extension docx).
 *
 * @author Winter
 */
class Docx(fileLocation: File) : Document(fileLocation, "docx", "doc") {
    override fun extract(): String {
        FileInputStream(location).use {
            XWPFDocument(FileInputStream(location)).use { document ->
                val content = StringBuilder()

                document.paragraphs.forEach { paragraph ->
                    content.append(paragraph.text).append("\n")
                }

                document.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            content.append(cell.text).append("\n")
                        }
                    }
                }

                document.close()

                if (cache) extractionCache = content

                return content.toString()
            }
        }
    }
}