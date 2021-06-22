package io.geekhub.service.script

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.interview.repository.InterviewSessionRepository
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
    private lateinit var interviewSessionRepository: InterviewSessionRepository


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
    fun `populate releaseResult field in Interview`() {

        val count = AtomicInteger()

        interviewRepository.findAll().forEach {

            if (it.releaseResult == null) {
                it.releaseResult = Interview.ReleaseResult.YES
                count.incrementAndGet()

                interviewRepository.save(it)
            }
        }

        println("Total updated interviews: $count")
    }
}