package bazel.events

enum class FinishType(private val _id: Int, val description: String) {
    Unspecified(0, "Unknown or unspecified; callers should never set this value."),
    Finished (1, "Set by the event publisher to indicate a build event stream is finished."),
    Expired(2, "Set by the WatchBuild RPC server when the publisher of a build event stream stops publishing events without publishing a BuildComponentStreamFinished event whose type equals FINISHED.")
}