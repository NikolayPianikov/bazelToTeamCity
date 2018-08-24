package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.events.BuildFinished
import bazel.events.BuildStatus
import bazel.messages.ServiceMessageContext

class BuildFinishedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event is BuildFinished) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (ctx.event.result.status) {
                BuildStatus.CommandSucceeded -> ctx.onNext(
                        ctx.messageFactory.createMessage(
                                ctx.buildMessage()
                                        .append("Build finished")
                                        .toString()))
                BuildStatus.Cancelled -> ctx.onNext(
                        ctx.messageFactory.createWarningMessage(
                                ctx.buildMessage()
                                        .append("Build canceled")
                                        .toString()))
                BuildStatus.CommandFailed,
                BuildStatus.SystemError,
                BuildStatus.UserError,
                BuildStatus.ResourceExhausted,
                BuildStatus.InvocationDeadlineExceeded,
                BuildStatus.RequestDeadlineExceeded
                    -> ctx.onNext(ctx.messageFactory.createErrorMessage(
                        ctx.buildMessage()
                                .append("Build failed")
                                .toString(), ctx.event.result.status.description))
            }
            true
        } else ctx.handlerIterator.next().handle(ctx)
}