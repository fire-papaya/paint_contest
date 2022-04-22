package uz.warcom.contest.persistence.domain

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "guid", columnDefinition = "binary(36)")
    var guid: UUID? = null

    @ManyToOne
    @JoinColumn(name = "entry_id")
    var entry: Entry? = null

    @Column(name = "is_ready")
    var isReady: Boolean = false

    @Column(name = "date_created")
    @CreationTimestamp
    var dateCreated: LocalDateTime? = null
}