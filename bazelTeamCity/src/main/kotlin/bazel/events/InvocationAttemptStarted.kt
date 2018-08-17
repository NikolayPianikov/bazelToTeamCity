package bazel.events

data class InvocationAttemptStarted(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp,
        val attemptNumber: Long)
    : OrderedBuildEvent