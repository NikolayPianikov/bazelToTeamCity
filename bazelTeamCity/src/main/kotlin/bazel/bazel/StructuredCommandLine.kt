package bazel.bazel

data class StructuredCommandLine(
        override val id: Id,
        override val children: List<Id>,
        val commandLineLabel: String) : BazelContent