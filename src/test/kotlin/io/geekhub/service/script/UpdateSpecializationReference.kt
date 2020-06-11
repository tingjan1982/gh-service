package io.geekhub.service.script

import io.geekhub.service.interview.model.PublishedInterview
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource

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
        mongoTemplate.remove(Query(Criteria.where("name").`is`("Test Engineer")), Specialization::class.java).let {
            println("Removed ${it.deletedCount} specialization")
        }

        specializationRepository.findById("5e96c1e2f1025462b03ee564").ifPresent {
            mongoTemplate.updateMulti(Query(),
                    Update.update("referencedInterview.specialization", it),
                    PublishedInterview::class.java
            )
        }
    }

    /**
     * Update all business objects reference to Specialization.
     */
    @Test
    @WithMockUser("script.UpdateSpecializationReference")
    fun run() {
        specializationRepository.findById("5e96c1e2f1025462b03ee564").ifPresent {
            println("Updating $it on all questions and interviews")

            questionRepository.findAll().forEach { question ->
                question.specialization = it

                questionRepository.save(question)
            }

            interviewRepository.findAll().forEach { interview ->
                interview.specialization = it

                interviewRepository.save(interview)
            }

            println("Update completed")

            specializationRepository.findAll().forEach { specialization ->
                if (specialization != it && specialization.name == it.name) {
                    specializationRepository.delete(specialization)
                    println("Deleted duplicate specialization $specialization")
                }
            }
        }


    }
}