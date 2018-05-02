package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.MonoQuestion
import io.geekhub.service.questions.repository.QuestionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InterviewServiceImplIntegrationTest {

    @Autowired
    private lateinit var interviewService: InterviewService

    @Autowired
    private lateinit var questionRepository: QuestionRepository<MonoQuestion>

    @BeforeAll
    fun populateData() {
        for (i in 1..50) {
            val monoQuestion = MonoQuestion()
            monoQuestion.statement = "Q $i - true or false?"
            questionRepository.save(monoQuestion)
        }

        val count = questionRepository.count()
        assertEquals(50, count)
    }

    @Test
    fun createInterview() {
        val interviewOption = InterviewOption("user", Interview.InterviewMode.REAL, 0)
        val createdInterview = this.interviewService.createInterview(interviewOption)

        assertNotNull(createdInterview.id)
        assertEquals(interviewOption.user, createdInterview.user)
        assertEquals(interviewOption.interviewMode, createdInterview.interviewMode)
        assertEquals(interviewOption.duration, createdInterview.selectedDuration)
        assertEquals(InterviewServiceImpl.questionCount, createdInterview.questionsCount())
        assertNotNull(createdInterview.startDate)
    }

    @Test
    fun `this test exists to verify that populateDate only run once`() {

    }
}