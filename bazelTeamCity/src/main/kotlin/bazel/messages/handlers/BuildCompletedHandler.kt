package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.bazel.events.BazelEvent
import bazel.bazel.events.BuildFinished
import bazel.bazel.events.BuildStarted
import bazel.messages.ServiceMessageContext

class BuildCompletedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is BazelEvent && ctx.event.payload.content is BuildFinished) {
            val event = ctx.event.payload.content
            ctx.createBlock("result", "", event.children)

            val exitCode = event.exitCode
            if (exitCode == 0) {
                ctx.onNext(ctx.messageFactory.createMessage(
                        ctx.buildMessage()
                            .append("Build finished", Verbosity.Minimal)
                            .append(", exit code: ${event.exitCode}", Verbosity.Normal)
                            .append("(${event.exitCodeName})", Verbosity.Detailed)
                            .toString()))
            } else {
                ctx.onNext(ctx.messageFactory.createErrorMessage(
                        ctx.buildMessage()
                        .append("Build failed", Verbosity.Minimal)
                        .append(", exit code: ${event.exitCode}", Verbosity.Normal)
                        .append("(${event.exitCodeName})", Verbosity.Detailed)
                        .toString()))
            }

            true
        } else ctx.handlerIterator.next().handle(ctx)
}