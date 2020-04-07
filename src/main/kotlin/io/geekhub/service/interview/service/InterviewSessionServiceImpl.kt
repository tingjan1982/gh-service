package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class InterviewSessionServiceImpl(val notificationService: NotificationService, val interviewSessionRepository: InterviewSessionRepository) : InterviewSessionService {

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
}