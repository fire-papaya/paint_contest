package uz.warcom.contest.persistence.service

import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Community
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.repository.ContestRepository
import java.time.LocalDateTime

@Service
class ContestService
constructor(
    private val contestRepository: ContestRepository
){
    fun currentContest (): Contest? {
        return contestRepository.findByStartDateBeforeAndEndDateAfter(LocalDateTime.now(), LocalDateTime.now())
    }

    fun currentContest(community: Community): Contest? {
        return contestRepository.findByCommunityAndStartDateBeforeAndEndDateAfter(
            community,
            LocalDateTime.now(),
            LocalDateTime.now()
        )
    }
}