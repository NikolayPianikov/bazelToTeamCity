package bazel.bazel.events

enum class AbortReason(private val _id: Int, val description: String) {
    Unknown(0, "Unknown reason."),
    UserInterrupted(1, "The user requested the build to be aborted (e.g., by hitting Ctl-C)."),
    NoAnalyze(8, "The user requested that no analysis be performed."),
    NoBuild(9, "The user requested that no build be carried out."),
    Timeout(2, "The build or target was aborted as a timeout was exceeded."),
    RemoteEnvironmentFailure(3, "The build or target was aborted as some remote environment (e.g., for remote execution of actions) was not available in the expected way."),
    Internal(4, "Failure due to reasons entirely internal to the build tool, e.g., running out of memory."),
    LoadingFailure (5, "A Failure occurred in the loading phase of a target."),
    AnalysisFailure(6, "A Failure occurred in the analysis phase of a target."),
    Skipped(7, "Target build was skipped (e.g. due to incompatible CPU constraints).")
}