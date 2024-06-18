package net.prismclient.model

import net.prismclient.feature.api.API
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload

/**
 * Provides the basis for interacting with an LLM.
 *
 * @author Winter
 */
abstract class Model(val modelName: String, val modelVersion: String) {
    val apis: MutableList<API> = mutableListOf()

    abstract fun establishConnection()

    /**
     * Inserts any active metadata from the [API] into the prompt before sending to the LLM.
     */
    abstract fun sendMessage(message: MessagePayload): ResponsePayload

    /**
     * Sends the provided message to the LLM. Does not include any API or other prompts.
     */
    abstract fun sendRawMessage(prompt: MessagePayload): ResponsePayload

    /**
     * If there are any active APIs, a prompt will be generated based on their requirements. Empty string will be
     * returned if no APIs are currently active.
     */
    fun generateAPIPrompt(): String {
        ""


        return ""
    }
}