package net.prismclient.model

import net.prismclient.payload.ResponsePayload
import net.prismclient.prompt.Prompt
import java.time.Instant

class Message {
    val creationTimeStamp: Instant = Instant.now()
    var sentTimeStamp: Instant? = null

    val prompt: Prompt = Prompt()
    var responsePayload: ResponsePayload? = null
}