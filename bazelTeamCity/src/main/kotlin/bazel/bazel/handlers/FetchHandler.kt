package bazel.bazel.handlers

import bazel.HandlerPriority
import bazel.bazel.events.Fetch

class FetchHandler: BazelHandler {
    override val priority = HandlerPriority.Medium

    override fun handle(ctx: HandlerContext) =
            if (ctx.event.hasFetch()) {
                val content = ctx.event.fetch
                Fetch(
                        ctx.id,
                        ctx.children,
                        content.success)
            } else ctx.handlerIterator.next().handle(ctx)
}