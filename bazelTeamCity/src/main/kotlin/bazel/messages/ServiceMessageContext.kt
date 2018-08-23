package bazel.messages

import bazel.events.OrderedBuildEvent
import bazel.messages.handlers.EventHandler
import devteam.rx.Observer
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class ServiceMessageContext(
        private val _observer: Observer<ServiceMessage>,
        val handlerIterator: Iterator<EventHandler>,
        val event: OrderedBuildEvent,
        val messageFactory: MessageFactory ): Observer<ServiceMessage> {
    override fun onNext(value: ServiceMessage) = _observer.onNext(value)

    override fun onError(error: Exception) = _observer.onError(error)

    override fun onCompleted() = _observer.onCompleted()
}