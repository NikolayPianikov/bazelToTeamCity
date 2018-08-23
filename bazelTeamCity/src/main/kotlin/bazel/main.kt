package bazel

import bazel.bazel.converters.BazelEventConverter
import bazel.messages.MessageFactoryImpl
import bazel.messages.ServiceMessageRootSubject
import bazel.messages.StreamSubject
import bazel.messages.StreamsSubject
import bazel.v1.BuildEventConverter
import bazel.v1.BuildEventSource
import bazel.v1.converters.BuildComponentConverter
import bazel.v1.converters.StreamIdConverter
import devteam.rx.use
import java.io.IOException

@Throws(IOException::class, InterruptedException::class)
fun main(args: Array<String>) {
    val buildEventConverter = BuildEventConverter(StreamIdConverter(BuildComponentConverter()), BazelEventConverter())
    val serviceMessageRootSubject = ServiceMessageRootSubject(StreamsSubject { StreamSubject(MessageFactoryImpl()) })
    val buildEventSource = BuildEventSource()
    GRpcServer(54321, buildEventSource, buildEventSource, buildEventConverter, serviceMessageRootSubject).use { }
}