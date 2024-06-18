package net.prismclient.payload

import net.prismclient.model.Message

/**
 * The message sent to the AI Model
 *
 * @author Winter
 * @param latent If the output is not needed immediately, the thread will not be held
 * @param callback Will be invoked when the message Payload is completed.
 */
class MessagePayload(val message: StringBuilder, /* val latent: Boolean, val callback: (String) -> Unit */) : Payload()