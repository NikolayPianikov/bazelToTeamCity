package bazel.messages

import jetbrains.buildServer.messages.serviceMessages.Message

class MessageFactoryImpl : MessageFactory {
    override fun createMessage(text: String, color: Color) =
            Message(text, "NORMAL", null)

    override fun createWarningMessage(warning: String, color: Color) =
            Message(warning, "NORMAL", null)

    override fun createErrorMessage(error: String, errorDetails: String) =
            Message(error, "ERROR", errorDetails)

    override fun createFlowStarted(flowId: String, parentFlowId: String) =
            FlowStarted(flowId, parentFlowId)

    override fun createFlowFinished(flowId: String) =
            FlowFinished(flowId)

}