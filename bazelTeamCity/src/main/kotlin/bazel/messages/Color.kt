package bazel.messages

enum class Color(val color: String) {
    Default(""),
    BuildStage("36"),
    Success("32;1"),
    Warning("35"),
    Error("31;1"),
    Details("1;30"),
    Items("1;30"),
    Trace("34")
}

fun String.apply(color: Color): String {
    if (color == Color.Default) {
        return this
    }

    val sb = StringBuilder()
    sb.append("\u001B[")
    sb.append(color.color)
    sb.append('m')
    sb.append(this)
    sb.append("\u001B[0m")
    return sb.toString()
}