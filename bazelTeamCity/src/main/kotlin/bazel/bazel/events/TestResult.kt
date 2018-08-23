package bazel.bazel.events

// Payload on events reporting about individual test action.

data class TestResult(
        override val id: Id,
        override val children: List<Id>,
        val label: String,
        val run: Int,
        val shard: Int,
        val attempt: Int,
        val status: TestStatus,
        val statusDetails: String,
        val cachedLocally: Boolean,
        val testAttemptStartMillisEpoch: Long,
        val testAttemptDurationMillis: Long,
        val testActionOutput: List<File>,
        val warning: List<String>) : BazelContent