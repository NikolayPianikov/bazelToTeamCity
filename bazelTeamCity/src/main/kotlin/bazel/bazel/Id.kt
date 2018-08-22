package bazel.bazel

data class Id(private val _id: Any) {
    companion object {
        val default = Id(Unit)
    }
}