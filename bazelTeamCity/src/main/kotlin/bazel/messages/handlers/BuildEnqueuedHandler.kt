package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.events.BuildEnqueued
import bazel.messages.ServiceMessageContext

class BuildEnqueuedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event is BuildEnqueued) {
            ctx.onNext(ctx.messageFactory.createMessage(
                    ctx.buildMessage()
                            .append("Build enqueued")
                            .append(" ${ctx.event}", Verbosity.Normal)
                            .toString()))
            true
        } else ctx.handlerIterator.next().handle(ctx)
}