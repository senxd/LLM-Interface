@file:Suppress("UNCHECKED_CAST")
package net.prismclient.feature.api

import net.prismclient.util.Copyable

/**
 *  The base class for defining APIs that can be interfaced by an AI model, such as web searching.
 *
 *  @author Winter
 *  @see APIFunction
 */
abstract class API {
    var apiFunctions: MutableList<APIFunction<*>> = mutableListOf()

    /**
     * Returns the prompt to be injected into the LLM query. Can be used to add additional information / documentation
     * about the API for the LLM to use prior to using it.
     */
    abstract fun injectionPrompt(): String
}

/**
 * [APIFunction] is an abstract class which acts as a wrapper for a standard function. The purpose is to allow an AI
 * model to interface with a defined API function to interact with. The two variables [functionDescription] and
 * [parameterPurpose] document the purpose of the function (and its parameters) for the AI Model's use. It is provided
 * directly into the context.
 *
 * All parameters are Strings, which can then be cast to the expected class based on the defined purpose. [R] serves
 * as the class type which the function will return.
 *
 * @author Winter
 */
class APIFunction<R>(
    val functionName: String,
    val functionDescription: String,
    val functionParameters: MutableList<APIParameter<*>>,
    val responseName: String = "response",
    val response: (MutableList<APIParameter<*>>) -> R
) {
    init {
//        Logger.debug(FunctionMarker, "Created API function $functionName")
    }
}

/**
 * @author Winter
 */
open class APIParameter<T>(val parameterName: String, val parameterDescription: String) : Copyable<APIParameter<T>> {
    open var parameterValue: T? = null

    fun castToParameter(value: Any?) {
        parameterValue = value as T
    }

    override fun copy(): APIParameter<T> =
        APIParameter<T>(parameterName, parameterDescription)
            .also { it.parameterValue = parameterValue }
}

/**
 * Creates an [APIFunction] which returns [R] and has no parameters.
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param responseName The name of the response, default is "response".
 * @param response The lambda to execute for the response.
 * @return An instance of [APIFunction] that wraps the provided lambda.
 */
inline fun <R> API.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<APIParameter<*>>,
    responseName: String = "response",
    crossinline response: () -> R
): APIFunction<R> = APIFunction<R>(
    functionName,
    functionDescription,
    functionParameters,
    responseName
) {
    response()
}.also {
    this.apiFunctions.add(it)
}

/**
 * Creates an [APIFunction] which returns [R] and has one parameter, [P1].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param responseName The name of the response, default is "response".
 * @param response The lambda to execute for the response, taking a single parameter [P1].
 * @return An instance of [APIFunction] that wraps the provided lambda.
 */
inline fun <P1, R> API.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<APIParameter<*>>,
    responseName: String = "response",
    crossinline response: (p1: P1) -> R
): APIFunction<R> = APIFunction(
    functionName,
    functionDescription,
    functionParameters,
    responseName
) { parameters ->
    response(parameters[0].parameterValue as P1)
}.also {
    this.apiFunctions.add(it)
}

/**
 * Creates an [APIFunction] which returns [R] and has two parameters, [P1] and [P2].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param responseName The name of the response, default is "response".
 * @param response The lambda to execute for the response, taking parameters [P1] and [P2].
 * @return An instance of [APIFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, R> API.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<APIParameter<*>>,
    responseName: String = "response",
    crossinline response: (p1: P1, p2: P2) -> R
): APIFunction<R> = APIFunction(
    functionName,
    functionDescription,
    functionParameters,
    responseName
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2)
}.also {
    this.apiFunctions.add(it)
}

/**
 * Creates an [APIFunction] which returns [R] and has three parameters, [P1], [P2], and [P3].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param responseName The name of the response, default is "response".
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], and [P3].
 * @return An instance of [APIFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, R> API.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<APIParameter<*>>,
    responseName: String = "response",
    crossinline response: (p1: P1, p2: P2, p3: P3) -> R
): APIFunction<R> = APIFunction(
    functionName,
    functionDescription,
    functionParameters,
    responseName
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2, parameters[2].parameterValue as P3)
}.also {
    this.apiFunctions.add(it)
}

/**
 * Creates an [APIFunction] which returns [R] and has four parameters, [P1], [P2], [P3], and [P4].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param responseName The name of the response, default is "response".
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], and [P4].
 * @return An instance of [APIFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, R> API.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<APIParameter<*>>,
    responseName: String = "response",
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4) -> R
): APIFunction<R> = APIFunction(
    functionName,
    functionDescription,
    functionParameters,
    responseName
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2, parameters[2].parameterValue as P3, parameters[3].parameterValue as P4)
}.also {
    this.apiFunctions.add(it)
}

/**
 * Creates an [APIFunction] which returns [R] and has five parameters, [P1], [P2], [P3], [P4], and [P5].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param responseName The name of the response, default is "response".
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], [P4], and [P5].
 * @return An instance of [APIFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, P5, R> API.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<APIParameter<*>>,
    responseName: String = "response",
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) -> R
): APIFunction<R> = APIFunction(
    functionName,
    functionDescription,
    functionParameters,
    responseName
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2, parameters[2].parameterValue as P3, parameters[3].parameterValue as P4, parameters[4].parameterValue as P5)
}.also {
    this.apiFunctions.add(it)
}

/**
 * Creates an [APIParameter] where the expected type is [R]
 */
fun <R> API.Parameter(
    parameterName: String, parameterDescription: String
) = APIParameter<R>(parameterName, parameterDescription)