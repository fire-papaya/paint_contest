package uz.warcom.contest.bot.representation

import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import uz.warcom.contest.bot.model.enum.Commands
import uz.warcom.contest.bot.service.AdminService

class AdminAbilityExtension (
    private val bot: AbilityBot,
    private val adminService: AdminService
): AbilityExtension {
    private val silent = bot.silent()

    fun entries () : Ability {
        return Ability
            .builder()
            .name(Commands.ENTRIES)
            .info("Retrieve information about current entries")
            .locality(Locality.ALL)
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
            .locality(Locality.ALL)
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
}