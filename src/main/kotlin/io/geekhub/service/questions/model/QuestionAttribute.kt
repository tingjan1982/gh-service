package io.geekhub.service.questions.model

import io.geekhub.service.shared.model.BaseAuditableObject
import javax.persistence.*

@Entity(name = "gh_question_attribute")
@SequenceGenerator(name = "question_attribute_sequence", sequenceName = "gh_question_attribute_seq")
@Table(uniqueConstraints = [(UniqueConstraint(columnNames = arrayOf("question_id", "key")))])
data class QuestionAttribute(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_attribute_sequence")
        val attributeId: Long? = null,

        @ManyToOne
        @JoinColumn(name = "question_id")
        val question: Question,
        val key: String,
        val value: String) : BaseAuditableObject<QuestionAttribute, Long>() {

    override fun getId(): Long? {
        return this.attributeId
    }
}