package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.service.bean.SectionAverageStats

interface InterviewSessionAggregationService {

    fun getAverageScores(interviewSession: InterviewSession): SectionAverageStats?
}