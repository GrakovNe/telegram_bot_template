package org.grakovne.template.telegram.telegram.messaging

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.sequence
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import org.grakovne.template.telegram.telegram.domain.PreparedMessage
import org.grakovne.template.telegram.localization.EnumLocalizationService
import org.grakovne.template.telegram.localization.HelpMessage
import org.grakovne.template.telegram.localization.HelpMessageItem
import org.grakovne.template.telegram.localization.MessageLocalizationService
import org.grakovne.template.telegram.telegram.TelegramUpdateProcessingError
import org.grakovne.template.telegram.telegram.domain.CommandType
import org.grakovne.template.telegram.user.domain.User
import org.springframework.stereotype.Service

@Service
class HelpMessageSender(
    bot: TelegramBot,
    private val localizationService: MessageLocalizationService,
    private val enumLocalizationService: EnumLocalizationService
) : MessageSender(bot) {

    fun sendResponse(
        origin: Update,
        user: User,
        helpMessage: List<Help>
    ): Either<TelegramUpdateProcessingError, Unit> {
        val targetLanguage = user.language

        return helpMessage
            .map {
                HelpMessageItem(
                    key = buildCommandUsage(it),
                    description = enumLocalizationService.localize(it.description, targetLanguage)
                )
            }
            .map { localizationService.localize(it, targetLanguage) }
            .sequence()
            .map { it.joinToString(separator = "\n", transform = PreparedMessage::text) }
            .map { HelpMessage(it) }
            .flatMap { localizationService.localize(it, targetLanguage) }
            .mapLeft { TelegramUpdateProcessingError.INTERNAL_ERROR }
            .flatMap { sendRawMessage(origin, it) }
    }

    private fun buildCommandUsage(it: Help): String = it.key
}

data class Help(
    val key: String,
    val description: CommandType
)