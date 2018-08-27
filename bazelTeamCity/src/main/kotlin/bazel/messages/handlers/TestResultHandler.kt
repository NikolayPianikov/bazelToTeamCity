package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.atLeast
import bazel.bazel.events.*
import bazel.messages.Color
import bazel.messages.ServiceMessageContext

class TestResultHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.High

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is BazelEvent && ctx.event.payload.content is TestResult) {
            val event = ctx.event.payload.content
            ctx.onNext(ctx.messageFactory.createMessage(
                    ctx.buildMessage()
                            .append("Test ${event.label} ${event.status} ")
                            .toString()))

            true
        } else ctx.handlerIterator.next().handle(ctx)
}