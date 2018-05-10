package io.geekhub.service.user.model

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.shared.model.BaseAuditableObject
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
data class User(
        // TODO: change id type to String if possible.
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var id: Long = 0,
        val username: String,
        var firstName: String,
        var lastName: String) : BaseAuditableObject<User, Long>() {

    constructor(username: String): this(username = username, firstName = "", lastName = "")

    var rank: Rank? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    var interviews: MutableList<Interview> = mutableListOf()


    enum class Rank {
        ADEPT, CHIEF, MASTER_CHIEF, ARCHITECT
    }
}