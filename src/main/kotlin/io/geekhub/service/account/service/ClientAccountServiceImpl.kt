package io.geekhub.service.account.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientAccountRepository
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.notification.service.NotificationService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.exception.BusinessObjectNotFoundException
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service

@Service
class ClientAccountServiceImpl(
    val repository: ClientAccountRepository,
    val clientUserService: ClientUserService,
    val questionService: QuestionService,
    val interviewService: InterviewService,
    val notificationService: NotificationService,
    val mongoTemplate: MongoTemplate
) : ClientAccountService {

    override fun saveClientAccount(clientAccount: ClientAccount): ClientAccount {
        return repository.save(clientAccount)
    }

    override fun getClientAccount(id: String): ClientAccount {
        return repository.findById(id).orElseThrow {
            throw BusinessObjectNotFoundException(ClientAccount::class, id)
        }
    }

    override fun lookupClientAccount(id: String): ClientAccount? {
        return repository.findById(id).orElse(null)
    }

    override fun getClientOrganizationAccount(id: String): ClientAccount {
        return repository.findByIdAndAccountType(id, ClientAccount.AccountType.CORPORATE)
            ?: throw BusinessObjectNotFoundException(ClientAccount::class, id)
    }

    override fun enableOrganization(clientUser: ClientUser, organizationName: String): ClientAccount {

        if (clientUser.clientAccount.accountType == ClientAccount.AccountType.CORPORATE) {
            throw BusinessException("User is already part of an organization")
        }

        ClientAccount(
            accountType = ClientAccount.AccountType.CORPORATE,
            planType = ClientAccount.PlanType.FREE,
            clientName = organizationName
        ).apply {
            this.addUser(clientUser)
            saveClientAccount(this)

            clientUser.clientAccount = this
            clientUserService.saveClientUser(clientUser)

            return this
        }
    }

    override fun changeOrganizationOwner(currentOwner: ClientUser, newOwner: ClientUser) {

        if (!currentOwner.isOrgOwner()) {
            throw BusinessException("Only owner can change organization ownership")
        }

        if (newOwner.clientAccount != currentOwner.clientAccount) {
            throw BusinessException("New owner is not in the same organization")
        }

        if (currentOwner == newOwner) {
            throw BusinessException("New owner is already the owner")
        }

        newOwner.accountPrivilege = ClientUser.AccountPrivilege.OWNER
        clientUserService.saveClientUser(newOwner)

        currentOwner.accountPrivilege = ClientUser.AccountPrivilege.ADMIN
        clientUserService.saveClientUser(currentOwner)
    }

    /**
     * User can be invited to many corporate accounts.
     */
    override fun getInvitedCorporateAccounts(email: String): List<ClientAccount.UserInvitation> {

        val query = Query.query(
            where("userInvitations").elemMatch(
                where("email").isEqualTo(email).and("status").isEqualTo(ClientAccount.InvitationStatus.INVITED)
            )
        )

        return mongoTemplate.find(query, ClientAccount::class.java).map { acc ->
            return@map acc.userInvitations.find { it.email == email }!!
        }.toList()
    }

    override fun inviteOrganizationUser(inviter: ClientUser, organizationAccount: ClientAccount, inviteeEmail: String): ClientAccount {

        checkClientOrganization(inviter, organizationAccount)

        if (clientUserService.clientUserExists(organizationAccount, inviteeEmail)) {
            throw BusinessException("User has already joined the organization")
        }

        organizationAccount.addUserInvitation(inviter, inviteeEmail).let {
            if (it.status == ClientAccount.InvitationStatus.DECLINED) {
                throw BusinessException("User has declined this invitation")
            }

            notificationService.sendOrganizationInvitation(it, organizationAccount)

            return saveClientAccount(organizationAccount)
        }
    }

    override fun uninviteOrganizationUser(clientUser: ClientUser, organizationAccount: ClientAccount, email: String): ClientAccount {

        checkClientOrganization(clientUser, organizationAccount)

        organizationAccount.removeUserInvitation(email).let {
            return saveClientAccount(it)
        }
    }

    override fun joinOrganization(clientUser: ClientUser, organizationAccount: ClientAccount): ClientAccount {

        leaveOrganization(clientUser, organizationAccount)

        clientUser.accountPrivilege = ClientUser.AccountPrivilege.USER
        clientUser.clientAccount = organizationAccount

        clientUserService.saveClientUser(clientUser).let {
            organizationAccount.userInvitationJoined(clientUser)

            return saveClientAccount(organizationAccount)
        }
    }

    override fun userDeclineOrganizationInvitation(clientUser: ClientUser, organizationAccount: ClientAccount) {

        organizationAccount.userInvitationDeclined(clientUser.email)
        saveClientAccount(organizationAccount)
    }

    override fun leaveOrganization(clientUser: ClientUser, organizationAccount: ClientAccount) {

        if (clientUser.clientAccount.accountType == ClientAccount.AccountType.INDIVIDUAL) {
            return
        }

        if (clientUser.isOrgOwner()) {
            if (organizationAccount.users.size > 1) {
                throw BusinessException("Your organization has more than 1 user. Please remove all organization users before leaving organization")
            }
        }

        clientUserService.getClientAccountOwner(organizationAccount).let { owner ->
            questionService.getQuestions(clientUser).forEach { q ->
                q.clientUser = owner
                questionService.saveQuestion(q)
            }

            interviewService.getInterviews(clientUser).forEach { iv ->
                iv.clientUser = owner
                interviewService.saveInterview(iv)
            }
        }

        organizationAccount.removeUser(clientUser)
        this.saveClientAccount(organizationAccount)

        // return to user's previous client account
        this.getClientAccount(clientUser.id.toString()).let {
            clientUser.department = null
            clientUser.clientAccount = it
            clientUser.accountPrivilege = ClientUser.AccountPrivilege.OWNER

            clientUserService.saveClientUser(clientUser)
        }
    }

    private fun checkClientOrganization(clientUser: ClientUser, organizationAccount: ClientAccount) {

        if (clientUser.clientAccount.id != organizationAccount.id || clientUser.accountPrivilege == ClientUser.AccountPrivilege.USER) {
            throw BusinessException("Only organization owner can perform this action")
        }
    }
}