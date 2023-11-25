package uz.warcom.contest.bot.representation

import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.Ability
import org.telegram.abilitybots.api.objects.Locality
import org.telegram.abilitybots.api.objects.Privacy
import org.telegram.abilitybots.api.util.AbilityExtension
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import uz.warcom.contest.bot.model.enums.Commands
import uz.warcom.contest.bot.model.enums.UserState
import uz.warcom.contest.bot.service.PersistenceFacade
import uz.warcom.contest.persistence.exception.ContestNotFoundException
import uz.warcom.contest.persistence.exception.UserWithoutCommunityException


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
                silent.send(
                    "Привет! Это бот для подачи твоих работ на конкурс покраса от WarComUz. " +
                            "Для начала выбери себе комьюнити используя команду `/communities`",
                    it.chatId()
                )
            }
            .post { updateUserState(it.user().id, UserState.START) }
            .build()
    }

    fun community () : Ability {
        return Ability
            .builder()
            .name(Commands.COMMUNITIES)
            .info("Show current community info and available communities")
            .locality(Locality.ALL)
            .privacy(Privacy.PUBLIC)
            .action {  messageContext ->
                val communityList = persistenceFacade.getCommunities()
                val info_message = "На данный момент ты часть комьюнити " +
                        try { persistenceFacade.getUser(messageContext.user()).community?.name ?: "бомжей" }
                        catch (e: UserWithoutCommunityException) { "бомжей" } +
                        " выбери себе комьюнити и используй его код вместе с командой \n/switch_community ," +
                        " например: \n`/switch_community WRCM`"

                val message = "Список доступных комьюнити:\nКод - Комьюнити\n" +
                        communityList.map { it.label + "  -  " + it.name + "\n" }
                            .joinToString(separator = "")


                silent.send(message + "\n" + info_message, messageContext.chatId())
            }
            .build()
    }

    fun switchCommunity (): Ability {
        return Ability
            .builder()
            .name(Commands.SWITCH_COMMUNITY)
            .info("Switch to a different community")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .input(1)
            .action {
                val communityCode = it.firstArg()
                val telegramUser = it.user()
                val data = persistenceFacade.switchUserCommunity(telegramUser, communityCode)

                silent.send("Твоё новое комьюнити: ${data.community?.name}", it.chatId())
            }
            .post { updateUserState(it.user().id, UserState.START) }
            .build()
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

    fun check () : Ability {
        return Ability
            .builder()
            .name(Commands.CHECK)
            .info("Review current submission")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .action { messageContext ->
                try {
                    val images = persistenceFacade.getEntryImages(messageContext.user())
                    val sendAlbum = SendMediaGroup()
                    sendAlbum.chatId = messageContext.chatId().toString()
                    sendAlbum.medias = images.map { InputMediaPhoto(it.telegramFileId!!) }
                    sendAlbum.medias[0].caption = "Вот твоя работа"
                    // Execute the method
                    bot.execute(sendAlbum)
                } catch (e: ContestNotFoundException) {
                    silent.send(
                        "На данный момент нет активных конкурсов, загляни позже",
                        messageContext.chatId())
                } catch (e: UserWithoutCommunityException) {
                    silent.send(
                        "Ты не привязан ни к одному из существующих комьюнити. Выбери комьюнити используя команду /community",
                        messageContext.chatId())
                }

            }
            .build()
    }

    fun contest () : Ability {
        return Ability
            .builder()
            .name(Commands.CONTEST)
            .info("Retrieve current contest")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .action {
                val message = try {
                    persistenceFacade.getCurrentContest(it.user()).toMessage()
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

    private fun updateUserState (userId: Long, state: UserState) {
        db.getMap<String, Any>("USER_STATES").entries.forEach{ ent -> println("${ent.key} : ${ent.value}") }
        val states: MutableMap<String, String> = db.getMap("USER_STATES")
        states[userId.toString()] = state.toString()
    }
}