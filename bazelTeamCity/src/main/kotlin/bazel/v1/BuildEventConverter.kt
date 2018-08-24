package bazel.v1

import bazel.Converter
import bazel.bazel.converters.BazelEventConverter
import bazel.events.OrderedBuildEvent
import bazel.events.StreamId
import bazel.events.Timestamp
import bazel.events.UnknownEvent
import bazel.v1.converters.BuildStatusConverter
import bazel.v1.converters.ConsoleOutputStreamConverter
import bazel.v1.converters.FinishTypeConverter
import bazel.v1.handlers.*
import java.util.logging.Level
import java.util.logging.Logger

class BuildEventConverter(
        private val _streamIdConverter: Converter<com.google.devtools.build.v1.StreamId, StreamId>)
    : Converter<com.google.devtools.build.v1.OrderedBuildEvent, OrderedBuildEvent> {
    override fun convert(source: com.google.devtools.build.v1.OrderedBuildEvent): OrderedBuildEvent {
        val streamId = if (source.hasStreamId()) _streamIdConverter.convert(source.streamId) else StreamId.default
        if (source.hasEvent()) {
            val event = source.event
            val sequenceNumber = source.sequenceNumber
            val eventTime = Timestamp(event.eventTime.seconds, event.eventTime.nanos)
            val handlersIterator = handlers.iterator()
            return handlersIterator.next().handle(HandlerContext(handlersIterator, streamId, sequenceNumber, eventTime, event))
        }

        logger.log(Level.SEVERE, "Unknown event: $source")
        return UnknownEvent(streamId)
    }

    companion object {
        private val logger = Logger.getLogger(BuildEventConverter::class.java.name)
        private val handlers = sequenceOf(
                // An invocation attempt has started.
                // invocation_attempt_started = 51
                InvocationAttemptStartedHandler(),

                // An invocation attempt has finished.
                // invocation_attempt_finished = 52
                InvocationAttemptFinishedHandler(BuildStatusConverter()),

                // The build is enqueued (just inserted to the build queue or put back
                // into the build queue due to a previous build failure).
                // build_enqueued = 53
                BuildEnqueuedHandler(),

                // The build has finished. Set when the build is terminated.
                // build_finished = 55
                BuildFinishedHandler(BuildStatusConverter()),

                // An event containing printed text.
                // console_output = 56
                ConsoleOutputHandler(ConsoleOutputStreamConverter()),

                // Indicates the end of a build event stream (with the same StreamId) from
                // a build component executing the requested build task.
                // *** This field does not indicate the WatchBuild RPC is finished. ***
                // component_stream_finished = 59
                ComponentStreamFinishedHandler(FinishTypeConverter()),

                // Structured build event generated by Bazel about its execution progress.
                // bazel_event = 60
                BazelEventHandler(BazelEventConverter()),

                // An event that contains supplemental tool-specific information about
                // build execution.
                // build_execution_event = 61

                // An event that contains supplemental tool-specific information about
                // source fetching.
                // source_fetch_event = 62

                // Unknown event.
                UnknownEventHandler()
        ).sortedBy { it.priority }.toList()
    }
}