package io.geekhub.service.script

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import io.geekhub.service.interview.model.LightInterviewSession
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.interview.repository.LightInterviewSessionRepository
import io.geekhub.service.interview.repository.PublishedInterviewRepository
import io.geekhub.service.shared.annotation.IntegrationTest
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

        interviewRepository.findAll().forEach { itvw ->

            if (itvw.interviewSessions.isNotEmpty()) {
                val verifyCount = itvw.interviewSessions.size

                itvw.lightInterviewSessions = itvw.interviewSessions.map {
                    lightInterviewSessionRepository.findById(it.id.toString()).orElseGet {
                        lightInterviewSessionRepository.save(LightInterviewSession(it))
                    }

                }.toMutableList()
                itvw.interviewSessions.clear()

                interviewRepository.save(itvw)
                println(itvw)

                assertThat(itvw.lightInterviewSessions).hasSize(verifyCount)
                assertThat(itvw.interviewSessions).isEmpty()
            }
        }
    }

    @Test
    @WithMockUser("script@geekhub.tw")
    fun `get interview status`() {

        interviewRepository.findAll().forEach {
            it.interviewSessions.clear()

            interviewRepository.save(it)
        }
    }
}