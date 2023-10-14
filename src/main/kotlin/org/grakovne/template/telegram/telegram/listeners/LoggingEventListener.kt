package org.grakovne.template.telegram.telegram.listeners

import arrow.core.Either
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import org.grakovne.template.telegram.events.core.*
import org.grakovne.template.telegram.events.internal.LogLevel.Companion.isWorseOrEqualThan
import org.grakovne.template.telegram.events.internal.LoggingEvent
import org.grakovne.template.telegram.telegram.ConfigurationProperties
import org.grakovne.template.telegram.telegram.TelegramUpdateProcessingError
import org.grakovne.template.telegram.user.UserService
import org.springframework.stereotype.Service

@Service
class LoggingEventListener(
    private val bot: TelegramBot,
    private val properties: ConfigurationProperties,
    private val userService: UserService
) : EventListener<LoggingEvent, TelegramUpdateProcessingError> {
    override fun acceptableEvents(): List<EventType> = listOf(EventType.LOG_SENT)

    override fun onEvent(event: Event): Either<EventProcessingError<TelegramUpdateProcessingError>, EventProcessingResult> {
        return when (event) {
            is LoggingEvent -> processLoggingEvent(event)
            else -> Either.Left(EventProcessingError(TelegramUpdateProcessingError.INTERNAL_ERROR))
        }
    }

    private fun processLoggingEvent(event: LoggingEvent): Either.Right<EventProcessingResult> {
        if (event.level.isWorseOrEqualThan(properties.level)) {
            userService.fetchSuperUsers()
                .forEach { bot.execute(SendMessage(it.id, event.toMessage()).parseMode(ParseMode.HTML)) }
        }

        return Either.Right(EventProcessingResult.PROCESSED)
    }


    private fun LoggingEvent.toMessage(): String = """
    <i>[Admin] Logging Event Occurred!
    
    Status: ${this.level}
    Message: ${this.message}</i>
""".trimIndent()
}
