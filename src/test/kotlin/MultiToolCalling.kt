import net.prismclient.model.OpenAIModel
import net.prismclient.tools.Function
import net.prismclient.tools.Parameter
import net.prismclient.util.Model

fun main() {
    val apiKey = System.getenv("OPENAI_API_KEY")

    Model(OpenAIModel("gpt-4o", apiKey)) {
        Chat {
            Function(
                "getWeatherCondition",
                "Returns the weather condition given the location",
                Parameter("Location", "Weather Location (City)")
            ) { location: String ->
                println("Retrieving condition for $location")
                listOf("Sunny", "Rainy", "Cloudy", "Hailing", "Overcast").random()
            }

            val message = Message("What is the time and weather condition in Los Angeles and San Francisco")

            println(message.response)
        }
    }
}