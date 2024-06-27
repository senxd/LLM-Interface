package net.prismclient.flow.modifiers

import net.prismclient.model.LLM

abstract class FlowModifier(val location: ModifierLocation, var model: LLM? /* temporarily nullable */) {

    abstract fun activate()
}