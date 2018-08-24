package bazel

import bazel.messages.MessageFactoryImpl
import bazel.messages.ServiceMessageRootSubject
import bazel.messages.StreamSubject
import bazel.messages.ControllerSubject
import bazel.v1.BuildEventConverter
import bazel.v1.BuildEventSource
import bazel.v1.converters.BuildComponentConverter
import bazel.v1.converters.StreamIdConverter
import devteam.rx.use
import java.io.IOException

@Throws(IOException::class, InterruptedException::class)
fun main(args: Array<String>) {
    val verbosity = Verbosity.Detailed
    val messageFactory = MessageFactoryImpl()
    val buildEventConverter = BuildEventConverter(StreamIdConverter(BuildComponentConverter()))
    val serviceMessageRootSubject = ServiceMessageRootSubject(ControllerSubject(verbosity, messageFactory) { StreamSubject(verbosity, messageFactory) })
    val buildEventSource = BuildEventSource()
    GRpcServer(54321, buildEventSource, buildEventSource, buildEventConverter, serviceMessageRootSubject).use { }
}