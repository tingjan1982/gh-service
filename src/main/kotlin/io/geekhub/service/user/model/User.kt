package io.geekhub.service.user.model

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseAuditableObject
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

/**
 * List of additional generators:
 * http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/mapping.html#d0e5294
 */
@Entity(name = "gh_user")
@EntityListeners(AuditingEntityListener::class)
data class User(
        @Id
        @GeneratedValue(generator = "uuid")
        @GenericGenerator(name = "uuid", strategy = "uuid2")
        var userId: String? = null,
        val username: String,
        var firstName: String,
        var lastName: String,
        var email: String) : BaseAuditableObject<User, String>() {

    constructor(username: String): this(username = username, firstName = "", lastName = "", email = "")

    var rank: Rank? = null

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    var interviews: MutableList<Interview> = mutableListOf()

    @ElementCollection
    @JoinTable(name = "gh_user_saved_questions")
    @JoinColumn(name = "saved_question_id")
    @MapKey(name = "questionId")
    var savedQuestions: MutableMap<String, Question> = mutableMapOf()

    override fun getId(): String? {
        return userId
    }

    enum class Rank {
        ADEPT, CHIEF, MASTER_CHIEF, ARCHITECT
    }
}