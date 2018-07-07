package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class InterviewServiceImplIntegrationTest {

    @Autowired
    private lateinit var interviewService: InterviewService

    @Autowired
    private lateinit var questionRepository: QuestionRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private var questionCount: Long = 0

    /**
     * This function should only run once.
     */
    @BeforeAll
    fun populateData() {
        for (i in 1..50) {
            questionRepository.save(Question(question = "Q $i"))
        }

        if (questionCount == 0L) {
            questionCount = questionRepository.count()
        }

        assertEquals(questionCount, questionRepository.count())

        this.userRepository.save(User(username = "username"))
    }

    @Test
    fun createInterview() {
        val interviewOption = InterviewOption(username = "username", interviewMode = Interview.InterviewMode.REAL, questionCount = 5)
        val createdInterview = this.interviewService.createInterview(interviewOption)

        assertNotNull(createdInterview.id)
        assertNotNull(createdInterview.user)
        assertEquals(interviewOption.interviewMode, createdInterview.interviewMode)
        assertEquals(interviewOption.duration, createdInterview.selectedDuration)
        assertEquals(interviewOption.questionCount, createdInterview.questionsCount())
        assertNotNull(createdInterview.startDate)

        this.userRepository.findByUsername("username").let {
            it.ifPresent({
                assertEquals(1, it.interviews.size)
            })
        }

        val interview = this.interviewService.getInterview(createdInterview.interviewId.toString())

        assertEquals(interviewOption.questionCount, interview.questionsCount())
        interview.getQuestions().forEach({
            assertEquals(it.key, it.value.questionId)
        })
    }

    @Test
    fun `this test exists to verify that populateData only run once`() {

    }
}