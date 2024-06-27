package net.prismclient.prompt

import net.prismclient.execution.Element

class Prompt : Element {
    val rawPrompt = StringBuilder()
    var responseFormat: ResponseFormat? = null

    fun merge(prompt: Prompt) {
        rawPrompt.append(prompt.rawPrompt)
    }

    operator fun String.unaryPlus() {
        rawPrompt.append(this)
    }

    operator fun String.unaryMinus() {
        responseFormat = ResponseFormat(this)
    }
}