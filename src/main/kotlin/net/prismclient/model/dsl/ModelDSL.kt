package net.prismclient.model.dsl

import net.prismclient.feature.api.API
import net.prismclient.model.Message
import net.prismclient.model.LLM
import net.prismclient.util.message
import net.prismclient.util.messagePayload
import kotlin.properties.Delegates

object ModelDSL {
    private val regex = Regex("""\d+\.\s""")

    var activeModel: LLM by Delegates.notNull()
    var activeAPIs: MutableList<API> = mutableListOf()


    inline fun Prompt(message: Message, action: ModelDSL.(response: String) -> Unit): String {
        val response = activeModel.sendMessage(message.messagePayload)

        this.action(response.response)

        return response.response
    }

    fun Prompt(message: String): String = Prompt(message.message) { /* ... */ }

    fun sendRawMessage(message: Message) {
        activeModel.sendRawMessage(message.messagePayload)
    }

    /**
     * Adds the provided API(s) to any calls to the LLM. The APIs will automatically be removed after the block
     * is completed.
     */
    inline fun api(vararg api: API, action: (ModelDSL.() -> Unit)) {
        activeAPIs += api
        ModelDSL.action()
        // Remove the applied APIs from the active API list.
        activeAPIs -= api
    }

    /**
     * Adds the provided API(s) to any calls to the LLM.
     */
    fun api(vararg api: API) {
        activeAPIs += api
        activeModel.apis += activeAPIs
    }

    /**
     * Removes the provided API(s) to any calls to the LLM.
     */
    fun removeApi(vararg api: API) {
        activeAPIs -= api
    }

    /**
     * Removes all active APIs.
     */
    fun clearApis() {
        activeAPIs.clear()
    }
}