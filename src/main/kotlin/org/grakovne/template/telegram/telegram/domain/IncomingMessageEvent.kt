package org.grakovne.template.telegram.telegram.domain

import com.pengrad.telegrambot.model.Update
import org.grakovne.template.telegram.events.core.Event
import org.grakovne.template.telegram.events.core.EventType
import org.grakovne.template.telegram.user.domain.User

data class IncomingMessageEvent(
    val update: Update,
    val user: User
) : Event(EventType.INCOMING_MESSAGE)