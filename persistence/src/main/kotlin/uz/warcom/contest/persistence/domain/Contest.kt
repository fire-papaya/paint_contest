package uz.warcom.contest.persistence.domain

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Contest : AbstractIntEntity() {

    var name: String? = null

    var description: String? = null

    var startDate: LocalDateTime? = null

    var endDate: LocalDateTime? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    var community: Community? = null
}