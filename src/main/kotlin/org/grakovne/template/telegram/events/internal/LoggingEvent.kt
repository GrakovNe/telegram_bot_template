package org.grakovne.template.telegram.events.internal

import org.grakovne.template.telegram.events.core.Event
import org.grakovne.template.telegram.events.core.EventType

data class LoggingEvent(val level: LogLevel, val message: String) : Event(EventType.LOG_SENT)

enum class LogLevel(private val severity: Int) {
    DEBUG(0),
    INFO(1),
    WARN(2);

    companion object {
        fun LogLevel.isWorseOrEqualThan(other: LogLevel): Boolean = severity >= other.severity
    }
}