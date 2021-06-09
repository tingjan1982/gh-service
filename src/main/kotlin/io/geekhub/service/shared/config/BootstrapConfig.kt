package io.geekhub.service.shared.config

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class BootstrapConfig(val clientAccountService: ClientAccountService, val clientUserService: ClientUserService) {

    companion object {
        val GUEST_CLIENT_ACCOUNT = ClientAccount("guest",
            ClientAccount.AccountType.INDIVIDUAL,
            ClientAccount.PlanType.FREE,
            "guest")

        val GUEST_CLIENT_USER = ClientUser(id = "guest",
            email = "guest@geekhub.tw",
            name = "guest",
            nickname = "guest",
            userType = ClientUser.UserType.AUTH0,
            clientAccount = GUEST_CLIENT_ACCOUNT)
    }

    @PostConstruct
    fun bootstrap() {

        if (clientAccountService.lookupClientAccount(GUEST_CLIENT_ACCOUNT.id.toString()) == null) {
            clientAccountService.saveClientAccount(GUEST_CLIENT_ACCOUNT)
        }

        if (clientUserService.lookupClientUser(GUEST_CLIENT_USER.id.toString()) == null) {
            clientUserService.saveClientUser(GUEST_CLIENT_USER);
        }
    }
}