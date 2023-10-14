package org.grakovne.template.telegram.telegram.messaging

import arrow.core.flatMap
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.template.telegram.localization.Message
import org.grakovne.template.telegram.localization.MessageLocalizationService
import org.grakovne.template.telegram.telegram.TelegramUpdateProcessingError
import org.grakovne.template.telegram.user.domain.User
import org.springframework.stereotype.Service

@Service
class SimpleMessageSender(
    bot: TelegramBot,
    private val localizationService: MessageLocalizationService
) : MessageSender(bot) {

    fun <T : Message> sendResponse(
        chatId: String,
        user: User,
        message: T
    ) = localizationService
        .localize(message, user.language)
        .mapLeft { TelegramUpdateProcessingError.INTERNAL_ERROR }
        .flatMap { sendRawMessage(chatId, it) }

    fun <T : Message> sendResponse(
        origin: Update,
        user: User,
        message: T
    ) = sendResponse(origin.message().chat().id().toString(), user, message)
}