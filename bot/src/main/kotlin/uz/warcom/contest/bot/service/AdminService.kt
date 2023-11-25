package uz.warcom.contest.bot.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import uz.warcom.contest.bot.model.ContestData
import uz.warcom.contest.bot.model.EntriesSummary
import uz.warcom.contest.bot.model.EntrySummary
import uz.warcom.contest.bot.model.ImageData
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.exception.DraftContestNotCreated
import uz.warcom.contest.persistence.exception.NotCommunityAdminException
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class AdminService(
    private val persistenceFacade: PersistenceFacade
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

    fun currentDraftContest(telegramUser: User, communityCode: String): ContestData {
        persistenceFacade.checkAdmin(telegramUser)

        return persistenceFacade.getCurrentDraftContest(communityCode) ?: throw DraftContestNotCreated()
    }

    fun createContest(telegramUser: User, communityCode: String): ContestData {
        val adminCommunities = persistenceFacade.fetchAdminCommunities(telegramUser)
            .associateBy { it.label }

        if (!adminCommunities.containsKey(communityCode))
            throw NotCommunityAdminException()

        val existingDraft = persistenceFacade.getCurrentDraftContest(communityCode)
            ?.let { cd -> Contest().also { it.id = cd.id } }

        val draft = (existingDraft ?: Contest()).apply {
            this.community = adminCommunities[communityCode]
            this.name = "placeholder"
            this.description = "placeholder"
            this.startDate = LocalDateTime.now()
            this.endDate = LocalDateTime.now()
            this.draft = true
        }

        return persistenceFacade.createContest(draft)
    }

    fun updateCurrentDraftContest(telegramUser: User, communityCode: String): ContestData {
        TODO()
    }

}