package uz.warcom.contest.persistence.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uz.warcom.contest.persistence.domain.Community
import uz.warcom.contest.persistence.domain.Contest
import java.time.LocalDateTime

@Repository
interface ContestRepository: CrudRepository<Contest, Int> {

    fun findByCommunityAndStartDateBeforeAndEndDateAfter(
        community: Community,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Contest?

    fun findFirstByCommunityOrderByIdDesc(community: Community): Contest?

    fun findFirstByCommunityAndDraftIsTrueOrderByIdDesc(community: Community): Contest?
}