package org.grakovne.sideload.kindle.telegram.domain

data class PreparedMessage(
    val text: String,
    val webPagePreview: Boolean
)
