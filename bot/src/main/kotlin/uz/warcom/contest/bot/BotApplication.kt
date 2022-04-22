package uz.warcom.contest.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.generics.BotSession
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import uz.warcom.contest.bot.representation.PaintContestBot
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@SpringBootApplication(scanBasePackages = ["uz.warcom.contest"])
class BotApplication

fun main(args: Array<String>) {
    runApplication<BotApplication>(*args)
}

@Component
class BotStarter constructor(
    private val bot: PaintContestBot
){

    private val api = TelegramBotsApi(DefaultBotSession::class.java)
    private var session: BotSession? = null

    @PostConstruct
    fun start () {
        session = api.registerBot(bot)
    }

    @PreDestroy
    fun close () {
        session?.stop()
    }
}