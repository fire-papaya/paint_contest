package uz.warcom.contest.persistence.domain

import javax.persistence.Entity

@Entity
class Community : AbstractIntEntity() {
    var label: String = ""

    var name: String = ""
}