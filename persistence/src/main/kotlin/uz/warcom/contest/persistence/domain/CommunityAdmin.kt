package uz.warcom.contest.persistence.domain

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "community_admin_user")
class CommunityAdmin: AbstractIntEntity() {
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: WarcomUser? = null

    @ManyToOne
    @JoinColumn(name = "community_id")
    var community: Community? = null
}