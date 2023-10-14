package org.grakovne.template.telegram.telegram.configuration

import arrow.core.Either
import arrow.core.sequence
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.BotCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SetMyCommands
import jakarta.annotation.PostConstruct
import org.grakovne.template.telegram.common.Language
import org.grakovne.template.telegram.common.ifTrue
import org.grakovne.template.telegram.events.core.EventProcessingResult
import org.grakovne.template.telegram.events.core.EventSender
import org.grakovne.template.telegram.events.internal.LogLevel.WARN
import org.grakovne.template.telegram.events.internal.LoggingEvent
import org.grakovne.template.telegram.localization.EnumLocalizationService
import org.grakovne.template.telegram.telegram.TelegramUpdateProcessingError
import org.grakovne.template.telegram.telegram.domain.IncomingMessageEvent
import org.grakovne.template.telegram.telegram.listeners.IncomingMessageEventListener
import org.grakovne.template.telegram.telegram.listeners.UnprocessedIncomingEventHandler
import org.grakovne.template.telegram.user.UserMessageReportService
import org.grakovne.template.telegram.user.UserService
import org.springframework.stereotype.Service

@Service
class MessageListenersConfiguration(
    private val bot: TelegramBot,
    private val incomingMessageEventListeners: List<IncomingMessageEventListener>,
    private val eventSender: EventSender,
    private val userService: UserService,
    private val enumLocalizationService: EnumLocalizationService,
    private val userMessageReportService: UserMessageReportService,
    private val unprocessedIncomingEventHandler: UnprocessedIncomingEventHandler
) {

    @PostConstruct
    fun onCreate() = bot
        .setUpdatesListener { updates ->
            onMessageBatch(updates)
            UpdatesListener.CONFIRMED_UPDATES_ALL
        }

    private fun onMessageBatch(batch: List<Update>) =
        batch
            .filter { update -> update.hasSender() }
            .filter { update -> update.hasMessage() }
            .forEach { update -> onMessage(update) }

    private fun onMessage(update: Update) = try {
        val user = userService.fetchUser(
            userId = update.message().chat().id().toString(),
            language = update.message()?.from()?.languageCode() ?: "en"
        )

        val incomingMessageEvent = IncomingMessageEvent(update, user)

        eventSender
            .sendEvent(incomingMessageEvent)
            .sequence()
            .tap { it.processedByNothing().ifTrue { unprocessedIncomingEventHandler.handle(incomingMessageEvent) } }
            .also { bot.execute(SetMyCommands(*messageListenersDescriptions(user.language))) }
            .also { userMessageReportService.createReportEntry(user.id, update.message()?.text()) }

    } catch (ex: Exception) {
        eventSender.sendEvent(LoggingEvent(WARN, "Internal Exception. Message = ${ex.message}"))
        Either.Left(TelegramUpdateProcessingError.INTERNAL_ERROR)
    }

    private fun Update.hasSender() = this.message()?.chat()?.id() != null
    private fun Update.hasMessage() = this.message()?.text() != null

    private fun messageListenersDescriptions(targetLanguage: Language?) = incomingMessageEventListeners
        .map {
            BotCommand(
                it.getDescription().key,
                enumLocalizationService.localize(it.getDescription().type, targetLanguage)
            )
        }
        .toTypedArray()

    private fun List<EventProcessingResult>.processedByNothing() =
        this.all { result -> result == EventProcessingResult.SKIPPED }
}