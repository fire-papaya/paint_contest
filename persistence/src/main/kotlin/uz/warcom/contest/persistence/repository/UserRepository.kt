package uz.warcom.contest.persistence.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uz.warcom.contest.persistence.domain.WarcomUser

@Repository
interface UserRepository: CrudRepository<WarcomUser, Int> {
    fun findByTelegramId(telegramId: Long): WarcomUser?
}