package io.geekhub.service.user.service

import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import io.geekhub.service.user.web.bean.UserRequest
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserServiceImpl(val repository: UserRepository) : UserService {

    override fun createUser(userRequest: UserRequest): User {

        val userToCreate = userRequest.toEntity()
        return this.createUser(userToCreate)
    }

    override fun createUser(user: User): User {
        return this.repository.save(user)
    }


    override fun getUser(id: String): User {
        return this.repository.getOne(id)
    }

    override fun updateUser(id: String, userRequest: UserRequest): User {

        val user = userRequest.toEntity().apply {
            this.userId = id
        }

        return this.updateUser(user)
    }

    override fun updateUser(user: User): User {
        return this.repository.getOne(user.id!!).apply {
            this.firstName = user.firstName
            this.lastName = user.lastName
            this.rank = user.rank
        }
    }
}