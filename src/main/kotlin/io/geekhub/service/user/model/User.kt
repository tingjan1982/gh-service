package io.geekhub.service.user.model

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.shared.model.BaseAuditableObject
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var id: Long = 0,
        val username: String,
        var firstName: String = "",
        var lastName: String = ""
) : BaseAuditableObject<User, Long>() {

    var rank: Rank? = null

    @OneToMany
    var interviews: MutableList<Interview> = mutableListOf()


    enum class Rank {
        ADEPT, CHIEF, MASTER_CHIEF, ARCHITECT
    }
}