package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.events.BuildEnqueued
import bazel.messages.ServiceMessageContext
import jetbrains.buildServer.messages.serviceMessages.Message

class UnknownEventHandler: EventHandler {
    override val priority: HandlerPriority get() = HandlerPriority.Last

    override fun handle(ctx: ServiceMessageContext) = ctx.onNext(Message(ctx.event.toString(), "NORMAL", null))
}