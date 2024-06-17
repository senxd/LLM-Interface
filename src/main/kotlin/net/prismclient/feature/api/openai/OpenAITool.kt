package net.prismclient.feature.api.openai

import net.prismclient.feature.api.API

abstract class OpenAITool(): API()

abstract class OpenAIRAGTool(
//    val retrieveDatabaseEntries: DatabaseRetrieval = DatabaseRetrieval
) : OpenAITool() {

//    object DatabaseRetrieval: APIFunction<List<String>>(
//        functionName = "retrieveDatabaseEntries",
//        functionPurpose = "",
//        functionParameters = mutableListOf(APIParameter("keywords", ""))
//    ) {
//        override fun invokeInternal(parameters: MutableList<APIParameter>): APIResponse<List<String>> {
//            TODO("Not yet implemented")
//        }
//    }
}

/**
 * Allows for interfacing with a database full of Research Papers for the AI to retrieve based on specified keywords.
 * Also known as RAG (Retrieval-Augmented Generation), a technique that enhances the accuracy of language models by
 * combining retrieval-based and generative-based approaches.
 *
 * @author Winter
 */
class PaperTool()