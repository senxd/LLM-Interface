package net.prismclient.flow

import net.prismclient.tools.Function
import net.prismclient.tools.Parameter
import net.prismclient.tools.Tool

/**
 * [FlowTool] injects into the toolset to allow for interpreting Flow based requests
 *
 * @author Winter
 */
class FlowTool : Tool() {
    val steps = HashMap<String, String>()
    val createStep = Function(
        name = "createStep",
        description = "",
        Parameter("Step Name", "The name of the step"),
        Parameter("Step Description", "Overview/Description of the Step")
    ) { name: String, description: String ->
        steps[name] = description
        "Success"
    }
}