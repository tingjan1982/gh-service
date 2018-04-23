package io.geekhub.service.questions.model

class Problem : Question() {

    lateinit var title: String

    lateinit var htmlProblemStatement: String

    lateinit var difficulty: Difficulty


    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }
}