package net.prismclient.tools

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ExampleTool : Tool() {
    val timeFunction = Function(
        "getTime",
        "Returns the current time based on the user's location."
    ) {
        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val locationFunction = Function(
        "getLocation",
        "Returns the current general location of the user"
    ) { "San Francisco" }

    val weatherFunction = Function(
        name = "getWeather",
        description = "Returns the weather condition of the given location",
        Parameter(
            name = "Location",
            description = "The city to retrieve the weather condition of."
        ),
        Parameter(
            name = "Time",
            description = "The time of retrieval. 'Now' for current weather."
        )
    ) { location: String, time: String->
        println("Tool Calling: Retrieving weather for $location")
        listOf("Sunny", "Rainy", "Cloudy").random()
    }
}