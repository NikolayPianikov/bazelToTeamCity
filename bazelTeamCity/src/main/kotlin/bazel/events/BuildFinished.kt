package bazel.events

data class BuildFinished(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp,
        val result: Result)
    : OrderedBuildEvent