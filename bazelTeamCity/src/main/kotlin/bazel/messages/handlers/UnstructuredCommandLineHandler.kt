package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.atLeast
import bazel.bazel.events.BazelEvent
import bazel.bazel.events.BuildFinished
import bazel.bazel.events.BuildStarted
import bazel.bazel.events.UnstructuredCommandLine
import bazel.messages.ServiceMessageContext

class UnstructuredCommandLineHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Medium

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is BazelEvent && ctx.event.payload.content is UnstructuredCommandLine) {
            val commandLine = ctx.event.payload.content
            if (ctx.verbosity.atLeast(Verbosity.Normal)) {
                ctx.onNext(ctx.messageFactory.createMessage(
                        ctx.buildMessage()
                                .append("Run command line: " + commandLine.args.joinToString(" ") { "\"$it\"" })
                                .toString())) }

            true
        } else ctx.handlerIterator.next().handle(ctx)
}