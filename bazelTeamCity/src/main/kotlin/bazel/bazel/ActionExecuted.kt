package bazel.bazel

// Payload of the event indicating the completion of an action. The main purpose
// of posting those events is to provide details on the root cause for a target
// failing; however, consumers of the build-event protocol must not assume
// that only failed actions are posted.

data class ActionExecuted(
        override val id: Id,
        override val children: List<Id>,
        val type: String,
        val cmdLines: List<String>,
        val success: Boolean,
        val primaryOutput: File,
        val stdout: File,
        val stderr: File,
        val exitCode: Int): BazelContent