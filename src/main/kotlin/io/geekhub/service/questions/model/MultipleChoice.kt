package io.geekhub.service.questions.model

class MultipleChoice: Question() {

    lateinit var question: String

    lateinit var choices: List<String>

    lateinit var answer: String
}