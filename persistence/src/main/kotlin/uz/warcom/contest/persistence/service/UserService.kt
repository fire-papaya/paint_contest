package uz.warcom.contest.persistence.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.dto.UserDto
import uz.warcom.contest.persistence.exception.UserNotFoundException
import uz.warcom.contest.persistence.repository.UserRepository

@Service
class UserService
@Autowired constructor(
    private val userRepository: UserRepository,
    private val communityService: CommunityService
) {

    fun getUserByTelegramId(telegramId: Long): WarcomUser {
        return userRepository.findByTelegramId(telegramId) ?: throw UserNotFoundException()
    }

    fun createUser (userData: UserDto): WarcomUser {
        val user = WarcomUser().apply {
            this.username = userData.username
            this.telegramId = userData.telegramId
        }

        return userRepository.save(user)
    }

    fun switchUserCommunity (telegramId: Long, communityCode: String): WarcomUser {
        val user = getUserByTelegramId(telegramId)

        val community = communityService.getCommunity(communityCode)

        user.community = community

        return userRepository.save(user)
    }
}