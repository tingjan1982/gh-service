package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.interview.repository.PublishedInterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.exception.BusinessException
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
 * todo: revisit this at some point: https://www.baeldung.com/kotlin-logging
 */
@Service
@Transactional
class InterviewServiceImpl(val mongoTemplate: MongoTemplate,
                           val questionRepository: QuestionRepository,
                           val interviewRepository: InterviewRepository,
                           val publishedInterviewRepository: PublishedInterviewRepository,
                           val specializationService: SpecializationService) : InterviewService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(InterviewServiceImpl::class.java)
    }

    override fun saveInterview(interview: Interview): Interview {

        this.interviewRepository.save(interview).also {
            logger.info("Created interview: $it")
            return it
        }
    }

    override fun getInterview(id: String): Interview {
        return interviewRepository.findById(id).orElseThrow { BusinessObjectNotFoundException(Interview::class, id) }
    }

    override fun publishInterview(id: String): PublishedInterview {

        getInterview(id).let {
            publishedInterviewRepository.save(PublishedInterview(referencedInterview = it)).let { published ->
                it.latestPublishedInterviewId = published.id

                interviewRepository.save(it)
                return published
            }
        }
    }

    override fun getPublishedInterviewByInterview(interviewId: String): PublishedInterview {

        getInterview(interviewId).let {
            return getPublishedInterviewByPublishedId(it.latestPublishedInterviewId)
        }
    }

    override fun getPublishedInterviewByPublishedId(publishedInterviewId: String?): PublishedInterview {

        publishedInterviewId?.let { publishedId ->
            return publishedInterviewRepository.findById(publishedId).orElseThrow {
                throw BusinessObjectNotFoundException(PublishedInterview::class, publishedId)
            }
        } ?: throw BusinessException("This interview has not been published")
    }

    override fun deleteInterview(id: String) {

        interviewRepository.findById(id).ifPresent {
            it.deleted = true
            interviewRepository.save(it)
        }
    }

    override fun getInterviews(searchCriteria: SearchCriteria): Page<Interview> {

        searchCriteria.toQuery(specializationService).let {
            val count = mongoTemplate.count(Query.of(it).limit(-1).skip(-1), Interview::class.java)
            val results = mongoTemplate.find(it, Interview::class.java)

            return PageImpl(results, searchCriteria.pageRequest, count)
        }
    }
}