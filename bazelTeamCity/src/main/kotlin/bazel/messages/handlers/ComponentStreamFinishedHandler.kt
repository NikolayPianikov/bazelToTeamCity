package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.atLeast
import bazel.events.ComponentStreamFinished
import bazel.events.FinishType
import bazel.messages.ServiceMessageContext

class ComponentStreamFinishedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event.payload is ComponentStreamFinished) {
            when(ctx.event.payload.finishType) {
                FinishType.Finished ->
                    if (ctx.verbosity.atLeast(Verbosity.Detailed)) {
                        ctx.onNext(ctx.messageFactory.createMessage(
                                ctx.buildMessage()
                                    .append("Component \"${ctx.event.payload.streamId.component}\" stream finished, invocation: \"${ctx.event.payload.streamId.invocationId}\", build: \"${ctx.event.payload.streamId.buildId}\"")
                                    .toString()))
                    }

                FinishType.Expired ->
                    if (ctx.verbosity.atLeast(Verbosity.Minimal)) {
                        ctx.onNext(ctx.messageFactory.createWarningMessage(
                                ctx.buildMessage()
                                    .append("Component \"${ctx.event.payload.streamId.component}\" stream expired")
                                    .append("(${FinishType.Expired.description}), invocation: \"${ctx.event.payload.streamId.invocationId}\", build: \"${ctx.event.payload.streamId.buildId}\"", Verbosity.Detailed)
                                    .toString()))
                    }
            }

            true
        } else ctx.handlerIterator.next().handle(ctx)
}