package net.prismclient

import net.prismclient.feature.api.API
import net.prismclient.feature.steps.Step
import net.prismclient.model.Model
import net.prismclient.payload.ResponsePayload
import net.prismclient.payload.toMessagePayload
import kotlin.properties.Delegates

inline fun Model(model: Model, action: ModelDSL.() -> Unit) {
    ModelDSL.activeModel = model
    ModelDSL.action()
}

object ModelDSL {
    private val regex = Regex("""\d+\.\s""")

    var activeModel: Model by Delegates.notNull()
    var activeAPIs: MutableList<API> = mutableListOf()

    /**
     * Prompts the model based on the given prompt. Automatically includes any enabled APIs.
     */
    inline fun Prompt(message: String, action: ModelDSL.(response: String) -> Unit) {
        val response = activeModel.sendMessage(message.toMessagePayload())

        this.action(response.response)
    }


    /**
     * Prompts the AI to generate a step-by-step list to solve the problem introduced by [prompt]. The purpose is to
     * leverage CoT (Chain of Thought), which makes the LLM explain its steps. This takes it further, however, as
     * instead of answering the question immediately, the steps batched individually with separate queries, and then
     * combined to create the final response.
     *
     * @see IncludeSteps
     */
    inline fun GenerateSteps(prompt: String, action: ModelDSL.(steps: Array<Step>) -> Unit) {
        val fullPrompt = """
            |Given the following prompt create a list of steps to solve the problem. Write one sentence
            |for each step in a numbered list. Say nothing else except the sentences for each step. Some 
            |example steps have been provided.
            |Example:
            |
            |Prompt: "What are the top 3 best Tom Cruise Movies"
            |Response: "
            |1. Visit Tom Cruise's IMDB discography page.
            |2. Aggregate all movies with Tom Cruise starring in it in a list.
            |3. Find the movie with the highest rating within the list.
            |"
            |
            |Prompt: "$prompt"
        """.trimMargin()

        val response = activeModel.sendMessage(fullPrompt.toMessagePayload()).response

        IncludeSteps(response, action)
    }

    /**
     * Periodically executes the provided steps.
     *
     * @see GenerateSteps
     */
    inline fun IncludeSteps(steps: String, action: ModelDSL.(steps: Array<Step>) -> Unit) {
        ModelDSL.action(parseSteps(steps))
    }

    /**
     * Adds the provided API(s) to any calls to the LLM. The APIs will automatically be removed after the block
     * is completed.
     */
    inline fun IncludeApi(vararg api: API, action: (ModelDSL.() -> Unit)) {
        activeAPIs += api
        activeModel
        ModelDSL.action()
        // Remove the applied APIs from the active API list.
        activeAPIs -= api
    }

    /**
     * Adds the provided API(s) to any calls to the LLM.
     */
    fun includeApi(vararg api: API) {
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

    fun sendAsynchronousMessage(message: String, callback: (ResponsePayload) -> Unit) {

    }

    fun sendSynchronousMessage(message: String): ResponsePayload {
        return ResponsePayload("")
    }

    // TODO: Observer to ensure the model properly responded to the prompt. Retry once or twice, and then error if did not succeed.
    fun parseSteps(message: String): Array<Step> =
        regex.split(message).filter { it.isNotBlank() }.map { Step(it) }.toTypedArray()
}