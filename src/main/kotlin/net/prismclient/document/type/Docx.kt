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
    override fun extract(): String = Builder {
        FileInputStream(file).use {
            XWPFDocument(FileInputStream(file)).use { document ->
                document.paragraphs.forEach { paragraph ->
                    this.append(paragraph.text).append("\n")
                }

                document.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            this.append(cell.text).append("\n")
                        }
                    }
                }

                document.close()
            }
        }
    }
}