package io.geekhub.service.questions.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity(name = "gh_short_answer")
@DiscriminatorValue("shortanswer")
class ShortAnswer: Question<String>() {

    var question: String? = null
}                       