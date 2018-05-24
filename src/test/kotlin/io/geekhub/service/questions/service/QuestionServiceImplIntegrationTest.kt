package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
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

        val question = this.questionService.saveQuestion(Question())
        val questionId = question.questionId!!
        assertNotNull(questionId)

        val createdAnswer = this.questionService.createQuestionAnswer(questionId, "hello")
        assertNotNull(createdAnswer.attributeId)

        this.questionService.getQuestion(questionId).let {
            assertEquals(1, it?.attributes?.size)
        }
    }
}