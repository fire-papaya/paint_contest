package uz.warcom.contest.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uz.warcom.contest.persistence.domain.Entry
import uz.warcom.contest.persistence.domain.Image

@Repository
interface ImageRepository: JpaRepository<Image, Int> {
    fun findAllByEntryOrderByDateCreatedDesc (entry: Entry): List<Image>
}