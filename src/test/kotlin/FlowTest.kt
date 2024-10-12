//
//import net.prismclient.flow.FlowTool
//import net.prismclient.model.OpenAIModel
//import net.prismclient.util.Model
//
//fun main() {
//    val flowTool = FlowTool()
//    Model(OpenAIModel("gpt-4o", System.getenv("OPENAI_API_KEY"))) {
//        chat {
//
////            force(flowTool.createStep)
//
//            val msg1 = message("Generate a list of steps to complete the following task (maximum 10 steps): I’m thinking about buying a 4070 TI Super, but I was wondering if there are better options, given that my budget is \$850. ")
//
//            println(msg1.response)
//
//            tool(flowTool)
//            val msg2 = message("Call createStep for each of the steps you created ")
//
//            println(msg2.response)
//
//            //responseOnly {
//                //Message("Call createStep for each of the steps you created ")
//            //}
//        }
//    }
//
//    println("Generated list:")
//    flowTool.steps.toList().forEachIndexed { index, pair ->
//        val (key, value) = pair
//        println("$index.) Name: $key, Description: $value")
//    }
//}