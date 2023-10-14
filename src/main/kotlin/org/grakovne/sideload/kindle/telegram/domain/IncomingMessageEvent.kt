package org.grakovne.sideload.kindle.telegram.domain

import com.pengrad.telegrambot.model.Update
import org.grakovne.sideload.kindle.events.core.Event
import org.grakovne.sideload.kindle.events.core.EventType
import org.grakovne.sideload.kindle.user.domain.UserReference

data class IncomingMessageEvent(
    val update: Update,
    val user: UserReference
) : Event(EventType.INCOMING_MESSAGE)