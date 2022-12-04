package uz.warcom.contest.persistence.dto


data class UserDto(
    var id: Int? = null,
    var telegramId: Long,
    var username: String,
)
