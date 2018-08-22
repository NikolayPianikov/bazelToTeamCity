package bazel.bazel

interface BazelContent {
    val id: Id
    val children: List<Id>
}