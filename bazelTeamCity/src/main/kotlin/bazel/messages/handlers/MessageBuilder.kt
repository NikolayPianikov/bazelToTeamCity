package bazel.messages.handlers

import bazel.Verbosity
import bazel.atLeast
import bazel.messages.ServiceMessageContext

class MessageBuilder(
        private val _serviceMessageContext: ServiceMessageContext) {

    private val _text = StringBuilder()

    init {
        if (_serviceMessageContext.verbosity.atLeast(Verbosity.Trace)) {
            _text.append(String.format("%8d", _serviceMessageContext.event.sequenceNumber))
            _text.append(' ')
            _text.append(_serviceMessageContext.event.streamId.component)
            _text.append(' ')
            val streamId = _serviceMessageContext.event.streamId
            _text.append(if (streamId.invocationId.isNotEmpty()) "${streamId.buildId.take(8)}:${streamId.invocationId.take(8)}" else streamId.buildId.take(8))
            _text.append(' ')
        }
    }

    fun append(text: String, verbosity: Verbosity = _serviceMessageContext.verbosity): MessageBuilder {
        if(_serviceMessageContext.verbosity.atLeast(verbosity)) {
            _text.append(text)
        }

        return this
    }

    override fun toString(): String {
        return _text.toString()
    }
}

fun ServiceMessageContext.buildMessage(): MessageBuilder {
    return MessageBuilder(this)
}