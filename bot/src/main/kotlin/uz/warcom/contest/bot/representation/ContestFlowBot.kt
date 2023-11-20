package uz.warcom.contest.bot.representation

import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.objects.ReplyFlow
import org.telegram.abilitybots.api.util.AbilityUtils.getChatId
import org.telegram.telegrambots.meta.api.objects.Update
import uz.warcom.contest.bot.config.BotConfiguration
import uz.warcom.contest.bot.model.enum.UserState
import uz.warcom.contest.bot.service.AdminService
import uz.warcom.contest.bot.service.PersistenceFacade
import java.util.function.Predicate


@Component
class ContestFlowBot
@Autowired constructor(
    private val botConfiguration: BotConfiguration,
    private val persistenceFacade: PersistenceFacade,
    private val adminService: AdminService
): AbilityBot(botConfiguration.token, botConfiguration.username) {

    fun adminFlow(): ReplyFlow {
        val dateFlow = ReplyFlow.builder(db, 1003)
            .onlyIf { upd ->
                getUserState(userId(upd)) == UserState.DESCRIPTION_SENT && !upd?.message?.text.isNullOrBlank()
            }
            .action { _, upd ->
                silent.send("Contest is created, here is the summary", getChatId(upd))
                updateUserState(userId(upd), UserState.START)
            }
            .build()

        val descrFlow = ReplyFlow.builder(db, 1002)
            .onlyIf {upd ->
                getUserState(userId(upd)) == UserState.NAME_SENT && !upd?.message?.text.isNullOrBlank()
            }
            .action { _, upd -> silent.send("Description received, specify dates", getChatId(upd)) }
            .next(dateFlow)
            .action { _, upd -> updateUserState(userId(upd), UserState.DESCRIPTION_SENT) }
            .build()

        val nameFLow = ReplyFlow.builder(db, 1001)
            .onlyIf { upd ->
                getUserState(userId(upd)) == UserState.CREATE_CONTEST && !upd?.message?.text.isNullOrBlank()
            }
            .action {
                    _, upd -> silent.send("Name received, send description", getChatId(upd))
            }
            .next(descrFlow)
            .action { _, upd -> updateUserState(userId(upd), UserState.NAME_SENT) }
            .build()


        val mainFlow = ReplyFlow.builder(db, 1000)
            .onlyIf(hasMessageWith("create"))
            .next(nameFLow)
            .action { _, upd ->
                updateUserState(userId(upd), UserState.CREATE_CONTEST)
                silent.send("Create draft contest, specify name", getChatId(upd))
            }
            .build()

        return mainFlow
    }

    private fun getUserState (userId: Long): UserState {
        return UserState.valueOf(db.getMap<String, String>(USER_STATES_KEY).getOrDefault(userId.toString(), "START"))
    }

    private fun updateUserState (userId: Long, state: UserState) {
        db.getMap<String, Any>(USER_STATES_KEY).entries.forEach{ ent -> println("${ent.key} : ${ent.value}") }
        val states: MutableMap<String, String> = db.getMap(USER_STATES_KEY)
        states[userId.toString()] = state.toString()
    }

    private fun hasMessageWith(msg: String): Predicate<Update> {
        return Predicate<Update> { upd -> upd?.message?.text?.equals(msg, ignoreCase = true) ?: false }
    }

    private fun userId (update: Update): Long {
        return update.message.from.id
    }

    override fun creatorId(): Long {
        return botConfiguration.creator
    }

    companion object {
        private val logger = LogManager.getLogger(ContestFlowBot::class.java)
        private const val USER_STATES_KEY = "USER_STATES"
    }
}
