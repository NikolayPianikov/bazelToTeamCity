package bazel.bazel.events

// Event indicating the end of a build.

data class BuildFinished(
        override val id: Id,
        override val children: List<Id>,
        val exitCode: Int,
        val exitCodeName: String) : BazelContent