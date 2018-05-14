package io.geekhub.service.questions.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity(name = "gh_mono_question")
@DiscriminatorValue("mono")
class MonoQuestion : Question<Boolean>() {

    var statement: String? = null
}