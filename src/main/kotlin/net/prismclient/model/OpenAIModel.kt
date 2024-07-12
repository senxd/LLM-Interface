package net.prismclient.model

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload
import net.prismclient.tools.Tool
import net.prismclient.tools.ToolFunction
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

/**
 * The [LLM] class for using Open AI (gpt series only) models.
 *
 * The [Tool] system fully supported with multiple calls being possible in a single request, however, if a tool is
 * [ToolFunction.forceCall], only that tool will be called, and only once. This is a limitation of the API and
 * cannot be "fixed."
 *
 * @author Winter
 */
open class OpenAIModel(
    model: String,
    val apiKey: String,
    readTimeout: Int = 30
) : OkHttpLLM(model, model.replace("gpt-", ""), readTimeout) {
    open var endpoint = "https://api.openai.com/v1"

    /**
     * If the package fails to send to OpenAI's servers due to a 429 error (rate limit), it will automatically resend
     * after the delay has passed. Set to -1 to disable resending.
     */
    open var rateLimitDelay: Long = 5000L

    /**
     * Maximum attempts to resend the message to the server.
     */
    open var maxResendingAttempts = 3

    /**
     * Adds a prompt to the message before sending it to the model. Not required for Open AI models,
     * however it can be useful for providing additional context.
     */
    open var useToolInjectionPrompt = true

    /**
     * Specifies the location where the tool injection prompt should be placed.
     */
    open var toolInjectionPromptLocation = InjectionPromptLocation.BEFORE

    override fun establishConnection() { /* ... */ }

    override fun sendMessage(payload: MessagePayload): ResponsePayload {
        val messageHistory = JSONArray()
        val toolDescription = StringBuilder()
        var toolChoice: JSONObject? = null

        // Generate the tool request based on the OpenAPI spec
        val activeTools: JSONArray? = if (tools.isEmpty() || tools.all { it.functions.isEmpty() }) null else JSONArray().apply {
            tools.forEach { tool ->
                if (tool.injectionPrompt.isNotBlank() && useToolInjectionPrompt) toolDescription.append("${tool.name}: ${tool.injectionPrompt}")
                tool.functions.filter { !it.disabled }.forEach { function ->
                    put(JSONObject().apply {
                        put("type", "function")
                        obj("function") {
                            put("name", function.name)
                            put("description", function.description)
                            obj("parameters") {
                                put("type", "object")
                                put("required", JSONArray(function.parameters.map { it.name }))
                                obj("properties") {
                                    function.parameters.forEach { parameter ->
                                        obj(parameter.name) {
                                            put("type", "string")
                                            put("description", parameter.description)
                                        }
                                    }
                                }
                            }
                        }
                    })

                    // If the function wants to be required to be make toolChoice non-null
                    // based on the function and check if there is already a forced function
                    // as it seems that OpenAI prohibits more than one forced function.
                    if (function.forceCall) {
                        if (toolChoice != null) {
                            println(
                                "Multiple forced functions being used, preferring ${function.name} over ${
                                    toolChoice!!.getJSONObject("function").getString("name")
                                }"
                            )
                        } else {
                            toolChoice = JSONObject().apply {
                                put("type", "function")
                                put("function", JSONObject().apply {
                                    put("name", function.name)
                                })
                            }
                        }
                    }
                }
            }
        }

        if (payload.chat?.useMessageHistory == true) {
            payload.chat.chatHistory.forEach { message: Message ->
                messageHistory.put(messageObject("user", message.prompt.toString()))
                messageHistory.put(messageObject("assistant", message.response))
            }
        }

        val prompt: StringBuilder = if (toolDescription.isNotBlank() && useToolInjectionPrompt) {
            toolDescription.insert(0, "The following Tools are available for use:\n")
            if (toolInjectionPromptLocation == InjectionPromptLocation.BEFORE) {
                toolDescription.append(payload.message.prompt)
            } else {
                payload.message.prompt.append(toolDescription)
            }
        } else payload.message.prompt

        messageHistory.put(messageObject("user", prompt.toString()))

        return sendMessage(messageHistory, activeTools, toolChoice)
    }

    private fun sendMessage(
        messageHistory: JSONArray, tools: JSONArray? = null, toolChoice: JSONObject? = null, attempt: Int = 0
    ): ResponsePayload {
        val json = JSONObject().apply {
            put("model", modelName)
            put("messages", messageHistory)
            if (tools != null) {
                put("tool_choice", toolChoice)
                put("tools", tools)
            }
        }

        val request = Request.Builder()
            .url("$endpoint/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())).build()

        val result = StringBuilder()
        val latch = CountDownLatch(1)

        call(request) { response ->
            response.use {
                if (!it.isSuccessful) {
                    latch.countDown()

                    val errorCode = response.code

                    when (errorCode) {
                        401 -> throw RuntimeException("Invalid OpenAI authentication key.")
                        429 -> {
                            if (rateLimitDelay != -1L && attempt + 1 > maxResendingAttempts) {
                                runBlocking {
                                    delay(rateLimitDelay)

                                    sendMessage(messageHistory, tools, toolChoice, attempt + 1)
                                }
                            }
                        }
                    }
                    throw RuntimeException(
                        "Failed to send message. Error Code: $errorCode, Attempts: $attempt\n${
                            json.toString(
                                1
                            )
                        }, Error: ${response.body?.string()}"
                    )
                }

                val responseBody = it.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "")
                val choices = jsonResponse.getJSONArray("choices")
                val responseMessage = choices.getJSONObject(0).getJSONObject("message")
                val messageContent = responseMessage.optString("content")

                // Append the result of the response. If it is a function call,
                // append the result of the resulting call from ChatGPT after
                // return the value of the function it wants.
                result.append(responseMessage.optJSONArray("tool_calls")?.let {
                    handleToolCalls(responseMessage, messageHistory, tools, toolChoice)
                } ?: messageContent)
                latch.countDown()
            }
        }

        latch.await()

        return ResponsePayload(result.toString())
    }

    /**
     * Interprets Chat-GPT's response when a function is desired that has been previously specified.
     *
     * @see [Tool]
     */
    private fun handleToolCalls(response: JSONObject, messageHistory: JSONArray, tools: JSONArray?, toolChoice: JSONObject? = null): String {
        val toolCalls = response.getJSONArray("tool_calls")

        // Add the tool call to the message history
        messageHistory.put(JSONObject().apply {
            put("role", "assistant")
            put("content", "")
            put("tool_calls", toolCalls)
        })

        for (i in 0 until toolCalls.length()) {
            val function = toolCalls.getJSONObject(i).getJSONObject("function")
            val functionName = function.getString("name")
            val arguments = JSONObject(function.getString("arguments"))

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
                put("role", "tool")
                put("content", functionResponse.toString())
                put("name", functionName)
                put("tool_call_id", toolCalls.getJSONObject(i).getString("id"))
            })
        }
        return sendMessage(messageHistory, tools, toolChoice).response
    }

    override fun forceTool(vararg tools: ToolFunction<*>) {
        // Open AI limits forced tool calling to a single
        // tool with a singed request. Therefore, we need
        // to ensure all the other functions are not currently
        // forced. It is still possible to have multiple tools
        // being forced and in this case, the last ToolFunction
        // of a Tool and last Tool within tools will be preferred.
        this.tools.flatMap { it.functions }.filter { it.forceCall }.forEach { it.forceCall = false }

        tools[0].forceCall = true
    }

    override fun handleCallException(exception: Exception, request: Request, callback: (response: Response) -> Unit) {
        super.handleCallException(exception, request, callback)
        // Obviously if it doesn't work once, try again!
        call(request, callback)
    }

    enum class InjectionPromptLocation {
       /**
        * Before the message prompt.
        */
        BEFORE,

        /**
         * After the message prompt.
         */
        AFTER
    }
}