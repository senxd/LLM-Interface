// IMPROVE: Documentation
package net.prismclient.dsl

import net.prismclient.chat.Chat
import net.prismclient.feature.api.API
import net.prismclient.feature.api.APIFunction
import net.prismclient.feature.api.InlineAPI
import net.prismclient.model.LLM
import net.prismclient.model.Message
import net.prismclient.payload.MessagePayload
import net.prismclient.prompt.Prompt
import kotlin.properties.Delegates

object ModelDSL {
    private val regex = Regex("""\d+\.\s""")

    var activeModel: LLM by Delegates.notNull()
    var activeChat: Chat by Delegates.notNull()

    val Message.response: String
        get() = this.responsePayload?.response ?: throw NullPointerException("Response is Null")

    inline fun Chat(logHistory: Boolean = true, useMessageHistory: Boolean = true, action: Chat.() -> Unit) {
        activeChat = Chat()
        activeChat.logHistory = logHistory
        activeChat.useMessageHistory = useMessageHistory

        action(activeChat)
    }

    inline fun Message(initialPrompt: Prompt? = null, action: MessageDSL.() -> Unit): Message {
        val message = Message()

        MessageDSL.message = message

        action(MessageDSL)

        message.responsePayload = activeModel.sendMessage(MessagePayload(activeChat, message))

        activeChat.addMessage(message)

        return message
    }

    fun Message(prompt: String, initialPrompt: Prompt? = null): Message = Message(initialPrompt) { Include(prompt) }

    // API
    /**
     * Adds the provided API(s) to any calls to the LLM. The APIs will automatically be removed after the block
     * is completed.
     */
    inline fun API(vararg api: API, action: (ModelDSL.() -> Unit)) {
        activeModel.apis.forEach { if (!activeModel.apis.contains(it)) activeModel.apis += it }
        ModelDSL.action()
        activeModel.apis -= api
    }

    /**
     * Adds the provided API(s) to any calls to the LLM.
     */
    fun API(vararg api: API) {
        API(api = api) {}
    }

    /**
     * Removes the provided API(s) to any calls to the LLM.
     */
    fun removeApi(vararg api: API) {
        activeModel.apis -= api.toSet()
    }

    /**
     * Removes all active APIs.
     */
    fun clearApis() {
        activeModel.apis.clear()
    }

    /**
     * Creates an [APIFunction] which returns [R] and has no parameters.
     *
     * @param functionName The name of the API function.
     * @param functionDescription A brief description of what the function does.
     * @param responseName The name of the response, default is "response".
     * @param response The lambda to execute for the response.
     * @return An instance of [APIFunction] that wraps the provided lambda.
     */
    inline fun <R> Function(
        functionName: String,
        functionDescription: String,
        responseName: String = "response",
        crossinline response: () -> R
    ): APIFunction<R> = APIFunction(
        functionName, functionDescription, mutableListOf(), responseName
    ) {
        response()
    }.apply {
        API(InlineAPI)
        InlineAPI.apiFunctions.add(this)
    }
}