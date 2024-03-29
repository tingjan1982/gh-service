package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.account.service.ClientAccountService
import io.geekhub.service.account.service.ClientUserService
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.likes.service.LikeService
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.extensions.toSnapshot
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.service.ObjectOwnershipService
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/interviews")
class InterviewController(val interviewService: InterviewService,
                          val interviewSessionService: InterviewSessionService,
                          val clientAccountService: ClientAccountService,
                          val clientUserService: ClientUserService,
                          val likeService: LikeService,
                          val objectOwnershipService: ObjectOwnershipService) {

    @PostMapping
    fun createInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @Valid @RequestBody request: InterviewRequest): InterviewResponse {

        val owningAccount = when (request.ownershipType) {
            Interview.OwnershipType.DEFAULT -> clientUser.clientAccount
            Interview.OwnershipType.PERSONAL -> clientAccountService.getClientAccount(clientUser.id.toString())
        }

        request.toEntity(clientUser, request.ownershipType, owningAccount).let {
            it.sections = toSections(it, request.sections)

            interviewService.saveInterview(it).let { created ->
                return created.toDTO(clientUser, true)
            }
        }
    }

    @GetMapping("/{id}")
    fun getInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                     @PathVariable id: String): InterviewResponse {

        interviewService.getInterview(id).let {
            val showAnswer = clientUser.id == it.clientUser.id || it.clientUser.isTemplateUser()

            return it.toDTO(clientUser, true, showAnswer)
        }
    }

    @GetMapping("/{id}/interviewSession")
    fun getCurrentInterviewSession(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                                   @PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getCurrentInterviewSession(id, clientUser).toDTO(clientUser)
    }

    @GetMapping
    fun listInterviews(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                       @RequestParam params: Map<String, String>,
                       uriComponentsBuilder: UriComponentsBuilder): InterviewsResponse {

        val searchCriteria = SearchCriteria.fromRequestParameters(clientUser, params)
        val interviews: Page<Interview> = if (searchCriteria.invited) {
            interviewSessionService.getInterviewSessions(searchCriteria, null).map {
                it.publishedInterview.referencedInterview
            }
        } else {
            interviewService.getInterviews(searchCriteria)
        }

        interviews.let { result ->
            val navigationLinkBuilder = uriComponentsBuilder.path("/interviews").let {
                params.forEach { entry ->
                    it.queryParam(entry.key, entry.value)
                }

                it
            }

            val likedInterviews = likeService.getLikedObjects(clientUser, Interview::class).map { it.objectId }.toList()

            return InterviewsResponse(result.map { it.toLightDTO(likedInterviews.contains(it.id.toString())) }, navigationLinkBuilder)
        }
    }

    @GetMapping("/stats")
    fun getInterviewStats(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser): ClientUser.AssessmentStats {

        clientUser.assessmentStats.apply {
            this.mine = interviewService.getInterviewsCount(clientUser)
            this.liked = likeService.getLikedObjects(clientUser, Interview::class).count()
        }

        clientUserService.saveClientUser(clientUser).let {
            return it.assessmentStats
        }
    }

    @PostMapping("/{id}")
    fun updateInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @PathVariable id: String,
                        @Valid @RequestBody request: InterviewRequest): InterviewResponse {

        objectOwnershipService.checkObjectOwnership(clientUser) { interviewService.getInterview(id) }.let {
            it.title = request.title
            it.description = request.description
            it.jobTitle = request.jobTitle
            it.visibility = request.visibility
            it.defaultDuration = request.defaultDuration
            it.releaseResult = request.releaseResult

            it.sections.clear()
            it.sections = toSections(it, request.sections)

            return interviewService.saveInterview(it).toDTO(clientUser, true)
        }
    }

    @PostMapping("/{id}/copy")
    fun copyInterview(
        @RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
        @PathVariable id: String
    ): InterviewResponse {

        interviewService.getInterview(id).let {
            return interviewService.copyInterview(it, clientUser).toDTO(clientUser)
        }
    }


    @PostMapping("/{id}/like")
    fun likeInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                      @PathVariable id: String): InterviewResponse {

        interviewService.getInterview(id).let {
            likeService.like(clientUser, it)
            return interviewService.getInterview(id).toDTO(clientUser)
        }
    }

    @PostMapping("/{id}/unlike")
    fun unlikeInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @PathVariable id: String): InterviewResponse {

        interviewService.getInterview(id).let {
            likeService.unlike(clientUser, it)
            return interviewService.getInterview(id).toDTO(clientUser)
        }
    }

    @PostMapping("/{id}/owner")
    fun changeInterviewOwner(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                             @PathVariable id: String,
                             @Valid @RequestBody request: ChangeOwnerRequest): InterviewResponse {

        interviewService.getInterview(id).let {
            return interviewService.updateInterviewOwner(it, request.userId).toDTO(clientUser)
        }
    }

    fun toSections(interview: Interview, sections: List<InterviewRequest.SectionRequest>): MutableList<Interview.Section> {

        // s@ is used to get rid of the warning of return@map as it detect return label clash.
        return sections.map s@{
            it.toEntity().let { s ->
                val questionSnapshots = it.questions.map { qRequest ->
                    if (qRequest.id == null && qRequest.questionId == null) {
                        qRequest.toEntity(interview).let { q ->
                            //questionService.saveQuestion(q)
                        }
                    }

                    qRequest.toSnapshot()

                }.toMutableList()

                s.questions = questionSnapshots

                return@let s
            }
        }.toMutableList()
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteInterview(@PathVariable id: String) {

        interviewService.deleteInterview(id)
    }
}