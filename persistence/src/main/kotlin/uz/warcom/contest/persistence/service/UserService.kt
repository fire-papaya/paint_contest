package uz.warcom.contest.persistence.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.dto.UserDto
import uz.warcom.contest.persistence.exception.UserNotFoundException
import uz.warcom.contest.persistence.exception.UserWithoutCommunityException
import uz.warcom.contest.persistence.repository.UserRepository

@Service
class UserService
@Autowired constructor(
    private val userRepository: UserRepository
) {

    fun getUserByTelegramId (telegramId: Long): WarcomUser {
        val user = userRepository.findByTelegramId(telegramId) ?: throw UserNotFoundException()

        if (user.community == null)
            throw UserWithoutCommunityException()

        return user
    }

    fun createUser (userData: UserDto): WarcomUser {
        val user = WarcomUser().apply {
            this.username = userData.username
            this.telegramId = userData.telegramId
        }

        return userRepository.save(user)
    }
}