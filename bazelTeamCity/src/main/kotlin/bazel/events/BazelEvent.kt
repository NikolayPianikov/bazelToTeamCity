package bazel.events

data class BazelEvent(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp)
    : OrderedBuildEvent