package net.prismclient.document.type.text

import net.prismclient.document.Document
import java.io.File

/**
 * Superclass which represents classes such as PDF files, along with other formats such as docx, and xlsx.
 *
 * @author Winter
 *
 */
abstract class TextDocument(file: File, vararg val fileExtensions: String) : Document(file) {
    open var extractionCache: StringBuilder? = null

    abstract fun extract(): String

    /**
     * Clears the cache of the parsed document if applicable.
     */
    fun clearCache() {
        extractionCache = null
    }

    /**
     * Return a String given the lambda with a [StringBuilder] as a receiver. Automatically updates [extractionCache].
     */
    protected inline fun Builder(lambda: StringBuilder.() -> Unit): String =
        StringBuilder()
            .apply {
                if (cache) extractionCache = this
                lambda(this)
            }.toString()
}