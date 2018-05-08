package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.model.MonoQuestion
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations.initMocks
import java.util.*

internal class InterviewServiceImplTest {

    private lateinit var interviewService: InterviewServiceImpl

    @Mock
    private lateinit var questionRepository: QuestionRepository<Question<*>>

    @Mock
    private lateinit var interviewRepository: InterviewRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        initMocks(this)
        this.interviewService = InterviewServiceImpl(this.questionRepository, this.interviewRepository, this.userRepository)
    }

    @Test
    fun createInterview() {
        `when`(this.questionRepository.count()).thenReturn(100)
        val questions = mutableListOf<Question<*>>()

        for (i in 1..100) {
            val question = MonoQuestion()
            question.id = i.toLong()
            questions.add(question)
        }

        `when`(this.questionRepository.findAllQuestions()).thenReturn(questions)
        `when`(this.interviewRepository.save(any(Interview::class.java))).thenAnswer({
            val interview: Interview = it.getArgument(0) as Interview
            interview.id = 1
            return@thenAnswer interview
        })

        val interviewOption = InterviewOption("dummy", Interview.InterviewMode.REAL, 60)
        `when`(this.userRepository.findByUsername(interviewOption.username)).thenReturn(Optional.of(User(username = interviewOption.username)))
        
        val createdInterview = this.interviewService.createInterview(interviewOption)

        assertNotNull(createdInterview.id)
        assertNotNull(createdInterview.user)
        assertEquals(Interview.InterviewMode.REAL, createdInterview.interviewMode)
        assertEquals(60, createdInterview.selectedDuration)
        assertEquals(InterviewServiceImpl.questionCount, createdInterview.questionsCount())
        assertNotNull(createdInterview.startDate)

    }
}