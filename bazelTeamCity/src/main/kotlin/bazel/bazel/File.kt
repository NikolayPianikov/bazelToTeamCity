package bazel.bazel

data class File(val name: String, val uri: String) {
    constructor(name: String, content: ByteArray) : this(name, "")

    companion object {
        val empty = File("", "")
    }
}