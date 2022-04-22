package uz.warcom.contest.persistence.dto

import java.util.*

data class UserDto(
    var id: Int? = null,
    var telegramId: Long,
    var username: String,
)
