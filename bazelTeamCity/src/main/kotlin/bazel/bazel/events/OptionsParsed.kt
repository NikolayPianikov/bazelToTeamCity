package bazel.bazel.events

data class OptionsParsed(
        override val id: Id,
        override val children: List<Id>,
        val cmdLines: List<String>,
        val explicitCmdLines: List<String>,
        val startupOptions: List<String>,
        val explicitCmdLines1: List<String>) : BazelContent