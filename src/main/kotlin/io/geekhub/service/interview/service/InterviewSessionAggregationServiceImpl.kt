package io.geekhub.service.interview.service

import io.geekhub.service.interview.model.InterviewSession
import io.geekhub.service.interview.service.bean.SectionAverageStats
import org.bson.Document
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.ConvertOperators
import org.springframework.data.mongodb.core.aggregation.ConvertOperators.ToDecimal
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class InterviewSessionAggregationServiceImpl(val mongoTemplate: MongoTemplate) : InterviewSessionAggregationService {

    override fun getAverageScores(interviewSession: InterviewSession): SectionAverageStats? {

        val filter = Aggregation.match(
                Criteria.where("publishedInterview.id").isEqualTo(interviewSession.publishedInterview.id)
        )

        // flattenedSections is a result of converting from map to array for subsequent processing.
        val projection = Aggregation.project()
                .and("publishedInterview").`as`("publishedInterview")
                .and(createToDecimal("totalScore")).`as`("totalScore")
                .and {
                    Document.parse("{ \$objectToArray: '\$answerAttemptSections' }")
                }.`as`("sectionsArray")

        val averageScore = Aggregation.group()
                .avg("totalScore").`as`("averageScore")
                .count().`as`("interviewSessionCount")

        val flattenedSections = Aggregation.unwind("sectionsArray")

        val flattenedAnswerStats = Aggregation.project("sectionsArray.v")
                .and("sectionsArray.v._id").`as`("sectionId")
                .and {
                    Document.parse("{ \$objectToArray: '\$sectionsArray.v.answerStats' }")
                }.`as`("answerStatsArray")

        // add the questions count and correct answer count using reduce operation.
        val sectionAnswersCount = Aggregation.project("sectionId").and {
            Document.parse("{ \$reduce: { input: '\$answerStatsArray', initialValue: { qTotal: 0, correct: 0 }, in: { qTotal: { \$add: ['\$\$value.qTotal', '\$\$this.v.questionTotal'] }, correct: { \$add: ['\$\$value.correct', '\$\$this.v.correct'] } } } }")
        }.`as`("sectionAnswersCount")

        // work out average score using Spring MongoDB's spEL expression.
        val averageSectionScore = Aggregation.project("sectionId")
                .and("sectionAnswersCount.qTotal").`as`("questionTotal")
                .and("sectionAnswersCount.correct").`as`("correctTotal")
                .andExpression("sectionAnswersCount.correct / sectionAnswersCount.qTotal").`as`("averageSectionScore")

        val facets = Aggregation.facet(averageScore).`as`("averageScore")
                .and(flattenedSections, flattenedAnswerStats, sectionAnswersCount, averageSectionScore).`as`("sectionsAverageScore")

        val aggregation = Aggregation.newAggregation(InterviewSession::class.java,
                filter,
                projection,
                facets
        )

        mongoTemplate.aggregate(aggregation, SectionAverageStats::class.java).let {
            return it.uniqueMappedResult
        }
    }

    private fun createToDecimal(fieldReference: String): ToDecimal {
        return ConvertOperators.valueOf(fieldReference).convertToDecimal()
    }
}