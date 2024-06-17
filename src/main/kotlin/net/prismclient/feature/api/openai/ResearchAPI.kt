package net.prismclient.feature.api.openai

import net.prismclient.feature.api.APIFunction
import net.prismclient.feature.api.Function
import net.prismclient.feature.api.Parameter

class ResearchAPI : OpenAIRAGTool() {
    override fun injectionPrompt(): String {
        TODO("Not yet implemented")
    }
}

// Demo testing class
object WeatherAPI : OpenAITool() {
    val getWeatherTemperatureFunction = Function(
        functionName = "getWeatherTemperature",
        functionDescription = "Returns the Weather temperature (in Celsius)",
        functionParameters = mutableListOf(
            Parameter<String>(
                parameterName = "Location",
                parameterDescription = "The City in which the Weather is to be retrieved from.",
            )
        ),
        response = { location: String ->
            30
        }
    )

    val getWeatherConditionFunction = APIFunction(
        functionName = "getWeatherCondition",
        functionDescription = "Returns the Weather Condition",
        functionParameters = mutableListOf(
            Parameter<String>(
                parameterName = "Location",
                parameterDescription = "The City in which the Weather is to be retrieved from.",
            )
        ),
        response = { parameters ->
            "Rainy"
        }
    )


    override fun injectionPrompt(): String {
        TODO("Not yet implemented")
    }

}

