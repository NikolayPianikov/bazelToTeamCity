package bazel

import bazel.v1.BuildEventConverter
import bazel.v1.BuildEventSource
import devteam.rx.use
import java.io.IOException

@Throws(IOException::class, InterruptedException::class)
fun main(args: Array<String>) {
    val buildEventConverter = BuildEventConverter(BazelEventConverter())
    val serviceMessageConverter =  ServiceMessageConverter()
    val buildEventSource = BuildEventSource()
    GRpcServer(54321, buildEventSource, buildEventSource, buildEventConverter, serviceMessageConverter).use { }
}