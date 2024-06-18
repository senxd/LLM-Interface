package net.prismclient

import net.prismclient.feature.api.API
import net.prismclient.model.Message
import net.prismclient.model.dsl.ModelDSL
import net.prismclient.model.dsl.ModelDSL.activeAPIs
import net.prismclient.model.dsl.ModelDSL.activeModel
import kotlin.properties.Delegates

object MessageDSL {
    var message: Message by Delegates.notNull()

    fun Include(text: String) {
        message.prompt.rawPrompt.append(text)
    }
}