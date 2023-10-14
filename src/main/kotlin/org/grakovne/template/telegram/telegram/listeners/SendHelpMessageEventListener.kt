package org.grakovne.template.telegram.telegram.listeners

import arrow.core.Either
import org.grakovne.template.telegram.events.core.EventProcessingError
import org.grakovne.template.telegram.events.core.EventSender
import org.grakovne.template.telegram.events.core.EventType
import org.grakovne.template.telegram.events.internal.LogLevel
import org.grakovne.template.telegram.events.internal.LoggingEvent
import org.grakovne.template.telegram.telegram.TelegramUpdateProcessingError
import org.grakovne.template.telegram.telegram.domain.CommandType
import org.grakovne.template.telegram.telegram.domain.IncomingMessageEvent
import org.grakovne.template.telegram.telegram.messaging.Help
import org.grakovne.template.telegram.telegram.messaging.HelpMessageSender
import org.springframework.stereotype.Service

@Service
class SendHelpMessageEventListener(
    private val incomingMessageEventListeners: List<IncomingMessageEventListener>,
    private val eventSender: EventSender,
    private val helpMessageSender: HelpMessageSender
) : IncomingMessageEventListener() {

    override fun getDescription() = IncomingMessageDescription("help", CommandType.SEND_HELP)

    override fun processEvent(event: IncomingMessageEvent) =
        incomingMessageEventListeners
            .map { listener -> listener.getDescription().let { Help(it.key, it.type) } }
            .let { helpMessageSender.sendResponse(event.update, event.user, it) }
            .mapLeft { EventProcessingError(it) }
            .tap {
                eventSender.sendEvent(
                    LoggingEvent(
                        LogLevel.DEBUG,
                        "Help text was sent in response on origin message: ${event.update.message().text()}"
                    )
                )
            }

    override fun acceptableEvents() = listOf(EventType.INCOMING_MESSAGE)

    fun forceProcessEvent(event: IncomingMessageEvent) = processEvent(event)

}