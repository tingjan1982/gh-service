package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class InterviewSessionServiceImpl(val interviewSessionRepository: InterviewSessionRepository) : InterviewSessionService {

    override fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession {
        return interviewSessionRepository.save(interviewSession)
    }

    override fun getInterviewSession(id: String): InterviewSession {
        return interviewSessionRepository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(InterviewSession::class, id)
        }
    }
}