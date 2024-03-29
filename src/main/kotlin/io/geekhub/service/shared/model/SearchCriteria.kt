package io.geekhub.service.shared.model

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.shared.userkey.UserKeyFilter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

data class SearchCriteria(
    val interviewId: String?,
    val filterByMine: Boolean,
    val filterByClientAccount: Boolean,
    val clientUser: ClientUser,
    val invited: Boolean,
    val template: Boolean,
    val keyword: String?,
    val specialization: String?,
    val userKey: String?,
    val pageRequest: PageRequest
) {
    companion object {
        fun fromRequestParameters(clientUser: ClientUser, map: Map<String, String>): SearchCriteria {

            val interviewId = if (map["interviewId"].isNullOrEmpty()) null else map["interviewId"]
            val owner = map["owner"]?.toBoolean() ?: false
            val organization = map["organization"]?.toBoolean() ?: false
            val invited = map["invited"]?.toBoolean() ?: false
            val keyword = if (map["keyword"].isNullOrEmpty()) null else map["keyword"]
            val specialization = map["specialization"]
            val userKey = UserKeyFilter.UserKeyHolder.getUserKey()

            val page = map["page"]?.toInt() ?: 0
            val pageSize = map["pageSize"]?.toInt() ?: 50
            val sortField = map.getOrElse("sort") { "lastModifiedDate" }
            val template = map["template"]?.toBoolean() ?: false
            val pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Order.desc(sortField)))
            val decoratedKeyword = if (keyword.isNullOrEmpty()) null else keyword

            return SearchCriteria(interviewId, owner, organization, clientUser, invited, template, decoratedKeyword, specialization, userKey, pageRequest)
        }
    }

    fun toQuery(): Query {

        Query().with(pageRequest).let {
            if (filterByMine) {
                it.addCriteria(Criteria.where("clientUser").`is`(clientUser))
            }

            if (filterByClientAccount) {
                it.addCriteria(Criteria.where("clientAccount").`is`(clientUser.clientAccount.id))
            }

            userKey?.let { key ->
                if (!template) {
                    it.addCriteria(Criteria.where("userKey").`is`(key))
                }
            } ?: it.addCriteria(Criteria.where("userKey").isEqualTo(null))

            it.addCriteria(Criteria.where("deleted").`is`(false))

            return it
        }
    }
}