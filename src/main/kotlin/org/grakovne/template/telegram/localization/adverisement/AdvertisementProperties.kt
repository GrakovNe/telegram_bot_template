package org.grakovne.template.telegram.localization.adverisement

import org.grakovne.template.telegram.common.Language
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import kotlin.properties.Delegates

@Configuration
@ConfigurationProperties(prefix = "ads")
class AdvertisementProperties {
    var blockDelimiter: String by Delegates.notNull()
    var creatives: List<AdvertisementCreativeProperties> = emptyList()
}

class AdvertisementCreativeProperties {
    var language: Language by Delegates.notNull()
    var text: String by Delegates.notNull()
    var name: String by Delegates.notNull()
    var type: AdvertisingType by Delegates.notNull()
}