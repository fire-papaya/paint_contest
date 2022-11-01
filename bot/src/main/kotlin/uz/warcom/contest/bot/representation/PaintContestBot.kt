package uz.warcom.contest.bot.representation

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import uz.warcom.contest.bot.config.BotConfiguration
import uz.warcom.contest.bot.exception.BotException
import uz.warcom.contest.bot.model.EntryData
import uz.warcom.contest.bot.model.ImageToSave
import uz.warcom.contest.bot.model.enum.Commands
import uz.warcom.contest.bot.model.enum.UserState
import uz.warcom.contest.bot.service.AdminService
import uz.warcom.contest.bot.service.PersistenceFacade
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import java.io.IOException
import java.io.InputStream


@Component
class PaintContestBot
@Autowired constructor(
    private val botConfiguration: BotConfiguration,
    private val persistenceFacade: PersistenceFacade,
    private val adminService: AdminService
): AbilityBot(botConfiguration.token, botConfiguration.username) {

    fun start () : Ability {
        return Ability
            .builder()
            .name(Commands.START)
            .info("Greets user")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                silent.send(
                    "Привет! Это бот для подачи твоих работ на конкурс покраса от WarComUz.",
                    it.chatId()
                )
            }
            .post { updateUserState(it.user().id, UserState.START) }
            .build()
    }

    fun code () : Ability {
        return Ability
            .builder()
            .name(Commands.CODE)
            .info("Generate code for submission")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    val entry = persistenceFacade.checkEntry(it.user())
                    "Твой код: ${entry.code}, используй команду /${Commands.PRIME} для продолжения процесса подачи " +
                            "работы на конкурс"
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                }

                silent.send(message, it.chatId())
            }
            .post { updateUserState(it.user().id, UserState.CODE) }
            .build()
    }

    fun prime () : Ability {
        return Ability
            .builder()
            .name(Commands.PRIME)
            .info("Prompt to submit primed miniature")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    val entry = persistenceFacade.checkEntry(it.user())
                    "Отправь изображение собранной и/или загрунтованной миниатюры с кодом ${entry.code}. " +
                            "Если ранее была уже отправлена фотография, то она будет заменена на новую"
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                }

                silent.send(message, it.chatId())
            }
            .post { updateUserState(it.user().id, UserState.PRIME) }
            .build()
    }

    fun ready () : Ability {
        return Ability
            .builder()
            .name(Commands.READY)
            .info("Prompt to submit finished miniature")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    val entry = persistenceFacade.checkEntry(it.user())

                    if (entry.images.find { img -> !img.isReady } == null)
                        "Сначала загрузи изображение собранной и/или загрунтованной миниатюры, используя команду /${Commands.PRIME}"
                    else
                        "Отправь три изображения покрашенной миниатюры"
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                }

                silent.send(
                    message, it.chatId()
                )
            }
            .post { updateUserState(it.user().id, UserState.READY) }
            .build()
    }

    fun check () : Ability {
        return Ability
            .builder()
            .name(Commands.CHECK)
            .info("Review current submission")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
//
//                try {
//                    val images = adminService.getEntryImagesInfo(entryId)
//                    val sendAlbum = SendMediaGroup()
//                    sendAlbum.chatId = messageContext.chatId().toString()
//                    sendAlbum.medias = images.map { InputMediaPhoto(it.telegramFileId!!) }
//                    sendAlbum.medias[0].caption = "Entry $entryId"
//                    // Execute the method
//                    execute(sendAlbum)
//                } catch (e: TelegramApiException) {
//                    logger.error(e)
//                } catch (e: IOException) {
//                    logger.error(e)
//                }
//
                silent.send(
                    "Вот твоя работа",
                    it.chatId()
                )
            }
            .build()
    }

    fun contest () : Ability {
        return Ability
            .builder()
            .name(Commands.CONTEST)
            .info("Retrieve current contest")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    persistenceFacade.getCurrentContest().toMessage()
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                }

                silent.send(
                    message, it.chatId()
                )
            }
            .build()
    }

    fun entries () : Ability {
        return Ability
            .builder()
            .name(Commands.ENTRIES)
            .info("Retrieve information about current entries")
            .locality(Locality.ALL)
            .privacy(Privacy.ADMIN)
            .action { messageContext ->
                val summary = adminService.getEntriesSummary()
                val messageBuffer = StringBuffer().append("Общее кол-во заявок: " + summary.usersMap.size + "\n")
                summary.usersMap.values.forEach {
                    messageBuffer.append(it.user)
                        .append(" [").append(it.id).append("]: ")
                        .append(if (it.isPrimed) '\u2714'.toString() else "")
                        .append(if (it.isReady) '\u2705'.toString() else "")
                        .append("\n")
                }

                silent.send(messageBuffer.toString(), messageContext.chatId())
            }
            .build()
    }

    fun entryImages (): Ability {
        return Ability
            .builder()
            .name(Commands.ENTRY)
            .info("Retrieve entry images")
            .locality(Locality.ALL)
            .privacy(Privacy.ADMIN)
            .input(1)
            .action { messageContext ->
                val entryId = messageContext.firstArg().toIntOrNull()
                if (entryId == null) {
                    silent.send("No entry id was given", messageContext.chatId())
                    return@action
                }

                try {
                    val images = adminService.getEntryImagesInfo(entryId)
                    val sendAlbum = SendMediaGroup()
                    sendAlbum.chatId = messageContext.chatId().toString()
                    sendAlbum.medias = images.map { InputMediaPhoto(it.telegramFileId!!) }
                    sendAlbum.medias[0].caption = "Entry $entryId"
                    // Execute the method
                    execute(sendAlbum)
                } catch (e: TelegramApiException) {
                    logger.error(e)
                } catch (e: IOException) {
                    logger.error(e)
                }
            }
            .build()
    }

    fun processImage(): Ability {
        return Ability.builder()
            .name(DEFAULT)
            .flag(Flag.PHOTO)
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .input(0)
            .action {
                val state = getUserState(it.user().id)
                if (state != UserState.PRIME && state != UserState.READY)
                    return@action

                val entry: EntryData

                try {
                    val photo = retrieveHighestQuality(it.update().message.photo)

                    entry = persistenceFacade.postPicture(ImageToSave(
                        it.user(),
                        state == UserState.READY,
                        photo.fileId
                    ))

                    val message = if (state == UserState.PRIME)
                        "Изображение получено, используй команду /${Commands.READY} , когда закончишь покрас"
                    else
                        "Изображение получено: ${entry.images.filter { img -> img.isReady }.size}/3"

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

    private fun getUserState (userId: Long): UserState {
        return UserState.valueOf(db.getMap<String, String>("USER_STATES").getOrDefault(userId.toString(), "START"))
    }

    override fun creatorId(): Long {
        return botConfiguration.creator
    }

    companion object {
        private val logger = LogManager.getLogger(PaintContestBot::class.java)
    }
}