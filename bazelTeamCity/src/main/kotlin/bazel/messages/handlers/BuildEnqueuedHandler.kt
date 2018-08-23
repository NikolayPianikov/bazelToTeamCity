package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.events.BuildEnqueued
import bazel.messages.ServiceMessageContext
import jetbrains.buildServer.messages.serviceMessages.Message

class BuildEnqueuedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event is BuildEnqueued) {
            ctx.onNext(ctx.messageFactory.createMessage("Build enqueued"))
        }
        else ctx.handlerIterator.next().handle(ctx)
}