package uz.warcom.contest.persistence.domain

import java.time.LocalDateTime
import javax.persistence.Entity

@Entity
class Contest : AbstractIntEntity() {

    var name: String? = null

    var description: String? = null

    var startDate: LocalDateTime? = null

    var endDate: LocalDateTime? = null
}