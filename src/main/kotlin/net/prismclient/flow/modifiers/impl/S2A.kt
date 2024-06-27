package net.prismclient.flow.modifiers.impl

import net.prismclient.flow.modifiers.FlowModifier
import net.prismclient.flow.modifiers.ModifierLocation

/**
 * [S2A,](https://arxiv.org/abs/2311.11829) or System 2 Attention introduced by Meta is a method for optimizing the user
 * prompt to include the important information.
 *
 * @author Winter
 */
class S2A : FlowModifier(ModifierLocation.INITIAL_PROMPT) {
    override fun activate() {
        TODO("Not yet implemented")
    }

}