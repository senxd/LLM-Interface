package net.prismclient.document

import java.io.Closeable
import java.io.File

/**
 * @author Winter
 */
abstract class Document(val file: File) {
    /**
     * The name of the file as written.
     */
    val name: String get() = file.nameWithoutExtension

    val extension: String get() = file.extension

    open var cache: Boolean = false

    /**
     * A [use] block but does not dispose of it if [cache] is true.
     */
    protected inline fun <T : Closeable?, R> T.dispose(block: (T) -> R): R =
        if (cache) block(this) else this.use(block)
}