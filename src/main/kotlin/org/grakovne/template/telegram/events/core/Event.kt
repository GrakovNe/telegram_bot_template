package org.grakovne.template.telegram.events.core

abstract class Event(val eventType: EventType)

enum class EventType {
    LOG_SENT,
    INCOMING_MESSAGE
}


