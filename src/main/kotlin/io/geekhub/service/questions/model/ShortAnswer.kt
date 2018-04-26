package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity
class ShortAnswer: Question() {

    var question: String? = null

    var answer: String? = null

}                       