package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.shared.extensions.DummyObject
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@IntegrationTest
internal class InterviewServiceImplIntegrationTest {

    @Autowired
    private lateinit var interviewService: InterviewService


    @Test
    fun createInterview() {
        val createdInterview = Interview(title = "sample interview",
                jobTitle = "Engineer",
                clientAccount = DummyObject.dummyClient(),
                specialization = DummyObject.dummySpecialization()).let {
            this.interviewService.saveInterview(it)
        }

        assertNotNull(createdInterview.id)

        this.interviewService.getInterview(createdInterview.id.toString()).let {

        }
    }
}