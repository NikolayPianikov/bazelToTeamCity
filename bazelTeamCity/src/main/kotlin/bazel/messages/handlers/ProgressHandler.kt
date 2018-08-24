package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.bazel.events.BazelEvent
import bazel.bazel.events.BuildStarted
import bazel.bazel.events.Progress
import bazel.messages.ServiceMessageContext

class ProgressHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is BazelEvent && ctx.event.payload.content is Progress) {
            val event = ctx.event.payload.content
            if (!event.stdout.isEmpty()) {
                ctx.onNext(ctx.messageFactory.createMessage(
                        ctx.buildMessage()
                                .append(event.stdout, Verbosity.Normal)
                                .toString()
                ))
            }

            if (!event.stderr.isEmpty()) {
                ctx.onNext(ctx.messageFactory.createErrorMessage(
                        ctx.buildMessage()
                                .append(event.stderr)
                                .toString()
                ))
            }

            true
        } else ctx.handlerIterator.next().handle(ctx)
}