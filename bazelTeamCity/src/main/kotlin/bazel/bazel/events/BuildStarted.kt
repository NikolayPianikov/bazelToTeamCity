package bazel.bazel.events

// Payload of an event indicating the beginning of a new build. Usually, events
// of those type start a new build-event stream. The target pattern requested
// to be build is contained in one of the announced child events; it is an
// invariant that precisely one of the announced child events has a non-empty
// target pattern.

data class BuildStarted(
        override val id: Id,
        override val children: List<Id>,
        val buildToolVersion: String,
        val command: String,
        val workingDirectory: String,
        val workspaceDirectory: String) : BazelContent