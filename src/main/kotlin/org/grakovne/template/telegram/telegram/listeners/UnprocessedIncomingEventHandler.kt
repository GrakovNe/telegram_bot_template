package org.grakovne.template.telegram.telegram.listeners

import org.grakovne.template.telegram.telegram.domain.IncomingMessageEvent
import org.springframework.stereotype.Service

@Service
class UnprocessedIncomingEventHandler(
    private val sender: SendHelpMessageEventListener
) {

    fun handle(incomingMessageEvent: IncomingMessageEvent) = sender.forceProcessEvent(incomingMessageEvent)
}