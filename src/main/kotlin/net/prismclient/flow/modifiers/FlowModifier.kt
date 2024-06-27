package net.prismclient.flow.modifiers

import net.prismclient.dsl.DefaultModel
import net.prismclient.model.LLM

abstract class FlowModifier(val location: ModifierLocation, var model: LLM = DefaultModel) {

    abstract fun activate()
}