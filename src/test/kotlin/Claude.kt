import net.prismclient.model.vendor.anthropic.AnthropicModel
import net.prismclient.tools.Function
import net.prismclient.tools.Parameter
import net.prismclient.util.Model
import java.util.*

fun main(args: Array<String>) {
    Model(AnthropicModel(AnthropicModel.Models.SONNET_3_7, args[0], false)) {
        chat {
            Function(
                "getWeatherCondition",
                "Returns the weather condition for a given location",
                Parameter("location", "The city and country to get weather for")
            ) { location: String ->
                println("Executing weather tool for: $location")
                val conditions = listOf("Sunny", "Rainy", "Cloudy", "Hailing", "Overcast")
                val temp = (5..35).random()
                val humidity = (30..90).random()
                val result =
                    mapOf(
                        "condition" to conditions.random(),
                        "temperature" to "$temp°C",
                        "humidity" to "$humidity%"
                    )
                println("Weather result: $result")
                result.toString()
            }

            Function(
                "calculate",
                "Performs basic arithmetic operations",
                Parameter(
                    "operation",
                    "The operation to perform (add, subtract, multiply, divide)"
                ),
                Parameter("num1", "The first number"),
                Parameter("num2", "The second number")
            ) { operation: String, num1: String, num2: String ->
                println("\nExecuting calculator: $operation($num1, $num2)")
                try {
                    val n1 = num1.toDouble()
                    val n2 = num2.toDouble()
                    val result =
                        when (operation.lowercase()) {
                            "add" -> n1 + n2
                            "subtract" -> n1 - n2
                            "multiply" -> n1 * n2
                            "divide" -> n1 / n2
                            else ->
                                throw IllegalArgumentException(
                                    "Unknown operation: $operation"
                                )
                        }
                    println("Calculator result: $result")
                    result.toString()
                } catch (e: Exception) {
                    val error = "Error: ${e.message}"
                    println(error)
                    error
                }
            }

            val prompt =
                """
                1. What's the current weather in San Francisco?
                2. Can you calculate 123.45 multiplied by 67.89?
                
                Please use the available tools to help answer these questions.
            """.trimIndent()

            println("PROMPT:")
            println("-".repeat(50))
            println(prompt)
            println("-".repeat(50))

            val response = message(true, prompt).response

            println("RESPONSE:")
            println("-".repeat(50))
            println(response)
            println("-".repeat(50))
        }
    }
}
