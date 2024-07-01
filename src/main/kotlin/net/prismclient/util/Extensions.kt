package net.prismclient.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import net.prismclient.document.Document
import net.prismclient.model.Message
import net.prismclient.model.LLM
import java.io.File

/**
 * Creates a new block given the [LLM]. Is used as the basis for interfacing with this Library.
 */
inline fun <T : LLM> Model(model: T, action: T.() -> Unit) {
    model.establishConnection()
    model.action()
}

/**
 * Returns a [File] representation of the String file from the resources folder of the project.
 */
val String.localResource: File
    get() = File(
        Thread.currentThread().contextClassLoader.getResource(this)?.path
            ?: throw IllegalArgumentException("Resource not found: $this")
    )

/**
 * Creates a [Message] given the String provided.
 */
//val String.message: Message
//    get() = Message(this)

//val Message.messagePayload: MessagePayload
//    get() = MessagePayload(StringBuilder(""))

fun <T : Document> Array<T>.batchExecute(exec: (document: T) -> Unit) {
    runBlocking {
        this@batchExecute.map { document ->
            async(Dispatchers.Default) {
                exec(document)
            }
        }.awaitAll()
    }
}