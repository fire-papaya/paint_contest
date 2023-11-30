package uz.warcom.contest.bot.model

import uz.warcom.contest.bot.util.DateHelper
import java.time.LocalDateTime

data class ContestData(
    var name: String = "",
    var description: String = "",
    var id: Int? = null,
    var startDate: LocalDateTime = LocalDateTime.MIN,
    var endDate: LocalDateTime = LocalDateTime.MIN,
    var communityData: CommunityData? = null,
    var isDraft: Boolean = true
) {
    fun toMessage (): String {
        return "Конкурс: $name \n" +
                "Детали: $description \n" +
                (startDate.let { "Начало: ${DateHelper.format(startDate)}\n" }) +
                (endDate.let {  "Конец: ${DateHelper.format(endDate)}" })
    }
}
