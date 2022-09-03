package uz.warcom.contest.persistence.domain

import uz.warcom.contest.persistence.util.CodeGenerator
import javax.persistence.*

@Entity
class Entry : AbstractIntEntity() {

    @Column(name = "code")
    var code: String = CodeGenerator.generate()
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: WarcomUser? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id")
    var contest: Contest? = null
        private set

    @OneToMany(mappedBy = "entry", fetch = FetchType.LAZY)
    var images: List<Image> = emptyList()

    fun withUser (user: WarcomUser) = apply {
        this.user = user
    }

    fun withContest (contest: Contest) = apply {
        this.contest = contest
    }

    companion object Factory {
        fun getInstance (): Entry {
            return Entry()/*.apply {
                this.code = CodeGenerator.generate()
            }*/
        }
    }
}