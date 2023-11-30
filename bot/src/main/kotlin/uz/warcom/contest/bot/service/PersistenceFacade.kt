package uz.warcom.contest.bot.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import uz.warcom.contest.bot.exception.BotRequesterException
import uz.warcom.contest.bot.model.*
import uz.warcom.contest.bot.model.mapper.CommunityMapStruct
import uz.warcom.contest.bot.model.mapper.EntryMapStruct
import uz.warcom.contest.bot.model.mapper.UserMapStruct
import uz.warcom.contest.persistence.domain.Community
import uz.warcom.contest.persistence.domain.Contest
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.dto.ImageDto
import uz.warcom.contest.persistence.dto.UserDto
import uz.warcom.contest.persistence.exception.*
import uz.warcom.contest.persistence.service.CommunityService
import uz.warcom.contest.persistence.service.ContestService
import uz.warcom.contest.persistence.service.EntryService
import uz.warcom.contest.persistence.service.UserService

@Service
class PersistenceFacade
@Autowired constructor(
    private val entryService: EntryService,
    private val userService: UserService,
    private val contestService: ContestService,
    private val communityService: CommunityService,
    private val userMapStruct: UserMapStruct,
    private val entryMapStruct: EntryMapStruct,
    private val communityMapStruct: CommunityMapStruct
){
    fun getUser (telegramUser: User): UserData {
        return userMapStruct.toUserData(checkUser(telegramUser))
    }

    fun getCommunities(): List<CommunityData> {
        return communityService.getCommunities().map { communityMapStruct.toCommunityData(it) }
    }

    fun switchUserCommunity(telegramUser: User, communityCode: String): UserData {
        return userMapStruct.toUserData(
            userService.switchUserCommunity(telegramUser.id, communityCode))
    }

    fun checkEntry (telegramUser: User): EntryData {
        val user = checkUser(telegramUser)

        val entry = entryService.createEntry(user)

        return entryMapStruct.toEntryData(entry)
    }

    fun getEntryImages(telegramUser: User): List<ImageData> {
        val user = checkUser(telegramUser)

        return entryService.getEntryImages(user)
            .map { entryMapStruct.toImageData(it) }
    }

    fun getEntryImagesInfo(entryId: Int): List<ImageData> {
        val images = entryService.getEntryImagesInfo(entryId)

        return images.map { entryMapStruct.toImageData(it) }
    }

    fun getEntries (communityCode: String): List<EntryData> {
        val community = communityService.getCommunity(communityCode) ?: throw CommunityNotFoundException()

        val entries = entryService.getCurrentEntries(community)

        return entries.map { entryMapStruct.toEntryData(it) }
    }

    fun postPicture (imageToSave: ImageToSave): EntryData {
        val user = checkUser(imageToSave.telegramUser)

        entryService.addEntryImage(
            ImageDto(
                user,
                imageToSave.isReady,
                imageToSave.fileId
            ))

        return checkEntry(imageToSave.telegramUser)
    }

    fun getCurrentContest (telegramUser: User): ContestData {
        val user = checkUser(telegramUser)

        val contest = contestService.currentContest(user.community!!)
            ?: throw ContestNotFoundException()

        return entryMapStruct.toContestData(contest)
    }

    fun getCurrentDraftContest (communityCode: String): ContestData? {
        val community = communityService.getCommunity(communityCode) ?: throw CommunityNotFoundException()

        return contestService.communityDraftContest(community)
            ?.let { entryMapStruct.toContestData(it) }
    }

    fun saveContest(contest: Contest): ContestData {
        return entryMapStruct.toContestData(contestService.saveContest(contest))
    }

    fun saveContest(contestData: ContestData): ContestData {
        val contest = entryMapStruct.toContest(contestData)
        val saved =contestService.saveContest(contest)

        return entryMapStruct.toContestData(saved)
    }

    fun checkUser (telegramUser: User): WarcomUser {
        if (telegramUser.isBot)
            throw BotRequesterException()

        return try {
            val user = userService.getUserByTelegramId(telegramUser.id)

            if (user.community == null)
                throw UserWithoutCommunityException()
            user
        } catch (e: UserNotFoundException) {
            val username = if (telegramUser.userName.isNullOrBlank()) "user_${telegramUser.id}" else telegramUser.userName
            userService.createUser(UserDto(telegramId = telegramUser.id, username = username))
        }
    }

    fun checkAdmin (telegramUser: User): WarcomUser {
        if (telegramUser.isBot)
            throw BotRequesterException()

        val user = checkUser(telegramUser)

        if (user.community == null)
            throw UserWithoutCommunityException()

        if (!user.isAdmin)
            throw UserNotAdminException()

        return user
    }

    fun fetchAdminCommunities(telegramUser: User): List<Community> {
        val admin = checkAdmin(telegramUser)

        return communityService.getAdminCommunities(admin)
    }
}