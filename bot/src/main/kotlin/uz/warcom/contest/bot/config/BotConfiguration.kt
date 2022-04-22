package uz.warcom.contest.bot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "tg.bot")
class BotConfiguration {
    var token: String = ""
    var username: String = ""
    var creator: Long = 0
}