package bazel.messages

import jetbrains.buildServer.messages.serviceMessages.Message

interface MessageFactory {
    fun createMessage(text: String, color: Color = Color.Default): Message

    fun createWarningMessage(warning: String, color: Color = Color.Warning): Message

    fun createErrorMessage(error: String, errorDetails: String? = null): Message
}