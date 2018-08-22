package bazel

import bazel.bazel.*
import com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos
import java.util.logging.Level
import java.util.logging.Logger


class BazelEventConverter: Converter<com.google.protobuf.Any, BazelContent> {
    override fun convert(source: com.google.protobuf.Any): BazelContent {
        val className = source.typeUrl.replace("type.googleapis.com/build_event_stream.", "")
        return when (className) {

            "BuildEvent" -> {
                val event = source.unpack(BuildEventStreamProtos.BuildEvent::class.java)
                val id = if (event.hasId()) Id(event.id) else Id.default
                val children = mutableListOf<Id>()
                for (i in 0 until event.childrenCount) {
                    children.add(Id(event.getChildren(i)))
                }

                // progress = 3
                if (event.hasProgress()) {
                    val content = event.progress
                    return Progress(
                            id,
                            children,
                            content.stdout,
                            content.stderr)
                }

                // aborted = 4
                if (event.hasAborted()) {
                    val content = event.aborted
                    return Aborted(
                            id,
                            children,
                            content.description,
                            convert(content.reason))
                }

                // started = 5
                if (event.hasStarted()) {
                    val content = event.started
                    return BuildStarted(
                            id,
                            children,
                            content.buildToolVersion,
                            content.command,
                            content.workingDirectory,
                            content.workspaceDirectory)
                }

                // unstructured_command_line = 12
                if (event.hasUnstructuredCommandLine()) {
                    val content = event.unstructuredCommandLine
                    val args = mutableListOf<String>()
                    for (i in 0 until content.argsCount) {
                        args.add(content.getArgs(i))
                    }

                    return UnstructuredCommandLine(
                            id,
                            children,
                            args)
                }

                // structured_command_line = 22
                if (event.hasStructuredCommandLine()) {
                    val content = event.structuredCommandLine
                    return StructuredCommandLine(
                            id,
                            children,
                            content.commandLineLabel)
                }

                // options_parsed = 13
                if (event.hasOptionsParsed()) {
                    val content = event.optionsParsed

                    val cmdLines = mutableListOf<String>()
                    for (i in 0 until content.cmdLineCount) {
                        cmdLines.add(content.getCmdLine(i))
                    }

                    val explicitCmdLines = mutableListOf<String>()
                    for (i in 0 until content.explicitCmdLineCount) {
                        explicitCmdLines.add(content.getExplicitCmdLine(i))
                    }

                    val startupOptions = mutableListOf<String>()
                    for (i in 0 until content.startupOptionsCount) {
                        startupOptions.add(content.getStartupOptions(i))
                    }

                    val explicitStartupOptions = mutableListOf<String>()
                    for (i in 0 until content.explicitStartupOptionsCount) {
                        explicitStartupOptions.add(content.getExplicitStartupOptions(i))
                    }

                    return OptionsParsed(
                            id,
                            children,
                            cmdLines,
                            explicitCmdLines,
                            startupOptions,
                            explicitCmdLines)
                }

                // workspace_status = 16
                if (event.hasWorkspaceStatus()) {
                    val content = event.workspaceStatus
                    val items = mutableMapOf<String, String>()
                    for (i in 0 until content.itemCount) {
                        val item = content.getItem(i)
                        items[item.key] = item.value
                    }
                    return WorkspaceStatus(
                            id,
                            children,
                            items)
                }

                // fetch = 21
                if (event.hasFetch()) {
                    val content = event.fetch
                    return Fetch(
                            id,
                            children,
                            content.success)
                }

                // configuration = 17
                if (event.hasConfiguration()) {
                    val content = event.configuration
                    return Configuration(
                            id,
                            children,
                            content.platformName,
                            content.mnemonic,
                            content.cpu,
                            content.makeVariableMap)
                }

                // expanded = 6
                if (event.hasExpanded()) {
                    return PatternExpanded(
                            id,
                            children)
                }

                // configured = 18
                if (event.hasConfigured()) {
                    if (event.hasId() && event.id.hasTargetConfigured()) {
                        val content = event.configured
                        val tags = mutableListOf<String>()
                        for (i in 0 until content.tagCount) {
                            tags.add(content.getTag(i))
                        }

                        return TargetConfigured(
                                id,
                                children,
                                event.id.targetConfigured.label,
                                event.id.targetConfigured.aspect,
                                content.targetKind,
                                convert(content.testSize),
                                tags)
                    }
                }

                // action = 7
                if (event.hasAction()) {
                    val content = event.action
                    val cmdLines = mutableListOf<String>()
                    for (i in 0 until content.commandLineCount) {
                        cmdLines.add(content.getCommandLine(i))
                    }

                    return ActionExecuted(
                            id,
                            children,
                            content.type,
                            cmdLines,
                            content.success,
                            convert(content.primaryOutput),
                            convert(content.stdout),
                            convert(content.stderr),
                            content.exitCode)
                }

                // named_set_of_files = 15
                if (event.hasNamedSetOfFiles()) {
                    val content = event.namedSetOfFiles
                    val files = mutableListOf<File>()
                    for (i in 0 until content.fileSetsCount) {
                        files.add(convert(content.getFiles(i)))
                    }

                    return NamedSetOfFiles(
                            id,
                            children,
                            files)
                }

                // completed = 8
                if (event.hasCompleted()) {
                    if (event.hasId() && event.id.hasTargetCompleted()) {
                        val content = event.completed
                        val tags = mutableListOf<String>()
                        for (i in 0 until content.tagCount) {
                            tags.add(content.getTag(i))
                        }

                        return TargetComplete(
                                id,
                                children,
                                event.id.targetCompleted.label,
                                content.success,
                                tags,
                                content.testTimeoutSeconds)
                    }
                }

                // test_result = 10
                if (event.hasTestResult()) {
                    val content = event.testResult
                    val testActionOutput = mutableListOf<File>()
                    for (i in 0 until content.testActionOutputCount) {
                        testActionOutput.add(convert(content.getTestActionOutput(i)))
                    }

                    val warnings = mutableListOf<String>()
                    for (i in 0 until content.warningCount) {
                        warnings.add(content.getWarning(i))
                    }

                    if (event.hasId() && event.id.hasTestResult())
                    {
                        return TestResult(
                                id,
                                children,
                                event.id.testResult.label,
                                event.id.testResult.run,
                                event.id.testResult.shard,
                                event.id.testResult.attempt,
                                convert(content.status),
                                content.statusDetails,
                                content.cachedLocally,
                                content.testAttemptStartMillisEpoch,
                                content.testAttemptDurationMillis,
                                testActionOutput,
                                warnings)
                    }
                }

                // test_summary = 9
                if (event.hasTestSummary()) {
                    val content = event.testSummary
                    if (event.hasId() && event.id.hasTestSummary()) {
                        val passed = mutableListOf<File>()
                        for (i in 0 until content.passedCount) {
                            passed.add(convert(content.getPassed(i)))
                        }

                        val failed = mutableListOf<File>()
                        for (i in 0 until content.failedCount) {
                            failed.add(convert(content.getFailed(i)))
                        }

                        return TestSummary(
                                id,
                                children,
                                event.id.testSummary.label,
                                convert(content.overallStatus),
                                content.totalRunCount,
                                passed,
                                failed,
                                content.totalNumCached)
                    }
                }

                // finished = 14
                if (event.hasFinished()) {
                    val content = event.finished
                    if (content.hasExitCode()) {
                        return BuildFinished(
                                id,
                                children,
                                content.exitCode.code,
                                content.exitCode.name)
                    }
                }

                // build_tool_logs = 23
                if (event.hasBuildToolLogs()) {
                    val content = event.buildToolLogs
                    val logs = mutableListOf<File>()
                    for (i in 0 until content.logCount) {
                        logs.add(convert(content.getLog(i)))
                    }

                    return BuildToolLogs(
                            id,
                            children,
                            logs)
                }

                // build_metrics = 24
                if (event.hasBuildMetrics()) {
                    val content = event.buildMetrics
                    val actionsCreated= if (content.hasActionSummary()) content.actionSummary.actionsCreated else 0
                    val usedHeapSizePostBuild= if (content.hasMemoryMetrics()) content.memoryMetrics.usedHeapSizePostBuild else 0
                    return BuildMetrics(
                            id,
                            children,
                            actionsCreated,
                            usedHeapSizePostBuild)
                }

                logger.log(Level.SEVERE, "Unknown bazel event type: $event")
                return BazelUnknownContent(id, children)
            }

            else -> {
                logger.log(Level.SEVERE, "Unknown bazel event: ${source.typeUrl}")
                BazelUnknownContent.default
            }
        }
    }

