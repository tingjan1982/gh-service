package io.geekhub.service.user.model

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.shared.model.BaseAuditableObject
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

/**
 * List of additional generators:
 * http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/mapping.html#d0e5294
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@AttributeOverride(name = "id", column = Column(name = "id2"))
data class User(
        @Id
        @GeneratedValue(generator = "uuid")
        @GenericGenerator(name = "uuid", strategy = "uuid2")
        var userId: String? = null,
        val username: String,
        var firstName: String,
        var email: String,
        var lastName: String) : BaseAuditableObject<User, String>() {

    constructor(username: String): this(username = username, firstName = "", lastName = "", email = "")

    var rank: Rank? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    var interviews: MutableList<Interview> = mutableListOf()

    override fun getId(): String? {
        return userId
    }

    enum class Rank {
        ADEPT, CHIEF, MASTER_CHIEF, ARCHITECT
    }
}