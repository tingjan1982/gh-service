package io.geekhub.service.interview.web.model

import io.geekhub.service.interview.service.bean.SectionAverageStats

data class InterviewSessionAverageStatsResponse(val averageScore: SectionAverageStats.OverallAverageScore,
                                                val sectionsAverageScore: List<SectionAverageStats.SectionAverageScore>)
