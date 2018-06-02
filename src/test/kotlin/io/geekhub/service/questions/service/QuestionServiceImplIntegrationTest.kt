package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.web.bean.AnswerRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
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