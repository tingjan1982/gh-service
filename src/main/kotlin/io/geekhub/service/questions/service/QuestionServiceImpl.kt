package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.QuestionAttribute
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.transaction.Transactional

/**
 * https://blog.ippon.tech/boost-the-performance-of-your-spring-data-jpa-application/
 */
@Service
@Transactional
class QuestionServiceImpl(val questionRepository: QuestionRepository, val entityManager: EntityManager) : QuestionService {
    companion object {

        val logger: Logger = LoggerFactory.getLogger(QuestionServiceImpl::class.java)
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
        } ?: throw BusinessObjectNotFoundException(Question::class, id)
    }

    override fun updateVisibility(id: String, visibilityToChange: Question.VisibilityScope): Question {

        this.getQuestion(id)?.let {
            logger.info("Updating question visibility from ${it.visibilityScope} to $visibilityToChange")
            it.visibilityScope = visibilityToChange

            return it

        } ?: throw BusinessObjectNotFoundException(Question::class, id)
    }

    override fun saveOrUpdateAttribute(id: String, questionAttribute: QuestionAttribute): Question {

        this.getQuestion(id)?.let {
            it.getAttribute(questionAttribute.key)?.let { attr ->
                attr.value = questionAttribute.value
                
            } ?: it.addAttribute(questionAttribute)

            return it

        } ?: throw BusinessObjectNotFoundException(Question::class, id)
    }
}
