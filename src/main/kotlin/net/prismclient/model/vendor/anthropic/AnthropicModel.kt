package net.prismclient.model.vendor.anthropic

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
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

class AnthropicModel(val model: Models, val apiKey: String) : OkHttpLLM(model.id, null, 1000, ModelVendor.Anthropic) {
    var maxTokens: Int = 1024
    var endpoint = "https://api.anthropic.com/v1"
    var anthropicVersion = "2023-06-01"
    var additionalParameters: JSONObject? = null
    var enableExtendedThinking: Boolean = false
    var extendedThinkingBudget: Int = 1024  // Default budget tokens for extended thinking
    var streamingCallback: ((String) -> Unit)? = null
    var useExtendedThinking: Boolean = false

    val effectiveMaxTokens: Int get() = maxTokens + (if (enableExtendedThinking) extendedThinkingBudget else 0)

    override fun establishConnection() { /* ... */
    }

    override fun sendMessage(payload: MessagePayload): ResponsePayload {
        val messageHistory = JSONArray()

        // TODO: Prompt Injection

        if (payload.chat?.useMessageHistory == true) {
            payload.chat.chatHistory.forEach { message: Message ->
                messageHistory.put(messageObject("user", message.prompt.toString()))
                messageHistory.put(messageObject("assistant", message.response))
            }
        }

        messageHistory.put(messageObject("user", payload.message.prompt.toString()))

        return sendMessageImpl(messageHistory)
    }

