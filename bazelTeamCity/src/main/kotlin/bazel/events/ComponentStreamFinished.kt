package bazel.events

data class ComponentStreamFinished(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp,
        val finishType: FinishType)
    : OrderedBuildEvent