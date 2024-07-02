//// IMPROVE: Documentation
//package net.prismclient.dsl
//
//import net.prismclient.chat.Chat
//import net.prismclient.execution.Action
//import net.prismclient.flow.Flow
//import net.prismclient.tools.Tool
//import net.prismclient.tools.ToolFunction
//import net.prismclient.tools.InlineTool
//import net.prismclient.model.LLM
//import net.prismclient.model.Message
//import net.prismclient.payload.MessagePayload
//import net.prismclient.prompt.Prompt
//import kotlin.properties.Delegates
//
//abstract class ModelDSL {
//    private val regex = Regex("""\d+\.\s""")
//
//    var activeModel: LLM by Delegates.notNull()
//    var activeChat: Chat by Delegates.notNull()
//
//    val Message.response: String
//        get() = this.responsePayload?.response ?: throw NullPointerException("Response is Null")
//
//    inline fun Chat(logHistory: Boolean = true, useMessageHistory: Boolean = true, action: Chat.() -> Unit) {
//        activeChat = Chat()
//        activeChat.logHistory = logHistory
//        activeChat.useMessageHistory = useMessageHistory
//
//        action(activeChat)
//    }
//
//    inline fun Message(initialPrompt: Prompt? = null, action: MessageDSL.() -> Unit): Message {
//        val message = Message()
//
//        MessageDSL.message = message
//
//        action(MessageDSL)
//
//        message.responsePayload = activeModel.sendMessage(MessagePayload(activeChat, message))
//
//        activeChat.addMessage(message)
//
//        return message
//    }
//
//    fun Message(prompt: String, initialPrompt: Prompt? = null): Message = Message(initialPrompt) { Include(prompt) }
//
//    // Tools
//    /**
//     * Adds the provided Tools(s) to any calls to the LLM. The Tools will automatically be removed after the block
//     * is completed.
//     */
//    inline fun Tool(vararg tool: Tool, action: (Model.() -> Unit)) {
//        Tool(tool = tool)
//        ModelDSL.action()
//        activeModel.tools -= tool
//    }
//
//    /**
//     * Adds the provided Tools(s) to any calls to the LLM.
//     */
//    fun Tool(vararg tool: Tool) {
//        activeModel.tools.forEach {
//            if (!activeModel.tools.contains(it))
//                return
//        }
//        activeModel.tools += tool
//    }
//
//    /**
//     * Removes the provided Tools(s) to any calls to the LLM.
//     */
//    fun removeTool(vararg tool: Tool) {
//        activeModel.tools -= tool.toSet()
//    }
//
//    /**
//     * Removes all active Tools.
//     */
//    fun clearTools() {
//        activeModel.tools.clear()
//    }
//
//    /**
//     * Creates an [ToolFunction] which returns [R] and has no parameters.
//     *
//     * @param functionName The name of the Tool function.
//     * @param functionDescription A brief description of what the function does.
//     * @param responseName The name of the response, default is "response".
//     * @param response The lambda to execute for the response.
//     * @return An instance of [ToolFunction] that wraps the provided lambda.
//     */
//    inline fun <R> Function(
//        functionName: String,
//        functionDescription: String,
//        responseName: String = "response",
//        crossinline response: () -> R
//    ): ToolFunction<R> = ToolFunction(
//        functionName, functionDescription, mutableListOf(), responseName
//    ) {
//        response()
//    }.apply {
//        Tool(InlineTool)
//        InlineTool.toolFunctions.add(this)
//    }
//
//    // NEW
//    val flow: Flow? = null
//
//    /**
//     * INTERNAL USE ONLY.
//     */
//    inline fun <A : Action> action(lambda: () -> A): A = lambda().also { flow?.actions?.add(it) }
//
//    inline fun Flow(lambda: Flow.() -> Unit): Flow =
//        Flow().also(lambda)
//
////    inline fun Prompt(lambda: Prompt.() -> Unit): Prompt = action { Prompt().also(lambda) }
//}