package uz.warcom.contest.bot.model

import uz.warcom.contest.bot.util.DateHelper
import java.time.LocalDateTime

data class ContestData(
    var name: String? = null,
    var description: String? = null,
    var id: Int? = null,
    var startDate: LocalDateTime? = null,
    var endDate: LocalDateTime? = null
) {
    fun toMessage (): String {
        return "Конкурс: $name \n" +
                "Детали: $description \n" +
                (startDate?.let { "Начало: ${DateHelper.format(startDate!!)}\n" } ?: "") +
                (endDate?.let {  "Конец: ${DateHelper.format(endDate!!)}" } ?: "")
    }
}
