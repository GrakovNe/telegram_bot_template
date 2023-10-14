package org.grakovne.template.telegram.telegram.listeners

import arrow.core.Either
import org.grakovne.template.telegram.events.core.Event
import org.grakovne.template.telegram.events.core.EventListener
import org.grakovne.template.telegram.events.core.EventProcessingError
import org.grakovne.template.telegram.events.core.EventProcessingResult
import org.grakovne.template.telegram.telegram.TelegramUpdateProcessingError
import org.grakovne.template.telegram.telegram.domain.CommandType
import org.grakovne.template.telegram.telegram.domain.IncomingMessageEvent

abstract class IncomingMessageEventListener : EventListener<IncomingMessageEvent, TelegramUpdateProcessingError> {

    override fun onEvent(event: Event) =
        when (event) {
            is IncomingMessageEvent -> when (event.update.message().text().startsWith("/" + getDescription().key)) {
                true -> processEvent(event).map { EventProcessingResult.PROCESSED }
                false -> Either.Right(EventProcessingResult.SKIPPED)
            }

            else -> Either.Left(EventProcessingError(TelegramUpdateProcessingError.INTERNAL_ERROR))
        }

    abstract fun getDescription(): IncomingMessageDescription

    protected abstract fun processEvent(event: IncomingMessageEvent): Either<EventProcessingError<TelegramUpdateProcessingError>, Unit>
}

data class IncomingMessageDescription(
    val key: String,
    val type: CommandType
)