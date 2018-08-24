package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.bazel.events.BazelEvent
import bazel.bazel.events.BuildStarted
import bazel.messages.ServiceMessageContext

class BuildStartedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event is BazelEvent && ctx.event.content is BuildStarted) {
            ctx.onNext(
                    ctx.messageFactory.createMessage(
                            ctx.buildMessage()
                                    .append("Build started", Verbosity.Minimal)
                                    .append(" by command \"${ctx.event.content.command}\"", Verbosity.Normal)
                                    .append(" v${ctx.event.content.buildToolVersion}", Verbosity.Detailed)
                                    .append(", directory: \"${ctx.event.content.workingDirectory}\"", Verbosity.Detailed)
                                    .append(", workspace: \"${ctx.event.content.workspaceDirectory}\"", Verbosity.Detailed)
                                    .toString()))
            true
        } else ctx.handlerIterator.next().handle(ctx)
}