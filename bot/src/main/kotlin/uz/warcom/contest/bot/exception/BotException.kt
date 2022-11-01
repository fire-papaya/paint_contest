package uz.warcom.contest.bot.exception

open class BotException(override val message: String): RuntimeException(message)

class BotRequesterException: BotException("Bots are not allowed to interact with me")
