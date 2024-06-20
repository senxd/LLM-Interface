package net.prismclient.prompt

class Prompt {
    val rawPrompt = StringBuilder()

    fun merge(prompt: Prompt) {
        rawPrompt.append(prompt.rawPrompt)
    }
}