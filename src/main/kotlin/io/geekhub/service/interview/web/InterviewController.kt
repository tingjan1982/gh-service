package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.web.model.InterviewRequest
import io.geekhub.service.interview.web.model.InterviewResponse
import io.geekhub.service.interview.web.model.InterviewsResponse
import io.geekhub.service.interview.web.model.PublishedInterviewResponse
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.exception.BusinessException
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.extensions.toSnapshot
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_KEY
import io.geekhub.service.specialization.service.SpecializationService
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/interviews")
class InterviewController(val interviewService: InterviewService,
                          val questionService: QuestionService,
                          val specializationService: SpecializationService,
                          val serverProperties: ServerProperties) {

    @PostMapping
    fun createInterview(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                        @Valid @RequestBody request: InterviewRequest): InterviewResponse {

        val specialization = request.specializationId.let {
            specializationService.getSpecialization(it)
        }

        request.toEntity(clientAccount, specialization).let {
            it.sections = toSections(it, request.sections)

            interviewService.saveInterview(it).let { created ->
                return created.toDTO()
            }
        }
    }

    @GetMapping("/{id}")
    fun getInterview(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                     @PathVariable id: String,
                     @RequestParam(value = "published", required = false) published: Boolean): InterviewResponse {

        interviewService.getInterview(id).let {
            if (published && it.latestPublishedInterviewId == null) {
                throw BusinessException("This interview has not been published yet: ${it.id}")
            }

            if (published) {
                interviewService.getPublishedInterviewByPublishedId(it.latestPublishedInterviewId).referencedInterview
            } else {
                it
            }.let { resolved ->
                val showAnswer = resolved.clientAccount.id == clientAccount.id
                return resolved.toDTO(showAnswer)
            }
        }
    }

    @GetMapping
    fun listInterviews(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                       @RequestParam map: Map<String, String>): InterviewsResponse {

        interviewService.getInterviews(SearchCriteria.fromRequestParameters(clientAccount, map)).let { result ->
            val contextPath = serverProperties.servlet.contextPath
            return InterviewsResponse(result.map { it.toLightDTO() }, contextPath, "interviews")
        }
    }

    @PostMapping("/{id}")
    fun updateInterview(@PathVariable id: String,
                        @Valid @RequestBody request: InterviewRequest): InterviewResponse {

        interviewService.getInterview(id).let {
            it.title = request.title
            it.jobTitle = request.jobTitle

            specializationService.getSpecialization(request.specializationId).let { specialization ->
                it.specialization = specialization
            }

            it.visibility = request.visibility

            it.sections.clear()
            it.sections = toSections(it, request.sections)

            return interviewService.saveInterview(it).toDTO()
        }
    }

    @PostMapping("/{id}/publish")
    fun publishInterview(@PathVariable id: String): PublishedInterviewResponse {

        interviewService.publishInterview(id).let {
            return it.toDTO()
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