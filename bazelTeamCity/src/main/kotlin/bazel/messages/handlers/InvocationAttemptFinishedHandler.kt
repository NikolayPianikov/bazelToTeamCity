package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.atLeast
import bazel.events.BuildStatus
import bazel.events.InvocationAttemptFinished
import bazel.messages.ServiceMessageContext

class InvocationAttemptFinishedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is InvocationAttemptFinished) {
            ctx.onNext(ctx.messageFactory.createFlowFinished(ctx.event.payload.streamId.invocationId))
            if (ctx.event.payload.invocationResult.status == BuildStatus.CommandSucceeded) {
                if (ctx.verbosity.atLeast(Verbosity.Minimal)) {
                    ctx.onNext(ctx.messageFactory.createMessage(
                            ctx.buildMessage()
                                    .append("Invocation attempt finished")
                                    .append(", exit code: ${ctx.event.payload.exitCode}", Verbosity.Detailed)
                                    .toString()))
                }
            }
            else {
                ctx.onNext(ctx.messageFactory.createBuildProblem(
                        ctx.buildMessage()
                                .append("Invocation attempt failed: ")
                                .append(" \"${ctx.event.payload.invocationResult.status.description}\"", Verbosity.Normal)
                                .append(", exit code: ${ctx.event.payload.exitCode}", Verbosity.Detailed)
                                .toString(),
                        ctx.event.projectId,
                        "Invocation:${ctx.event.payload.invocationResult.status}:${ctx.event.payload.exitCode}"))
            }

            true
        } else ctx.handlerIterator.next().handle(ctx)
}