package uz.warcom.contest.bot.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import uz.warcom.contest.bot.exception.BotRequesterException
import uz.warcom.contest.bot.model.ContestData
import uz.warcom.contest.bot.model.EntryData
import uz.warcom.contest.bot.model.ImageToSave
import uz.warcom.contest.bot.model.UserData
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

    fun getEntry (telegramUser: User): EntryData {
        val user = checkUser(telegramUser)

        val entry = entryService.createEntry(user)

        return entryMapStruct.toEntryData(entry)
    }

    fun postPicture (imageToSave: ImageToSave): EntryData {
        val user = checkUser(imageToSave.telegramUser)

        entryService.addEntryImage(
            ImageDto(
                imageToSave.bytes,
                user,
                imageToSave.isReady
            ))

        return getEntry(imageToSave.telegramUser)
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