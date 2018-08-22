package bazel.bazel

data class BuildToolLogs(
        override val id: Id,
        override val children: List<Id>,
        val logs: MutableList<File>) : BazelContent