import net.prismclient.model.OpenAIModel
import net.prismclient.tools.ExampleTool
import net.prismclient.util.Model

fun main() {
    val apiKey = System.getenv("OPENAI_API_KEY")
    Model(OpenAIModel("gpt-4o", apiKey)) {
        Chat {
            Tool(ExampleTool)

            ExampleTool.weatherFunction.force = true

            val message = Message("How are you doing?")
                //                Message("Tell me the weather conditions in the following places: San Diego, New York, " +
//                        "San Francisco, Seattle and Irvine")

            println(message.response)
        }
    }
}