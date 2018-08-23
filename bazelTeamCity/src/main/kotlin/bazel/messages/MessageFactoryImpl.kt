package bazel.messages

import jetbrains.buildServer.messages.serviceMessages.Message

class MessageFactoryImpl : MessageFactory {
    override fun createMessage(text: String, color: Color): Message {
        return Message(text, "NORMAL", null)
    }

    override fun createWarningMessage(warning: String, color: Color): Message {
        return Message(warning, "NORMAL", null)
    }

    override fun createErrorMessage(error: String, errorDetails: String?): Message {
        return Message(error, "ERROR", errorDetails)
    }
}