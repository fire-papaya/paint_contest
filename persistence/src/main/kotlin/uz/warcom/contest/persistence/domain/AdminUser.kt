package uz.warcom.contest.persistence.domain

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
class AdminUser: AbstractIntEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    var user: WarcomUser? = null

    @ManyToOne(fetch = FetchType.LAZY)
    var community: Community? = null
}