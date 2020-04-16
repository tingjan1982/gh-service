package io.geekhub.service.shared.model

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.specialization.service.SpecializationService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.TextCriteria

data class SearchCriteria(
        val interviewId: String?,
        val filterByClientAccount: Boolean,
        val clientAccount: ClientAccount,
        val keyword: String?,
        val specialization: String?,
        val pageRequest: PageRequest
) {
    companion object {
        fun fromRequestParameters(clientAccount: ClientAccount, map: Map<String, String>): SearchCriteria {

            val interviewId = if (map["interviewId"].isNullOrEmpty()) null else map["interviewId"]
            val owner = map["owner"]?.toBoolean() ?: false
            val keyword = if (map["keyword"].isNullOrEmpty()) null else map["keyword"]
            val specialization = map["specialization"]
            val page = map["page"]?.toInt() ?: 0
            val pageSize = map["pageSize"]?.toInt() ?: 50
            val sortField = map.getOrElse("sort") { "lastModifiedDate" }

            val pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc(sortField)))
            val decoratedKeyword = if (keyword.isNullOrEmpty()) null else keyword

            return SearchCriteria(interviewId, owner, clientAccount, decoratedKeyword, specialization, pageRequest)
        }
    }

    fun toQuery(specializationService: SpecializationService): Query {

        Query().with(pageRequest).let {
            if (filterByClientAccount) {
                it.addCriteria(Criteria.where("clientAccount").`is`(clientAccount))
            } else {
                it.addCriteria(Criteria.where("visibility").`in`(Visibility.PUBLIC, null))
            }

            keyword?.let { keyword ->
                it.addCriteria(TextCriteria.forDefaultLanguage().matching(keyword))
            }

            specialization?.let { id ->
                specializationService.lookupSpecialization(id)?.let { specialization ->
                    it.addCriteria(Criteria.where("specialization").`is`(specialization))
                }
            }
            
            it.addCriteria(Criteria.where("deleted").`is`(false))

            return it
        }
    }
}