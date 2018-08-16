package bazel

import com.google.devtools.build.v1.PublishBuildToolEventStreamRequest
import io.grpc.stub.StreamObserver

class StreamObserverImpl: StreamObserver<PublishBuildToolEventStreamRequest> {
    override fun onNext(value: PublishBuildToolEventStreamRequest?) {
    }

    override fun onError(t: Throwable?) {
    }

    override fun onCompleted() {
    }
}