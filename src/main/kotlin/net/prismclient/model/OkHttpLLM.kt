package net.prismclient.model

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * [OkHttpLLM] provides some tools if [OkHttp] is used to access the LLM.
 *
 * @author Winter
 */
abstract class OkHttpLLM(
    modelName: String,
    modelVersion: String?,
    readTimeout: Int = 30,
    modelVendor: ModelVendor = ModelVendor.Custom_OpenAI
) : LLM(modelName, modelVersion, modelVendor) {
    protected open var client = OkHttpClient()
        .newBuilder()
        .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        .build()

    open var readTimeout: Int = readTimeout
        set(value) {
            field = value
            client = client.newBuilder().readTimeout(value.toLong(), TimeUnit.SECONDS).build()
        }

    /**
     * If the package fails to send due to a 429 (rate limit) error, it will automatically resend
     * after the delay has passed. `-1` disables resending.
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

    protected inline fun JSONObject.obj(name: String, lambda: JSONObject.() -> Unit) {
        put(name, JSONObject().apply(lambda))
    }

    protected fun messageObject(role: String, content: String) = JSONObject().apply {
        put("role", role)
        put("content", content)
    }

    protected fun messageObject(role: String, content: JSONObject) = JSONObject().apply {
        put("role", role)
        put("content", content)
    }

    protected fun call(request: Request, callback: (response: Response) -> Unit) {
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    callback(response)
                }
            })
        } catch(exception: Exception) {
            handleCallException(exception, request, callback)
        }
    }

    open fun handleCallException(exception: Exception, request: Request, callback: (response: Response) -> Unit) {
        exception.printStackTrace()
    }
}