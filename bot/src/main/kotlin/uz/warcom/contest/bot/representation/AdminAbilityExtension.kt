package uz.warcom.contest.bot.representation

import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.*
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import uz.warcom.contest.bot.model.enums.ButtonLabel
import uz.warcom.contest.bot.model.enums.Commands
import uz.warcom.contest.bot.model.enums.EmojiCmd
import uz.warcom.contest.bot.model.enums.UserState
import uz.warcom.contest.bot.service.AdminService
import uz.warcom.contest.bot.util.PredicateBuilder.startsWith

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

    fun createContest() : Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val user = extractUser(upd)
            updateUserState(user, UserState.SET_NAME)
            adminService.createContest(user)

            val message = contestMenuMessage(user, "Отправь название конкурса")

            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.CREATE_CONTEST))
    }

    fun updateContestName() : Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val user = extractUser(upd)
            updateUserState(user, UserState.SET_NAME)
            adminService.currentDraftContest(user)

            val message = contestMenuMessage(user, "Отправь название конкурса")

            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.UPDATE_NAME))
    }

    fun updateContestDescription() : Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val user = extractUser(upd)
            updateUserState(user, UserState.SET_DESCRIPTION)
            adminService.currentDraftContest(user)

            val message = contestMenuMessage(user, "Отправь название конкурса")

            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.UPDATE_NAME))
    }

    fun updateContestDates() : Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val user = extractUser(upd)
            updateUserState(user, UserState.SET_DATES)
            adminService.currentDraftContest(user)

            val message = contestMenuMessage(user, "Отправь даты проведения конкурса в формате DDMMYY DDMMYY" +
                    ", пример: `010224 310324` (1 фев 2024 - 31 мар 2024)")

            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.UPDATE_DATES))
    }

    private fun contestMenuMessage(user: User, text: String): SendMessage {
        val message = SendMessage()

        val keyboard = ReplyKeyboardMarkup()

        val row1 = KeyboardRow().also {
            it.add(ButtonLabel.DRAFT_CONTEST)
            it.add(ButtonLabel.UPDATE_NAME)
        }

        val row2 = KeyboardRow().also {
            it.add(ButtonLabel.UPDATE_DESCRIPTION)
            it.add(ButtonLabel.UPDATE_DATES)
        }

        val row3 = KeyboardRow().also {
            it.add(ButtonLabel.SUBMIT_CONTEST)
            it.add(ButtonLabel.HOME)
        }

        keyboard.keyboard = arrayListOf(row1, row2, row3)
        keyboard.resizeKeyboard = true

        message.replyMarkup = keyboard
        message.chatId = user.id.toString()
        message.text = text
        return message
    }


    private fun updateUserState (userId: Long, state: UserState) {
        db.getMap<String, Any>("USER_STATES").entries.forEach{ ent -> println("${ent.key} : ${ent.value}") }
        val states: MutableMap<String, String> = db.getMap("USER_STATES")
        states[userId.toString()] = state.toString()
    }

    private fun updateUserState (user: User, state: UserState) {
        updateUserState(user.id, state)
    }

    private fun extractUser (update: Update): User {
        return update.message.from
    }

    private fun userState (userId: Long): UserState {
        return UserState.valueOf(db.getMap<String, String>("USER_STATES").getOrDefault(userId.toString(), "START"))
    }

    private fun userState(update: Update): UserState {
        return this.userState(update.message.from.id)
    }
}