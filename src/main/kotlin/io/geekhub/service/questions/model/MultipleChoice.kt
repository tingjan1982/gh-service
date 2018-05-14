package io.geekhub.service.questions.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.Transient

@Entity(name = "gh_multiple_choice")
@DiscriminatorValue("multiple")
class MultipleChoice: Question<String>() {

    lateinit var question: String

    @Transient
    lateinit var choices: List<String>
}