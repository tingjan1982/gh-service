package io.geekhub.service.questions.model

class MultipleChoice: Question<String>() {

    lateinit var question: String

    lateinit var choices: List<String>
}