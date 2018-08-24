package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.atLeast
import bazel.messages.ServiceMessageContext

class UnknownEventHandler: EventHandler {
    override val priority: HandlerPriority get() = HandlerPriority.Last

    override fun handle(ctx: ServiceMessageContext): Boolean {
        if(ctx.verbosity.atLeast(Verbosity.Normal)) {
            ctx.onNext(ctx.messageFactory.createWarningMessage(ctx.buildMessage().append("Unknown event: ${ctx.event}").toString()))
        }

        return false
    }
}