package net.prismclient.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.prismclient.document.Document
import java.io.File

/**
 * Returns a [File] representation of the String file from the resources folder of the project.
 */
val String.localResource: File
    get() = File(
        Thread.currentThread().contextClassLoader.getResource(this)?.path
            ?: throw IllegalArgumentException("Resource not found: $this")
    )

fun <T : Document> Array<T>.batchExecute(exec: (document: T) -> Unit) {
    runBlocking {
        this@batchExecute.map { document ->
            async(Dispatchers.Default) {
                exec(document)
            }
        }.awaitAll()
    }
}