package bazel.v1

import bazel.Converter
import bazel.events.*

class BuildEventConverter: Converter<com.google.devtools.build.v1.OrderedBuildEvent, OrderedBuildEvent> {
    override fun convert(source: com.google.devtools.build.v1.OrderedBuildEvent): OrderedBuildEvent {
        val streamId = if (source.hasStreamId()) convert(source.streamId) else StreamId.default
        if (source.hasEvent()) {
            val event = source.event
            val sequenceNumber = source.sequenceNumber
            val eventTime = Timestamp(event.eventTime.seconds, event.eventTime.nanos)

            if (event.hasBazelEvent()) {
                val bazelEvent = event.bazelEvent
                val typeUrl = bazelEvent.typeUrl
                val value = bazelEvent.value
                return BazelEvent(
                        streamId,
                        sequenceNumber,
                        eventTime)
            }

            if (event.hasBuildEnqueued()) {
                return BuildEnqueued(
                        streamId,
                        sequenceNumber,
                        eventTime)
            }

            if (event.hasInvocationAttemptStarted()) {
                return InvocationAttemptStarted(
                        streamId,
                        sequenceNumber,
                        eventTime,
                        event.invocationAttemptStarted.attemptNumber)
            }

            if (event.hasInvocationAttemptFinished()) {
                return InvocationAttemptFinished(
                        streamId,
                        sequenceNumber,
                        eventTime,
                        if (event.invocationAttemptFinished.hasInvocationStatus()) convert(event.invocationAttemptFinished.invocationStatus) else Result.default,
                        if (event.invocationAttemptFinished.hasExitCode()) event.invocationAttemptFinished.exitCode.value else 0)
            }

            if (event.hasConsoleOutput()) {
                return ConsoleOutput(
                        streamId,
                        sequenceNumber,
                        eventTime,
                        convert(event.consoleOutput.type),
                        event.consoleOutput.textOutput)
            }

            if (event.hasComponentStreamFinished()) {
                return ComponentStreamFinished(
                        streamId,
                        sequenceNumber,
                        eventTime,
                        convert(event.componentStreamFinished.type))
            }

            if (event.hasBuildFinished()) {
                return BuildFinished(
                        streamId,
                        sequenceNumber,
                        eventTime,
                        if (event.buildFinished.hasStatus()) convert(event.buildFinished.status) else Result.default)
            }
        }

        return UnknownEvent(streamId)
    }

    private fun convert(finishType: com.google.devtools.build.v1.BuildEvent.BuildComponentStreamFinished.FinishType) =
            when(finishType.number) {
                1 -> FinishType.Finished
                2 -> FinishType.Expired
                else -> FinishType.Unspecified
            }

    private fun convert(consoleOutputStream: com.google.devtools.build.v1.ConsoleOutputStream) =
        when(consoleOutputStream.number) {
            1 -> ConsoleOutputStream.Stdout
            2 -> ConsoleOutputStream.Stderr
            else -> ConsoleOutputStream.Unknown
        }

    private fun convert(buildStatus: com.google.devtools.build.v1.BuildStatus) =
        Result(
                when (buildStatus.resultValue) {
                    1 -> BuildStatus.CommandSucceeded
                    2 -> BuildStatus.CommandFailed
                    3 -> BuildStatus.UserError
                    4 -> BuildStatus.SystemError
                    5 -> BuildStatus.ResourceExhausted
                    6 -> BuildStatus.InvocationDeadlineExceeded
                    8 -> BuildStatus.RequestDeadlineExceeded
                    7 -> BuildStatus.Cancelled
                    else -> BuildStatus.Unknown
                }
        )

    private fun convert(originalStreamId: com.google.devtools.build.v1.StreamId) =
            StreamId(
                    originalStreamId.buildId,
                    originalStreamId.invocationId,
                    convert(originalStreamId.component))

    private fun convert(originalBuildComponent: com.google.devtools.build.v1.StreamId.BuildComponent) =
            when (originalBuildComponent.number) {
                1 -> BuildComponent.Controller
                2 -> BuildComponent.Worker
                3 -> BuildComponent.Tool
                else -> BuildComponent.UnknownComponent
            }
}