@file:Suppress("UNCHECKED_CAST", "UNUSED", "FunctionName")

package net.prismclient.tools

import net.prismclient.model.LLM
import net.prismclient.util.Copyable

/**
 *  The base class for defining APIs that can be interfaced by an AI model, such as web searching.
 *
 *  Different providers use vary in their naming of this feature. In the project, "Functions" are individual actions
 *  that an LLM can do, while "Tools" are the group of functions to complete some task, e.g., a calculator **tool**
 *  might have an add **function** and a subtract **function**.
 *
 *  @author Winter
 *  @see ToolFunction
 */
abstract class Tool(var name: String) {
    constructor() : this("") { name = this::class.java.simpleName }

    /**
     * A message which is injected into all Messages with this [Tool] active. Useful for providing an overall
     * description of the Tools.
     */
    open val injectionPrompt: String get() = ""

    var functions: MutableList<ToolFunction<*>> = mutableListOf()
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
    val name: String, val description: String, val parameters: MutableList<ToolParameter<*>>,
    /**
     * If enabled, this function will not appear as part of the LLM's available tools.
     */
    var disabled: Boolean = false,
    /**
     * Some models allow for forcing a certain function(s) to be called. Refer to the model documentation for reference.
     */
    var forceCall: Boolean = false, val response: (MutableList<ToolParameter<*>>) -> R
)

/**
 * @author Winter
 */
open class ToolParameter<T>(val name: String, val description: String) : Copyable<ToolParameter<T>> {
    open var parameterValue: T? = null

    fun castTo(value: Any?) {
        parameterValue = value as T
    }

    override fun copy(): ToolParameter<T> =
        ToolParameter<T>(name, description).also { it.parameterValue = parameterValue }
}


/**
 * Creates an [ToolParameter] where the expected type is [R]
 */
fun <R> Parameter(
    name: String, description: String
) = ToolParameter<R>(name, description)


// Tool Function Extensions

/**
 * Creates an [ToolFunction] which returns [R] and has no parameters.
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param response The lambda to execute for the response.
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <R> Tool.Function(
    name: String, description: String, crossinline response: () -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf()
) {
    response()
}.also { functions.add(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has one parameter, [P1].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param response The lambda to execute for the response, taking a single parameter [P1].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, R> Tool.Function(
    name: String, description: String, parameter1: ToolParameter<P1>, crossinline response: (p1: P1) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1)
) { parameters ->
    response(parameters[0].parameterValue as P1)
}.also { functions.add(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has two parameters, [P1] and [P2].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1] and [P2].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, R> Tool.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    crossinline response: (p1: P1, p2: P2) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2)
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2)
}.also { functions.add(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has three parameters, [P1], [P2], and [P3].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param parameter3 The 3rd parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], and [P3].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, R> Tool.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    parameter3: ToolParameter<P3>,
    crossinline response: (p1: P1, p2: P2, p3: P3) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2, parameter3)
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2, parameters[2].parameterValue as P3)
}.also { functions.add(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has four parameters, [P1], [P2], [P3], and [P4].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param parameter3 The 3rd parameter of the function.
 * @param parameter4 The 4th parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], and [P4].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, R> Tool.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    parameter3: ToolParameter<P3>,
    parameter4: ToolParameter<P4>,
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2, parameter3, parameter4)
) { parameters ->
    response(
        parameters[0].parameterValue as P1,
        parameters[1].parameterValue as P2,
        parameters[2].parameterValue as P3,
        parameters[3].parameterValue as P4
    )
}.also { functions.add(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has five parameters, [P1], [P2], [P3], [P4], and [P5].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param parameter3 The 3rd parameter of the function.
 * @param parameter4 The 4th parameter of the function.
 * @param parameter5 The 5th parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], [P4], and [P5].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, P5, R> Tool.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    parameter3: ToolParameter<P3>,
    parameter4: ToolParameter<P4>,
    parameter5: ToolParameter<P5>,
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2, parameter3, parameter4, parameter5)
) { parameters ->
    response(
        parameters[0].parameterValue as P1,
        parameters[1].parameterValue as P2,
        parameters[2].parameterValue as P3,
        parameters[3].parameterValue as P4,
        parameters[4].parameterValue as P5
    )
}.also { functions.add(it) }


// LLM Function Extensions

/**
 * Creates an [ToolFunction] which returns [R] and has no parameters.
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param response The lambda to execute for the response.
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <R> LLM.Function(
    name: String, description: String, crossinline response: () -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf()
) {
    response()
}.also { this.addInlineTool(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has one parameter, [P1].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param response The lambda to execute for the response, taking a single parameter [P1].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, R> LLM.Function(
    name: String, description: String, parameter1: ToolParameter<P1>, crossinline response: (p1: P1) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1)
) { parameters ->
    response(parameters[0].parameterValue as P1)
}.also { this.addInlineTool(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has two parameters, [P1] and [P2].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1] and [P2].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, R> LLM.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    crossinline response: (p1: P1, p2: P2) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2)
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2)
}.also { this.addInlineTool(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has three parameters, [P1], [P2], and [P3].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param parameter3 The 3rd parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], and [P3].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, R> LLM.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    parameter3: ToolParameter<P3>,
    crossinline response: (p1: P1, p2: P2, p3: P3) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2, parameter3)
) { parameters ->
    response(parameters[0].parameterValue as P1, parameters[1].parameterValue as P2, parameters[2].parameterValue as P3)
}.also { this.addInlineTool(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has four parameters, [P1], [P2], [P3], and [P4].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param parameter3 The 3rd parameter of the function.
 * @param parameter4 The 4th parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], and [P4].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, R> LLM.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    parameter3: ToolParameter<P3>,
    parameter4: ToolParameter<P4>,
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2, parameter3, parameter4)
) { parameters ->
    response(
        parameters[0].parameterValue as P1,
        parameters[1].parameterValue as P2,
        parameters[2].parameterValue as P3,
        parameters[3].parameterValue as P4
    )
}.also { this.addInlineTool(it) }

/**
 * Creates an [ToolFunction] which returns [R] and has five parameters, [P1], [P2], [P3], [P4], and [P5].
 *
 * @param name The name of the Tool function.
 * @param description A brief description of what the function does.
 * @param parameter1 The 1st parameter of the function.
 * @param parameter2 The 2nd parameter of the function.
 * @param parameter3 The 3rd parameter of the function.
 * @param parameter4 The 4th parameter of the function.
 * @param parameter5 The 5th parameter of the function.
 * @param response The lambda to execute for the response, taking parameters [P1], [P2], [P3], [P4], and [P5].
 * @return An instance of [ToolFunction] that wraps the provided lambda.
 */
inline fun <P1, P2, P3, P4, P5, R> LLM.Function(
    name: String,
    description: String,
    parameter1: ToolParameter<P1>,
    parameter2: ToolParameter<P2>,
    parameter3: ToolParameter<P3>,
    parameter4: ToolParameter<P4>,
    parameter5: ToolParameter<P5>,
    crossinline response: (p1: P1, p2: P2, p3: P3, p4: P4, p5: P5) -> R
): ToolFunction<R> = ToolFunction(
    name, description, mutableListOf(parameter1, parameter2, parameter3, parameter4, parameter5)
) { parameters ->
    response(
        parameters[0].parameterValue as P1,
        parameters[1].parameterValue as P2,
        parameters[2].parameterValue as P3,
        parameters[3].parameterValue as P4,
        parameters[4].parameterValue as P5
    )
}.also { this.addInlineTool(it) }
