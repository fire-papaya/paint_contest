package uz.warcom.contest.persistence.dto

import uz.warcom.contest.persistence.domain.WarcomUser

data class ImageDto (
    val user: WarcomUser,
    val isReady: Boolean,
    val fileId: String? = null,
)