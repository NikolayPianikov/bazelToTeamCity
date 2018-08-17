package bazel.events

enum class BuildComponent(private val _id: Int, val description: String) {
    UnknownComponent(0, "Unknown or unspecified; callers should never set this value."),
    Controller(1, "A component that coordinates builds."),
    Worker(2, "A component that runs executables needed to complete a build."),
    Tool(3, "A component that builds something.")
}