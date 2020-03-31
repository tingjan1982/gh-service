package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.QuestionAttribute
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.model.SearchCriteria
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/
 */
@Service
@Transactional
class QuestionServiceImpl(val questionRepository: QuestionRepository) : QuestionService {
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

    override fun getQuestions(searchCriteria: SearchCriteria, pageRequest: PageRequest): Page<Question> {

        return searchCriteria.keyword?.let {
            val textCriteria = TextCriteria.forDefaultLanguage().matching(it)

            if (searchCriteria.filterByClientAccount) {
                questionRepository.findAllByClientAccount(searchCriteria.clientAccount, textCriteria, pageRequest)
            } else {
                questionRepository.findAllBy(textCriteria, pageRequest)
            }
        } ?: if (searchCriteria.filterByClientAccount) {
            questionRepository.findAllByClientAccount(searchCriteria.clientAccount, pageRequest)
        } else {
            questionRepository.findAll(pageRequest)
        }

        //val query = TextQuery.queryText(it).with(pageRequest).addCriteria(Criteria.where("clientAccount").`is`(searchCriteria.clientAccount))
        //return questionRepository.findAll(pageRequest)
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
