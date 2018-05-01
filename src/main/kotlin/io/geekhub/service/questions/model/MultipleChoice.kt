package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity
class MultipleChoice: Question<String>() {

    lateinit var question: String

    lateinit var choices: List<String>
}