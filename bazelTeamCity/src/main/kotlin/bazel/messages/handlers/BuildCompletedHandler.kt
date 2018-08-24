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
        if (ctx.event is BazelEvent && ctx.event.content is BuildFinished) {
            ctx.onNext(
                    ctx.messageFactory.createMessage(
                            ctx.buildMessage()
                                    .append("Build finished", Verbosity.Minimal)
                                    .append(", exit code: ${ctx.event.content.exitCode}", Verbosity.Normal)
                                    .append(" - ${ctx.event.content.exitCodeName}", Verbosity.Detailed)
                                    .toString()))
            true
        } else ctx.handlerIterator.next().handle(ctx)
}