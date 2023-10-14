package org.grakovne.template.telegram.user.repository

import org.grakovne.template.telegram.user.domain.Type
import org.grakovne.template.telegram.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {

    fun findByType(type: Type): List<User>
}