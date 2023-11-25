package uz.warcom.contest.bot.model

import uz.warcom.contest.bot.util.DateHelper
import java.time.LocalDateTime

data class ContestData(
    var name: String = "placeholder",
    var description: String = "placeholder",
    var id: Int? = null,
    var startDate: LocalDateTime = LocalDateTime.now(),
    var endDate: LocalDateTime = LocalDateTime.now(),
    var communityData: CommunityData? = null,
) {
    fun toMessage (): String {
        return "Конкурс: $name \n" +
                "Детали: $description \n" +
                (startDate?.let { "Начало: ${DateHelper.format(startDate!!)}\n" } ?: "") +
                (endDate?.let {  "Конец: ${DateHelper.format(endDate!!)}" } ?: "")
    }
}
