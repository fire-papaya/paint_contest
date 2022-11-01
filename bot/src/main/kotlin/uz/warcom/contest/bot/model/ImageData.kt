package uz.warcom.contest.bot.model

import org.telegram.telegrambots.meta.api.objects.User
import java.time.LocalDateTime

data class ImageData(
    var id: Int,
    var isReady: Boolean = false,
    var dateCreated: LocalDateTime? = null,
    var telegramFileId: String? = null
)

data class ImageToSave (
    var telegramUser: User,
    var isReady: Boolean,
    var fileId: String
)