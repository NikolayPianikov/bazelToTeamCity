package bazel.events

data class ConsoleOutput(
        override val streamId: StreamId,
        override val sequenceNumber: Long,
        override val eventTime: Timestamp,
        val consoleOutputStream: ConsoleOutputStream,
        val text: String)
    : OrderedBuildEvent