package uz.warcom.contest.bot.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import uz.warcom.contest.bot.exception.BotRequesterException
import uz.warcom.contest.bot.model.*
import uz.warcom.contest.bot.model.mapper.EntryMapStruct
import uz.warcom.contest.bot.model.mapper.UserMapStruct
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.dto.ImageDto
import uz.warcom.contest.persistence.dto.UserDto
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import uz.warcom.contest.persistence.exception.UserNotFoundException
import uz.warcom.contest.persistence.service.ContestService
import uz.warcom.contest.persistence.service.EntryService
import uz.warcom.contest.persistence.service.UserService
import java.awt.image.BufferedImage

@Service
class PersistenceFacade
@Autowired constructor(
    private val entryService: EntryService,
    private val userService: UserService,
    private val contestService: ContestService,
    private val userMapStruct: UserMapStruct,
    private val entryMapStruct: EntryMapStruct
){
    fun getUser (telegramUser: User): UserData {
        return userMapStruct.toUserData(checkUser(telegramUser))
    }

    fun checkEntry (telegramUser: User): EntryData {
        val user = checkUser(telegramUser)

        val entry = entryService.createEntry(user)

        return entryMapStruct.toEntryData(entry)
    }

    fun getEntryImages(telegramUser: User): List<BufferedImage> {
        val user = checkUser(telegramUser)

        return entryService.getEntryImages(user)
    }

    fun getEntryImagesInfo(entryId: Int): List<ImageData> {
        val images = entryService.getEntryImagesInfo(entryId)

        return images.map { entryMapStruct.toImageData(it) }
    }

    fun getEntries (): List<EntryData> {
        val entries = entryService.getCurrentEntries()

        return entries.map { entryMapStruct.toEntryData(it) }
    }

    fun postPicture (imageToSave: ImageToSave): EntryData {
        val user = checkUser(imageToSave.telegramUser)

        entryService.addEntryImage(
            ImageDto(
                imageToSave.bytes,
                user,
                imageToSave.isReady,
                imageToSave.fileId
            ))

        return checkEntry(imageToSave.telegramUser)
    }

    fun getCurrentContest (): ContestData {
        val contest = contestService.currentContest() ?: throw ContestNotFoundException()

        return entryMapStruct.toContestData(contest)
    }

    private fun checkUser (telegramUser: User): WarcomUser {
        if (telegramUser.isBot)
            throw BotRequesterException()

        return try {
            userService.getUserByTelegramId(telegramUser.id)
        } catch (e: UserNotFoundException) {
            userService.createUser(UserDto(telegramId = telegramUser.id, username = telegramUser.userName))
        }
    }
}