package bazel

import bazel.events.OrderedBuildEvent
import jetbrains.buildServer.messages.serviceMessages.Message
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class ServiceMessageConverter: Converter<OrderedBuildEvent, ServiceMessage> {
    override fun convert(source: OrderedBuildEvent): ServiceMessage {
        val text = source.toString()
        return Message(text, "NORMAL", null)
    }
}