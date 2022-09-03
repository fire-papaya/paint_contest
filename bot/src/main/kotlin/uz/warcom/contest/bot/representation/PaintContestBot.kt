package uz.warcom.contest.bot.representation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.PhotoSize
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import uz.warcom.contest.bot.config.BotConfiguration
import uz.warcom.contest.bot.exception.BotException
import uz.warcom.contest.bot.model.*
import uz.warcom.contest.bot.model.enum.Commands
import uz.warcom.contest.bot.model.enum.UserState
import uz.warcom.contest.bot.service.AdminService
import uz.warcom.contest.bot.service.PersistenceFacade
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.Serializable
import javax.imageio.ImageIO


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
//                val sendPhoto = SendPhoto()
//                sendPhoto.chatId = it.chatId().toString()
//                sendPhoto.photo = InputFile()

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
                    messageBuffer.append(it.user).append(": ")
                        .append(if (it.isPrimed) '\u2714'.toString() else "")
                        .append(if (it.isReady) '\u2705'.toString() else "")
                        .append("\n")
                }

                silent.send(messageBuffer.toString(), messageContext.chatId())
            }
            .build()
    }

    fun croppedImages (): Ability {
        return Ability
            .builder()
            .name(Commands.CROPPED)
            .info("Retrieve cropped images")
            .locality(Locality.ALL)
            .privacy(Privacy.ADMIN)
            .action { messageContext ->
                try {
                    val cropped = adminService.getEntryImage(messageContext.user())
                    val sendPhoto = SendPhoto()
                    sendPhoto.chatId = messageContext.chatId().toString()

                    val os = ByteArrayOutputStream()
                    ImageIO.write(cropped[0], "jpeg", os)

                    val inputStream: InputStream = ByteArrayInputStream(os.toByteArray())
                    sendPhoto.photo = InputFile(inputStream, "myImage.jpg")
                    // Execute the method
                    execute(sendPhoto)
                } catch (e: TelegramApiException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            .build()
    }

    fun processImage(): Ability {
        return Ability.builder()
            .name(DEFAULT)
            .flag(Flag.DOCUMENT.or(Flag.PHOTO))
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .input(0)
            .action {
                val state = getUserState(it.user().id)
                var entry: EntryData?

                if (it.update().message.hasDocument()) {
                    silent.send("Файлы и Документы не поддерживаются, отправь изображение как фотографию", it.chatId())
                    return@action
                }


                try {
                    if (state == UserState.PRIME || state == UserState.READY) {
                        downloadPhoto(it.update()).use { ist ->
                            val bytes = ist.readAllBytes()

                            entry = persistenceFacade.postPicture(ImageToSave(
                                it.user(),
                                bytes,
                                state == UserState.READY
                            ))
                        }

                        val message = if (state == UserState.PRIME)
                            "Изображение получено, используй команду /${Commands.READY} , когда закончишь покрас"
                        else
                            "Изображение получено: ${entry?.images?.filter { img -> img.isReady }?.size ?: 0}/3"

                        silent.send(message, it.chatId())
                    }
                } catch (e: BotException) {
                    silent.send(e.message, it.chatId())
                }
            }
            .build()
    }

    fun checkPhoto(update: Update): PhotoSize {
        // Check that the update contains a message and the message has a photo
        if (update.message?.hasPhoto() != true)
            throw IllegalStateException("No photos were found in message")

        val photos = update.message.photo

        return photos.stream()
            .max(Comparator.comparing { obj: PhotoSize -> obj.fileSize })
            .orElseThrow { IllegalStateException("No photos were found in photos stream") }
    }

    fun getFilePath(photo: PhotoSize): String {
        return if (photo.filePath != null) photo.filePath
        else {
            // We create a GetFile method and set the file_id from the photo
            val getFileMethod = GetFile()
            getFileMethod.fileId = photo.fileId
            // We execute the method using AbsSender::execute method.
            val file = this.execute(getFileMethod)
            file.filePath
        }
    }

    fun downloadPhoto(filePath: String): InputStream {
        return this.downloadFileAsStream(filePath)
    }

    fun downloadPhoto(update: Update): InputStream {
        val filePath = if (update.message.hasPhoto()) {
            val photoSize = checkPhoto(update)
            getFilePath(photoSize)
        } else throw RuntimeException("No photo received")
            // getFilePath(update.message.document)

        return downloadPhoto(filePath)
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
}