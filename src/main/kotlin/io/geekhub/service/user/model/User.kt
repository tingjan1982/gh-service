package io.geekhub.service.user.model

import io.geekhub.service.questions.model.Question
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * List of additional generators:
 * http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html/mapping.html#d0e5294
 */
@Document
data class User(
        @Id
        var userId: String? = null,
        val username: String,
        var firstName: String = "",
        var lastName: String = "",
        var email: String = "") {

    var rank: Rank? = null

    var savedQuestions: MutableMap<String, Question> = mutableMapOf()

    var likedQuestions: MutableMap<String, Question> = mutableMapOf()

    enum class Rank {
        ADEPT, CHIEF, MASTER_CHIEF, ARCHITECT
    }
}