package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.likes.service.LikeService
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.extensions.toSnapshot
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.model.Visibility
import io.geekhub.service.shared.service.ObjectOwnershipService
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_USER_KEY
import io.geekhub.service.specialization.service.SpecializationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.Valid

@RestController
@RequestMapping("/interviews")
class InterviewController(val interviewService: InterviewService,
                          val interviewSessionService: InterviewSessionService,
                          val questionService: QuestionService,
                          val specializationService: SpecializationService,
                          val likeService: LikeService,
                          val objectOwnershipService: ObjectOwnershipService) {

    @PostMapping
    fun createInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                        @Valid @RequestBody request: InterviewRequest): InterviewResponse {

        request.toEntity(clientUser).let {
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
            if (it.visibility == Visibility.PRIVATE && it.clientUser != clientUser) {
                throw BusinessException("You are not the owner of this assessment")
            }

            return it.toDTO(clientUser, it.clientUser == clientUser)
        }
    }

    @GetMapping("/{id}/interviewSession")
    fun getCurrentInterviewSession(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                                   @PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getCurrentInterviewSession(id, clientUser).toDTO(clientUser)
    }

    @GetMapping
    fun listInterviews(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                       @RequestParam map: Map<String, String>,
                       uriComponentsBuilder: UriComponentsBuilder): InterviewsResponse {

        interviewService.getInterviews(SearchCriteria.fromRequestParameters(clientUser, map)).let { result ->
            val navigationLinkBuilder = uriComponentsBuilder.path("/interviews").let {
                map.forEach { entry ->
                    it.queryParam(entry.key, entry.value)
                }

                it
            }

            val likedInterviews = likeService.getLikedObjects(clientUser, Interview::class).map { it.objectId }.toList()

            return InterviewsResponse(result.map { it.toLightDTO(likedInterviews.contains(it.id.toString())) }, navigationLinkBuilder)
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

            it.sections.clear()
            it.sections = toSections(it, request.sections)

            return interviewService.saveInterview(it).toDTO(clientUser, true)
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

    @PostMapping("/{id}/publish")
    fun publishInterview(@RequestAttribute(CLIENT_USER_KEY) clientUser: ClientUser,
                         @PathVariable id: String): PublishedInterviewResponse {

        objectOwnershipService.checkObjectOwnership(clientUser) { interviewService.getInterview(id) }.let {
            interviewService.publishInterview(id).let {
                return it.toDTO(clientUser)
            }
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
                            questionService.saveQuestion(q)
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