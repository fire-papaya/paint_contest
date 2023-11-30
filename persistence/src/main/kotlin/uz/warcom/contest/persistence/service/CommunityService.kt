package uz.warcom.contest.persistence.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Community
import uz.warcom.contest.persistence.domain.WarcomUser
import uz.warcom.contest.persistence.repository.CommunityAdminRepository
import uz.warcom.contest.persistence.repository.CommunityRepository

@Service
class CommunityService constructor(
    private val communityRepository: CommunityRepository,
    private val communityAdminRepository: CommunityAdminRepository
){
    fun getCommunities(): List<Community> {
        return communityRepository.findAll().toList()
    }

    fun getCommunity(id: Int): Community? {
        return communityRepository.findByIdOrNull(id)
    }

    fun getCommunity(label: String): Community? {
        return communityRepository.findByLabel(label)
    }

    fun getAdminCommunities(user: WarcomUser): List<Community> {
        return communityAdminRepository.findAllByUser(user)
            .mapNotNull { it.community }
    }
}