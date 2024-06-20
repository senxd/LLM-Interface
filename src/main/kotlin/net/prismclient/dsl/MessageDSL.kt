package net.prismclient.dsl

import net.prismclient.model.Message
import net.prismclient.util.localResource
import java.io.File
import kotlin.properties.Delegates

object MessageDSL {
    var message: Message by Delegates.notNull()

    var localPromptFolder = "prompts".localResource

    fun Include(text: String) {
        message.prompt.rawPrompt.append(text)
    }

    fun LocalPrompt(name: String) = Include(File(localPromptFolder, "$name.txt").readText())
}