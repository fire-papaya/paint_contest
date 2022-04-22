package uz.warcom.contest.persistence.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "user")
class WarcomUser: AbstractIntEntity() {

    @Column(name = "telegram_id")
    var telegramId: Long? = null

    @Column(name = "username")
    var username: String? = null
}