package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.model.SearchCriteria
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * todo: revisit this at some point: https://www.baeldung.com/kotlin-logging
 */
@Service
@Transactional
class InterviewServiceImpl(val mongoTemplate: MongoTemplate,
                           val questionRepository: QuestionRepository,
                           val interviewRepository: InterviewRepository) : InterviewService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InterviewServiceImpl::class.java)
    }

    override fun saveInterview(interview: Interview): Interview {

        this.interviewRepository.save(interview).also {
            logger.info("Created interview: $it")
            return it
        }
    }

//    private fun populateQuestions(interviewOption: InterviewOption, interview: Interview) {
//
//        val allQuestions = questionRepository.findAll().distinct()
//        val numbers = mutableMapOf<Int, Int>()
//
//        for (i in 1..interviewOption.questionCount) {
//            val nextInt = this.generateUniqueRandomNumber(allQuestions.size, numbers)
//            val selectedQuestion = allQuestions[nextInt]
//            interview.addQuestion(selectedQuestion)
//        }
//    }
//
//    private fun generateUniqueRandomNumber(questionsCount: Int, numbers: MutableMap<Int, Int>): Int {
//
//        var nextInt: Int
//
//        do {
//            nextInt = ThreadLocalRandom.current().nextInt(0, (questionsCount - 1))
//        } while (numbers.containsKey(nextInt))
//
//        numbers[nextInt] = nextInt
//        logger.trace("Generated random integer: $nextInt")
//
//        return nextInt
//    }

    override fun getInterview(id: String): Interview {
        return interviewRepository.findById(id).orElseThrow { BusinessObjectNotFoundException(Interview::class, id) }
    }

    override fun deleteInterview(id: String) {
        interviewRepository.deleteById(id)
    }

    override fun getInterviews(searchCriteria: SearchCriteria): Page<Interview> {

        Query().with(searchCriteria.pageRequest).let {
            if (searchCriteria.filterByClientAccount) {
                it.addCriteria(Criteria.where("clientAccount").`is`(searchCriteria.clientAccount))
            }

            searchCriteria.keyword?.let {keyword ->
                it.addCriteria(TextCriteria.forDefaultLanguage().matching(keyword))
            }

            val count = mongoTemplate.count(Query.of(it).limit(-1).skip(-1), Interview::class.java)
            val results = mongoTemplate.find(it, Interview::class.java)

            return PageImpl(results, searchCriteria.pageRequest, count)
        }
    }


}