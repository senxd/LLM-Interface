package net.prismclient.payload

import net.prismclient.chat.Chat
import net.prismclient.model.Message

/**
 * The message sent to the AI Model
 *
 * @author Winter
 */
data class MessagePayload(val chat: Chat, val message: Message) : Payload()