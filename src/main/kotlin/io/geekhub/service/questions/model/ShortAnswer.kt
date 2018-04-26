package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity
class ShortAnswer: Question<String>() {

    var question: String? = null
}                       