    private fun sendMessageImpl(messageHistory: JSONArray): ResponsePayload {
        val latch = CountDownLatch(1)
        val messageContent = StringBuilder()
        val thinkingContent = StringBuilder()
        var responsePayload: ResponsePayload? = null

        val requestBody = buildRequestBody(messageHistory)

        println("Request: ${requestBody.toString(4)}")

        val request = Request.Builder()
            .url("$endpoint/messages")
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", anthropicVersion)
            .addHeader("content-type", "application/json")
            .build()

        call(request) { response ->
            response.use { resp ->
                if (!resp.isSuccessful) {
                    val errorCode = resp.code
                    val errorBody = resp.body?.string() ?: "Unknown error"

                    println("Error: $errorCode, $errorBody")
                    responsePayload = ResponsePayload("Error: $errorCode, $errorBody")
                    latch.countDown()
                    throw RuntimeException("Failed to send message. Error Code: $errorCode, Error: $errorBody")
                }

                val reader = resp.body?.byteStream()?.let {
                    BufferedReader(InputStreamReader(it))
                }

                if (reader == null) {
                    responsePayload = ResponsePayload("Error: No response body")
                    latch.countDown()
                    return@use
                }

                var line: String? = null
                var toolUse: Boolean = false
                var toolName: String? = null
                var toolId: String? = null
                val rawToolJson = StringBuilder()

                while (reader.readLine()?.also { line = it } != null) {
                    if (line.isNullOrEmpty()) continue

                    if (line!!.startsWith("data: ")) {
                        val jsonData = line!!.substring(6)

                        if (jsonData == "[DONE]") {
                            continue
                        }

                        try {
                            val eventJson = JSONObject(jsonData)
                            val type = eventJson.getString("type")

//                            println(eventJson)

                            when (type) {
                                "message_start" -> {}
                                "content_block_start" -> {
                                    val contentBlock = eventJson.getJSONObject("content_block")
                                    val contentType = contentBlock.getString("type")

                                    when (contentType) {
                                        "thinking" -> {
                                            thinkingContent.append("<think>")
                                        }

                                        "tool_use" -> {
                                            toolUse = true
                                            toolName = contentBlock.getString("name")
                                            toolId = contentBlock.getString("id")
                                        }
                                    }
                                }

                                "content_block_delta" -> {
                                    val delta = eventJson.optJSONObject("delta")

                                    // Standard text response
                                    delta.optString("text")?.let { text ->
                                        messageContent.append(text)
                                        streamingCallback?.invoke(text)
                                    }

                                    // Tool Response
                                    if (toolUse && delta.optString("partial_json") != null) {
                                        rawToolJson.append(delta.optString("partial_json"))
                                    }
                                }

                                "content_block_stop" -> {
                                    val contentType = eventJson.optJSONObject("content_block")?.optString("type", "")

                                    if (toolUse) {
                                        toolUse = false

                                        val toolArguments = JSONObject(rawToolJson.toString())

                                        messageHistory.put(JSONObject().apply {
                                            put("role", "assistant")
                                            put("content", JSONArray().put(JSONObject().apply {
                                                put("type", "tool_use")
                                                put("id", toolId)
                                                put("name", toolName)
                                                put("input", toolArguments)
                                            }))
                                        })

                                        // Anthropic names Functions as Tools.
                                        responsePayload = handleToolResponse(toolName!!, toolId!!, toolArguments, messageHistory)
                                        return@call
                                    }

                                    if (contentType == "thinking") {
                                        thinkingContent.append("</think>")
                                    }
                                }

                                "message_delta" -> {}
                                "message_stop" -> {
                                    println("Content: $messageContent")
                                    latch.countDown()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                // Create response payload with final message content
                responsePayload = responsePayload ?: ResponsePayload(messageContent.toString())
                if (!latch.count.equals(0)) {
                    latch.countDown()
                }
            }
        }

        // Wait for completion
        latch.await()
        return responsePayload ?: ResponsePayload("Error: Failed to get response")
    }

    private fun handleToolResponse(
        functionName: String,
        toolId: String,
        arguments: JSONObject,
        messageHistory: JSONArray
    ): ResponsePayload {
        var tool: Tool by Delegates.notNull()
        val mappedFunction =
            this.tools.firstNotNullOfOrNull { it.functions.find { f -> tool = it; f.name == functionName } }
                ?: throw NullPointerException("Function $functionName not found within Tools")

        val mappedParameters = mappedFunction.parameters.map {
            it.copy().apply {
                // IMPROVE: Better Casting method instead of only allowing Strings
                castTo(arguments.getString(name))
            }
        }.toTypedArray().toMutableList()

        val functionResponse = mappedFunction.response(mappedParameters)

        messageHistory.put(JSONObject().apply {
            put("role", "user")
            put("content", JSONArray().put(JSONObject().apply {
                put("type", "tool_result")
                put("tool_use_id", toolId)
                put("content", functionResponse)
            }))
        })

        return sendMessageImpl(messageHistory)
    }

    private fun buildRequestBody(messageHistory: JSONArray?): JSONObject = JSONObject().apply {
        put("model", model.id)
        put("messages", messageHistory)
        put("max_tokens", maxTokens)
        put("stream", true)
        val toolsList = buildToolsList()
        if (toolsList.length() > 0) {
            put("tools", toolsList)
        }
        if (useExtendedThinking) {
            obj("system") {
                put("extended_thinking", JSONObject().apply {
                    put("enabled", true)
                    put("max_tokens", effectiveMaxTokens)
                })
            }
        }
        additionalParameters?.let {
            for (key in it.keys()) {
                put(key, it[key])
            }
        }
    }

    fun buildToolsList(): JSONArray {
        val toolsList = JSONArray()

        tools.forEach { tool ->
            tool.functions.filter { !it.disabled }.forEach { function ->
                toolsList.put(JSONObject().apply {
                    put("name", function.name)
                    put("description", function.description)
                    put("input_schema", JSONObject().apply {
                        put("type", "object")
                        put("properties", JSONObject().apply {
                            function.parameters.forEach { parameter ->
                                put(parameter.name, JSONObject().apply {
                                    put("type", "string")
                                    put("description", parameter.description)
                                })
                            }
                        })
                        put("required", JSONArray(function.parameters.map { it.name }))
                    })
                })
            }
        }

        return toolsList
    }

    override fun forceTool(vararg tools: ToolFunction<*>) {
        // Set force call flag on specified tools
        tools.forEach { tool ->
            tool.forceCall = true
        }
    }

    enum class Models(val id: String) {
        SONNET_3_7("claude-3-7-sonnet-20250219"),
        SONNET_3_5("claude-3-5-sonnet-20241022"),
        HAIKU_3_5("claude-3-5-haiku-20241022"),
        OPUS_3_0("claude-3-opus-20240229"),
        HAIKU_3_0("claude-3-haiku-20240307")
    }
}
