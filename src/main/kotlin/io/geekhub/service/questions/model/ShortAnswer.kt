package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity(name = "gh_short_answer")
class ShortAnswer: Question<String>() {

    var question: String? = null
}                       