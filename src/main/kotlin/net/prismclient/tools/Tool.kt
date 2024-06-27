// IMPROVE: Debug stats
@file:Suppress("UNCHECKED_CAST", "UNUSED", "FunctionName")

package net.prismclient.tools

import net.prismclient.util.Copyable

/**
 *  The base class for defining APIs that can be interfaced by an AI model, such as web searching.
 *
 *  @author Winter
 *  @see ToolFunction
 */
abstract class Tool {
    var toolFunctions: MutableList<ToolFunction<*>> = mutableListOf()
}

/**
 * [ToolFunction] is an abstract class which acts as a wrapper for a standard function. The purpose is to allow an AI
 * model to interface with a defined API function to interact with. [description] provides the purpose of the
 * function to the LLM.
 *
 * All parameters are Strings, which can then be cast to the expected class based on the defined purpose. [R] serves
 * as the class type which the function will return.
 *
 * @author Winter
 */
class ToolFunction<R>(
    val name: String,
    val description: String,
    val parameters: MutableList<ToolParameter<*>>,
    /**
     * If enabled, this function will not appear as part of the LLM's available tools.
     */
    var disableToolCall: Boolean = false,
    /**
     * Some models allow for forcing a certain function(s) to be called. Refer to the model documentation for reference.
     */
    var forceToolCall: Boolean = false,
    val response: (MutableList<ToolParameter<*>>) -> R
)

/**
 * @author Winter
 */
open class ToolParameter<T>(val name: String, val description: String) : Copyable<ToolParameter<T>> {
    open var parameterValue: T? = null

    fun castToParameter(value: Any?) {
        parameterValue = value as T
    }

    override fun copy(): ToolParameter<T> =
        ToolParameter<T>(name, description).also { it.parameterValue = parameterValue }
}

/**
 * Creates an [ToolFunction] which returns [R] and has no parameters.
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param response The lambda to execute for the response.
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <R> Tool.Function(
    functionName: String,
    functionDescription: String,
    crossinline response: () -> R
): ToolFunction<R> = ToolFunction(
    functionName, functionDescription, mutableListOf()
) {
    response()
}.also {
    this.toolFunctions.add(it)
}

/**
 * Creates an [ToolFunction] which returns [R] and has one parameter, [P1].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param response The lambda to execute for the response, taking a single parameter [P1].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, R> Tool.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<ToolParameter<*>>,
    crossinline response: (p1: P1) -> R
): ToolFunction<R> = ToolFunction(
    functionName, functionDescription, functionParameters
) { parameters ->
    response(parameters[0].parameterValue as P1)
}.also {
    this.toolFunctions.add(it)
}

/**
 * Creates an [ToolFunction] which returns [R] and has two parameters, [P1] and [P2].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param response The lambda to execute for the response, taking parameters [P1] and [P2].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, R> Tool.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<ToolParameter<*>>,
    crossinline response: (p1: P1, p2: P2) -> R
): ToolFunction<R> = ToolFunction(
    functionName, functionDescription, functionParameters
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2)
}.also {
    this.toolFunctions.add(it)
}

/**
 * Creates an [ToolFunction] which returns [R] and has three parameters, [P1], [P2], and [P3].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], and [P3].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, R> Tool.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<ToolParameter<*>>,
    crossinline response: (p1: P1, p2: P2, p3: P3) -> R
): ToolFunction<R> = ToolFunction(
    functionName, functionDescription, functionParameters
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2, parameters[2].parameterValue as P3)
}.also {
    this.toolFunctions.add(it)
}

/**
 * Creates an [ToolFunction] which returns [R] and has four parameters, [P1], [P2], [P3], and [P4].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], and [P4].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, R> Tool.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<ToolParameter<*>>,
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4) -> R
): ToolFunction<R> = ToolFunction(
    functionName, functionDescription, functionParameters
) { parameters ->
    response(
        parameters[0].parameterValue as P1,
        parameters[1].parameterValue as P2,
        parameters[2].parameterValue as P3,
        parameters[3].parameterValue as P4
    )
}.also {
    this.toolFunctions.add(it)
}

/**
 * Creates an [ToolFunction] which returns [R] and has five parameters, [P1], [P2], [P3], [P4], and [P5].
 *
 * @param functionName The name of the API function.
 * @param functionDescription A brief description of what the function does.
 * @param functionParameters A list of parameters for the API function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], [P4], and [P5].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, P5, R> Tool.Function(
    functionName: String,
    functionDescription: String,
    functionParameters: MutableList<ToolParameter<*>>,
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) -> R
): ToolFunction<R> = ToolFunction(
    functionName, functionDescription, functionParameters
) { parameters ->
    response(
        parameters[0].parameterValue as P1,
        parameters[1].parameterValue as P2,
        parameters[2].parameterValue as P3,
        parameters[3].parameterValue as P4,
        parameters[4].parameterValue as P5
    )
}.also {
    this.toolFunctions.add(it)
}

/**
 * Creates an [ToolParameter] where the expected type is [R]
 */
fun <R> Parameter(
    parameterName: String, parameterDescription: String
) = ToolParameter<R>(parameterName, parameterDescription)