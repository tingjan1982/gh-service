package io.geekhub.service.script

import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource

@Disabled
@IntegrationTest
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"])
internal class UpdateSpecializationReference {

    @Autowired
    lateinit var specializationRepository: SpecializationRepository

    @Autowired
    lateinit var questionRepository: QuestionRepository

    @Autowired
    lateinit var interviewRepository: InterviewRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate


    @BeforeEach
    fun cleanup() {

    }

    /**
     * Update all business objects reference to Specialization.
     */
    @Test
    @WithMockUser("script.UpdateSpecializationReference")
    fun run() {
        
    }
}