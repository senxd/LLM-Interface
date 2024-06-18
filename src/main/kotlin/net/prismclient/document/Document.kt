package net.prismclient.document

import java.io.File

/**
 * Superclass which represents classes such as PDF files, along with other formats such as docx, and xlsx.
 *
 * @author Winter
 *
 */
abstract class Document(val location: File, vararg val fileExtensions: String, var cache: Boolean = false) {
    /**
     * The name of the file as written.
     */
    val name: String get() = location.nameWithoutExtension

    var extractionCache: StringBuilder? = null

    abstract fun extract(): String

    /**
     * Clears the cache of the parsed document if applicable.
     */
    fun clearCache() {
        extractionCache = null
    }
}