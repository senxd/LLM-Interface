package net.prismclient.tools

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ExampleTool : Tool() {
    val timeFunction = Function(
        functionName = "getTime",
        functionDescription = "Returns the current time based on the user's location."

    ) {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val locationFunction = Function(
        functionName = "getLocation",
        functionDescription = "Returns the current general location of the user"

    ) { "San Francisco" }

    val weatherFunction = Function(
        functionName = "getWeather",
        functionDescription = "Returns the weather condition of the given location",
        functionParameters = mutableListOf(
            ToolParameter<String>(
                name = "Location",
                description = "The city to retrieve the weather condition of."
            ),
            ToolParameter<String>(
                name = "Time",
                description = "The time of retrieval. 'Now' for current weather."
            ),
        )
    ) { location: String ->
        println("Tool Calling: Retrieving weather for $location")
        listOf("Sunny", "Rainy", "Cloudy").random()
    }

}