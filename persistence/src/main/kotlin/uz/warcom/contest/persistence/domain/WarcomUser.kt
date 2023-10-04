package uz.warcom.contest.persistence.domain

import javax.persistence.*

@Entity
@Table(name = "user")
class WarcomUser: AbstractIntEntity() {

    @Column(name = "telegram_id")
    var telegramId: Long? = null

    @Column(name = "username")
    var username: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    var community: Community? = null
}