package io.geekhub.service.interview.web

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.interview.model.Interview
import io.geekhub.service.interview.service.InterviewService
import io.geekhub.service.interview.web.model.InterviewRequest
import io.geekhub.service.interview.web.model.InterviewResponse
import io.geekhub.service.interview.web.model.InterviewsResponse
import io.geekhub.service.questions.service.QuestionService
import io.geekhub.service.shared.extensions.toDTO
import io.geekhub.service.shared.extensions.toEntity
import io.geekhub.service.shared.extensions.toLightDTO
import io.geekhub.service.shared.web.filter.ClientAccountFilter.Companion.CLIENT_KEY
import io.geekhub.service.specialization.service.SpecializationService
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.data.domain.PageRequest
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

        request.toEntity(clientAccount, specialization, toSections(request.sections)).let {
            interviewService.saveInterview(it).let { created ->
                return created.toDTO()
            }
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

            it.sections.clear()
            toSections(request.sections).forEach { section ->
                it.addSection(section)
            }

            return interviewService.saveInterview(it).toDTO()
        }
    }

    fun toSections(sections: List<InterviewRequest.SectionRequest>): MutableList<Interview.Section> {
        return sections.map {
            it.toEntity().let { s ->
                it.questions.forEachIndexed { index, qid ->
                    s.addQuestion(index.toString(), questionService.getQuestion(qid))
                }

                return@map s
            }
        }.toMutableList()
    }

    @GetMapping("/{id}")
    fun getInterview(@PathVariable id: String): InterviewResponse {

        interviewService.getInterview(id).let {
            return it.toDTO()
        }
    }

    @GetMapping
    fun listInterviews(@RequestAttribute(CLIENT_KEY) clientAccount: ClientAccount,
                       @RequestParam(value = "currentPage", defaultValue = "-1") currentPage: Int,
                       @RequestParam(value = "next", defaultValue = "true") next: Boolean,
                       @RequestParam(value = "page", defaultValue = "0") page: Int,
                       @RequestParam(value = "pageSize", defaultValue = "50") pageSize: Int,
                       @RequestParam(value = "sort", defaultValue = "lastModifiedDate") sortField: String): InterviewsResponse {

        val pageToUse: Int = if (next) currentPage + 1 else page
        val pageRequest = PageRequest.of(pageToUse, pageSize)
        interviewService.getInterviews(clientAccount, pageRequest).let { result ->
            val contextPath = serverProperties.servlet.contextPath
            return InterviewsResponse(result.map { it.toLightDTO() }, contextPath, "interviews")
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    fun deleteInterview(@PathVariable id: String) {

        interviewService.deleteInterview(id)
    }
}