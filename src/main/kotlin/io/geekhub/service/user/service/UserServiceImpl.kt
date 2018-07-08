package io.geekhub.service.user.service

import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import io.geekhub.service.user.web.bean.UpdateUserRequest
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

        this.checkUserRequest(userRequest)

        val userBuilder = SpringSecurityUser.builder()
                .passwordEncoder(this.passwordEncoder::encode)
                .username(userRequest.username)
                .password(userRequest.password)
                .roles(USER_ROLE)

        this.userDetailsManager.createUser(userBuilder.build())
        logger.info("Created user credentials for ${userRequest.username}")

        userRequest.toEntity().let {
            this.repository.save(it).also {
                logger.info("Created user: $it")
            }

            return it
        }
    }

    private fun checkUserRequest(userRequest: UserRequest) {
        if (this.userDetailsManager.userExists(userRequest.username)) {
            throw EntityExistsException("Username ${userRequest.username} already exists in the system")
        }

        this.repository.findByEmail(userRequest.email).ifPresent {
            throw EntityExistsException("Email ${userRequest.email} already exists in the system")
        }
    }

    override fun getUser(id: String): User {
        return this.repository.getOne(id)
    }

    override fun checkUserExists(id: String): Boolean {
        return this.repository.existsById(id)
    }

    /**
     * It is important to construct UserDetails and retain authorities when calling updateUser function,
     * so authorities are not deleted.
     */
    override fun updateUser(id: String, userRequest: UpdateUserRequest): User {

        this.getUser(id).let {
            val userBuilder = SpringSecurityUser.builder()
                    .passwordEncoder(this.passwordEncoder::encode)
                    .username(it.username)
                    .password(userRequest.password)
                    .roles(USER_ROLE)

            this.userDetailsManager.updateUser(userBuilder.build())
            logger.info("User ${it.username} updated password")

            it.apply {
                this.firstName = userRequest.firstName ?: this.firstName
                this.lastName = userRequest.lastName ?: this.lastName
            }

            return it
        }
    }

    override fun addSavedQuestion(id: String, questions: List<String>): User {

        return this.getUser(id).apply {
            questions.forEach {
                questionService.getQuestion(it)?.let { question ->
                    this.savedQuestions[it] = question
                }
            }
        }
    }
}
