package io.geekhub.service.interview.web.model

data class MarkAnswerRequest(
        val sectionId: String,
        val questionSnapshotId: String,
        val correct: Boolean
)