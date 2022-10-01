package uz.warcom.contest.bot.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import uz.warcom.contest.bot.model.EntriesSummary
import uz.warcom.contest.bot.model.EntrySummary
import java.awt.image.BufferedImage
import kotlin.random.Random

@Service
class AdminService(
    private val persistenceFacade: PersistenceFacade
){
    fun getEntriesSummary (): EntriesSummary {
        val entriesData = persistenceFacade.getEntries()

        val entriesSummary = EntriesSummary()

        entriesData.forEach { entryData ->
            val user = entryData.user ?: ("no_username_" + Random.nextInt())
            entriesSummary.usersMap[user] = EntrySummary(
                user = user,
                isPrimed = entryData.images.isNotEmpty(),
                isReady = entryData.images.count { img -> img.isReady } >= 3
            )
        }

        return entriesSummary
    }

    fun getEntryImages(telegramUser: Long): List<BufferedImage> {
        val user = User(telegramUser, telegramUser.toString(), false)
        return persistenceFacade.getEntryImages(user)
    }
}