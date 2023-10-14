package org.grakovne.template.telegram.user

import org.grakovne.template.telegram.user.domain.UserMessageReport
import org.grakovne.template.telegram.user.repository.UserMessageReportRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class UserMessageReportService(private val repository: UserMessageReportRepository) {

    fun createReportEntry(userId: String, text: String?) =
        UserMessageReport(
            id = UUID.randomUUID(),
            userId = userId,
            createdAt = Instant.now(),
            text = text
        ).let(repository::save)
}