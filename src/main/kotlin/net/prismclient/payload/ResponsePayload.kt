package net.prismclient.payload

import org.json.JSONObject

open class ResponsePayload(val response: String, val payload: JSONObject? = null/* val latent: Boolean,  val callback: (String) -> Unit */) : Payload()