package uz.warcom.contest.persistence.dto

import uz.warcom.contest.persistence.domain.WarcomUser

data class ImageDto (
    val file: ByteArray,
    val user: WarcomUser,
    val isReady: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageDto

        if (!file.contentEquals(other.file)) return false
        if (user != other.user) return false
        if (isReady != other.isReady) return false

        return true
    }

    override fun hashCode(): Int {
        var result = file.contentHashCode()
        result = 31 * result + user.hashCode()
        result = 31 * result + isReady.hashCode()
        return result
    }
}