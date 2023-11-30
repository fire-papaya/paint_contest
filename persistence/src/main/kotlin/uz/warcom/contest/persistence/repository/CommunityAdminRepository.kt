package uz.warcom.contest.persistence.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uz.warcom.contest.persistence.domain.CommunityAdmin
import uz.warcom.contest.persistence.domain.WarcomUser

@Repository
interface CommunityAdminRepository: CrudRepository<CommunityAdmin, Int> {
    fun findAllByUser(user: WarcomUser): List<CommunityAdmin>
}