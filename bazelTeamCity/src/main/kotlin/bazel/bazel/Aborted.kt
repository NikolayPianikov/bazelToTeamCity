package bazel.bazel

// Payload of an event indicating that an expected event will not come, as
// the build is aborted prematurely for some reason.
data class Aborted(
        override val id: Id,
        override val children: List<Id>,
        val description: String,
        val reason: AbortReason) : BazelContent