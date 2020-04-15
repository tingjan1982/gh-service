package io.geekhub.service.interview.web.model

data class AnswerAttemptRequest(
        val sectionId: String,
        val questionSnapshotId: String,
        val answerId: List<String>?,
        val answer: String?
)
