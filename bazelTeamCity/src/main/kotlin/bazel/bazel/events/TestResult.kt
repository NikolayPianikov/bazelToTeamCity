package bazel.bazel.events

// Payload on events reporting about individual test action.

data class TestResult(
        override val id: Id,
        override val children: List<Id>,
        val label: String,
        val run: Int,
        val shard: Int,
        val attempt: Int,
        // The status of this test.
        val status: TestStatus,
        // Additional details about the status of the test. This is intended for
        // user display and must not be parsed.
        val statusDetails: String,
        val cachedLocally: Boolean,
        // Time in milliseconds since the epoch at which the test attempt was started.
        // Note: for cached test results, this is time can be before the start of the
        // build.
        val testAttemptStartMillisEpoch: Long,
        // Time the test took to run. For locally cached results, this is the time
        // the cached invocation took when it was invoked.
        val testAttemptDurationMillis: Long,
        // Files (logs, test.xml, undeclared outputs, etc) generated by that test
        // action.
        val testActionOutput: List<File>,
        // Warnings generated by that test action.
        val warning: List<String>) : BazelContent