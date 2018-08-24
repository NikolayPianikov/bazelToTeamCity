package bazel.messages

import jetbrains.buildServer.messages.serviceMessages.MessageWithAttributes

class FlowStarted(flowId: String, parentFlowId: String)
    : MessageWithAttributes(
        "flowStarted",
        mapOf(
                "flowId" to flowId,
                "parent" to parentFlowId))