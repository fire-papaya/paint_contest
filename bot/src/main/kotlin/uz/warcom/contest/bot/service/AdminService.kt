package uz.warcom.contest.bot.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import uz.warcom.contest.bot.model.ContestData
import uz.warcom.contest.bot.model.EntriesSummary
import uz.warcom.contest.bot.model.EntrySummary
import uz.warcom.contest.bot.model.ImageData
import uz.warcom.contest.bot.model.mapper.EntryMapStruct
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.exception.DraftContestNotCreated
import uz.warcom.contest.persistence.exception.NotCommunityAdminException
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class AdminService(
    private val persistenceFacade: PersistenceFacade,
    private val entryMapStruct: EntryMapStruct
){
    fun getEntriesSummary (communityCode: String): EntriesSummary {
        val entriesData = persistenceFacade.getEntries(communityCode)

        val entriesSummary = EntriesSummary()

        entriesData.forEach { entryData ->
            val user = entryData.user ?: ("no_username_" + Random.nextInt())
            entriesSummary.usersMap[user] = EntrySummary(
                user = user,
                isPrimed = entryData.images.isNotEmpty(),
                isReady = entryData.images.count { img -> img.isReady } >= 3,
                id = entryData.id ?: throw IllegalStateException("entry doesn't have id")
            )
        }

        return entriesSummary
    }

    fun getEntryImagesInfo(entryId: Int): List<ImageData> {
        return persistenceFacade.getEntryImagesInfo(entryId)
    }

    fun currentDraftContest(telegramUser: User): Contest {
        val adminCommunities = persistenceFacade.fetchAdminCommunities(telegramUser)
            .associateBy { it.label }

        val communityCode = persistenceFacade.checkUser(telegramUser).community!!.label

        if (!adminCommunities.containsKey(communityCode))
            throw NotCommunityAdminException()

        return persistenceFacade.getCurrentDraftContest(communityCode) ?: throw DraftContestNotCreated()
    }

    fun createContest(telegramUser: User): Contest {
        val adminCommunities = persistenceFacade.fetchAdminCommunities(telegramUser)
            .associateBy { it.label }

        val communityCode = persistenceFacade.checkUser(telegramUser).community!!.label

        if (!adminCommunities.containsKey(communityCode))
            throw NotCommunityAdminException()

        val existingDraft = persistenceFacade.getCurrentDraftContest(communityCode)

        return if (existingDraft != null) existingDraft
        else {
            val draft = Contest().apply {
                this.community = adminCommunities[communityCode]
                this.name = "placeholder"
                this.description = "placeholder"
                this.startDate = LocalDateTime.now()
                this.endDate = LocalDateTime.now()
                this.draft = true
            }

            persistenceFacade.saveContest(draft)
        }
    }

    fun updateCurrentDraftContest(telegramUser: User, updateData: ContestData): Contest {
        val currentDraft = currentDraftContest(telegramUser)

        if (updateData.name.isNotEmpty())
            currentDraft.name = updateData.name

        if (updateData.description.isNotEmpty())
            currentDraft.description = updateData.description

        if (updateData.startDate != LocalDateTime.MIN && updateData.endDate != LocalDateTime.MIN) {
            currentDraft.startDate = updateData.startDate
            currentDraft.endDate = updateData.endDate
        }

        return persistenceFacade.saveContest(currentDraft)
    }

    fun submitContest(telegramUser: User): ContestData {
        val currentDraft = currentDraftContest(telegramUser)

        currentDraft.draft = false

        val saved = persistenceFacade.saveContest(currentDraft)
        return entryMapStruct.toContestData(saved)
    }

    fun draftContestData(telegramUser: User): ContestData {
        val currentDraft = currentDraftContest(telegramUser)

        return entryMapStruct.toContestData(currentDraft)
    }

}