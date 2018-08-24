package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.events.InvocationAttemptFinished
import bazel.messages.ServiceMessageContext

class InvocationAttemptFinishedHandler: EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Low

    override fun handle(ctx: ServiceMessageContext) =
        if (ctx.event is InvocationAttemptFinished) {
            ctx.onNext(ctx.messageFactory.createFlowFinished(ctx.event.streamId.invocationId))
            //ctx.onNext(ctx.messageFactory.createMessage(ctx.event.))
            true
        } else ctx.handlerIterator.next().handle(ctx)
}