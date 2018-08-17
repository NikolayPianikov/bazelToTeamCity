package bazel.events

data class BuildEnqueued(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp)
    : OrderedBuildEvent