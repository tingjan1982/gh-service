package io.geekhub.service.interview.web.model

data class InterviewRequest(
        val title: String,
        val description: String?,
        val jobTitle: String,
        val specializationId: String,
        val sections: List<SectionRequest> = listOf()
) {
    data class SectionRequest(
            val title: String,
            val questions: List<String> = listOf()
    )
}
