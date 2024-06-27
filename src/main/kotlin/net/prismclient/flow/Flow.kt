// IMPROVE: Add ability to define parameters, so the same flow can be executed with different datasets, etc...
package net.prismclient.flow

import net.prismclient.execution.Action
import net.prismclient.execution.Element
import net.prismclient.flow.modifiers.FlowModifier
import net.prismclient.flow.modifiers.ModifierLocation

/**
 * Flow defines a tracked sequence of tasks. Instead of actually executing, the execution flow is stored which then
 * can be used to execute.
 *
 * @author Winter
 */
class Flow : Action {
    val elements: ArrayList<Element> = arrayListOf()
    val actions: ArrayList<Action> get() = elements.filterIsInstance<Action>().toCollection(ArrayList())
    val flows: ArrayList<Flow> get() = elements.filterIsInstance<Flow>().toCollection(ArrayList())

    val modifiers: ArrayList<FlowModifier> = arrayListOf()

    var disabled = false

    override fun execute() {
        if (!disabled) {
            actions.forEach(Action::execute)
        }
    }

    fun deriveBaseThought() {

    }

    fun modifiers(vararg modifiers: FlowModifier) {
        this.modifiers += modifiers
    }

    private fun applyModifiers(location: ModifierLocation) {
        modifiers.filter { it.location == location }.forEach(FlowModifier::activate)
    }
}