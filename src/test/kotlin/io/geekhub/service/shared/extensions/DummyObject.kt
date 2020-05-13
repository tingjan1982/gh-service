package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.specialization.repository.Specialization

object DummyObject {

    fun dummyClient() = ClientAccount(accountType = ClientAccount.AccountType.CORPORATE,
            clientName = "Test Client Account",
            email = "joelin@geekhub.tw"
    )

    fun dummySpecialization() = Specialization(name = "Front End Engineer")

    fun dummyQuestion(clientAccount: ClientAccount) = Question(
            question = "dummy question",
            clientAccount = clientAccount,
            questionType = Question.QuestionType.MULTI_CHOICE,
            jobTitle = "dummy job title")

    fun dummyInterview(clientAccount: ClientAccount, specialization: Specialization) = Interview(
            title = "dummy interview",
            clientAccount = clientAccount,
            jobTitle = "dummy job title",
            specialization = specialization)
}
