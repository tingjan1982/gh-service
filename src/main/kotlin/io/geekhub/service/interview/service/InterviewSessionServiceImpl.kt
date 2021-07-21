package io.geekhub.service.interview.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.LightInterviewSession
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.interview.repository.LightInterviewSessionRepository
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.annotation.TransactionSupport
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectAlreadyExistsException
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

@Service
@TransactionSupport
class InterviewSessionServiceImpl(
    val interviewSessionRepository: InterviewSessionRepository,
    val lightInterviewSessionRepository: LightInterviewSessionRepository,
    val interviewService: InterviewService,
    val notificationService: NotificationService,
    val mongoTemplate: MongoTemplate
) : InterviewSessionService {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(InterviewSessionServiceImpl::class.java)
    }

    override fun createInterviewSession(interviewSession: InterviewSession): InterviewSession {

        if (interviewSessionRepository.existsByPublishedInterviewAndUserEmail(interviewSession.publishedInterview, interviewSession.userEmail)
        ) {
            throw BusinessObjectAlreadyExistsException("Interview session is already created for ${interviewSession.userEmail}")
        }

        return interviewSessionRepository.save(interviewSession).let {
            val lightSession = lightInterviewSessionRepository.findById(it.id.toString()).orElseThrow {
                throw BusinessObjectNotFoundException(LightInterviewSession::class, it.id.toString())
            }

            interviewService.getInterview(it.publishedInterview.referencedInterview.id.toString()).let { interview ->
                interview.addInterviewSession(lightSession)
                interviewService.saveInterview(interview)
            }

            it
        }
    }

    override fun saveInterviewSession(interviewSession: InterviewSession): InterviewSession {
        return interviewSessionRepository.save(interviewSession)
    }

    override fun sendInterviewSession(sender: ClientUser, interviewSession: InterviewSession): InterviewSession {

        interviewSession.interviewSentDate = Date()
        notificationService.sendInterviewInvitation(sender, interviewSession)

        return saveInterviewSession(interviewSession)
    }

    override fun startInterviewSession(interviewSession: InterviewSession, candidateUser: ClientUser): InterviewSession {

        if (interviewSession.status == InterviewSession.Status.STARTED) {
            throw BusinessException("Interview has already started at ${interviewSession.interviewStartDate}")
        }

        interviewSession.candidateUser = candidateUser
        interviewSession.status = InterviewSession.Status.STARTED
        interviewSession.interviewStartDate = Date()

        return saveInterviewSession(interviewSession)
    }

    override fun addAnswerAttempt(
        interviewSession: InterviewSession,
        answerAttempt: InterviewSession.QuestionAnswerAttempt
    ): InterviewSession {

        checkInterviewSessionTime(interviewSession)
        validateSectionAndQuestion(interviewSession, answerAttempt.sectionId, answerAttempt.questionSnapshotId)

        answerAttempt.let {
            it.questionType = if (it.answer != null) {
                Question.QuestionType.SHORT_ANSWER
            } else {
                Question.QuestionType.MULTI_CHOICE
            }
        }

        interviewSession.answerAttemptSections.getOrPut(answerAttempt.sectionId) { initializeAnswerAttemptSection(interviewSession, answerAttempt.sectionId) }.let {
            it.answerAttempts[answerAttempt.questionSnapshotId] = answerAttempt
        }


        return saveInterviewSession(interviewSession)
    }

    override fun markInterviewSessionAnswer(
        interviewSession: InterviewSession,
        sectionId: String,
        questionSnapshotId: String,
        correct: Boolean
    ): InterviewSession {

        if (interviewSession.status != InterviewSession.Status.ENDED) {
            throw BusinessException("You can only mark answer on a submitted interview: ${interviewSession.id}")
        }

        validateSectionAndQuestion(interviewSession, sectionId, questionSnapshotId)

        interviewSession.answerAttemptSections.getOrPut(sectionId) { initializeAnswerAttemptSection(interviewSession, sectionId) }.let {

            val incorrectAttempt = InterviewSession.QuestionAnswerAttempt(
                sectionId = sectionId,
                questionSnapshotId = questionSnapshotId,
                questionType = Question.QuestionType.SHORT_ANSWER,
                correct = false)

            it.answerAttempts.getOrPut(questionSnapshotId) { incorrectAttempt }.let { attempt ->
                attempt.correct = correct
            }

            it.computeAnswerStats(Question.QuestionType.SHORT_ANSWER)
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

    private fun initializeAnswerAttemptSection(
        interviewSession: InterviewSession,
        sectionId: String
    ): InterviewSession.AnswerAttemptSection {

        LOGGER.info("Initialize AnswerAttemptSection for section: $sectionId")

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

        if (interviewSession.status != InterviewSession.Status.STARTED) {
            throw BusinessException("You can only submit a started interview: ${interviewSession.id}")
        }

        if (interviewSession.interviewEndDate == null) {
            interviewSession.status = InterviewSession.Status.ENDED
            interviewSession.interviewEndDate = Date()

            scoreMultiChoiceQuestions(interviewSession)
            calculateScore(interviewSession)

            notificationService.sendInterviewResult(interviewSession)
        }

        return interviewSession
    }

    private fun scoreMultiChoiceQuestions(interviewSession: InterviewSession) {

        val correctAnswers = interviewSession.publishedInterview.referencedInterview.sections
            .flatMap { it.questions }
            .filter { it.questionType == Question.QuestionType.MULTI_CHOICE }
            .associate { Pair(it.id, it.possibleAnswers.filter { ans -> ans.correctAnswer }.map { ans -> ans.answerId }.toList()) }

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

    override fun calculateScore(interviewSession: InterviewSession): InterviewSession {

        val totalQuestions = interviewSession.answerAttemptSections
            .flatMap { sections -> sections.value.answerStats.values }
            .sumBy { stats -> stats.questionTotal }

        if (totalQuestions > 0) {
            val totalCorrectAnswers = interviewSession.answerAttemptSections
                .flatMap { sections -> sections.value.answerStats.values }
                .sumBy { stats -> stats.correct }

            interviewSession.totalScore = BigDecimal(totalCorrectAnswers).divide(BigDecimal(totalQuestions), 2, RoundingMode.CEILING)
        }

        return saveInterviewSession(interviewSession)
    }

    override fun getInterviewSession(id: String): InterviewSession {
        return interviewSessionRepository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(InterviewSession::class, id)
        }
    }

    override fun getCurrentInterviewSession(interviewId: String, clientUser: ClientUser): InterviewSession {

        interviewService.getPublishedInterviewByInterview(interviewId).let {
            interviewSessionRepository.findByPublishedInterviewAndCandidateUser(it, clientUser)?.let { s ->
                return s

            } ?: throw BusinessException("This interview $interviewId has no current interview session")
        }
    }

    override fun getInterviewSessions(searchCriteria: SearchCriteria, status: InterviewSession.Status?): Page<InterviewSession> {

        searchCriteria.toQuery().let {
            searchCriteria.interviewId?.let { id ->
                interviewService.getPublishedInterviewByInterview(id).apply {
                    it.addCriteria(Criteria.where("publishedInterview").`is`(this))
                }
            }

            if (searchCriteria.invited) {
                it.addCriteria(
                    Criteria.where("userEmail").`is`(searchCriteria.clientUser.email)
                        .and("status").`in`(InterviewSession.Status.NOT_STARTED, InterviewSession.Status.STARTED)
                )
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