package org.grakovne.template.telegram.localization.adverisement

import org.grakovne.template.telegram.common.Language
import org.grakovne.swiftbot.localization.MessageTemplate
import org.springframework.stereotype.Service

@Service
class AdvertisingService(
    private val advertisementProperties: AdvertisementProperties
) {

    fun provideContent(template: MessageTemplate, language: Language?) = advertisementProperties
        .creatives
        .filter { it.language == language }
        .find { it.name == template.advertising.creativeName }
        .takeIf { it?.type == AdvertisingType.ENABLED }
        .takeIf { template.advertising.status == AdvertisingType.ENABLED }
        .let { creative -> creative?.let { advertisementProperties.blockDelimiter + it.text } }
        ?: ""
}