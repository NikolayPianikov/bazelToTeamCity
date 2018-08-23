package bazel.messages

import bazel.events.OrderedBuildEvent
import devteam.rx.Disposable
import devteam.rx.Observable
import devteam.rx.Observer
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage

interface ServiceMessageSubject: Observer<OrderedBuildEvent>, Observable<ServiceMessage>, Disposable