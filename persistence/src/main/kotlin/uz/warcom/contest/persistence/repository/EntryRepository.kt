package uz.warcom.contest.persistence.repository

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.WarcomUser

@Repository
interface EntryRepository: JpaRepository<Entry, Int> {
    @EntityGraph(attributePaths = ["images", "user"])
    fun findFirstByContestAndUser(contest: Contest, user: WarcomUser) : Entry?

    @EntityGraph(attributePaths = ["images", "user"])
    fun findAllByContest(contest: Contest): List<Entry>
}