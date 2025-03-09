import net.prismclient.openai.OpenAIModel
import net.prismclient.util.Model

fun main() {
    val apiKey = System.getenv("OPENAI_API_KEY")

    Model(OpenAIModel("o1-preview", apiKey, readTimeout = 300)) {
        chat {
            parameter("max_completion_tokens", 512)

            message(true,"1+1=").apply {
                println("Response: $response")
                println("Reasoning Tokens: $reasoningTokens")
            }
        }
    }
}