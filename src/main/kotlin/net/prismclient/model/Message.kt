package net.prismclient.model

import net.prismclient.chat.Chat
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload
import net.prismclient.util.Copyable
import net.prismclient.util.localResource
import java.io.File
import java.time.Instant

class Message : Copyable<Message> {
    val creationTimeStamp = Instant.now()
    var sentTimeStamp: Instant? = null

    val prompt = StringBuilder()
    var messageResponse: ResponsePayload? = null

    /**
     * If the message has been sent to an LLM model.
     */
    val sent: Boolean get() = sentTimeStamp != null

    val response: String
        get() = this.messageResponse?.response ?: throw NullPointerException("Response is Null")

    fun send(model: LLM, chat: Chat?) {
        sentTimeStamp = Instant.now()
        messageResponse = model.sendMessage(MessagePayload(chat = chat, message = this))
    }

    //// DSL /////
    fun Include(text: String) {
        prompt.append(text)
    }

    fun LocalPrompt(name: String) = Include(File(localPromptFolder, "$name.txt").readText())

    override fun copy(): Message = Message().also { it.prompt.append(prompt) }

    /**
     * An alternative method to invoke [Include].
     */
    operator fun String.unaryPlus() = Include(this)

    companion object {
        var localPromptFolder = "prompts".localResource
    }
}