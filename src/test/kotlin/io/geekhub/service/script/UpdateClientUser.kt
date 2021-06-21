package io.geekhub.service.script

import assertk.assertThat
import assertk.assertions.isNotNull
import com.mongodb.DBRef
import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.repository.ClientUserRepository
import io.geekhub.service.interview.repository.InterviewRepository
import io.geekhub.service.interview.repository.InterviewSessionRepository
import io.geekhub.service.interview.repository.PublishedInterviewRepository
import io.geekhub.service.likes.data.LikeRecordRepository
import io.geekhub.service.questions.repository.QuestionRepository
import io.geekhub.service.shared.annotation.IntegrationTest
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.repository.SpecializationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.TestPropertySource

@Disabled
@IntegrationTest
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"])
class UpdateClientUser {

    @Autowired
    lateinit var clientAccountRepository: ClientAccountRepository

    @Autowired
    lateinit var questionRepository: QuestionRepository

    @Autowired
    lateinit var interviewRepository: InterviewRepository

    @Autowired
    lateinit var interviewSessionRepository: InterviewSessionRepository

    @Autowired
    lateinit var publishedInterviewRepository: PublishedInterviewRepository

    @Autowired
    lateinit var specializationRepository: SpecializationRepository

    @Autowired
    lateinit var likeRecordRepository: LikeRecordRepository

    @Autowired
    lateinit var clientUserRepository: ClientUserRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun cleanData() {
        mongoTemplate.remove(Query(Criteria.where("clientName").`is`("Test Client Account")), ClientAccount::class.java).let {
            println("Removed ${it.deletedCount} client account")
        }

        mongoTemplate.remove(Query(Criteria.where("email").`is`("test@geekhub.tw")), ClientUser::class.java).let {
            println("Removed ${it.deletedCount} client user")
        }

        mongoTemplate.remove(Query(Criteria.where("name").`is`("Test Engineer")), Specialization::class.java).let {
            println("Removed ${it.deletedCount} specialization")
        }

        mongoTemplate.find<org.bson.Document>(query = Query(Criteria.where("clientAccount").exists(true)), collectionName = "question").forEach {
            val questionId = it["_id"]
            val clientAccount = it["clientAccount"] as DBRef

            if (!clientAccountRepository.existsById(clientAccount.id.toString())) {
                println("Removing question $questionId with obsolete client account reference: ${clientAccount.id}")
                questionRepository.deleteById(questionId.toString())
            }
        }
    }

    @Test
    @WithMockUser("script.updateName")
    fun updateName() {

        clientUserRepository.findAll().forEach {
            it.name = it.nickname!!
            clientUserRepository.save(it)

            println("Updated client user ${it.id} name")
        }
    }

    @Test
    @WithMockUser("script.syncAccount")
    fun syncClientUserAndClientAccount() {

        clientUserRepository.findById("auth0|605ae0cc0ad0db006ec08a31").ifPresent {
            ClientAccount(it.id, ClientAccount.AccountType.INDIVIDUAL, ClientAccount.PlanType.FREE, it.name).let { acc ->
                clientAccountRepository.save(acc);
            }
        }
    }

    @Test
    @WithMockUser("script.UpdateClientUser")
    fun run() {
//        `update client account's default plan type and create default client user`()
        `update question's client user reference`()
        `update interview's client user reference`()
        `update interview session's client user reference`()
        `update published interview's client user reference`()
        `update liked record's client user reference`()
    }

    private fun `update liked record's client user reference`() {

        likeRecordRepository.count().run {
            println("Found $this records, updating these like records")
        }

        likeRecordRepository.findAll().forEach {

            if (it.likedClientUserId == null) {
                // commented out as deprecated reference has been removed
                /*it.likedClientUserId = it.likedClientAccount

                likeRecordRepository.save(it)
                println("Updated like record ${it.id} client user id")*/
            }

            assertThat(it.likedClientUserId).isNotNull()
        }
    }

    private fun `update published interview's client user reference`() {

        publishedInterviewRepository.count().run {
            println("Found $this records, updating these published interviews")
        }

        publishedInterviewRepository.findAll().forEach {

            if (it.referencedInterview.clientUser == null) {
                // commented out as deprecated reference has been removed
                /*clientUserRepository.findById(it.referencedInterview.clientAccount.id.toString()).ifPresent { user ->
                    it.referencedInterview.clientUser = user
                    publishedInterviewRepository.save(it)

                    println("Updated published interview ${it.id} client user")
                }*/
            }

            assertThat(it.referencedInterview.clientUser).isNotNull()
        }
    }

    private fun `update interview session's client user reference`() {

        interviewSessionRepository.count().run {
            println("Found $this records, updating these interview sessions")
        }

        interviewSessionRepository.findAll().forEach {

            // commented out as deprecated reference has been removed

            /*if (it.clientUser == null) {
                clientUserRepository.findById(it.clientAccount.id.toString()).ifPresent { user ->
                    it.clientUser = user
                    interviewSessionRepository.save(it)

                    println("Updated interview session ${it.id} client user")
                }
            }

            assertThat(it.clientUser).isNotNull()

            it.candidateAccount?.let { account ->
                clientUserRepository.findById(account.id.toString()).ifPresent { user ->
                    it.candidateUser = user
                    interviewSessionRepository.save(it)

                    println("Updated interview session ${it.id} candidate")
                }

                assertThat(it.candidateUser).isNotNull()
            }*/
        }

    }

    private fun `update interview's client user reference`() {

        interviewRepository.count().run {
            println("Found $this records, updating these interviews")
        }

        interviewRepository.findAll().forEach {

            if (it.clientUser == null) {
                println("Updating interview ${it.id}")

                // commented out as deprecated reference has been removed
                /*clientUserRepository.findById(it.clientAccount.id.toString()).ifPresent { user ->
                    it.clientUser = user
                    interviewRepository.save(it)

                    println("Updated interview ${it.id} client user")
                }*/
            }

            assertThat(it.clientUser).isNotNull()
        }
    }

    private fun `update question's client user reference`() {

        questionRepository.count().run {
            println("Found $this records, updating these questions")
        }

        questionRepository.findAll().forEach {

            if (it.clientUser == null) {
                // commented out as deprecated reference has been removed
                /*clientUserRepository.findById(it.clientAccount.id.toString()).ifPresent { user ->
                    it.clientUser = user
                    questionRepository.save(it)

                    println("Updated question ${it.id} client user")
                }*/
            }

            assertThat(it.clientUser).isNotNull()
        }
    }

//    private fun `update client account's default plan type and create default client user`() {
//
//        clientAccountRepository.count().run {
//            println("Found $this records, updating these client accounts")
//        }
//
//        clientAccountRepository.findAll().forEach {
//
//            if (it.planType == null) {
//                it.planType = ClientAccount.PlanType.FREE
//                clientAccountRepository.save(it)
//
//                println("Updated client account ${it.id}=${it.email}")
//            }
//
//            if (clientUserRepository.findById(it.id.toString()).isEmpty) {
//
//                val userType = if (it.id.toString().startsWith("github"))
//                    ClientUser.UserType.GITHUB
//                else
//                    ClientUser.UserType.AUTH0
//
//                ClientUser(it.id, it.email, it.clientName, it.clientName, it.avatar, userType, it).let { user ->
//                    clientUserRepository.save(user)
//                    println("Created default client user for ${it.id}")
//                }
//            }
//
//            assertThat(clientUserRepository.findById(it.id.toString())).isNotNull()
//        }
//    }
}