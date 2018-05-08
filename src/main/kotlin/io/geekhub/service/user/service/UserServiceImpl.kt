package io.geekhub.service.user.service

import io.geekhub.service.user.model.User
import io.geekhub.service.user.repository.UserRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class UserServiceImpl(val repository: UserRepository) : UserService {

    override fun createUser(user: User): User {
        return this.repository.save(user)
    }

    override fun updateUser(user: User): User {
        return this.repository.getOne(user.id).apply {
            this.firstName = user.firstName
            this.lastName = user.lastName
            this.rank = user.rank
        }
    }
}