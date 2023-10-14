package org.grakovne.template.telegram.events.core

class EventProcessingError<T>(
    val code: T,
    val details: String? = null
)