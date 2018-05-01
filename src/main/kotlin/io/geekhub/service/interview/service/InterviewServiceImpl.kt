package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom

@Service
class InterviewServiceImpl(val questionRepository: QuestionRepository<*>, val interviewRepository: InterviewRepository) : InterviewService {

    companion object {
        val logger = LoggerFactory.getLogger(InterviewServiceImpl::class.java)!!
        const val questionCount = 10
    }

    /**
     *
     */
    override fun createInterview(interviewOption: InterviewOption): Interview {

        var interview = Interview(interviewOption)

        val questionsCount = questionRepository.count()
        val allQuestions = questionRepository.findAllQuestions()
        val numbers = mutableMapOf<Int, Int>()

        for (i in 1..questionCount) {
            val nextInt = this.generateUniqueRandomNumber(questionsCount, numbers)
            val selectedQuestion = allQuestions[nextInt]
            interview.addQuestion(selectedQuestion)
        }

        interview = this.interviewRepository.save(interview)
        logger.info("Created interview: $interview")

        return interview
    }

    private fun generateUniqueRandomNumber(questionsCount: Long, numbers: MutableMap<Int, Int>): Int {

        var nextInt: Int

        do {
            nextInt = ThreadLocalRandom.current().nextInt(0, (questionsCount - 1).toInt())
        } while (numbers.containsKey(nextInt))

        numbers[nextInt] = nextInt
        logger.debug("Generated random integer: $nextInt")

        return nextInt
    }
}