package bazel.bazel.events

import bazel.events.OrderedBuildEvent
import bazel.events.StreamId
import bazel.events.Timestamp

data class BazelEvent(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp,
        val content: BazelContent)
    : OrderedBuildEvent