package bazel.events

enum class ConsoleOutputStream(private val _id: Int, val description: String) {
    Unknown(0, "Unspecified or unknown."),
    Stdout(1, "Normal output stream."),
    Stderr(2, "Error output stream.")
}