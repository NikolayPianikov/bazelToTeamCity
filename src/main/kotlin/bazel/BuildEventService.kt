/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bazel

import com.google.devtools.build.v1.PublishBuildEventGrpc.PublishBuildEventImplBase
import com.google.devtools.build.v1.PublishBuildToolEventStreamRequest
import com.google.devtools.build.v1.PublishBuildToolEventStreamResponse
import com.google.devtools.build.v1.PublishLifecycleEventRequest
import com.google.protobuf.Empty
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Server that manages startup/shutdown of a `Greeter` server.
 *
 * Note: this file was automatically converted from Java
 */
class BuildEventService {

    private var server: Server? = null

    @Throws(IOException::class)
    private fun start() {
        /* The port on which the server should run */
        val port = 54321
        server = ServerBuilder.forPort(port)
                .addService(PublishBuildEventImpl())
                .build()
                .start()
        logger.log(Level.INFO, "Server started, listening on {0}", port)
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down")
                this@BuildEventService.stop()
                System.err.println("*** server shut down")
            }
        })
    }

    private fun stop() {
        server?.shutdown()
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    @Throws(InterruptedException::class)
    private fun blockUntilShutdown() {
        server?.awaitTermination()
    }

    internal class PublishBuildEventImpl : PublishBuildEventImplBase() {
        override fun publishLifecycleEvent(request: PublishLifecycleEventRequest?, responseObserver: StreamObserver<Empty>?) {
            responseObserver?.onNext(Empty.getDefaultInstance())
            responseObserver?.onCompleted()
        }

        override fun publishBuildToolEventStream(responseObserver: StreamObserver<PublishBuildToolEventStreamResponse>?): StreamObserver<PublishBuildToolEventStreamRequest> {
            return StreamObserverImpl()
        }
    }

    companion object {
        private val logger = Logger.getLogger(BuildEventService::class.java.name)

        /**
         * Main launches the server from the command line.
         */
        @Throws(IOException::class, InterruptedException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val server = BuildEventService()
            server.start()
            server.blockUntilShutdown()
        }
    }
}