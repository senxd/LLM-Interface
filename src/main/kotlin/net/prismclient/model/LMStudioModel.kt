package net.prismclient.model

import kotlinx.coroutines.runBlocking
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload
import net.prismclient.tools.ToolFunction
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CountDownLatch

/**
 * Allows for the use of LM Studio models hosted locally using the Local Server feature.
 *
 * Tool calling & Resending requests are not supported.
 *
 * @author Winter
 */
class LMStudioModel(val endpoint: String, model: String, var apiKey: String): OkHttpLLM(model, "v1") {
    override fun establishConnection() {
        val request = Request.Builder()
            .url("${endpoint}v1/models")
            .build()

        call(request) { callback ->
            if (callback.isSuccessful)
                println("Success establishing connection")
        }
    }

    override fun sendMessage(payload: MessagePayload): ResponsePayload {
        val messageHistory = JSONArray()

        if (payload.chat.useMessageHistory) {
            payload.chat.chatHistory.forEach { message: Message ->
                messageHistory.put(messageObject("user", message.prompt.rawPrompt.toString()))
                messageHistory.put(messageObject("assistant", message.response))
            }
        }

        messageHistory.put(messageObject("user", payload.message.prompt.rawPrompt.toString()))

        val json = JSONObject().apply {
            put("model", modelName)
            put("messages", messageHistory)
            put("stream", false)
        }

        val request = Request.Builder()
            .url("${endpoint}v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())).build()

        val result = StringBuilder()
        val latch = CountDownLatch(1)

        call(request) { response ->
            response.use {
                result.append(
                    JSONObject(it.body!!.string())
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                )
            }

            latch.countDown()
        }

        latch.await()

        return ResponsePayload(result.toString())
    }

    override fun forceTool(vararg tools: ToolFunction<*>) {
        throw RuntimeException("Tool calling is not supported for LM Studio")
    }
}