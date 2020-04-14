package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.model.SearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class InterviewSessionServiceImpl(val interviewSessionRepository: InterviewSessionRepository,
                                  val mongoTemplate: MongoTemplate,
                                  val notificationService: NotificationService) : InterviewSessionService {

    override fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession {
        return interviewSessionRepository.save(interviewSession)
    }

    override fun sendInterviewSession(interviewSession: InterviewSession): InterviewSession {

        interviewSession.interviewSentDate = Date()
        notificationService.sendNotification(interviewSession)

        return saveInterviewSession(interviewSession)
    }

    override fun startInterviewSession(interviewSession: InterviewSession): InterviewSession {

        interviewSession.interviewStartDate?.let {
            throw BusinessException("Interview has already started at $it")
        }

        interviewSession.interviewStartDate = Date()

        return saveInterviewSession(interviewSession)
    }

    override fun addAnswerAttempt(interviewSession: InterviewSession, questionId: String, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession {

        checkInterviewSessionTime(interviewSession)
        interviewSession.answerAttempts[questionId] = answerAttempt

        return saveInterviewSession(interviewSession)
    }

    private fun checkInterviewSessionTime(interviewSession: InterviewSession) {

        interviewSession.interviewStartDate?.let {
            if (interviewSession.duration > 0) {
                val startedDate = it.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                Duration.between(startedDate, LocalDateTime.now()).let { duration ->
                    if (duration.toMinutes() >= interviewSession.duration) {
                        throw BusinessException("Interview has exceeded the set duration. Answer attempt is no longer possible.")
                    }
                }
            }

        } ?: throw BusinessException("Interview has not started yet")
    }

    override fun submitInterviewSession(interviewSession: InterviewSession): InterviewSession {

        interviewSession.interviewEndDate = Date()

        return saveInterviewSession(interviewSession)
    }

    override fun getInterviewSession(id: String): InterviewSession {
        return interviewSessionRepository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(InterviewSession::class, id)
        }
    }

    override fun getInterviewSessions(searchCriteria: SearchCriteria): Page<InterviewSession> {

        Query().with(searchCriteria.pageRequest).let {
            if (searchCriteria.filterByClientAccount) {
                it.addCriteria(Criteria.where("clientAccount").`is`(searchCriteria.clientAccount))
            }

            searchCriteria.interviewId?.let { id ->
                it.addCriteria(Criteria.where("id").`is`(id))
            }

            val count = mongoTemplate.count(Query.of(it).limit(-1).skip(-1), InterviewSession::class.java)
            val results = mongoTemplate.find(it, InterviewSession::class.java)

            return PageImpl(results, searchCriteria.pageRequest, count)
        }
    }
}