package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity
class Problem : Question() {

    var title: String? = null

    var htmlProblemStatement: String? = null

    var difficulty: Difficulty? = null


    enum class Difficulty {
        EASY, INTERMEDIATE, HARD
    }
}