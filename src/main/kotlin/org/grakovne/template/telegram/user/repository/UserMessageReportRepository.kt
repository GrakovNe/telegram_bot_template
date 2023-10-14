package org.grakovne.template.telegram.user.repository

import org.grakovne.template.telegram.user.domain.UserMessageReport
import org.springframework.data.jpa.repository.JpaRepository

interface UserMessageReportRepository : JpaRepository<UserMessageReport, String>