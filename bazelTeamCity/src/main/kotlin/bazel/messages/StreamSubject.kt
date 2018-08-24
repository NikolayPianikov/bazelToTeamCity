package bazel.messages

import bazel.Event
import bazel.Verbosity
import bazel.bazel.events.BazelEvent
import bazel.events.OrderedBuildEvent
import bazel.messages.handlers.*
import devteam.rx.*
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

class StreamSubject(
        private val _verbosity: Verbosity,
        private val _messageFactory: MessageFactory)
    : ServiceMessageSubject {
    private val _messageSubject = subjectOf<ServiceMessage>()
    private val _blocks = mutableListOf<ServiceMessageContext.Block>()

    override fun onNext(value: Event<OrderedBuildEvent>) {
        val handlerIterator = handlers.iterator()
        val subject = subjectOf<ServiceMessage>()
        val ctx = ServiceMessageContext(subject, handlerIterator, value, _messageFactory, _verbosity)
        subject.map { updateHeader(value.payload, it) }.subscribe(_messageSubject).use {
            handlerIterator.next().handle(ctx)

            var blocksToFinish: List<ServiceMessageContext.Block> = emptyList()
            synchronized(_blocks) {
                if (value.payload is BazelEvent) {
                    val event = value.payload.content
                    for (block in _blocks) {
                        block.expand(event.children)
                    }

                    @Suppress("NestedLambdaShadowedImplicitParameter")
                    blocksToFinish = _blocks.filter { it.process(event.id) }.toList()
                    _blocks.addAll(blocksToFinish)
                }

                _blocks.addAll(0, ctx.blocks)
                _blocks.removeAll(blocksToFinish)
            }

            for (block in blocksToFinish) {
                subject.onNext(_messageFactory.createBlockClosed(block.blockName))
            }
        }
    }

    override fun onError(error: Exception) = _messageSubject.onError(error)

    override fun onCompleted() = _messageSubject.onCompleted()

    override fun subscribe(observer: Observer<ServiceMessage>): Disposable = _messageSubject.subscribe(observer)

    override fun dispose() = Unit

    private fun updateHeader(event: OrderedBuildEvent, message: ServiceMessage): ServiceMessage {
        if (message.flowId.isNullOrEmpty()) {
            message.setFlowId(event.streamId.invocationId)
        }

        // message.setTimestamp(event.eventTime)
        return message
    }

    companion object {
        private val handlers = sequenceOf(
                // Progress progress = 3;
                ProgressHandler(),

                //Aborted aborted = 4;

                //BuildStarted started = 5;
                BuildStartedHandler(),

                //UnstructuredCommandLine unstructured_command_line = 12;
                UnstructuredCommandLineHandler(),

                //command_line.CommandLine structured_command_line = 22;
                StructuredCommandLineHandler(),

                //OptionsParsed options_parsed = 13;

                //WorkspaceStatus workspace_status = 16;

                //Fetch fetch = 21;

                //Configuration configuration = 17;

                //PatternExpanded expanded = 6;

                //TargetConfigured configured = 18;

                //ActionExecuted action = 7;

                //NamedSetOfFiles named_set_of_files = 15;

                //TargetComplete completed = 8;

                //TestResult test_result = 10;

                //TestSummary test_summary = 9;

                //BuildFinished finished = 14;
                BuildCompletedHandler(),

                //BuildToolLogs build_tool_logs = 23;
                //BuildMetrics build_metrics = 24;

                // Unknown
                UnknownEventHandler()
        ).sortedBy { it.priority }.toList()
    }
}