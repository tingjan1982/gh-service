package io.geekhub.service.questions.service

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isNotNull
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.PossibleAnswer
import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.questions.model.Question.QuestionAttribute.Companion.DESCRIPTION_KEY
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.extensions.DummyObject
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class QuestionServiceImplIntegrationTest {

    @Autowired
    lateinit var questionService: QuestionService

    /**
     * https://medium.com/@elye.project/mastering-kotlin-standard-functions-run-with-let-also-and-apply-9cd334b0ef84
     */
    @Test
    fun saveQuestion() {

        val createdQuestion = Question(
                question = "Dummy question",
                questionType = Question.QuestionType.MULTI_CHOICE,
                clientAccount = DummyObject.dummyClient(),
                jobTitle = "Senior Engineer").apply {
            this.addAnswer(PossibleAnswer(answer = "A", correctAnswer = true))
            this.addAnswer(PossibleAnswer(answer = "B", correctAnswer = false))
        }.let {
            return@let this.questionService.saveQuestion(it)
        }

        assertThat(createdQuestion.questionId).isNotNull()
        assertThat(createdQuestion.possibleAnswers).hasSize(2)

        questionService.saveOrUpdateAttribute(createdQuestion.questionId.toString(), QuestionAttribute(key = DESCRIPTION_KEY, value = "something")).let {
            assertThat(it.getAttribute(DESCRIPTION_KEY)).isNotNull()
        }
    }
}