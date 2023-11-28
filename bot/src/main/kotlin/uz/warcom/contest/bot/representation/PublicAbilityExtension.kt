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
import uz.warcom.contest.bot.service.PersistenceFacade
import uz.warcom.contest.persistence.exception.*


class PublicAbilityExtension (
    private val bot: AbilityBot,
    private val persistenceFacade: PersistenceFacade
): AbilityExtension {
    private val silent = bot.silent()
    private val db = bot.db()

    fun start () : Ability {
        return Ability
            .builder()
            .name(Commands.START)
            .info("Greets user")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {
                updateUserState(it.user().id, UserState.START)

                val messageText = "Это бот для подачи твоих работ на конкурсы покраса от WarComUz. " +
                        "Если ты еще не состоишь ни в одном комьюнити, выбери один в разделе " +
                        "${EmojiCmd.COMMUNITY} Мой комьюнити\""

                val sendMessage = mainMenuMessage(it.user(), messageText)

                bot.execute(sendMessage)
            }
            .build()
    }

    fun communityReply(): Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val communityList = persistenceFacade.getCommunities()
            val userCommunity = try {
                persistenceFacade.getUser(upd.message.from).community }
            catch (e: UserWithoutCommunityException) { null }

            val text = "На данный момент ты часть комьюнити " + (userCommunity?.name ?: "бомжей") +
                    " выбери себе комьюнити из меню"

            val keyboard = ReplyKeyboardMarkup()
            val rows = arrayListOf<KeyboardRow>()
            var row = KeyboardRow()

            communityList.forEachIndexed { index, community ->
                val icon = if (userCommunity?.label.equals(community.label, ignoreCase = true)) EmojiCmd.CONFIRM_COMMUNITY
                else EmojiCmd.SWITCH_COMMUNITY

                row.add("$icon ${community.label} (${community.name})")

                if (index % 2 != 0 || index == communityList.size - 1) {
                    rows.add(row)
                    row = KeyboardRow()
                }
            }

            rows.add(KeyboardRow().also { it.add("${EmojiCmd.HOME} Домой") })

            keyboard.keyboard = rows
            keyboard.resizeKeyboard = true

            val message = SendMessage()
            message.replyMarkup = keyboard
            message.chatId = upd.message.chatId.toString()
            message.text = text
            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.COMMUNITY))
    }

    fun switchCommunity (): Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val communityCode = upd.message.text.split(" ").filter { it.isNotEmpty() }[1]
            val telegramUser = extractUser(upd)

            val data = persistenceFacade.switchUserCommunity(telegramUser, communityCode)

            val text = "Твой новый комьюнити: ${data.community?.name}"

            val message = mainMenuMessage(telegramUser, text)
            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.SWITCH_COMMUNITY))
    }

    fun code () : Ability {
        return Ability
            .builder()
            .name(Commands.CODE)
            .info("Generate code for submission")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    val entry = persistenceFacade.checkEntry(it.user())
                    updateUserState(it.user().id, UserState.PRIMED)
                    "Твой код: ${entry.code}, используй команду /${Commands.PRIME} для продолжения процесса подачи " +
                            "работы на конкурс"
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                } catch (e: UserWithoutCommunityException) {
                    "Ты не привязан ни к одному из существующих комьюнити. Выбери комьюнити используя команду /community"
                }

                silent.send(message, it.chatId())
            }
            .build()
    }

    fun prime () : Ability {
        return Ability
            .builder()
            .name(Commands.PRIME)
            .info("Prompt to submit primed miniature")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    val entry = persistenceFacade.checkEntry(it.user())
                    updateUserState(it.user().id, UserState.PRIMED)
                    "Отправь изображение собранной и/или загрунтованной миниатюры с кодом ${entry.code}. " +
                            "Если ранее была уже отправлена фотография, то она будет заменена на новую"
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                } catch (e: UserWithoutCommunityException) {
                    "Ты не привязан ни к одному из существующих комьюнити. Выбери комьюнити используя команду /community"
                }

                silent.send(message, it.chatId())
            }
            .build()
    }

    fun ready () : Ability {
        return Ability
            .builder()
            .name(Commands.READY)
            .info("Prompt to submit finished miniature")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    val entry = persistenceFacade.checkEntry(it.user())

                    if (entry.images.find { img -> !img.isReady } == null) {
                        updateUserState(it.user().id, UserState.PRIMED)
                        "Сначала загрузи изображение собранной и/или загрунтованной миниатюры с кодом ${entry.code}"
                    } else {
                        updateUserState(it.user().id, UserState.PAINTED)
                        "Отправь три изображения покрашенной миниатюры"
                    }
                } catch (e: ContestNotFoundException) {
                    "На данный момент нет активных конкурсов, загляни позже"
                } catch (e: UserWithoutCommunityException) {
                    "Ты не привязан ни к одному из существующих комьюнити. Выбери комьюнити используя команду /community"
                }

                silent.send(
                    message, it.chatId()
                )
            }
            .build()
    }

    fun check () : Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val user = extractUser(upd)
            val message = try {
                val images = persistenceFacade.getEntryImages(extractUser(upd))
                val sendAlbum = SendMediaGroup()
                sendAlbum.chatId = extractUser(upd).id.toString()
                sendAlbum.medias = images.map { InputMediaPhoto(it.telegramFileId!!) }
                sendAlbum.medias[0].caption = "Вот твоя работа"

                bot.executeAsync(sendAlbum)
                entryMenuMessage(user, "Ты можешь заменить фотографии, используя соответствующие кнопки")
            } catch (e: EntryNotFoundException) {
                entryMenuMessage(user, "Для создания заявки сгенерируй код, используя ${ButtonLabel.CODE}")
            } catch (e: NoPrimedImageException) {
                entryMenuMessage(user, "Отправь изображение непокрашенной модели")
            } catch (e: NoPaintedImageException) {
                entryMenuMessage(user, "Отправь 3 изображения покрашенной модели")
            } catch (e: ContestNotFoundException) {
                mainMenuMessage(user, "На данный момент нет активных конкурсов, загляни позже")
            } catch (e: UserWithoutCommunityException) {
                mainMenuMessage(user, "Ты не привязан к комьюнити. Выбери комьюнити в разделе ${ButtonLabel.COMMUNITY}")
            }

            bot.execute(message)
        }

        return Reply.of(action, startsWith(EmojiCmd.SUBMISSION))
    }

    fun contest () : Reply {
        val action: (BaseAbilityBot, Update) -> Unit = { _, upd ->
            val message = try {
                persistenceFacade.getCurrentContest(extractUser(upd)).toMessage()
            } catch (e: ContestNotFoundException) {
                "На данный момент нет активных конкурсов, загляни позже"
            } catch (e: UserWithoutCommunityException) {
                "Ты не привязан к комьюнити. Выбери комьюнити в разделе \"${EmojiCmd.COMMUNITY} Мой комьюнити\""
            }

            silent.send(message, extractUser(upd).id)
        }

        return Reply.of(action, startsWith(EmojiCmd.CONTEST))
    }

    fun startsWith(flag: String): (Update) -> Boolean {
        return {
                upd: Update -> upd.message?.text?.startsWith("$flag ") ?: false
        }
    }

    private fun mainMenuMessage (user: User, text: String = "Главное меню") : SendMessage {
        val message = SendMessage()

        val keyboard = ReplyKeyboardMarkup()

        val row1 = KeyboardRow().also {
            it.add(ButtonLabel.CONTEST)
            it.add(ButtonLabel.SUBMISSION)
        }

        val row2 =  KeyboardRow().also {
            it.add(ButtonLabel.COMMUNITY)
        }

        keyboard.keyboard = arrayListOf(row1, row2)
        keyboard.resizeKeyboard = true

        message.replyMarkup = keyboard
        message.chatId = user.id.toString()
        message.text = text
        return message
    }

    private fun entryMenuMessage (user: User, text: String): SendMessage {
        val message = SendMessage()

        val keyboard = ReplyKeyboardMarkup()

        val row1 = KeyboardRow().also {
            it.add(ButtonLabel.SUBMISSION)
            it.add(ButtonLabel.CODE)
        }

        val row2 =  KeyboardRow().also {
            it.add(ButtonLabel.PRIMED)
            it.add(ButtonLabel.PAINTED)
        }

        val row3 =  KeyboardRow().also {
            it.add(ButtonLabel.HOME)
        }

        keyboard.keyboard = arrayListOf(row1, row2, row3)
        keyboard.resizeKeyboard = true

        message.replyMarkup = keyboard
        message.chatId = user.id.toString()
        message.text = text
        return message
    }

    private fun extractUser (update: Update): User {
        return update.message.from
    }

    private fun updateUserState (userId: Long, state: UserState) {
        db.getMap<String, Any>("USER_STATES").entries.forEach{ ent -> println("${ent.key} : ${ent.value}") }
        val states: MutableMap<String, String> = db.getMap("USER_STATES")
        states[userId.toString()] = state.toString()
    }
}