package net.prismclient.model

import net.prismclient.chat.Chat
import net.prismclient.execution.Action
import net.prismclient.flow.Flow
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload
import net.prismclient.prompt.Prompt
import net.prismclient.tools.InlineTool
import net.prismclient.tools.Tool
import net.prismclient.tools.ToolFunction

/**
 * Provides the basis for interacting with an LLM.
 *
 * @author Winter
 */
abstract class LLM(val modelName: String, val modelVersion: String) {
    val tools: MutableList<Tool> = mutableListOf()

    protected var inlineTools: InlineTool? = null

    abstract fun establishConnection()

    /**
     * Inserts any active metadata from the [Tool] into the prompt before sending to the LLM.
     */
    abstract fun sendMessage(payload: MessagePayload): ResponsePayload

    /**
     * If there are any active APIs, a prompt will be generated based on their requirements. Empty string will be
     * returned if no APIs are currently active.
     */
    open fun generateAPIPrompt(): String {
        throw RuntimeException("Generating API prompts is not supported for ${this::class.simpleName}")
    }

    open fun addInlineTool(tool: ToolFunction<*>) {
        inlineTools = inlineTools ?: InlineTool().apply { Tool(this) }
        inlineTools!!.functions.add(tool)
    }

    open fun removeInlineTool(tool: ToolFunction<*>) {
        inlineTools?.functions?.find { it == tool }?.let { inlineTools!!.functions.remove(it) }
    }

    /**
     * Forces the tool(s) to be used by the LLM
     */
    protected abstract fun forceTool(vararg tools: ToolFunction<*>)

    //// DSL /////
    inline fun Chat(logHistory: Boolean = true, useMessageHistory: Boolean = true, action: Chat.() -> Unit): Chat =
        Chat().apply {
            this.logHistory = logHistory
            this.useMessageHistory = useMessageHistory

            action(this)
        }

    inline fun Chat.Message(initialPrompt: Prompt? = null, action: Message.() -> Unit): Message = Message().also { message ->
        action(message)
        message.responsePayload = sendMessage(MessagePayload(chat = this, message = message))
        addMessage(message)
    }

    fun Chat.Message(prompt: String, initialPrompt: Prompt? = null): Message = Message(initialPrompt) { Include(prompt) }

    // Tools
    /**
     * Adds the provided Tools(s) to any calls to the LLM. The Tools will automatically be removed after the block
     * is completed.
     */
    inline fun Tool(vararg tool: Tool, action: (LLM.() -> Unit)) {
        Tool(tool = tool)
        action(this)
        tools -= tool
    }

    /**
     * Adds the provided Tools(s) to any calls to the LLM.
     */
    fun Tool(vararg tool: Tool) {
        tools.forEach {
            if (!tools.contains(it)) return
        }
        tools += tool
    }

    /**
     * Removes the provided Tools(s) to any calls to the LLM.
     */
    fun removeTool(vararg tool: Tool) {
        tools -= tool.toSet()
    }
    
    fun clearTools() {
        tools.clear()
    }

    /**
     * Forces the given [tools] to be called within the [lambda].
     */
    inline fun Force(vararg tools: ToolFunction<*>, lambda: () -> Unit) {
        Force(*tools)
        lambda()
        tools.forEach { it.forceCall = false }
    }

    /**
     * Forces the given [tools] to be called.
     */
    fun Force(vararg tools: ToolFunction<*>) {
        forceTool(*tools)
    }

    // NEW
    val flow: Flow? = null

    /**
     * INTERNAL USE ONLY.
     */
    inline fun <A : Action> action(lambda: () -> A): A = lambda().also { flow?.actions?.add(it) }

    inline fun Flow(lambda: Flow.() -> Unit): Flow = Flow().also(lambda)
}