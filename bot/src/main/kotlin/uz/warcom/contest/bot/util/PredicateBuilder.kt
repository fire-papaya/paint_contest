package uz.warcom.contest.bot.util

import org.telegram.telegrambots.meta.api.objects.Update
import uz.warcom.contest.bot.model.enums.EmojiCmd

object PredicateBuilder {

    val commandPrefixes = EmojiCmd.values().map{ it.code }.asSequence()

    fun startsWith(flag: String): (Update) -> Boolean {
        return {
            upd: Update -> upd.message?.text?.startsWith("$flag ") ?: false
        }
    }

    fun startsWith(emojiCmd: EmojiCmd): (Update) -> Boolean {
        return {
            upd: Update -> upd.message?.text?.startsWith("$emojiCmd ") ?: false
        }
    }

    fun notCommand(): (Update) -> Boolean {
        return { upd -> commandPrefixes.all { !upd.message.text.startsWith(it) } }
    }

    fun notStartsWith(flag: String): (Update) -> Boolean {
        return {
            upd: Update -> upd.message?.text?.startsWith(flag)?.not() ?: false
        }
    }
}