package io.geekhub.service.shared.model

import org.springframework.data.domain.Auditable
import org.springframework.lang.Nullable
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.persistence.Temporal
import javax.persistence.TemporalType

/**
 * Implementation is copied from AbstractAuditable because we don't want to inherit the predefined
 * id field as we want subclasses to have the flexibility of defining their own id and generated strategy.
 *
 * Subclass will need to use a different name other than 'id' for @Id and override getId() method.
 */
@MappedSuperclass
abstract class BaseAuditableObject<U, PK : Serializable> : Auditable<U, PK, LocalDateTime> {

    override fun isNew(): Boolean {
        return this.id == null
    }

    @ManyToOne
    @Nullable
    private var createdBy: U? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private var createdDate: Date? = null

    @ManyToOne
    @Nullable
    private var lastModifiedBy: U? = null

    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private var lastModifiedDate: Date? = null

    override fun getCreatedBy(): Optional<U> {
        return Optional.ofNullable(createdBy)
    }

    override fun setCreatedBy(createdBy: U) {
        this.createdBy = createdBy
    }

    override fun getCreatedDate(): Optional<LocalDateTime> {
        return if (null == createdDate)
            Optional.empty()
        else
            Optional.of(LocalDateTime.ofInstant(createdDate!!.toInstant(), ZoneId.systemDefault()))
    }

    override fun setCreatedDate(createdDate: LocalDateTime) {
        this.createdDate = Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant())
    }

    override fun getLastModifiedBy(): Optional<U> {
        return Optional.ofNullable(lastModifiedBy)
    }

    override fun setLastModifiedBy(lastModifiedBy: U) {
        this.lastModifiedBy = lastModifiedBy
    }

    override fun getLastModifiedDate(): Optional<LocalDateTime> {
        return if (null == lastModifiedDate)
            Optional.empty()
        else
            Optional.of(LocalDateTime.ofInstant(lastModifiedDate!!.toInstant(), ZoneId.systemDefault()))
    }

    override fun setLastModifiedDate(lastModifiedDate: LocalDateTime) {
        this.lastModifiedDate = Date.from(lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant())
    }
}