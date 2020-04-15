package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import io.geekhub.service.shared.model.SearchCriteria
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class InterviewSessionServiceImpl(val interviewSessionRepository: InterviewSessionRepository,
                                  val mongoTemplate: MongoTemplate,
                                  val interviewService: InterviewService,
                                  val notificationService: NotificationService) : InterviewSessionService {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(InterviewSessionServiceImpl::class.java)
    }
    
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

    override fun addAnswerAttempt(interviewSession: InterviewSession, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession {

        checkInterviewSessionTime(interviewSession)

        interviewSession.answerAttemptSections.getOrPut(answerAttempt.sectionId, { initializeAnswerAttemptSection(interviewSession, answerAttempt) }).let {
            it.answerAttempts[answerAttempt.questionSnapshotId] = answerAttempt
        }

        return saveInterviewSession(interviewSession)
    }

    private fun initializeAnswerAttemptSection(interviewSession: InterviewSession, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession.AnswerAttemptSection {

        LOGGER.info("Initialize AnswerAttemptSection for section: ${answerAttempt.sectionId}")
        interviewSession.publishedInterview.referencedInterview.sections
                .find { it.id == answerAttempt.sectionId }?.let {
                    val answerStats = it.questions.groupBy({ q -> q.questionType }, { qsnapshot -> qsnapshot })
                            .map { entry -> Pair(entry.key, InterviewSession.AnswerAttemptSection.AnswerStats(questionTotal = entry.value.size)) }
                            .toMap()

                    return InterviewSession.AnswerAttemptSection(id = answerAttempt.sectionId, answerStats = answerStats)

                } ?: throw BusinessException("Provided section id is not found :${answerAttempt.sectionId}")
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

        checkInterviewSessionTime(interviewSession)
        interviewSession.interviewEndDate = Date()

        val correctAnswers = interviewSession.publishedInterview.referencedInterview.sections
                .flatMap { it.questions }
                .filter { it.questionType == Question.QuestionType.MULTI_CHOICE }
                .map { Pair(it.id, it.possibleAnswers.filter { ans -> ans.correctAnswer }.map { ans -> ans.answerId }.toList()) }
                .toMap()

        val totalMultipleChoice = correctAnswers.size
        var answeredCorrectly = 0

        interviewSession.answerAttemptSections.forEach {
            LOGGER.info("Scoring answers for section: ${it.key}")

            val answerAttemptSection = it.value
            answerAttemptSection.answerStats[Question.QuestionType.MULTI_CHOICE]?.let { multiChoiceStats ->
                multiChoiceStats.answered = answerAttemptSection.answerAttempts.size

                answerAttemptSection.answerAttempts.forEach { ans ->
                            correctAnswers[ans.key]?.let { correctAnswerIds ->
                                val answerAttempt = ans.value
                                answerAttempt.correct = correctAnswerIds.containsAll(answerAttempt.answerId.orEmpty())
                                multiChoiceStats.correct++

                                answeredCorrectly++
                            }
                        }
            } ?: println("No multi choice questions for this interview session: ${interviewSession.id}")
        }

        interviewSession.totalScore = BigDecimal(answeredCorrectly).divide(BigDecimal(totalMultipleChoice), 2, RoundingMode.CEILING)

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
                interviewService.getPublishedInterviewByInterview(id).apply {
                    it.addCriteria(Criteria.where("publishedInterview").`is`(this))
                }
            }

            val count = mongoTemplate.count(Query.of(it).limit(-1).skip(-1), InterviewSession::class.java)
            val results = mongoTemplate.find(it, InterviewSession::class.java)

            return PageImpl(results, searchCriteria.pageRequest, count)
        }
    }
}