package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.Visibility

object DummyObject {

    fun dummyClientAccount() = ClientAccount(
        accountType = ClientAccount.AccountType.CORPORATE,
        planType = ClientAccount.PlanType.FREE,
        clientName = "Test Client Account"
    )

    fun dummyClientUser(clientAccount: ClientAccount) = ClientUser(
        email = "test@geekhub.tw",
        name = "Test Client User",
        nickname = "Test Client User",
        userType = ClientUser.UserType.AUTH0,
        clientAccount = clientAccount
    )

    fun dummyQuestion(clientUser: ClientUser) = Question(
        question = "dummy question",
        clientUser = clientUser,
        questionType = Question.QuestionType.MULTI_CHOICE,
        jobTitle = "dummy job title"
    )

    fun dummyInterview(clientUser: ClientUser) = Interview(
        title = "dummy interview",
        clientUser = clientUser,
        jobTitle = "dummy job title",
        visibility = Visibility.PUBLIC,
        releaseResult = Interview.ReleaseResult.YES
    )


}
