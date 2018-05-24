package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.QuestionAttribute
import io.geekhub.service.questions.repository.QuestionAttributeRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

/**
 * https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/
 */
@Service
@Transactional
class QuestionServiceImpl(val questionRepository: QuestionRepository, val questionAttributeRepository: QuestionAttributeRepository, val entityManager: EntityManager) : QuestionService {

    companion object {
        val logger = LoggerFactory.getLogger(QuestionServiceImpl::class.java)
    }

    override fun saveQuestion(question: Question): Question {
        return questionRepository.save(question)
    }

    override fun getQuestion(id: String): Question? {
        return questionRepository.findById(id).orElse(null)
    }

    override fun loadQuestion(id: String): Question {
        this.getQuestion(id)?.let {
            entityManager.refresh(it)

            return it
        } ?: throw BusinessObjectNotFoundException(Question, id)
    }

    override fun createQuestionAnswer(id: String, answer: String): QuestionAttribute {

        this.getQuestion(id)?.let {
            val questionAttribute = QuestionAttribute(question = it, key = Question.ANSWER, value = answer)

            return questionAttributeRepository.save(questionAttribute).also {
                logger.debug("Created attribute: $it")
            }
        } ?: throw BusinessObjectNotFoundException(Question, id)
    }
}