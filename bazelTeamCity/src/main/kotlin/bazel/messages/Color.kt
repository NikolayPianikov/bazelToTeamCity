package bazel.messages

enum class Color(color: String) {
    Default(""),
    BuildStage("36;1"),
    Success("32;1"),
    Warning("33;1"),
    WarningSummary("33;1"),
    Error("31;1"),
    ErrorSummary("31;1"),
    Details("34"),
    Task("36"),
    SummaryHeader("32;1"),
    SummaryInfo("36"),
    PerformanceHeader("32;1"),
    PerformanceCounterInfo("35"),
    Items("32;1")
}