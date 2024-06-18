package net.prismclient.model

import net.prismclient.feature.api.API
import net.prismclient.model.dsl.ModelDSL.response
import net.prismclient.payload.MessagePayload
import net.prismclient.payload.ResponsePayload
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CountDownLatch
import kotlin.properties.Delegates

class OpenAIModel(model: String, val apiKey: String) :
    LLM(model, model.replace("gpt-", "")) {
    private val client = OkHttpClient()

    override fun establishConnection() {
        // ...
    }

    // TODO: Move somewhere else
    inline fun JSONObject.obj(name: String, lambda: JSONObject.() -> Unit) {
        put(name, JSONObject().apply(lambda))
    }

    override fun sendMessage(payload: MessagePayload): ResponsePayload {
        // Generate the tool request based on the OpenAPI spec
        val tools = JSONArray().apply {
            apis.forEach { tool ->
                tool.apiFunctions.forEach { function ->
                    put(JSONObject().apply {
                        put("name", function.functionName)
                        put("description", function.functionDescription)
                        obj("parameters") {
                            put("type", "object")
                            put("required", JSONArray(function.functionParameters.map { it.parameterName }))
                            obj("properties") {
                                function.functionParameters.forEach { parameter ->
                                    obj(parameter.parameterName) {
                                        put("type", "string")
                                        put("description", parameter.parameterDescription)
                                    }
                                }
                            }
                        }
                    })
                }
            }
        }

        val messageHistory = JSONArray()

        if (payload.chat.useMessageHistory) {
            payload.chat.chatHistory.forEach { message: Message ->
                messageHistory.put(messageObject("user", message.prompt.rawPrompt.toString()))
                messageHistory.put(messageObject("assistant", message.response))
            }
        }

        messageHistory.put(messageObject("user", payload.message.prompt.rawPrompt.toString()))

        return sendMessage(messageHistory, if (apis.isNotEmpty()) tools else null)
    }

    private fun messageObject(role: String, content: String) = JSONObject().apply {
        put("role", role)
        put("content", content)
    }

    private fun sendMessage(messageHistory: JSONArray, tools: JSONArray? = null): ResponsePayload {
        val json = JSONObject().apply {
            put("model", modelName)
            put("messages", messageHistory)
            if (tools != null) put("functions", tools)
        }

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(json.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        val result = StringBuilder()
        val latch = CountDownLatch(1)

        call(request) { response ->
            response.use {
                if (!it.isSuccessful) {
                    latch.countDown()
                    throw RuntimeException("Failed to send request: $it")
                }

                val responseBody = it.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "")
                val choices = jsonResponse.getJSONArray("choices")
                val responseMessage = choices.getJSONObject(0).getJSONObject("message")
                val messageContent = responseMessage.optString("content")
                // OpenAI calls it "tools," but the JSON response is "function"
                val functionCall = responseMessage.optJSONObject("function_call")

                // Append the result of the response. If it is a function call,
                // append the result of the resulting call from ChatGPT after
                // return the value of the function it wants.
                functionCall?.let {
                    // TODO: Allow for multiple functions to be called in a single request
                    result.append(handleAPICalls(responseMessage, messageHistory))
                } ?: result.append(messageContent)
                latch.countDown()
            }
        }

        latch.await()

        return ResponsePayload(result.toString())
    }

    /**
     * Interprets Chat-GPT's response when a function is desired that has been previously specified.
     *
     * @see [API]
     */
    private fun handleAPICalls(response: JSONObject, messageHistory: JSONArray): String {
        val functionCall = response.optJSONObject("function_call")
        val functionName = functionCall.getString("name")
        // Chat-GPT prefers to write the arguments as a String
        // instead of as an actual JSON Structure, so it must
        // be parsed as a String and converted into a JSON Object
        val functionArguments = JSONObject(functionCall.getString("arguments"))

        messageHistory.put(response)

        // Find the corresponding function based on the functionName.
        // This should be changed in the future to also identify the
        // exact API used due to potential naming conflicts. Alternatively,
        // I presume it automatically picks the first defined API, so Priority
        // will take place, but a warning should be mentioned in an additional
        // check when adding APIs to mention naming conflicts.
        var api: API by Delegates.notNull()
        val mappedFunction =
            apis.firstNotNullOfOrNull { it.apiFunctions.find { f -> api = it; f.functionName == functionName } }
                ?: throw NullPointerException("Function $functionName not found within APIs")

        // Map the parameters provided by the response from
        // ChatGPT to the APIParameters of the mapped function.
        val mappedParameters = mappedFunction.functionParameters
            .map {
                // Create a copy of the parameter
                it.copy().apply {
                    // Since the generic type is not known at runtime,
                    // an additional function is added to APIFunction
                    // to accept Any? which will cast to the return type.
                    castToParameter(functionArguments.getString(parameterName))
                }
            }.toTypedArray().toMutableList()

        // Call the internal lambda function of the
        // APIFunction with the mapped parameters.
        val r = mappedFunction.response(mappedParameters)
//        Logger.debug(
//            FunctionMarker,
//            "Invoked function {} from API {} with parameters",
//            mappedFunction.functionName,
//            api::class.java.name
//        )
//        mappedParameters.forEach {
//            Logger.debug(
//                FunctionMarker,
//                "\t\"{}\" : \"{}\" of type {}",
//                it.parameterName,
//                it.parameterValue,
//                it.parameterValue!!::class.java.simpleName
//            )
//        }

        // Given the function response, add a new message based
        // on the response and send it back to the LLM
        messageHistory.put(
            JSONObject().apply {
                put("role", "function")
                put("name", functionName)
                put("content", "{\"${mappedFunction.responseName}\": \"$r\"}")
            }
        )

        return sendMessage(messageHistory).response
    }

    private fun call(request: Request, callback: (response: Response) -> Unit) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
//                Logger.warn(e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response)
            }
        })
    }

    override fun sendRawMessage(payload: MessagePayload): ResponsePayload {
        TODO("Not implemented")
    }
}