package net.prismclient.model

import net.prismclient.tools.Tool
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload

/**
 * Provides the basis for interacting with an LLM.
 *
 * @author Winter
 */
abstract class LLM(val modelName: String, val modelVersion: String) {
    val tools: MutableList<Tool> = mutableListOf()

    abstract fun establishConnection()

    /**
     * Inserts any active metadata from the [Tool] into the prompt before sending to the LLM.
     */
    abstract fun sendMessage(payload: MessagePayload): ResponsePayload

    /**
     * Sends the provided message to the LLM. Does not include any API or other prompts.
     */
    abstract fun sendRawMessage(payload: MessagePayload): ResponsePayload

    /**
     * If there are any active APIs, a prompt will be generated based on their requirements. Empty string will be
     * returned if no APIs are currently active.
     */
    open fun generateAPIPrompt(): String {
        throw RuntimeException("Generating API prompts is not supported for ${this::class.simpleName}")
    }
}