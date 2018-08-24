package bazel.messages

import bazel.Verbosity
import bazel.events.OrderedBuildEvent
import bazel.messages.handlers.*
import devteam.rx.*
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class StreamSubject(
        private val _verbosity: Verbosity,
        private val _messageFactory: MessageFactory)
    : ServiceMessageSubject {
    private val _messageSubject = subjectOf<ServiceMessage>()

    override fun onNext(value: OrderedBuildEvent) {
        val handlerIterator = handlers.iterator()
        val subject = subjectOf<ServiceMessage>()
        subject.map { updateHeader(value, it) }.subscribe(_messageSubject).use {
            handlerIterator.next().handle(ServiceMessageContext(subject, handlerIterator, value, _messageFactory, _verbosity))
        }
    }

    override fun onError(error: Exception) = _messageSubject.onError(error)

    override fun onCompleted() = _messageSubject.onCompleted()

    override fun subscribe(observer: Observer<ServiceMessage>): Disposable = _messageSubject.subscribe(observer)

    override fun dispose() = Unit

    private fun updateHeader(event: OrderedBuildEvent, message: ServiceMessage): ServiceMessage {
        if (message.flowId.isNullOrEmpty()) {
            message.setFlowId(event.streamId.invocationId)
        }

        // message.setTimestamp(event.eventTime)
        return message
    }

    companion object {
        private val handlers = sequenceOf(
                BuildStartedHandler(),
                BuildCompletedHandler(),
                UnknownEventHandler()
        ).sortedBy { it.priority }.toList()
    }
}