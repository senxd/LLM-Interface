import net.prismclient.model.vendor.anthropic.AnthropicModel
import net.prismclient.tools.Function
import net.prismclient.tools.Parameter
import net.prismclient.util.Model

fun main(args: Array<String>) {
    val apiKey = args[0]

    // Create the model
    val claudeModel = AnthropicModel(AnthropicModel.Models.SONNET_3_7, apiKey).apply {
        // Set up configuration
//        useExtendedThinking = true
//        extendedThinkingMaxTokens = 2000
//        maxTokens = 1024
        
        // Set up a streaming callback for real-time responses
        streamingCallback = { text ->
//            print(text)
        }
    }

    Model(claudeModel) {
        chat {
            println("Registering tools...")
            
            // Weather tool
           Function(
               "getWeatherCondition",
               "Returns the weather condition for a given location",
               Parameter("location", "The city and country to get weather for")
           ) { location: String ->
               println("\nExecuting weather tool for: $location")
               val conditions = listOf("Sunny", "Rainy", "Cloudy", "Hailing", "Overcast")
               val temp = (5..35).random()
               val humidity = (30..90).random()
               val result = mapOf(
                   "condition" to conditions.random(),
                   "temperature" to "$temp°C",
                   "humidity" to "$humidity%"
               )
               println("Weather result: $result")
               result.toString()
           }
            
            // Calculator tool
            // Function(
            //     "calculate",
            //     "Performs basic arithmetic operations",
            //     Parameter("operation", "The operation to perform (add, subtract, multiply, divide)"),
            //     Parameter("num1", "The first number"),
            //     Parameter("num2", "The second number")
            // ) { operation: String, num1: String, num2: String ->
            //     println("\nExecuting calculator: $operation($num1, $num2)")
            //     try {
            //         val n1 = num1.toDouble()
            //         val n2 = num2.toDouble()
            //         val result = when (operation.toLowerCase()) {
            //             "add" -> n1 + n2
            //             "subtract" -> n1 - n2
            //             "multiply" -> n1 * n2
            //             "divide" -> n1 / n2
            //             else -> throw IllegalArgumentException("Unknown operation: $operation")
            //         }
            //         println("Calculator result: $result")
            //         result.toString()
            //     } catch (e: Exception) {
            //         val error = "Error: ${e.message}"
            //         println(error)
            //         error
            //     }
            // }
            
            // A prompt that should trigger tool usage
            val toolPrompt = """
                
                1. What's the current weather in San Francisco?
                
                Please use the available tools to help answer these questions.
            """.trimIndent()

            // 2. Can you calculate 123.45 multiplied by 67.89?
            
            println("\nSending question to test tools:")
            println("PROMPT: $toolPrompt")
            println("\nRESPONSE (streaming):\n")
            
            // Send the message and get the response
            val response = message(true, toolPrompt).response

//            val response1 = message(true, "How is your day?").response
            
            // Print the final response
            println("\n\nFull response received.")
        }
    }
}