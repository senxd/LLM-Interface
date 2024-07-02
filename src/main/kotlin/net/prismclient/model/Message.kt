package net.prismclient.model

import net.prismclient.payload.ResponsePayload
import net.prismclient.prompt.Prompt
import net.prismclient.util.localResource
import java.io.File
import java.time.Instant

class Message {
    val creationTimeStamp: Instant = Instant.now()
    var sentTimeStamp: Instant? = null

    val prompt: Prompt = Prompt()
    var responsePayload: ResponsePayload? = null

    val response: String
        get() = this.responsePayload?.response ?: throw NullPointerException("Response is Null")

    //// DSL /////
    fun Include(text: String) {
        prompt.rawPrompt.append(text)
    }

    fun LocalPrompt(name: String) = Include(File(localPromptFolder, "$name.txt").readText())

    /**
     * An alternative method to invoke [Include].
     */
    operator fun String.unaryPlus() = Include(this)

    companion object {
        var localPromptFolder = "prompts".localResource
    }
}