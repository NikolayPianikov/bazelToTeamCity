package bazel.bazel.events

// Payload of the event indicating the completion of a target. The target is
// specified in the id. If the target failed the root causes are provided as
// children events.

data class TargetComplete(
        override val id: Id,
        override val children: List<Id>,
        val label: String,
        val success: Boolean,
        val tags: List<String>,
        val testTimeoutSeconds: Long) : BazelContent