package net.prismclient.chat

import net.prismclient.model.Message

/**
 * [Chat] stores all requests, messages and responses between the "frontend" API and the LLM (think Chat-GPT web).
 *
 * @author Winter
 */
class Chat {
    /**
     * Locally logs the history of messages and other metadata.
     *
     * @see useMessageHistory
     */
    var logHistory: Boolean = true

    /**
     * Sends previous messages as part of new requests. Allows for the LLM to respond based on previous messages.
     * Limited by the context window.
     */
    var useMessageHistory: Boolean = true
        set(value) {
            if (!logHistory) {
                logHistory = true
                // Warn the user that logHistory was automatically enabled, and it should be enabled, etc...
            }
            field = value
        }

    val chatHistory: MutableList<Message> = mutableListOf()

    fun addMessage(message: Message) {
        if (logHistory) {
            chatHistory.add(message)
        }
    }
}