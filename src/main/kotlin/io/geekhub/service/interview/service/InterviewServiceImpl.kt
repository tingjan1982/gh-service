package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.Interview
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InterviewServiceImpl : InterviewService {

    companion object {
        val logger = LoggerFactory.getLogger(InterviewServiceImpl::class.java)!!
    }

    /**
     *
     */
    override fun createInterview(interviewOption: InterviewOption): Interview {

        val interview = Interview(interviewOption)

        // TODO: design algorithm to select interview questions.

        logger.info("Created interview: $interview")
        return interview
    }
}