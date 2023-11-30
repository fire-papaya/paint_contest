package uz.warcom.contest.bot.util

import org.telegram.telegrambots.meta.api.objects.Update

object PredicateBuilder {
    fun startsWith(flag: String): (Update) -> Boolean {
        return {
            upd: Update -> upd.message?.text?.startsWith("$flag ") ?: false
        }
    }

    fun notStartsWith(flag: String): (Update) -> Boolean {
        return {
            upd: Update -> upd.message?.text?.startsWith(flag)?.not() ?: false
        }
    }
}