    private fun convert(abortReason: com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.Aborted.AbortReason) =
        when(abortReason.number) {
            1 -> AbortReason.UserInterrupted
            8 -> AbortReason.NoAnalyze
            9 -> AbortReason.NoBuild
            2 -> AbortReason.Timeout
            3 -> AbortReason.RemoteEnvironmentFailure
            4 -> AbortReason.Internal
            5 -> AbortReason.LoadingFailure
            6 -> AbortReason.AnalysisFailure
            7 -> AbortReason.Skipped
            else -> AbortReason.Unknown
        }

    private fun convert(testSize: com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.TestSize) =
        when(testSize.number) {
            1 -> TestSize.Small
            2 -> TestSize.Medium
            3 -> TestSize.Large
            4 -> TestSize.Enormous
            else -> TestSize.Unknown
        }

    private fun convert(testStatus: com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.TestStatus) =
        when(testStatus.number) {
            1 -> TestStatus.Passed
            2 -> TestStatus.Flaky
            3 -> TestStatus.Timeout
            4 -> TestStatus.Failed
            5 -> TestStatus.Incomplete
            6 -> TestStatus.RemoteFailure
            7 -> TestStatus.FailedToBuild
            8 -> TestStatus.ToolHaltedBeforeTesting
            else -> TestStatus.NoStatus
        }

    private fun convert(file: com.google.devtools.build.lib.buildeventstream.BuildEventStreamProtos.File) =
        when (file.fileCase) {
            BuildEventStreamProtos.File.FileCase.URI -> File(file.name, file.uri)
            BuildEventStreamProtos.File.FileCase.CONTENTS -> File(file.name, file.contents.toByteArray())
            else -> File.empty
        }

    companion object {
        private val logger = Logger.getLogger(BazelEventConverter::class.java.name)
    }
}