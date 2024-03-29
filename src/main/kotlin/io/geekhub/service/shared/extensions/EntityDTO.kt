package io.geekhub.service.shared.extensions

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.web.ClientOrganizationController
import io.geekhub.service.account.web.ClientUserController
import io.geekhub.service.account.web.model.*
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.web.model.InterviewRequest
import io.geekhub.service.interview.web.model.InterviewResponse
import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.model.Question.PossibleAnswer
import io.geekhub.service.questions.web.bean.QuestionRequest
import io.geekhub.service.questions.web.bean.QuestionResponse
import io.geekhub.service.specialization.repository.Specialization
import io.geekhub.service.specialization.web.model.SpecializationRequest
import io.geekhub.service.specialization.web.model.SpecializationResponse
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder


fun QuestionRequest.toEntity(user: ClientUser) = Question(
        question = this.question,
        questionType = this.questionType ?: Question.QuestionType.MULTI_CHOICE,
        clientUser = user,
        jobTitle = this.jobTitle,
        visibility = this.visibility,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun QuestionRequest.PossibleAnswerRequest.toEntity() = PossibleAnswer(answer = this.answer, correctAnswer = this.correctAnswer)

fun Question.toDTO(liked: Boolean = false) = QuestionResponse(
        this.id.toString(),
        this.question,
        this.questionType,
        this.clientUser.toDTO(),
        this.jobTitle,
        this.possibleAnswers.map { it.toDTO(true) }.toList(),
        this.visibility,
        this.likeCount,
        liked,
        this.deleted,
        this.createdDate,
        this.lastModifiedDate
)

fun PossibleAnswer.toDTO(showAnswer: Boolean) = QuestionResponse.PossibleAnswerResponse(this.answerId, this.answer, if (showAnswer) this.correctAnswer else null)

fun InterviewRequest.toEntity(user: ClientUser, ownershipType: Interview.OwnershipType, owningAccount: ClientAccount) = Interview(
        title = this.title,
        description = this.description,
        jobTitle = this.jobTitle,
        clientUser = user,
        ownershipType = ownershipType,
        clientAccount = owningAccount.id,
        visibility = this.visibility,
        defaultDuration = this.defaultDuration,
        releaseResult = this.releaseResult
)

fun InterviewRequest.SectionRequest.toEntity() = Interview.Section(
        title = this.title
)

fun InterviewRequest.InterviewQuestionRequest.toEntity(interview: Interview) = Question(
        question = this.question,
        questionType = this.questionType,
        clientUser = interview.clientUser,
        jobTitle = interview.jobTitle,
        visibility = interview.visibility,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun InterviewRequest.InterviewQuestionRequest.toSnapshot() = Interview.QuestionSnapshot(
        question = this.question,
        questionType = this.questionType,
        possibleAnswers = this.possibleAnswers.map { it.toEntity() }.toMutableList()
)

fun Interview.toDTO(currentUser: ClientUser, showSection: Boolean = false, showAnswer: Boolean = currentUser.id == this.clientUser.id): InterviewResponse {

    return InterviewResponse(
            id = this.id.toString(),
            title = this.title,
            description = this.description,
            jobTitle = this.jobTitle,
            clientUser = this.clientUser.toDTO(),
            ownershipType = this.ownershipType,
            sections = if (showSection) { this.sections.map { it.toDTO(showAnswer) } } else { listOf() },
            visibility = this.visibility,
            defaultDuration = this.defaultDuration,
            releaseResult = this.releaseResult,
            publishedInterviewId = this.latestPublishedInterviewId,
            likeCount = this.likeCount,
            groupedInterviewSessions = this.groupInterviewSessions(),
            deleted = this.deleted,
            createdDate = this.createdDate,
            lastModifiedDate = this.lastModifiedDate
    )
}

fun Interview.Section.toDTO(showAnswer: Boolean) = InterviewResponse.SectionResponse(
        id = this.id,
        title = this.title,
        questions = this.questions.map { it.toDTO(showAnswer) }.toList()
)

fun Interview.QuestionSnapshot.toDTO(showAnswer: Boolean) = InterviewResponse.QuestionSnapshotResponse(
        id = this.id,
        question = this.question,
        questionType = this.questionType,
        possibleAnswers = this.possibleAnswers.map { it.toDTO(showAnswer) }
)

fun ClientAccount.toDTO() = ClientAccountResponse(
    this.id.toString(),
    this.clientName,
    this.accountType,
    this.planType,
    this.userInvitations.map { it.toDTO() }.toSet()
)

fun ClientAccount.toLightOrganization() = ClientUserResponse.OrganizationResponse(
        this.id.toString(),
        this.clientName
)

fun ClientAccount.toOrganization(): ClientOrganizationResponse {

        val users = this.users.map { it.toOrgUserDTO() }.toList()
        val uriPrefix = MvcUriComponentsBuilder.fromController(ClientOrganizationController::class.java).toUriString()

        return ClientOrganizationResponse(
                this.id.toString(),
                this.clientName,
                if (this.avatarBinary != null) { "$uriPrefix/$id/avatar" } else { null },
                this.userInvitations,
                users
        )
}

fun ClientUser.toDTO(
    metadata: Map<String, Any>? = mapOf(),
    invitations: List<UserInvitationResponse> = listOf()
): ClientUserResponse {

    val uriPrefix = MvcUriComponentsBuilder.fromController(ClientUserController::class.java).toUriString()

    val locale = metadata?.get("locale")?.let {
        if (it is List<*> && it.isNotEmpty()) {
            return@let it[0] as String
        } else if (it is String) {
            return@let it
        } else {
            return@let ClientUser.DEFAULT_LOCALE
        }
    } ?: ClientUser.DEFAULT_LOCALE

    return ClientUserResponse(
        id = this.id.toString(),
        email = this.email,
        name = this.name,
        nickname = this.nickname,
        avatar = if (this.avatarBinary != null) {
            "$uriPrefix/$id/avatar"
        } else {
            this.avatar
        },
        userType = this.userType,
        accountType = this.clientAccount.accountType,
        accountPrivilege = this.accountPrivilege,
        organization = if (this.clientAccount.accountType == ClientAccount.AccountType.CORPORATE) {
            this.clientAccount.toLightOrganization()
        } else {
            null
        },
        department = this.department?.toDTO(),
        stats = this.assessmentStats,
        locale = locale,
        metadata = metadata,
        invitations = invitations
    )
}

fun ClientUser.toLightDTO(): LightClientUserResponse {

        val uriPrefix = MvcUriComponentsBuilder.fromController(ClientUserController::class.java).toUriString()

        return LightClientUserResponse(
                id = this.id.toString(),
                name = this.name,
                nickname = this.nickname,
                email = this.email,
                avatar = if (this.avatarBinary != null) { "$uriPrefix/$id/avatar" } else { this.avatar },
                organization = if (this.clientAccount.accountType == ClientAccount.AccountType.CORPORATE) { this.clientAccount.toLightOrganization() } else { null },
        )
}

fun ClientUser.toOrgUserDTO(): OrganizationClientUserResponse {

        val uriPrefix = MvcUriComponentsBuilder.fromController(ClientUserController::class.java).toUriString()

        return OrganizationClientUserResponse(
                id = this.id.toString(),
                name = this.name,
                email = this.email,
                avatar = if (this.avatarBinary != null) { "$uriPrefix/$id/avatar" } else { this.avatar },
                department = this.department?.toDTO(),
                accountPrivilege = this.accountPrivilege
        )
}

fun ClientAccount.UserInvitation.toDTO() = UserInvitationResponse(
        inviterId = this.inviterId,
        inviterName = this.inviterName,
        inviterEmail = this.inviterEmail,
        inviterOrganization = this.inviterOrganization,
        inviterOrganizationId = this.inviterOrganizationId!!,
        email = this.email,
        status = this.status
)

fun SpecializationRequest.toEntity() = Specialization(
        name = this.name
)

fun Specialization.toDTO() = SpecializationResponse(
        this.id.toString(),
        this.name
)

