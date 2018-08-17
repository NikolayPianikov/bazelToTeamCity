package bazel

import bazel.events.BuildFinished
import bazel.events.OrderedBuildEvent
import devteam.rx.*
import io.grpc.Attributes
import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerTransportFilter
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Level
import java.util.logging.Logger

class GRpcServer<TProtoEvent>(
        port: Int,
        service: io.grpc.BindableService,
        eventSource: Observable<TProtoEvent>,
        buildEventConverter: Converter<TProtoEvent, OrderedBuildEvent>,
        serviceMessageConverter: Converter<OrderedBuildEvent, ServiceMessage>)
    : ServerTransportFilter(), Disposable {

    private val server: Server =
            ServerBuilder.forPort(port)
            .addService(service)
            .addTransportFilter(this)
            .build()
            .start()

    private val _subscription: Disposable = eventSource
            .map { buildEventConverter.convert(it) }
            .map { Pair(it, serviceMessageConverter.convert(it)) }
            .subscribe(
                {
                    System.out.println(it.second)
                    if (it.first is BuildFinished) {
                        shutdown()
                    }
                },
                {
                    logger.log(Level.SEVERE, "Error", it)
                    shutdown()
                },
                {
                    shutdown()
                }
            )

    private val _connectionCounter = AtomicInteger()

    init {
        logger.log(Level.INFO, "Server started, listening on {0}", port)
    }

    override fun dispose() {
        server.awaitTermination()
        logger.log(Level.INFO, "Server shut down")
        _subscription.dispose()
    }

    override fun transportReady(transportAttrs: Attributes?): Attributes {
        connectionCounterChanged(_connectionCounter.incrementAndGet())
        return super.transportReady(transportAttrs)
    }

    override fun transportTerminated(transportAttrs: Attributes?) {
        super.transportTerminated(transportAttrs)
        connectionCounterChanged(_connectionCounter.decrementAndGet())
    }

    private fun connectionCounterChanged(connectionCounter: Int) {
        logger.log(Level.INFO, "Connections: {0}", connectionCounter)
        if (connectionCounter == 0)
        {
            shutdown()
        }
    }

    private fun shutdown() {
        val shutdownTread = object : Thread() {
            override fun run() {
                logger.log(Level.INFO, "Server shutting down")
                server.shutdownNow()
            }
        }

        shutdownTread.start()
        shutdownTread.join()
    }

    companion object {
        private val logger = Logger.getLogger(GRpcServer::class.java.name)
    }
}