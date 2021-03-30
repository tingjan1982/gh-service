package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.auth0.service.Auth0ManagementService
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service

@Service
class ClientAccountServiceImpl(val repository: ClientAccountRepository,
                               val clientUserService: ClientUserService,
                               val questionService: QuestionService,
                               val interviewService: InterviewService,
                               val mongoTemplate: MongoTemplate,
                               val auth0ManagementService: Auth0ManagementService) : ClientAccountService {

    override fun saveClientAccount(clientAccount: ClientAccount): ClientAccount {
        return repository.save(clientAccount)
    }

    override fun getClientAccount(id: String): ClientAccount {
        return repository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(ClientAccount::class, id)
        }
    }

    override fun getClientOrganizationAccount(id: String): ClientAccount {
        return repository.findByIdAndAccountType(id, ClientAccount.AccountType.CORPORATE)
            ?: throw BusinessObjectNotFoundException(ClientAccount::class, id)
    }

    override fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount {

        if (clientUser.clientAccount.accountType == ClientAccount.AccountType.CORPORATE) {
            return clientUser.clientAccount
        }

        clientUser.clientAccount.apply {
            this.accountType = ClientAccount.AccountType.CORPORATE
            this.clientName = organizationName
        }.let {
            return saveClientAccount(it)
        }
    }

    /**
     * User can be invited to many corporate accounts.
     */
    override fun getInvitedCorporateAccounts(email: String): List<ClientAccount.UserInvitation> {

        val query = Query.query(where("userInvitations").elemMatch(where("email").isEqualTo(email)))

        return mongoTemplate.find(query, ClientAccount::class.java).map { acc ->
            return@map acc.userInvitations.find { it.email == email }!!
        }.toList()
    }

    override fun inviteOrganizationUser(inviter: ClientUser, organizationAccount: ClientAccount, inviteeEmail: String): ClientAccount {

        checkClientOrganization(inviter, organizationAccount)

        if (clientUserService.clientUserExists(organizationAccount, inviteeEmail)) {
            throw BusinessException("Invited email is already used")
        }

        organizationAccount.addUserInvitation(inviter, inviteeEmail).let {
            return saveClientAccount(it)
        }
    }

    override fun uninviteOrganizationUser(clientUser: ClientUser, organizationAccount: ClientAccount, email: String): ClientAccount {

        checkClientOrganization(clientUser, organizationAccount)

        organizationAccount.removeUserInvitation(email).let {
            return saveClientAccount(it)
        }
    }

    override fun joinOrganization(clientUser: ClientUser, organizationAccount: ClientAccount): ClientAccount {

        checkClientUserAccess(clientUser)

        clientUser.clientAccount.let {
            repository.delete(it)
        }

        clientUser.accountPrivilege = ClientUser.AccountPrivilege.USER
        clientUser.clientAccount = organizationAccount

        clientUserService.saveClientUser(clientUser).let {
            organizationAccount.userInvitationJoined(clientUser.email)

            return saveClientAccount(organizationAccount)
        }
    }

    override fun leaveOrganization(clientUser: ClientUser) {

        if (clientUser.clientAccount.accountType != ClientAccount.AccountType.CORPORATE) {
            throw BusinessException("User is not part of an organization")
        }

        if (clientUser.accountPrivilege == ClientUser.AccountPrivilege.OWNER) {
            throw BusinessException("Organization owner cannot leave organization")
        }

        clientUserService.getClientAccountOwner(clientUser.clientAccount).let { owner ->
            questionService.getQuestions(clientUser).forEach { q ->
                q.clientUser = owner
                questionService.saveQuestion(q)
            }

            interviewService.getInterviews(clientUser).forEach { iv ->
                iv.clientUser = owner
                interviewService.saveInterview(iv)
            }
        }

        ClientAccount(clientUser.id, ClientAccount.AccountType.INDIVIDUAL, ClientAccount.PlanType.FREE, clientUser.name).let {
            this.saveClientAccount(it)
            clientUser.accountPrivilege = ClientUser.AccountPrivilege.OWNER
            clientUser.clientAccount = it
            clientUserService.saveClientUser(clientUser)
        }
    }

    private fun checkClientUserAccess(clientUser: ClientUser) {

        if (clientUser.clientAccount.accountType == ClientAccount.AccountType.CORPORATE) {
            throw BusinessException("User is already part of an organization: ${clientUser.clientAccount.clientName}")
        }
    }

    private fun checkClientOrganization(clientUser: ClientUser, organizationAccount: ClientAccount) {

        if(clientUser.clientAccount.id != organizationAccount.id || clientUser.accountPrivilege == ClientUser.AccountPrivilege.USER) {
            throw BusinessException("Only organization owner can perform this action")
        }
    }
}