package bazel.bazel.events

// Payload of the event summarizing a test.

data class TestSummary(
        override val id: Id,
        override val children: List<Id>,
        val label: String,
        val overallStatus: TestStatus,
        val totalRunCount: Int,
        val passed: MutableList<File>,
        val failed: MutableList<File>,
        val totalNumCached: Int) : BazelContent