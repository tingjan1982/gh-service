package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ThreadLocalRandom
import javax.transaction.Transactional

@Service
@Transactional
class InterviewServiceImpl(val questionRepository: QuestionRepository,
                           val interviewRepository: InterviewRepository,
                           val userRepository: UserRepository) : InterviewService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InterviewServiceImpl::class.java)
    }

    override fun createInterview(interviewOption: InterviewOption): Interview {

        val questionsCount = questionRepository.count()

        if (questionsCount <= 0) {
            throw Exception("There is no questions available. Unable to create interview.")
        }

        val foundUser = this.userRepository.findByUsername(interviewOption.username).orElseThrow {
            BusinessObjectNotFoundException(User::class, interviewOption.username)
        }

        val interview = Interview()

        this.populateQuestions(interviewOption, interview)

        return this.interviewRepository.save(interview).also {
            logger.info("Created interview: $it")
        }
    }

    private fun populateQuestions(interviewOption: InterviewOption, interview: Interview) {

        val allQuestions = questionRepository.findAll().distinct()
        val numbers = mutableMapOf<Int, Int>()

        for (i in 1..interviewOption.questionCount) {
            val nextInt = this.generateUniqueRandomNumber(allQuestions.size, numbers)
            val selectedQuestion = allQuestions[nextInt]
            interview.addQuestion(selectedQuestion)
        }
    }

    private fun generateUniqueRandomNumber(questionsCount: Int, numbers: MutableMap<Int, Int>): Int {

        var nextInt: Int

        do {
            nextInt = ThreadLocalRandom.current().nextInt(0, (questionsCount - 1))
        } while (numbers.containsKey(nextInt))

        numbers[nextInt] = nextInt
        logger.trace("Generated random integer: $nextInt")

        return nextInt
    }

    override fun getInterview(id: String): Interview {
        return this.interviewRepository.findById(id).orElseThrow { BusinessObjectNotFoundException(Interview::class, id) }
    }


}