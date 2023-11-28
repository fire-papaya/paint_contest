package uz.warcom.contest.bot.representation

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.*
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import uz.warcom.contest.bot.config.BotConfiguration
import uz.warcom.contest.bot.exception.BotException
import uz.warcom.contest.bot.model.ImageToSave
import uz.warcom.contest.bot.model.enums.ButtonLabel
import uz.warcom.contest.bot.model.enums.Commands
import uz.warcom.contest.bot.model.enums.EmojiCmd
import uz.warcom.contest.bot.model.enums.UserState
import uz.warcom.contest.bot.service.AdminService
import uz.warcom.contest.bot.service.PersistenceFacade
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import uz.warcom.contest.persistence.exception.UserWithoutCommunityException


@Component
class PaintContestBot
@Autowired
constructor(
    private val botConfiguration: BotConfiguration,
    private val persistenceFacade: PersistenceFacade,
    private val adminService: AdminService
): AbilityBot(botConfiguration.token, botConfiguration.username) {

    fun adminExtensions(): AbilityExtension {
        return AdminAbilityExtension(this, adminService)
    }

    fun publicAbilityExtension(): AbilityExtension {
        return PublicAbilityExtension(this, persistenceFacade)
    }

    override fun creatorId(): Long {
        return botConfiguration.creator
    }

    companion object {
        private val logger = LogManager.getLogger(PaintContestBot::class.java)
    }
}