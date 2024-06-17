package net.prismclient.document

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

/**
 * [DocumentBatch] allows for parallel processing of a subtype of [Document] which can make processing more efficient
 * when processing a large amount of documents at once.
 *
 * @author Winter
 */
class DocumentBatch<T : Document> {
    val documents = mutableListOf<T>()

    fun batchExecute(exec: T.() -> Unit) {
        runBlocking {
            documents.map { document ->
                async(Dispatchers.Default) {
                    document.exec()
                }
            }.awaitAll()
        }
    }
}