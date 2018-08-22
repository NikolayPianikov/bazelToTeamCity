package bazel.bazel

enum class TestStatus(private val _id: Int) {
    NoStatus(0),
    Passed(1),
    Flaky(2),
    Timeout(3),
    Failed(4),
    Incomplete(5),
    RemoteFailure(6),
    FailedToBuild(7),
    ToolHaltedBeforeTesting(8)
}