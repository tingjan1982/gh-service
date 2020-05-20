package io.geekhub.service.interview.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
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

    override fun createInterviewSession(interviewSession: InterviewSession): InterviewSession {

        return interviewSessionRepository.save(interviewSession).let {
            interviewService.getInterview(it.publishedInterview.referencedInterview.id.toString()).let {interview ->
                interview.addInterviewSession(it)
                interviewService.saveInterview(interview)
            }

            it
        }
    }

    override fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession {
        return interviewSessionRepository.save(interviewSession)
    }

    override fun sendInterviewSession(interviewSession: InterviewSession): InterviewSession {

        interviewSession.interviewSentDate = Date()
        notificationService.sendInterviewInvitation(interviewSession)

        return saveInterviewSession(interviewSession)
    }

    override fun startInterviewSession(interviewSession: InterviewSession, candidateAccount: ClientAccount): InterviewSession {

        if (interviewSession.status == InterviewSession.Status.STARTED) {
            throw BusinessException("Interview has already started at ${interviewSession.interviewStartDate}")
        }

        interviewSession.candidateAccount = candidateAccount
        interviewSession.status = InterviewSession.Status.STARTED
        interviewSession.interviewStartDate = Date()

        return saveInterviewSession(interviewSession)
    }

    override fun addAnswerAttempt(interviewSession: InterviewSession, answerAttempt: InterviewSession.QuestionAnswerAttempt): InterviewSession {

        checkInterviewSessionTime(interviewSession)
        validateSectionAndQuestion(interviewSession, answerAttempt.sectionId, answerAttempt.questionSnapshotId)

        interviewSession.answerAttemptSections.getOrPut(answerAttempt.sectionId, { initializeAnswerAttemptSection(interviewSession, answerAttempt.sectionId) }).let {
            it.answerAttempts[answerAttempt.questionSnapshotId] = answerAttempt
        }


        return saveInterviewSession(interviewSession)
    }

    override fun markInterviewSessionAnswer(interviewSession: InterviewSession, sectionId: String, questionSnapshotId: String, correct: Boolean): InterviewSession {

        validateSectionAndQuestion(interviewSession, sectionId, questionSnapshotId)

        interviewSession.answerAttemptSections.getOrPut(sectionId, { initializeAnswerAttemptSection(interviewSession, sectionId) }).let {

            val incorrectAttempt = InterviewSession.QuestionAnswerAttempt(sectionId = sectionId, questionSnapshotId = questionSnapshotId, correct = false)
            it.answerAttempts.getOrPut(questionSnapshotId, { incorrectAttempt }).let { attempt ->
                attempt.correct = correct
            }

            it.answerStats[Question.QuestionType.SHORT_ANSWER]?.run {
                this.answered++

                if (correct) {
                    this.correct++
                }
            }
        }

        return saveInterviewSession(interviewSession)
    }


    private fun validateSectionAndQuestion(interviewSession: InterviewSession, sectionId: String, questionSnapshotId: String) {

        interviewSession.publishedInterview.referencedInterview.sections.find { it.id == sectionId }?.let {
            if (!it.questions.any { q -> q.id == questionSnapshotId }) {
                throw BusinessObjectNotFoundException(Interview.QuestionSnapshot::class, questionSnapshotId)
            }

        } ?: throw BusinessObjectNotFoundException(Interview.Section::class, sectionId)

    }

    private fun initializeAnswerAttemptSection(interviewSession: InterviewSession, sectionId: String): InterviewSession.AnswerAttemptSection {

        LOGGER.info("Initialize AnswerAttemptSection for section: ${sectionId}")
        interviewSession.publishedInterview.referencedInterview.sections
                .find { it.id == sectionId }?.let {
                    val answerStats = it.questions.groupBy({ q -> q.questionType }, { qsnapshot -> qsnapshot })
                            .map { entry -> Pair(entry.key, InterviewSession.AnswerAttemptSection.AnswerStats(questionTotal = entry.value.size)) }
                            .toMap()

                    return InterviewSession.AnswerAttemptSection(id = sectionId, answerStats = answerStats)

                } ?: throw BusinessException("Provided section id is not found :${sectionId}")
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

        if (interviewSession.interviewEndDate == null) {
            interviewSession.status = InterviewSession.Status.ENDED
            interviewSession.interviewEndDate = Date()

            scoreMultiChoiceQuestions(interviewSession)

            notificationService.sendInterviewResult(interviewSession)

            return saveInterviewSession(interviewSession)
        }

        return interviewSession
    }

    private fun scoreMultiChoiceQuestions(interviewSession: InterviewSession) {

        val correctAnswers = interviewSession.publishedInterview.referencedInterview.sections
                .flatMap { it.questions }
                .filter { it.questionType == Question.QuestionType.MULTI_CHOICE }
                .map { Pair(it.id, it.possibleAnswers.filter { ans -> ans.correctAnswer }.map { ans -> ans.answerId }.toList()) }
                .toMap()

        interviewSession.answerAttemptSections.forEach {
            LOGGER.info("Scoring answers for section: ${it.key}")

            val answerAttemptSection = it.value
            answerAttemptSection.answerStats[Question.QuestionType.MULTI_CHOICE]?.let { multiChoiceStats ->

                answerAttemptSection.answerAttempts.forEach { ans ->
                    correctAnswers[ans.key]?.let { correctAnswerIds ->
                        multiChoiceStats.answered++

                        val answerAttempt = ans.value

                        answerAttempt.correct = correctAnswerIds.containsAll(answerAttempt.answerIds.orEmpty())

                        if (answerAttempt.correct!!) {
                            multiChoiceStats.correct++
                        }
                    }
                }
            }
        }
    }

    override fun calculateScore(id: String): InterviewSession {
        this.getInterviewSession(id).let {

            val totalQuestions = it.answerAttemptSections.flatMap { sections -> sections.value.answerStats.values }
                    .sumBy { stats -> stats.questionTotal }

            val totalCorrectAnswers = it.answerAttemptSections.flatMap { sections -> sections.value.answerStats.values }
                    .sumBy { stats -> stats.correct }

            it.totalScore = BigDecimal(totalCorrectAnswers).divide(BigDecimal(totalQuestions), 2, RoundingMode.CEILING)

            return saveInterviewSession(it)
        }
    }

    override fun getInterviewSession(id: String): InterviewSession {
        return interviewSessionRepository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(InterviewSession::class, id)
        }
    }

    override fun getCurrentInterviewSession(interviewId: String, clientAccount: ClientAccount): InterviewSession {

        interviewService.getPublishedInterviewByInterview(interviewId).let {
            interviewSessionRepository.findByPublishedInterviewAndCandidateAccountAndStatusIn(it, clientAccount,
                    listOf(InterviewSession.Status.NOT_STARTED, InterviewSession.Status.STARTED))?.let { s ->
                return s

            } ?: throw BusinessException("This interview $interviewId has no current interview session")
        }
    }

    override fun getInterviewSessions(searchCriteria: SearchCriteria, status: InterviewSession.Status?): Page<InterviewSession> {

        Query().with(searchCriteria.pageRequest).let {
            if (searchCriteria.filterByClientAccount) {
                it.addCriteria(Criteria.where("clientAccount").`is`(searchCriteria.clientAccount))
            }

            searchCriteria.interviewId?.let { id ->
                interviewService.getPublishedInterviewByInterview(id).apply {
                    it.addCriteria(Criteria.where("publishedInterview").`is`(this))
                }
            }

            status?.let { s ->
                it.addCriteria(Criteria.where("status").`is`(s))
            }

            val count = mongoTemplate.count(Query.of(it).limit(-1).skip(-1), InterviewSession::class.java)
            val results = mongoTemplate.find(it, InterviewSession::class.java)

            return PageImpl(results, searchCriteria.pageRequest, count)
        }
    }
}