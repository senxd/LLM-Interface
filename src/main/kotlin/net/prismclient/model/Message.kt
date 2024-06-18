package net.prismclient.model

import net.prismclient.payload.ResponsePayload
import net.prismclient.prompt.Prompt

class Message {
    val creationTimeStamp: String = ""
    lateinit var sentTimeStamp: String

    val prompt: Prompt = Prompt()
    var responsePayload: ResponsePayload? = null
}