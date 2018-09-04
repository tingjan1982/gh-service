package io.geekhub.service.interview.model

import io.geekhub.service.questions.model.Question
import io.geekhub.service.shared.model.BaseAuditableObject
import io.geekhub.service.user.model.User
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@Entity(name = "gh_interview")
@EntityListeners(AuditingEntityListener::class)
data class Interview(
        @Id
        @GeneratedValue(generator = "uuid")
        @GenericGenerator(name = "uuid", strategy = "uuid2")
        var interviewId: String? = null,
        @ManyToOne
        var user: User): BaseAuditableObject<Interview, String>() {

    private val maxScore = 100

    var interviewMode: InterviewMode = InterviewMode.MOCK

    /**
     * Default to -1 which indicates unlimited time.
     */
    var selectedDuration: Int = -1

    @OneToMany(mappedBy = "id.interview", fetch = FetchType.EAGER, cascade = [(CascadeType.ALL)])
    @MapKeyClass(Question::class)
    @MapKeyJoinColumn(name = "question_id")
    private val questions: MutableMap<Question, InterviewQuestionAnswer> = mutableMapOf()

    /**
     * This stores the computed score of this interview.
     */
    var score: Double? = null

    val startDate: Date = Date()

    var completeDate: Date? = null


    fun addQuestion(question: Question) {
        questions[question] = InterviewQuestionAnswer(InterviewQuestionAnswer.PK(this, question))
    }

    fun addAnswerAttempt(question: Question, answer: String) {

        questions[question]?.let {
            it.answer = answer

            // todo: change this to BusinessObjectNotFoundException.
        } ?: throw EntityNotFoundException("Question {id=${question.id}} is not found in the interview {id=$interviewId")
    }

    fun getQuestions(): Map<String, Question> {
        return this.questions.map {
            Pair(it.key.questionId.toString(), it.key)
        }.toMap()
    }

    fun questionsCount(): Int {
        return questions.size
    }

    /**
     * Main method
     */
    fun assessInterview() {
        val computedScore = this.computeScore()
        this.score = computedScore

        completeDate = Date()
    }

    /**
     * Computes the score of this interview taking into the questions' weight.
     */
    internal fun computeScore(): Double {

        var score = 0.0
        val weightsMap = this.calculateQuestionsWeight()

        // todo: revisit interview score calculation
//        score = questions.filter {
//            it.value.answer == it.key.getAnswer()
//        }.map {
//            weightsMap[it.key.id]
//        }.sumByDouble {
//            score + it!!
//        }

        return score
    }

    private fun calculateQuestionsWeight(): Map<String, Double> {

        val averageWeight = maxScore / this.questions.size

        return this.questions.map {
            it.key.id.toString() to it.key.weight * averageWeight
        }.toMap()
    }

    override fun getId(): String? {
        return this.interviewId
    }

    enum class InterviewMode {
        MOCK, REAL
    }
}