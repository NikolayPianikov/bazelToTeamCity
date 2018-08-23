package bazel.messages

import bazel.events.OrderedBuildEvent
import bazel.messages.handlers.BuildEnqueuedHandler
import bazel.messages.handlers.BuildFinishedHandler
import bazel.messages.handlers.UnknownEventHandler
import devteam.rx.Disposable
import devteam.rx.Observer
import devteam.rx.subjectOf
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class StreamSubject(private val _messageFactory: MessageFactory): ServiceMessageSubject {
    private val _messageSubject = subjectOf<ServiceMessage>()

    override fun onNext(value: OrderedBuildEvent) {
        val handlerIterator = handlers.iterator();
        handlerIterator.next().handle(ServiceMessageContext(_messageSubject, handlerIterator, value, _messageFactory))
    }

    override fun onError(error: Exception) = _messageSubject.onError(error)

    override fun onCompleted() = _messageSubject.onCompleted()

    override fun subscribe(observer: Observer<ServiceMessage>): Disposable = _messageSubject.subscribe(observer)

    override fun dispose() = Unit

    companion object {
        private val handlers = sequenceOf(
                BuildEnqueuedHandler(),
                BuildFinishedHandler(),
                UnknownEventHandler()
        ).sortedBy { it.priority }.toList()
    }
}