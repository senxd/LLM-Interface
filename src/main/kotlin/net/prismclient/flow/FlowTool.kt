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
    val createStep = Function(
        name = "createStep",
        description = "",
        Parameter("name", ""),
        Parameter("", "")
    ) { name: String, description: String ->
        ""
    }
}