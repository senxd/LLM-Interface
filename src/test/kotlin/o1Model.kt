import net.prismclient.model.OpenAIModel
import net.prismclient.util.Model

fun main() {
    val apiKey = System.getenv("OPENAI_API_KEY")

    Model(OpenAIModel("o1-preview", apiKey, readTimeout = 300)) {
        chat {
            parameter("max_completion_tokens", 1024)

            println(message(true,"What is the meaning of life?").response)
        }
    }
}