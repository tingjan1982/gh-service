package io.geekhub.service.script

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.shared.annotation.IntegrationTest
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.atomic.AtomicInteger

@Disabled
@IntegrationTest
@TestPropertySource(properties = ["spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration"])
class ManageClientAccount {

    @Autowired
    lateinit var clientUserService: ClientUserService

    @Autowired
    lateinit var clientAccountService: ClientAccountService

    @Autowired
    lateinit var clientAccountRepository: ClientAccountRepository

    @Test
    fun updateClientAccounts() {

        val changeCount = AtomicInteger()

        clientAccountRepository.findAll().forEach { acc ->
            if (acc.userInvitations.isNotEmpty()) {
                println("Processing account: ${acc.id} ${acc.clientName} ${acc.accountType}")

                val clientUsers = clientUserService.getClientUsers(acc)
                clientUsers.forEach {
                    println("> user: ${it.id} ${it.name}")
                }

                if (acc.accountType == ClientAccount.AccountType.CORPORATE && clientUsers.isEmpty()) {
                    clientAccountRepository.delete(acc)
                    println("Deleted corporate account without users")

                } else {
                    acc.userInvitations.forEach {
                        it.inviterOrganizationId = acc.id

                        println("> user invitation: $it")
                        changeCount.incrementAndGet()
                    }

                    clientAccountRepository.save(acc)
                }
            }
        }

        println("Updated user invitations: $changeCount")

    }

    @Test
    fun `Remove ClientAccounts without owner`() {

        val count = AtomicInteger()
        clientAccountRepository.findAll()
            .filter { it.accountType == ClientAccount.AccountType.CORPORATE }
            .filter { it.users.isEmpty() }
            .filter { it.userInvitations.isEmpty() }
            .forEach {
                clientAccountRepository.delete(it)
                count.incrementAndGet()
            }

        println("Removed $count ClientAccount")
    }

    @Test
    fun updateOrgAccount() {

        
    }
}