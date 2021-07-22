package io.geekhub.service.likes.service

import io.geekhub.service.account.repository.ClientUser
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.likes.data.LikeRecord
import io.geekhub.service.likes.data.LikeRecordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import kotlin.reflect.KClass

@Service
@Transactional
class LikeServiceImpl(
    val likeRecordRepository: LikeRecordRepository,
    val mongoTemplate: MongoTemplate
) : LikeService {

    /**
     * Use MongoDB's $inc modifier operation for atomicity.
     *
     * https://stackoverflow.com/questions/6997835/how-does-mongodb-deal-with-concurrent-updates
     * https://docs.mongodb.com/manual/reference/operator/update/inc/
     */
    override fun like(clientUser: ClientUser, likableObject: LikableObject): LikeRecord {

        likableObject.id(clientUser).let {
            return likeRecordRepository.findById(it).orElseGet {
                LikeRecord(
                    id = it,
                    likedClientUserId = clientUser.id.toString(),
                    objectId = likableObject.getObjectId(),
                    objectType = likableObject.getObjectType()
                ).let { likeRecord ->

                    likeRecordRepository.save(likeRecord).also {
                        updateLikeCount(likableObject, 1)
                    }
                }
            }
        }
    }

    override fun unlike(clientUser: ClientUser, likableObject: LikableObject) {

        mongoTemplate.remove(Query.query(where("id").`is`(likableObject.id(clientUser))), LikeRecord::class.java).let {
            if (it.deletedCount > 0) {
                updateLikeCount(likableObject, -1)
            }
        }
    }

    private fun updateLikeCount(likableObject: LikableObject, likeCount: Int) {

        val query = Query.query(where("id").`is`(likableObject.getObjectId()))
        val update = Update().inc("likeCount", likeCount)
        mongoTemplate.updateFirst(query, update, likableObject::class.java)
    }

    override fun getLikedObjects(clientUser: ClientUser, likableObjectType: KClass<out LikableObject>): List<LikeRecord> {
        return likeRecordRepository.findAllByLikedClientUserIdAndObjectType(clientUser.id.toString(), likableObjectType.toString())
    }

    override fun <T : LikableObject> getLikedObjectsAsType(clientUser: ClientUser, likableObjectType: KClass<T>, pageRequest: PageRequest, keyword: String?): Page<T> {

        this.getLikedObjects(clientUser, likableObjectType).map { it.objectId }.toList().let {
            val query = Query().with(pageRequest)
                .addCriteria(where("id").`in`(it))

            keyword?.let {
                query.addCriteria(TextCriteria.forDefaultLanguage().matching(keyword))
            }

            val count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), likableObjectType.java)
            val results = mongoTemplate.find(query, likableObjectType.java)

            return PageImpl(results, pageRequest, count)
        }
    }
}