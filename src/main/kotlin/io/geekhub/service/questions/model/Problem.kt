package io.geekhub.service.questions.model

import javax.persistence.Entity

@Entity
class Problem : Question<String>() {

    var title: String? = null

    var htmlProblemStatement: String? = null


}