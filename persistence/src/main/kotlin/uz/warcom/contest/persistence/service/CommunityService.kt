package uz.warcom.contest.persistence.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uz.warcom.contest.persistence.domain.Community
import uz.warcom.contest.persistence.repository.CommunityRepository

@Service
class CommunityService constructor(
    private val communityRepository: CommunityRepository
){
    fun getCommunities(): List<Community> {
        return communityRepository.findAll().map{ x -> x }
    }

    fun getCommunity(id: Int): Community? {
        return communityRepository.findByIdOrNull(id)
    }
}