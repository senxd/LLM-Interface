package net.prismclient.document

import java.io.File

/**
 * Superclass which represents classes such as PDF files, along with other formats such as docx, and xlsx.
 *
 * @author Winter
 *
 */
abstract class Document(val location: File, vararg val fileExtension: String) {
    /**
     * The name of the file as written.
     */
    val name: String get() = location.nameWithoutExtension
}