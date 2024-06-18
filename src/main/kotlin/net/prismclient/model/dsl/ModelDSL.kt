// IMPROVE: Documentation
package net.prismclient.model.dsl

import net.prismclient.MessageDSL
import net.prismclient.chat.Chat
import net.prismclient.feature.api.API
import net.prismclient.model.LLM
import net.prismclient.model.Message
import net.prismclient.payload.MessagePayload
import kotlin.properties.Delegates

object ModelDSL {
    private val regex = Regex("""\d+\.\s""")

    var activeModel: LLM by Delegates.notNull()
    var activeChat: Chat by Delegates.notNull()
    var activeAPIs: MutableList<API> = mutableListOf()

    val Message.response: String
        get() = this.responsePayload?.response ?: throw NullPointerException("Response is Null")

    inline fun Chat(logHistory: Boolean = true, useMessageHistory: Boolean = true, action: Chat.() -> Unit) {
        activeChat = Chat()
        activeChat.logHistory = logHistory
        activeChat.useMessageHistory = useMessageHistory

        action(activeChat)
    }

    inline fun Message(action: MessageDSL.() -> Unit): Message {
        val message = Message()

        MessageDSL.message = message

        action(MessageDSL)

        message.responsePayload = activeModel.sendMessage(MessagePayload(activeChat, message))

        activeChat.addMessage(message)

        return message
    }

    // API
    /**
     * Adds the provided API(s) to any calls to the LLM. The APIs will automatically be removed after the block
     * is completed.
     */
    inline fun API(vararg api: API, action: (ModelDSL.() -> Unit)) {
        activeAPIs += api
        ModelDSL.action()
        // Remove the applied APIs from the active API list.
        activeAPIs -= api
    }

    /**
     * Adds the provided API(s) to any calls to the LLM.
     */
    fun API(vararg api: API) {
        activeAPIs += api
        activeModel.apis += activeAPIs
    }

    /**
     * Removes the provided API(s) to any calls to the LLM.
     */
    fun removeApi(vararg api: API) {
        activeAPIs -= api
    }

    /**
     * Removes all active APIs.
     */
    fun clearApis() {
        activeAPIs.clear()
    }


//    inline fun Prompt(message: String, action: ModelDSL.(response: String) -> Unit): String =
//        Prompt(message.message, action)
//
//    inline fun Prompt(message: Message, action: ModelDSL.(response: String) -> Unit): String {
//        val response = activeModel.sendMessage(message.messagePayload)
//
//        this.action(response.response)
//
//        return response.response
//    }

//    fun Prompt(message: String): String = Prompt(message.message) { /* ... */ }

//    fun sendRawMessage(message: Message) {
//        activeModel.sendRawMessage(message.messagePayload)
//    }
}