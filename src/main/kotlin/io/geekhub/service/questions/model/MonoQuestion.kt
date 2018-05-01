package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity
class MonoQuestion : Question<Boolean>() {

    var statement: String? = null
}