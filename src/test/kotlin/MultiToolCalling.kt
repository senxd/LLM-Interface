import net.prismclient.model.OpenAIModel
import net.prismclient.tools.ExampleTool
import net.prismclient.util.Model

fun main() {
    val apiKey = System.getenv("OPENAI_API_KEY")

    Model(OpenAIModel("gpt-4o", apiKey)) {
        Chat {
            Tool(ExampleTool)

            val message = Message("What is the time and weather condition in Los Angeles and San Francisco")

            println(message.response)
        }
    }
}