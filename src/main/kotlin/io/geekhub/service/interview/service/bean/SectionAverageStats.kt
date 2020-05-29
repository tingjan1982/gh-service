package io.geekhub.service.interview.service.bean

import java.math.BigDecimal

data class SectionAverageStats(var averageScore: List<OverallAverageScore>,
                               var sectionsAverageScore: List<SectionAverageScore>) {


    data class OverallAverageScore(val id: String?, val averageScore: BigDecimal, val interviewSessionCount: Int)

    data class SectionAverageScore(val id: String?,
                                   val sectionId: String,
                                   val questionTotal: Int,
                                   val correctTotal: Int,
                                   val averageSectionScore: BigDecimal)
}