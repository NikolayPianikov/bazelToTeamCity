package bazel.events

data class InvocationAttemptFinished(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp,
        val invocationResult: Result,
        val exitCode: Int)
    : OrderedBuildEvent