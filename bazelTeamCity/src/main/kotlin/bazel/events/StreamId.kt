package bazel.events

data class StreamId(
        val buildId: String,
        val invocationId: String,
        val component: BuildComponent) {
    companion object {
        val default = StreamId("", "", BuildComponent.UnknownComponent)
    }
}