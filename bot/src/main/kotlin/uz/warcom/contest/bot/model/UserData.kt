package uz.warcom.contest.bot.model

data class UserData(
    var username: String,
    var id: Int,
    var telegramId: Long,
    var community: CommunityData? = null,
    var isAdmin: Boolean = false
)
