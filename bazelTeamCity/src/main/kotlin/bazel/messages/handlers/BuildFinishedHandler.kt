package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.events.BuildEnqueued
import bazel.events.BuildFinished
import bazel.events.BuildStatus
import bazel.messages.Color
import bazel.messages.ServiceMessageContext
import jetbrains.buildServer.messages.serviceMessages.Message

class BuildFinishedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event is BuildFinished) {
            when (ctx.event.result.status) {
                BuildStatus.CommandSucceeded -> ctx.onNext(ctx.messageFactory.createMessage("Build finished"))
                BuildStatus.Cancelled -> ctx.onNext(ctx.messageFactory.createWarningMessage("Build cancelled"))
                BuildStatus.CommandFailed,
                BuildStatus.SystemError,
                BuildStatus.UserError,
                BuildStatus.ResourceExhausted,
                BuildStatus.InvocationDeadlineExceeded,
                BuildStatus.RequestDeadlineExceeded
                    -> ctx.onNext(ctx.messageFactory.createErrorMessage("Build failed", ctx.event.result.status.description))
                else -> Unit
            }
        }

        else ctx.handlerIterator.next().handle(ctx)
}