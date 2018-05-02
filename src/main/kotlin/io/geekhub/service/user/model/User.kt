package io.geekhub.service.user.model

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.shared.model.BaseAuditableObject
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Id
import javax.persistence.Transient

@Entity
@EntityListeners(AuditingEntityListener::class)
data class User(
        @Id
        var id: Long,
        var firstName: String,
        var lastName: String
) : BaseAuditableObject<User, Long>() {

    var rank: Rank? = null

    @Transient
    var interviews: MutableList<Interview> = mutableListOf()


    enum class Rank {
        ADEPT, CHIEF, MASTER_CHIEF, ARCHITECT
    }
}