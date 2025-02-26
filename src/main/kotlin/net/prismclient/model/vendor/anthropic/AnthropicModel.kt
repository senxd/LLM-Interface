package net.prismclient.model.vendor.anthropic

import kotlinx.coroutines.runBlocking
import net.prismclient.model.Message
import net.prismclient.model.ModelVendor
import net.prismclient.model.OkHttpLLM
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload
import net.prismclient.tools.Tool
import net.prismclient.tools.ToolFunction
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CountDownLatch

class AnthropicModel(val model: Models, val apiKey: String) : OkHttpLLM(model.id, null, 1000, ModelVendor.Anthropic) {
    var maxTokens: Int = 1024
    var endpoint = "https://api.anthropic.com/v1"
    var anthropicVersion = "2023-06-01"
    var additionalParameters: JSONObject? = null

    override fun establishConnection() { /* ... */ }

    override fun sendMessage(payload: MessagePayload): ResponsePayload {
        TODO("Not implemented")
    }

    override fun forceTool(vararg tools: ToolFunction<*>) {
    }
    

    enum class Models(val id: String) {
        SONNET_3_7("claude-3-7-sonnet-20250219"),
        SONNET_3_5("claude-3-5-sonnet-20241022"),
        HAIKU_3_5("claude-3-5-haiku-20241022"),
        OPUS_3_0("claude-3-opus-20240229"),
        HAIKU_3_0("claude-3-haiku-20240307")
    }
}
