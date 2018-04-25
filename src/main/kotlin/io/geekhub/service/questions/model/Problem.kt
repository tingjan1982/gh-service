package io.geekhub.service.questions.model

class Problem : Question() {

    var title: String? = null

    var htmlProblemStatement: String? = null

    var difficulty: Difficulty? = null


    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }
}