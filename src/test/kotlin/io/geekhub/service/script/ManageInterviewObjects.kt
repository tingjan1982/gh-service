package io.geekhub.service.script

import io.geekhub.service.account.repository.ClientUserRepository
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.interview.repository.LightInterviewSessionRepository
import io.geekhub.service.interview.repository.PublishedInterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.atomic.AtomicInteger

@Disabled
@IntegrationTest
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"])
class ManageInterviewObjects {

    @Autowired
    private lateinit var interviewRepository: InterviewRepository

    @Autowired
    private lateinit var publishedInterviewRepository: PublishedInterviewRepository

    @Autowired
    private lateinit var interviewSessionRepository: InterviewSessionRepository

    @Autowired
    private lateinit var lightInterviewSessionRepository: LightInterviewSessionRepository

    @Autowired
    private lateinit var questionRepository: QuestionRepository

    @Autowired
    private lateinit var clientUserRepository: ClientUserRepository

    @Autowired
    private lateinit var specializationRepository: SpecializationRepository


    @Test
    @WithMockUser("script@geekhub.tw")
    fun `populate currentInterview field in InterviewSession`() {

        val count = AtomicInteger()
        interviewSessionRepository.findAll().forEach {
            count.incrementAndGet()
            it.currentInterview = it.publishedInterview.referencedInterview

            interviewSessionRepository.save(it)
        }

        println("Total updated interview sessions: $count")
    }

    @Test
    @WithMockUser("script@geekhub.tw")
    fun `populate field in Interview`() {

        val count = AtomicInteger()

        interviewRepository.findAll().forEach {
            count.incrementAndGet()
            it.clientAccount = it.clientUser.clientAccount.id
            interviewRepository.save(it)
        }

        println("Total updated interviews: $count")
    }

    @Test
    @WithMockUser("script@geekhub.tw")
    fun `refactor interview's interview session reference`() {

        val count = AtomicInteger(0)

        interviewRepository.findAll().forEach { itvw ->

            if (itvw.lightInterviewSessions.isNotEmpty()) {
                itvw.lightInterviewSessions.clear()
                interviewRepository.save(itvw)

                count.incrementAndGet()
            }
        }

        println("Updated interviews: $count")
    }

    @Test
    @WithMockUser("script@geekhub.tw")
    fun `delete interviews`() {

        questionRepository.deleteAll()
        lightInterviewSessionRepository.deleteAll()
        interviewSessionRepository.deleteAll()
        specializationRepository.deleteAll()

        println("Interview count: ${interviewRepository.count()}")
        val templateCount = AtomicInteger()

        val templateUser = clientUserRepository.findByEmail("template@geekhub.tw")

        interviewRepository.findAll().forEach {

            if (it.clientUser == templateUser) {
                templateCount.incrementAndGet()
            } else {
                publishedInterviewRepository.deleteAllByReferencedInterview_Id(it.id.toString())

                interviewRepository.delete(it)
            }
        }

        println("Template interviews: $templateCount")
    }
}