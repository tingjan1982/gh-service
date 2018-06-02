package io.geekhub.service.questions.service

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.questions.web.bean.SearchRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class QuestionSearchServiceImpl(val questionRepository: QuestionRepository) : QuestionSearchService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(QuestionSearchServiceImpl::class.java)
    }

    override fun searchQuestions(searchRequest: SearchRequest): Page<Question> {
        logger.debug("Search questions by $searchRequest")

        return questionRepository.findQuestionsBySearchRequest(searchRequest.searchText,
                searchRequest.category,
                searchRequest.topic,
                searchRequest.page)
    }
}