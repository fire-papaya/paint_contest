package uz.warcom.contest.bot.representation

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import uz.warcom.contest.bot.config.BotConfiguration
import uz.warcom.contest.bot.exception.BotException
import uz.warcom.contest.bot.model.ImageToSave
import uz.warcom.contest.bot.model.enums.UserState
import uz.warcom.contest.bot.service.AdminService
import uz.warcom.contest.bot.service.PersistenceFacade


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

    private val contestStates = setOf(UserState.PRIMED, UserState.PAINTED)

    fun processImage(): Ability {
        return Ability.builder()
            .name(DEFAULT)
            .flag(Flag.PHOTO.and { userState(it) in contestStates })
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                val state = userState(it.user().id)

                try {
                    val photo = retrieveHighestQuality(it.update().message.photo)

                    val entry = persistenceFacade.postPicture(ImageToSave(
                        it.user(),
                        state == UserState.PAINTED,
                        photo.fileId
                    ))

                    val message = if (state == UserState.PRIMED) {
                        updateUserState(it.user().id, UserState.PAINTED)
                        "Изображение получено. Отправь три изображения покрашенной миниатюры, когда закончишь покрас"
                    } else {
                        "Изображение получено: ${entry.images.filter { img -> img.isReady }.size}/3"
                    }

                    silent.send(message, it.chatId())
                } catch (e: BotException) {
                    silent.send(e.message, it.chatId())
                }
            }
            .build()
    }

    fun retrieveHighestQuality(photos: List<PhotoSize>): PhotoSize {
        return photos.stream()
            .max(Comparator.comparing { obj: PhotoSize -> obj.fileSize })
            .orElseThrow { IllegalStateException("No photos were found in photos stream") }
    }

    private fun updateUserState (userId: Long, state: UserState) {
        db.getMap<String, Any>("USER_STATES").entries.forEach{ ent -> println("${ent.key} : ${ent.value}") }
        val states: MutableMap<String, String> = db.getMap("USER_STATES")
        states[userId.toString()] = state.toString()
    }

    private fun userState (userId: Long): UserState {
        return UserState.valueOf(db.getMap<String, String>("USER_STATES").getOrDefault(userId.toString(), "START"))
    }

    private fun userState(update: Update): UserState {
        return this.userState(update.message.from.id)
    }

    override fun creatorId(): Long {
        return botConfiguration.creator
    }

    companion object {
        private val logger = LogManager.getLogger(PaintContestBot::class.java)
    }
}