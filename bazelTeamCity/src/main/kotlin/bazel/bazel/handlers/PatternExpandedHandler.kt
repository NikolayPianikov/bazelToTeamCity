package bazel.bazel.handlers

import bazel.HandlerPriority
import bazel.bazel.events.PatternExpanded

class PatternExpandedHandler: BazelHandler {
    override val priority = HandlerPriority.Medium

    override fun handle(ctx: HandlerContext) =
            if (ctx.event.hasExpanded()) {
                PatternExpanded(
                        ctx.id,
                        ctx.children)
            } else ctx.handlerIterator.next().handle(ctx)
}