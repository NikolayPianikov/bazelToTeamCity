package bazel

enum class HandlerPriority(private val _id: Int) {
    High(0),

    Medium(1),

    Low(2),

    Last(Int.MAX_VALUE)
}