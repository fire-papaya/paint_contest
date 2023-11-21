package uz.warcom.contest.bot.representation

import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Flag
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import uz.warcom.contest.bot.model.enum.Commands
import uz.warcom.contest.bot.model.enum.UserState
import uz.warcom.contest.bot.service.AdminService

class AdminAbilityExtension (
    private val bot: AbilityBot,
    private val adminService: AdminService
): AbilityExtension {
    private val silent = bot.silent()
    private val db = bot.db()

    fun entries () : Ability {
        return Ability
            .builder()
            .name(Commands.ENTRIES)
            .info("Retrieve information about current entries")
            .locality(Locality.USER)
            .privacy(Privacy.ADMIN)
            .input(1)
            .action { messageContext ->
                val communityCode = messageContext.firstArg()
                val summary = adminService.getEntriesSummary(communityCode)
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
            .locality(Locality.USER)
            .privacy(Privacy.ADMIN)
            .input(1)
            .action { messageContext ->
                val entryId = messageContext.firstArg().toIntOrNull()
                if (entryId == null)
                    silent.send("No entry id was given", messageContext.chatId())
                else {
                    val images = adminService.getEntryImagesInfo(entryId)
                    val sendAlbum = SendMediaGroup()
                    sendAlbum.chatId = messageContext.chatId().toString()
                    sendAlbum.medias = images.map { InputMediaPhoto(it.telegramFileId!!) }
                    sendAlbum.medias[0].caption = "Entry $entryId"
                    bot.execute(sendAlbum)
                }
            }
            .build()
    }

    fun createContest (): Ability {
        return Ability
            .builder()
            .name(Commands.CREATE_CONTEST)
            .info("Create new contest")
            .locality(Locality.USER)
            .privacy(Privacy.ADMIN)
            .action { messageContext ->
                updateUserState(messageContext.user().id, UserState.CREATE_CONTEST)
            }
            .build()
    }

    fun updateContest (): Ability {
        return Ability
            .builder()
            .name(Commands.UPDATE_CONTEST)
            .info("Update new contest")
            .locality(Locality.USER)
            .privacy(Privacy.ADMIN)
            .input(1)
            .action { messageContext ->
                updateUserState(messageContext.user().id, UserState.CREATE_CONTEST)
            }
            .build()
    }

    fun processTextMessage (): Ability {
        return Ability
            .builder()
            .name(Commands.CREATE_CONTEST)
            .info("Retrieve entry images")
            .locality(Locality.USER)
            .privacy(Privacy.ADMIN)
            .flag(Flag.MESSAGE)
            .action { messageContext ->
            /* Todo implement logic for processing names etc. */
            }
            .build()
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
}