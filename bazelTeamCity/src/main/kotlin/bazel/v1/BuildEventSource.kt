package bazel.v1

import bazel.toObserver
import bazel.toStreamObserver
import com.google.devtools.build.v1.*
import com.google.protobuf.Empty
import devteam.rx.*
import io.grpc.stub.StreamObserver
import java.util.logging.Level
import java.util.logging.Logger
import com.google.devtools.build.v1.OrderedBuildEvent

internal class BuildEventSource
    : PublishBuildEventGrpc.PublishBuildEventImplBase(), Observable<OrderedBuildEvent>{

    private val _eventSubject = subjectOf<OrderedBuildEvent>()

    override fun subscribe(observer: Observer<OrderedBuildEvent>): Disposable = _eventSubject.subscribe(observer)

    override fun publishLifecycleEvent(request: PublishLifecycleEventRequest?, responseObserver: StreamObserver<Empty>?) {
        logger.log(Level.FINE, "publishLifecycleEvent: $request")
        if (request?.hasBuildEvent() == true) {
            _eventSubject.onNext(request.buildEvent)
        }

        responseObserver?.onNext(Empty.getDefaultInstance())
        responseObserver?.onCompleted()
    }


    override fun publishBuildToolEventStream(responseObserver: StreamObserver<PublishBuildToolEventStreamResponse>?): StreamObserver<PublishBuildToolEventStreamRequest> {
        logger.log(Level.FINE, "publishBuildToolEventStream: $responseObserver")
        val responses = responseObserver?.toObserver() ?: emptyObserver()
        return PublishEventObserver(responses, _eventSubject).toStreamObserver()
    }

    companion object {
        private val logger = Logger.getLogger(BuildEventSource::class.java.name)
    }

    private class PublishEventObserver(
            private val _responseObserver: Observer<PublishBuildToolEventStreamResponse>,
            private val _eventObserver: Observer<OrderedBuildEvent>)
        : Observer<PublishBuildToolEventStreamRequest> {

        override fun onNext(value: PublishBuildToolEventStreamRequest) {
            logger.log(Level.FINE, "onNext: $value")

            if (value.hasOrderedBuildEvent()) {
                if (value.orderedBuildEvent.event.hasComponentStreamFinished()) {
                    _responseObserver.onCompleted()
                }

                _eventObserver.onNext(value.orderedBuildEvent)
            }
        }

        override fun onError(error: Exception) {
            logger.log(Level.FINE, "onError: $error")
        }

        override fun onCompleted() {
            logger.log(Level.FINE, "onCompleted")
        }

        companion object {
            private val logger = Logger.getLogger(PublishEventObserver::class.java.name)
        }
    }
}