package io.geekhub.service.interview.model

import io.geekhub.service.questions.model.Answer
import io.geekhub.service.questions.model.MonoQuestion
import io.geekhub.service.user.model.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class InterviewTest {

    private val logger: Logger = LoggerFactory.getLogger(InterviewTest::class.java)

    @Test
    fun assessInterview() {

        val interview = Interview(user = User(username = "username"))
        this.createQuestionsWithPartialAnswers(interview)

        logger.info("Initialized interview: {}", interview)

        interview.assessInterview()
        assertEquals(50.0, interview.score)
        assertNotNull(interview.completeDate)
    }

    private fun createQuestionsWithPartialAnswers(interview: Interview) {

        for (i in 1..10) {
            val monoQuestion = MonoQuestion()
            monoQuestion.id = i.toLong()
            monoQuestion.statement = "Question $i"

            if (i % 2 == 0) {
                monoQuestion.answer = true
            }

            interview.addQuestion(monoQuestion)
        }

        for (i in 1..10) {
            if (i % 2 == 0) {
                val answer = Answer(true)
                interview.addAnswerAttempt(i.toLong(), answer)
            }
        }
    }

    @Test
    fun computeScore_DifferentQuestionWeight() {

        val interview = Interview(user = User(username = "username"))
        this.createDifferentWeightQuestions(interview)

        val score = interview.computeScore()
        assertEquals(60.0, score)
    }

    private fun createDifferentWeightQuestions(interview: Interview) {

        for (i in 1..6) {
            val monoQuestion = MonoQuestion()
            monoQuestion.id = i.toLong()
            monoQuestion.statement = "Question $i"

            if (i % 2 == 0) {
                monoQuestion.answer = true
                monoQuestion.weight = 1.25
            }

            interview.addQuestion(monoQuestion)
        }

        for (i in 1..6) {
            if (i % 2 == 0) {
                val answer = Answer(true)
                interview.addAnswerAttempt(i.toLong(), answer)
            }
        }
    }
}