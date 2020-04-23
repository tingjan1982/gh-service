package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.service.InterviewSessionService
import io.geekhub.service.interview.toDTO
import io.geekhub.service.interview.web.model.*
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.extensions.toSnapshot
import io.geekhub.service.shared.model.SearchCriteria
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_KEY
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
                          val specializationService: SpecializationService) {

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
                     @PathVariable id: String): InterviewResponse {

        interviewService.getInterview(id).let {
            val showAnswer = it.clientAccount.id == clientAccount.id
            return it.toDTO(showAnswer)
        }
    }

    @GetMapping("/{id}/interviewSession")
    fun getCurrentInterviewSession(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                                   @PathVariable id: String): InterviewSessionResponse {

        return interviewSessionService.getCurrentInterviewSession(id, clientAccount).toDTO(clientAccount)
    }

    @GetMapping
    fun listInterviews(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                       @RequestParam map: Map<String, String>,
                       uriComponentsBuilder: UriComponentsBuilder): InterviewsResponse {

        interviewService.getInterviews(SearchCriteria.fromRequestParameters(clientAccount, map)).let { result ->
            val navigationLinkBuilder = uriComponentsBuilder.path("/interviews").let {
                map.forEach { entry ->
                    it.queryParam(entry.key, entry.value)
                }

                it
            }

            return InterviewsResponse(result.map { it.toLightDTO() }, navigationLinkBuilder)
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