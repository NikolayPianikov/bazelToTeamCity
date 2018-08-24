package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.events.BuildFinished
import bazel.events.BuildStatus
import bazel.messages.ServiceMessageContext

class BuildFinishedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is BuildFinished) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (ctx.event.payload.result.status) {
                BuildStatus.CommandSucceeded ->
                    ctx.onNext(ctx.messageFactory.createBuildStatus(
                            ctx.buildMessage()
                            .append("Build finished")
                            .toString()))

                BuildStatus.Cancelled ->
                    ctx.onNext(ctx.messageFactory.createBuildProblem(
                            ctx.buildMessage()
                            .append("Build canceled")
                            .append(" - ${ctx.event.payload.result.status.description}", Verbosity.Detailed)
                            .toString(),
                            ctx.event.projectId,
                            "${ctx.event.payload.result.status}"))

                BuildStatus.CommandFailed,
                BuildStatus.SystemError,
                BuildStatus.UserError,
                BuildStatus.ResourceExhausted,
                BuildStatus.InvocationDeadlineExceeded,
                BuildStatus.RequestDeadlineExceeded ->
                    ctx.onNext(ctx.messageFactory.createBuildProblem(
                            ctx.buildMessage()
                            .append("Build failed")
                            .append(" - {ctx.event.result.status.description}", Verbosity.Detailed)
                            .toString(),
                            ctx.event.projectId,
                                "Build:${ctx.event.payload.result.status}"))
            }
            true
        } else ctx.handlerIterator.next().handle(ctx)
}