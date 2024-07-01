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
abstract class OkHttpLLM(modelName: String, modelVersion: String, readTimeout: Int = 30) : LLM(modelName, modelVersion) {
    protected open var client = OkHttpClient()
        .newBuilder()
        .readTimeout(readTimeout.toLong(), TimeUnit.SECONDS)
        .build()

    protected inline fun JSONObject.obj(name: String, lambda: JSONObject.() -> Unit) {
        put(name, JSONObject().apply(lambda))
    }

    protected fun messageObject(role: String, content: String) = JSONObject().apply {
        put("role", role)
        put("content", content)
    }

    protected fun call(request: Request, callback: (response: Response) -> Unit) {
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response)
            }
        })
    }
}