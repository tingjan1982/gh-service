package io.geekhub.service.user.service

import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import io.geekhub.service.user.web.bean.UserRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.stereotype.Service
import javax.persistence.EntityExistsException
import javax.transaction.Transactional
import org.springframework.security.core.userdetails.User as SpringSecurityUser

@Service
@Transactional
class UserServiceImpl(
        val repository: UserRepository,
        val questionService: QuestionService,
        val userDetailsManager: JdbcUserDetailsManager,
        val passwordEncoder: PasswordEncoder
) : UserService {

    private val logger: Logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    companion object {
        const val USER_ROLE = "USER"
    }

    override fun createUser(userRequest: UserRequest): User {

        if (this.userDetailsManager.userExists(userRequest.username)) {
            throw EntityExistsException("Username ${userRequest.username} already exists in the system")
        }

        val userBuilder = SpringSecurityUser.builder()
                .passwordEncoder(this.passwordEncoder::encode)
                .username(userRequest.username)
                .password(userRequest.password)
                .roles(USER_ROLE)

        this.userDetailsManager.createUser(userBuilder.build())
        logger.info("Created user credentials for ${userRequest.username}")

        val userToCreate = userRequest.toEntity()

        return this.createUser(userToCreate)
    }

    override fun createUser(user: User): User {
        return this.repository.save(user).also {
            logger.info("Created user: $user")
        }
    }


    override fun getUser(id: String): User {
        return this.repository.getOne(id)
    }

    override fun checkUserExists(id: String): Boolean {
        return this.repository.existsById(id)
    }

    override fun updateUser(id: String, userRequest: UserRequest): User {

        val user = userRequest.toEntity().apply {
            this.userId = id
        }

        return this.updateUser(user)
    }

    override fun updateUser(user: User): User {
        return this.repository.getOne(user.id.toString()).apply {
            this.firstName = user.firstName
            this.lastName = user.lastName
            this.rank = user.rank
        }
    }

    override fun addSavedQuestion(id: String, questions: List<String>): User {

        return this.getUser(id).apply {
            questions.forEach({
                questionService.getQuestion(it)?.let { question ->
                    this.savedQuestions[it] = question
                }
            })
        }
    }
}
