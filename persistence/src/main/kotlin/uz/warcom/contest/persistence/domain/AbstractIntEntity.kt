package uz.warcom.contest.persistence.domain

import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.util.*
import javax.persistence.*

@MappedSuperclass
abstract class AbstractIntEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null


    @Column(name = "guid", columnDefinition = "binary(36)")
    @Generated(GenerationTime.INSERT)
    var guid: UUID? = null
}