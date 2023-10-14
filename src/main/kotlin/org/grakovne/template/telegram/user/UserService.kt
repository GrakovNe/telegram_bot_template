package org.grakovne.template.telegram.user

import org.grakovne.template.telegram.user.domain.Type
import org.grakovne.template.telegram.user.domain.User
import org.grakovne.template.telegram.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService(private val userRepository: UserRepository) {

    fun fetchSuperUsers() = userRepository.findByType(Type.SUPER_USER)

    fun fetchUser(userId: String, language: String): User =
        userRepository
            .findById(userId)
            .orElseGet { persistUser(userId, language, Type.FREE_USER) }
            .copy(language = language)
            .let { persistUser(it.id,it.language ?: "en", it.type) }


    private fun persistUser(
        id: String,
        language: String,
        type: Type
    ): User = User(
        id = id,
        language = language,
        type = type,
        lastActivityTimestamp = Instant.now()
    ).let { userRepository.save(it) }
}