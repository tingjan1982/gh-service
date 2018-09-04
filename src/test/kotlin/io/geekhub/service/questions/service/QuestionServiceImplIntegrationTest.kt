package io.geekhub.service.questions.service

import assertk.assert
import assertk.assertions.isNotNull
import io.geekhub.service.questions.model.PossibleAnswer
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class QuestionServiceImplIntegrationTest {

    @Autowired
    lateinit var questionService: QuestionService

    @Test
    fun saveQuestion() {

        val createdQuestion = Question(question = "Dummy question").apply {
            this.addAnswer(PossibleAnswer(answer = "A", correct = true))
            this.addAnswer(PossibleAnswer(answer = "B", correct = false))
        }.let {
            return@let this.questionService.saveQuestion(it)
        }

        assert(createdQuestion.id).isNotNull()
        createdQuestion.possibleAnswers.forEach {
            assert(it.id).isNotNull()
        }
    }
}