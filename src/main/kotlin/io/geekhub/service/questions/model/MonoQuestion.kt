package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity(name = "gh_mono_question")
class MonoQuestion : Question<Boolean>() {

    var statement: String? = null
}