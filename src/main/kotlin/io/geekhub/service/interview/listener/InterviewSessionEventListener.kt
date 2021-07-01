package io.geekhub.service.interview.listener

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.model.LightInterviewSession
import io.geekhub.service.interview.repository.LightInterviewSessionRepository
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import org.springframework.stereotype.Component

@Component
class InterviewSessionEventListener(val lightInterviewSessionRepository: LightInterviewSessionRepository) : AbstractMongoEventListener<InterviewSession>() {

    override fun onAfterSave(event: AfterSaveEvent<InterviewSession>) {
        super.onAfterSave(event)

        lightInterviewSessionRepository.findById(event.source.id.toString()).orElseGet {
            return@orElseGet LightInterviewSession(event.source)
        }.let {
            it.status = event.source.status

            lightInterviewSessionRepository.save(it)
        }
    }
}