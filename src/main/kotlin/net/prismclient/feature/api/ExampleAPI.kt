package net.prismclient.feature.api

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ExampleAPI : API() {
    val exampleFunction = Function(
        functionName = "getTime",
        functionDescription = "Returns the current time based on the user's location."

    ) {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }
}