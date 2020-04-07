package io.geekhub.service.questions.service

import assertk.all
import assertk.assertThat
import assertk.assertions.each
import assertk.assertions.hasSize
import assertk.assertions.isNotNull
import assertk.assertions.prop
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.PossibleAnswer
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser

@IntegrationTest
internal class QuestionServiceImplIntegrationTest {

    @Autowired
    lateinit var questionService: QuestionService

    @Autowired
    lateinit var clientAccount: ClientAccount

    /**
     * https://medium.com/@elye.project/mastering-kotlin-standard-functions-run-with-let-also-and-apply-9cd334b0ef84
     */
    @Test
    @WithMockUser("dummy-user")
    fun saveQuestion() {

        Question(
                question = "Dummy question",
                questionType = Question.QuestionType.MULTI_CHOICE,
                clientAccount = clientAccount,
                jobTitle = "Senior Engineer").apply {
            this.addAnswer(PossibleAnswer(answer = "A", correctAnswer = true))
            this.addAnswer(PossibleAnswer(answer = "B", correctAnswer = false))
        }.let {
            return@let this.questionService.saveQuestion(it)
        }.let {
            assertThat(it.questionId).isNotNull()
            assertThat(it.possibleAnswers).all {
                hasSize(2)
                each { ans ->
                    ans.prop(PossibleAnswer::answerId).isNotNull()
                }
            }

            it.possibleAnswers.clear()
            it.addAnswer(PossibleAnswer(answer = "updated", correctAnswer = true))

            questionService.saveQuestion(it).let { updated ->
                assertThat(updated.possibleAnswers).hasSize(1)
            }
        }


    }
}