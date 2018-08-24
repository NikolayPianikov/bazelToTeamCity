package bazel.messages

import jetbrains.buildServer.messages.serviceMessages.MessageWithAttributes

class FlowFinished(flowId: String)
    : MessageWithAttributes(
        "flowFinished",
        mapOf(
                "flowId" to flowId))