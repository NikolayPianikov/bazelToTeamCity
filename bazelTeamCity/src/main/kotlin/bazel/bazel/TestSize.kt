package bazel.bazel

enum class TestSize(private val _id: Int) {
    Unknown(0),
    Small(1),
    Medium(2),
    Large(3),
    Enormous(4),
}