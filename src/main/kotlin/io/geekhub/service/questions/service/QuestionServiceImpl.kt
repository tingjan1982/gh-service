package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.specialization.service.SpecializationService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/
 */
@Service
@Transactional
class QuestionServiceImpl(val mongoTemplate: MongoTemplate,
                          val questionRepository: QuestionRepository,
                          val specializationService: SpecializationService) : QuestionService {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionServiceImpl::class.java)
    }

    override fun saveQuestion(question: Question): Question {
        return questionRepository.save(question)
    }

    override fun getQuestion(id: String): Question {
        return questionRepository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(Question::class, id)
        }
    }

    /**
     * See SimpleMongoRepository and PageableExecutableUtils.
     *
     * Reference:
     * https://stackoverflow.com/questions/29030542/pagination-with-mongotemplate
     * https://stackoverflow.com/questions/30801801/spring-data-mongodb-text-search-for-phrase-or-words-in-phrase
     */
    override fun getQuestions(searchCriteria: SearchCriteria): Page<Question> {

        searchCriteria.toQuery(specializationService).let {
            val count = mongoTemplate.count(Query.of(it).limit(-1).skip(-1), Question::class.java)
            val results = mongoTemplate.find(it, Question::class.java)

            return PageImpl(results, searchCriteria.pageRequest, count)
        }
    }

    override fun deleteQuestion(id: String) {
        questionRepository.deleteById(id)
    }

    override fun saveOrUpdateAttribute(id: String, questionAttribute: QuestionAttribute): Question {

        this.getQuestion(id).let {
            it.getAttribute(questionAttribute.key)?.let { attr ->
                attr.value = questionAttribute.value

            } ?: it.addAttribute(questionAttribute)

            return it

        }
    }

    override fun getQuestionAttribute(id: String, key: String): QuestionAttribute? {

        this.getQuestion(id).let {
            return it.getAttribute(key)

        }
    }
}
