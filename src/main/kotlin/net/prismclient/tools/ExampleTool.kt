package net.prismclient.tools

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ExampleTool : Tool() {
    val exampleFunction = Function(
        functionName = "getTime",
        functionDescription = "Returns the current time based on the user's location."

    ) {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}