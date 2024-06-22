package net.prismclient

// How the DSL should be like

fun main() {
//    Model(OpenAIModel("gpt-3.5-turbo", "...")) {
//        Chat(useMessageHistory = false) {
//            val response = Message {
//                // IDEA: Inline API definitions (good for single functions)
////                val customAPI = apiFrom(
////                        // ...
////                    ) {
////                        // Custom Functions (Creating an API in context)
////                    }
//
//                API(ExampleAPI)
//
//                Include("What is the time?")
//            }
//        }
//    }
}

//    Model( ... ) {
//        Chat(logHistory = true, sendHistory = false) {
//            val documents = ...
//
//            documents.forEach { document ->
//                val response = Prompt {
//                    val customAPI = apiFrom(
//                        // ...
//                    ) {
//                        // Custom Functions (Creating an API in context)
//                    }
//
//                    API(ResearchAPI, WebAPI, LocalDatabaseAPI)
//                    Structure(basePrompt = Library.Summarizer) {
//                        // Example response with an input (question / prompt) and expected formatted response.
//                        Example(
//                            input = "",
//                            output = ""
//                        )
//                        // Example response (no input, just output)
//                        Example(
//                            output = ""
//                        )
//                    }
//                    Include(FastExtract(
//                        ExtractionLibrary.KeyInformation
//                    ) {
//                        setParameter("abc" to "dfe")
//                    }.result
//                    )
//                }.response
//            }
//        }
//    }