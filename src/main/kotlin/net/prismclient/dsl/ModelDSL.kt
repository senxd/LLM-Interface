// IMPROVE: Documentation
package net.prismclient.dsl

import net.prismclient.chat.Chat
import net.prismclient.tools.Tool
import net.prismclient.tools.ToolFunction
import net.prismclient.tools.InlineTool
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
    inline fun API(vararg tool: Tool, action: (ModelDSL.() -> Unit)) {
        activeModel.tools.forEach { if (!activeModel.tools.contains(it)) activeModel.tools += it }
        ModelDSL.action()
        activeModel.tools -= tool
    }

    /**
     * Adds the provided API(s) to any calls to the LLM.
     */
    fun API(vararg tool: Tool) {
        API(tool = tool) {}
    }

    /**
     * Removes the provided API(s) to any calls to the LLM.
     */
    fun removeApi(vararg tool: Tool) {
        activeModel.tools -= tool.toSet()
    }

    /**
     * Removes all active APIs.
     */
    fun clearApis() {
        activeModel.tools.clear()
    }

    /**
     * Creates an [ToolFunction] which returns [R] and has no parameters.
     *
     * @param functionName The name of the API function.
     * @param functionDescription A brief description of what the function does.
     * @param responseName The name of the response, default is "response".
     * @param response The lambda to execute for the response.
     * @return An instance of [ToolFunction] that wraps the provided lambda.
     */
    inline fun <R> Function(
        functionName: String,
        functionDescription: String,
        responseName: String = "response",
        crossinline response: () -> R
    ): ToolFunction<R> = ToolFunction(
        functionName, functionDescription, mutableListOf(), responseName
    ) {
        response()
    }.apply {
        API(InlineTool)
        InlineTool.toolFunctions.add(this)
    }
}