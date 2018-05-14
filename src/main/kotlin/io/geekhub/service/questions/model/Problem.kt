package io.geekhub.service.questions.model

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity(name = "gh_problem")
@DiscriminatorValue("problem")
class Problem : Question<String>() {

    var title: String? = null

    var htmlProblemStatement: String? = null


}