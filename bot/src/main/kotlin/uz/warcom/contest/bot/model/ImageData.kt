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
    var bytes: ByteArray,
    var isReady: Boolean,
    var fileId: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageToSave

        if (telegramUser != other.telegramUser) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (isReady != other.isReady) return false

        return true
    }

    override fun hashCode(): Int {
        var result = telegramUser.hashCode()
        result = 31 * result + isReady.hashCode()
        result = 31 * result + fileId.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}