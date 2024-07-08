package net.prismclient.model

import net.prismclient.tools.ToolFunction
import okhttp3.*

/**
 * Allows for the use of LM Studio models hosted locally using the Local Server feature.
 *
 * Tool calling & Resending requests are not supported.
 *
 * @author Winter
 */
class LMStudioModel(apiEndpoint: String, model: String, apiKey: String): OpenAIModel(model, apiKey) {
    var toolCalling: (tools: List<ToolFunction<*>>) -> Unit = { throw RuntimeException("Tool calling is not supported for LM Studio") }

    override var endpoint: String = apiEndpoint

    override fun establishConnection() {
        val request = Request.Builder()
            .url("${endpoint}v1/models")
            .build()

        call(request) { callback ->
            if (callback.isSuccessful)
                println("Success establishing connection")
        }
    }

    override fun forceTool(vararg tools: ToolFunction<*>) {
        toolCalling(tools.toList())
    }
}