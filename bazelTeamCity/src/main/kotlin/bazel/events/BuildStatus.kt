package bazel.events

enum class BuildStatus(private val _id: Int, val description: String) {
    Unknown(0, "Unspecified or unknown"),
    CommandSucceeded(1, "Build was successful and tests (if requested) all pass."),
    CommandFailed(2, "Build error and/or test failure."),
    UserError(3, "Unable to obtain a result due to input provided by the user."),
    SystemError(4, "Unable to obtain a result due to a failure within the build system."),
    ResourceExhausted(5, "Build required too many resources, such as build tool RAM."),
    InvocationDeadlineExceeded(6, "An invocation attempt time exceeded its deadline."),
    RequestDeadlineExceeded(8, "Build request time exceeded the request_deadline."),
    Cancelled(7, "The build was cancelled by a call to CancelBuild.")
}