package uz.warcom.contest.persistence.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uz.warcom.contest.persistence.domain.Community

@Repository
interface CommunityRepository: CrudRepository<Community, Int>
