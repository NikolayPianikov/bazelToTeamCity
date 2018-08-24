package bazel.messages

import bazel.Event
import bazel.Verbosity
import bazel.bazel.events.Id
import bazel.events.OrderedBuildEvent
import bazel.messages.handlers.EventHandler
import devteam.rx.Observer
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class ServiceMessageContext(
        private val _observer: Observer<ServiceMessage>,
        val handlerIterator: Iterator<EventHandler>,
        val event: Event<OrderedBuildEvent>,
        val messageFactory: MessageFactory,
        val verbosity: Verbosity): Observer<ServiceMessage> {
    private val _blocks = mutableListOf<Block>()

    val blocks: List<Block> get() = _blocks

    override fun onNext(value: ServiceMessage) = _observer.onNext(value)

    override fun onError(error: Exception) = _observer.onError(error)

    override fun onCompleted() = _observer.onCompleted()

    fun createBlock(blockName: String, description: String, children: List<Id>) {
        if (children.isNotEmpty()) {
            onNext(messageFactory.createBlockOpened(blockName, description))
            _blocks.add(Block(blockName, description, children))
        }
    }

    class Block(val blockName: String, val description: String, children: List<Id>) {
        private val _children: MutableList<Id> = children.toMutableList()

        fun expand(children: List<Id>) {
            synchronized(_children) {
                _children.addAll(children)
            }
        }

        fun process(id: Id): Boolean {
            synchronized(_children) {
                _children.remove(id)
                return _children.isEmpty()
            }
        }
    }
}