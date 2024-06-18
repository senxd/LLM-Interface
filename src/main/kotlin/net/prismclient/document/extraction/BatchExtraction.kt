// IMPROVE: Rewrite debug statements
package net.prismclient.document.extraction

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.prismclient.document.Document
import java.io.File

/**
 * A utility class used for extracting a folder of [Document]s.
 *
 * @author Winter
 */
class BatchExtraction(private val documents: ArrayList<Document>) {
    /**
     * Given a [folder], add all documents with the provided [extension](s) based the specified [Document] type defined
     * by [map].
     */
    constructor(folder: File, vararg extension: String, map: (file: File) -> Document) : this(arrayListOf()) {
        if (!folder.isDirectory || !folder.exists())
            throw RuntimeException("$folder, file path: ${folder.absolutePath} is not a directory or does not exist.")

        val filteredFiles = folder.listFiles { file -> file.isFile && extension.contains(file.extension) }
            ?: throw RuntimeException("No files found with the given extension")

        filteredFiles.forEach { file -> documents.add(map(file)) }
    }

    fun addDocument(document: Document) {
        documents += document
    }

    fun removeDocument(document: Document) {
        documents -= document
    }

    /**
     * Applies the [lambda] to every document in the execution.
     */
    fun extract(poolSize: Int = 10, lambda: (Document) -> Unit) {
        val semaphore = Semaphore(poolSize)

        runBlocking {
            documents.map { document ->
                async(Dispatchers.Default) {
                    semaphore.withPermit {
                        lambda(document)
                    }
                }
            }.awaitAll()
        }
    }
}