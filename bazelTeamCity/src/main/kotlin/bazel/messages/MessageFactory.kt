package bazel.messages

import bazel.Verbosity
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

interface MessageFactory {
    fun createMessage(text: String, color: Color = Color.Default): ServiceMessage

    fun createWarningMessage(warning: String, color: Color = Color.Warning): ServiceMessage

    fun createErrorMessage(error: String, errorDetails: String): ServiceMessage

    fun createFlowStarted(flowId: String, parentFlowId: String): ServiceMessage

    fun createFlowFinished(flowId: String): ServiceMessage
}