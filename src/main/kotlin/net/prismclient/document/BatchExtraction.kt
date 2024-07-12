package net.prismclient.document

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.prismclient.document.type.text.TextDocument
import java.io.File

/**
 * A utility class used for extracting a folder of [TextDocument]s.
 *
 * @author Winter
 */
@Deprecated("dont use")
class BatchExtraction(private val documents: ArrayList<TextDocument>) {
    /**
     * Given a [folder], add all documents with the provided [extension](s) based the specified [TextDocument] type defined
     * by [map].
     */
    constructor(folder: File, vararg extension: String, map: (file: File) -> TextDocument) : this(arrayListOf()) {
        if (!folder.isDirectory || !folder.exists())
            throw RuntimeException("$folder, file path: ${folder.absolutePath} is not a directory or does not exist.")

        val filteredFiles = folder.listFiles { file -> file.isFile && extension.contains(file.extension) }
            ?: throw RuntimeException("No files found with the given extension")

        filteredFiles.forEach { file -> documents.add(map(file)) }
    }

    fun addDocument(document: TextDocument) {
        documents += document
    }

    fun removeDocument(document: TextDocument) {
        documents -= document
    }

    /**
     * Applies the [lambda] to every document in the execution.
     */
    fun extract(poolSize: Int = 10, delay: Long = 0L, lambda: TextDocument.(extractedText: String) -> Unit) {
        val semaphore = Semaphore(poolSize)

        runBlocking {
            documents.map { document ->
                async(Dispatchers.Default) {
                    semaphore.withPermit {
                        document.lambda(document.extract())
                        delay(delay)
                    }
                }
            }.awaitAll()
        }
    }
}