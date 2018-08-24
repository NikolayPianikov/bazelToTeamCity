package bazel.messages

import jetbrains.buildServer.messages.serviceMessages.*

class MessageFactoryImpl : MessageFactory {
    override fun createMessage(text: String, color: Color) =
            Message(text, Normal, null)

    override fun createWarningMessage(warning: String, color: Color) =
            Message(warning, Normal, null)

    override fun createErrorMessage(error: String, errorDetails: String?) =
            Message(error, Error, errorDetails)

    override fun createFlowStarted(flowId: String, parentFlowId: String) =
            FlowStarted(flowId, parentFlowId)

    override fun createFlowFinished(flowId: String) =
            FlowFinished(flowId)

    override fun createBuildStatus(text: String, success: Boolean) =
            BuildStatus(text, if(success) Normal else Error)

    override fun createBuildProblem(description: String, projectId: String, errorId: String) =
            BuildProblem(description, "$projectId-$errorId")

    override fun createBlockOpened(blockName: String, description: String): ServiceMessage {
        return BlockOpened(blockName, description)
    }

    override fun createBlockClosed(blockName: String): ServiceMessage {
        return BlockClosed(blockName)
    }

    companion object {
        private const val Normal = "NORMAL"
        private const val Error = "ERROR"
    }
}