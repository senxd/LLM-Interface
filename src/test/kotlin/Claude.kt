import net.prismclient.model.vendor.anthropic.AnthropicModel
import net.prismclient.tools.Function
import net.prismclient.tools.Parameter
import net.prismclient.util.Model

fun main(args: Array<String>) {
    val apiKey = args[0]

    Model(AnthropicModel(AnthropicModel.Models.SONNET_3_7, apiKey)) {
        chat {
            Function(
                "getWeatherCondition",
                "Returns the weather condition given the location",
                Parameter("Location", "Weather Location (City)")
            ) { location: String ->
                println("Retrieving condition for $location")
                listOf("Sunny", "Rainy", "Cloudy", "Hailing", "Overcast").random()
            }

            println(message(true, "What is the weather in SF?").response)
        }
    }
}