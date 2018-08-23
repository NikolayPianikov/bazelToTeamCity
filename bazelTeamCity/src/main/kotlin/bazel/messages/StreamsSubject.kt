package bazel.messages

import bazel.events.OrderedBuildEvent
import bazel.events.StreamId
import devteam.rx.Disposable
import devteam.rx.Observer
import devteam.rx.subjectOf
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import java.util.concurrent.atomic.AtomicBoolean

class StreamsSubject(private val _streamSubjectFactory: () -> ServiceMessageSubject): ServiceMessageSubject {
    private val _streams = mutableMapOf<StreamId, Stream>()
    private val _commonSubject = subjectOf<ServiceMessage>()
    private val _disposed: AtomicBoolean = AtomicBoolean();

    override fun onNext(value: OrderedBuildEvent) {
        if (_disposed.get()) {
            return
        }

        _streams.getOrPut(value.streamId) { createStreamSubject() }.subject.onNext(value)
    }

    override fun onError(error: Exception) = _commonSubject.onError(error)

    override fun onCompleted() = _commonSubject.onCompleted()

    override fun subscribe(observer: Observer<ServiceMessage>): Disposable = _commonSubject.subscribe(observer)

    override fun dispose() {
        if (_disposed.compareAndSet(false, true)) {
            for (stream in _streams.values) {
                stream.dispose()
            }
        }
    }

    private fun createStreamSubject(): Stream {
        val newStreamSubject = _streamSubjectFactory();
        return Stream(newStreamSubject, newStreamSubject.subscribe(_commonSubject))
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
}