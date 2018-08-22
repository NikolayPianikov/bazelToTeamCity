package bazel.bazel

// Payload of an event summarizing the progress of the build so far. Those
// events are also used to be parents of events where the more logical parent
// event cannot be posted yet as the needed information is not yet complete.

data class Progress(
        override val id: Id,
        override val children: List<Id>,
        val stdout: String,
        val stderr: String) : BazelContent