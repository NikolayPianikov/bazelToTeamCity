package bazel.bazel

// Payload of the event indicating that the configurations for a target have
// been identified. As with pattern expansion the main information is in the
// chaining part: the id will contain the target that was configured and the
// children id will contain the configured targets it was configured to.

data class TargetConfigured(
        override val id: Id,
        override val children: List<Id>,
        val label: String,
        val aspect: String,
        val targetKind: String,
        val testSize: TestSize,
        val tags: List<String>) : BazelContent