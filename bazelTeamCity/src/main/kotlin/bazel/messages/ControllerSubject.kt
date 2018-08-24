package bazel.messages

import bazel.Event
import bazel.Verbosity
import bazel.events.*
import bazel.messages.handlers.*
import devteam.rx.*
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import java.util.concurrent.atomic.AtomicBoolean

class ControllerSubject(
        private val _verbosity: Verbosity,
        private val _messageFactory: MessageFactory,
        private val _streamSubjectFactory: () -> ServiceMessageSubject)
    : ServiceMessageSubject {
    private val _controllerSubject = subjectOf<ServiceMessage>()
    private val _streams = mutableMapOf<String, Stream>()
    private val _disposed: AtomicBoolean = AtomicBoolean()

    override fun onNext(value: Event<OrderedBuildEvent>) {
        if (_disposed.get()) {
            return
        }

        val invocationId = value.payload.streamId.invocationId
        val handlerIterator = handlers.iterator()
        val subject = subjectOf<ServiceMessage>()
        subject.map { updateHeader(value.payload, it) }.subscribe(_controllerSubject).use {
            val processed = handlerIterator.next().handle(ServiceMessageContext(subject, handlerIterator, value, _messageFactory, _verbosity))
            if (processed) {
                if (value.payload is InvocationAttemptFinished) {
                    // remove stream state
                    _streams.remove(invocationId)
                }
            }
        }

        _streams.getOrPut(value.payload.streamId.invocationId) { createStreamSubject() }.subject.onNext(value)
    }

    override fun onError(error: Exception) = _controllerSubject.onError(error)

    override fun onCompleted() = _controllerSubject.onCompleted()

    override fun subscribe(observer: Observer<ServiceMessage>): Disposable = _controllerSubject.subscribe(observer)

    override fun dispose() {
        if (_disposed.compareAndSet(false, true)) {
            for (stream in _streams.values) {
                stream.dispose()
            }
        }
    }

    private fun createStreamSubject(): Stream {
        val newStreamSubject = _streamSubjectFactory()
        return Stream(newStreamSubject, newStreamSubject.subscribe(_controllerSubject))
    }

    private fun updateHeader(event: OrderedBuildEvent, message: ServiceMessage): ServiceMessage {
        if (message.flowId.isNullOrEmpty()) {
            message.setFlowId(event.streamId.buildId)
        }

        // message.setTimestamp(event.eventTime)
        return message
    }

    private class Stream(
            val subject: ServiceMessageSubject,
            private val _subscription: Disposable): Disposable
    {
        override fun dispose() {
            _subscription.dispose()
            subject.dispose()
        }
    }

    companion object {
        private val handlers = sequenceOf(
                BuildEnqueuedHandler(),
                InvocationAttemptStartedHandler(),
                InvocationAttemptFinishedHandler(),
                BuildFinishedHandler(),
                NotProcessedEventHandler()
        ).sortedBy { it.priority }.toList()
    }
}