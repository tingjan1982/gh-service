package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.AnswerRequest
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class QuestionServiceImplIntegrationTest {

    @Autowired
    lateinit var questionService: QuestionService

    @Test
    fun saveQuestion() {
    }

    @Test
    fun getQuestion() {
    }

    @Test
    fun createQuestionAnswer() {

        val question = this.questionService.saveQuestion(Question(question = "Question"))
        val questionId = question.questionId.toString()
        assertNotNull(questionId)

        this.questionService.createQuestionAnswer(questionId, AnswerRequest("correct", listOf("possible 1", "possible 2")))

        this.questionService.getQuestion(questionId)?.let {
            it.getAnswerDetails().let {
                assertEquals("correct", it.correctAnswer)
                assertEquals(2, it.possibleAnswers.size)
            }
        }
    }
}