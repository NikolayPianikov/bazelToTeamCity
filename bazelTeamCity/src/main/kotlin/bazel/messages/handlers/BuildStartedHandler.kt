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
        if (ctx.event.payload is BazelEvent && ctx.event.payload.content is BuildStarted) {
            val event = ctx.event.payload.content
            val description = ctx.buildMessage(false)
                    .append("Build started", Verbosity.Minimal)
                    .append(" by command \"${event.command}\"", Verbosity.Normal)
                    .append(" v${event.buildToolVersion}", Verbosity.Detailed)
                    .append(", directory: \"${event.workingDirectory}\"", Verbosity.Detailed)
                    .append(", workspace: \"${event.workspaceDirectory}\"", Verbosity.Detailed)
                    .toString()
            ctx.createBlock(event.command, description, event.children)
            ctx.onNext(ctx.messageFactory.createBuildStatus(ctx.buildMessage().append(description).toString()))
            true
        } else ctx.handlerIterator.next().handle(ctx)
}