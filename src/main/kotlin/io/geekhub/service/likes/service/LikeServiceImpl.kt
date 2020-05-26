package io.geekhub.service.likes.service

import io.geekhub.service.account.repository.ClientAccount
import io.geekhub.service.likes.data.LikableObject
import io.geekhub.service.likes.data.LikeRecord
import io.geekhub.service.likes.data.LikeRecordRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import javax.transaction.Transactional
import kotlin.reflect.KClass

@Service
@Transactional
class LikeServiceImpl(val likeRecordRepository: LikeRecordRepository,
                      val mongoTemplate: MongoTemplate) : LikeService {

    /**
     * Use MongoDB's $inc modifier operation for atomicity.
     *
     * https://stackoverflow.com/questions/6997835/how-does-mongodb-deal-with-concurrent-updates
     * https://docs.mongodb.com/manual/reference/operator/update/inc/
     */
    override fun like(clientAccount: ClientAccount, likableObject: LikableObject): LikeRecord {

        likableObject.id(clientAccount).let {
            return likeRecordRepository.findById(it).orElseGet {
                LikeRecord(id = it,
                        likedClientAccount = clientAccount.id.toString(),
                        objectId = likableObject.getObjectId(),
                        objectType = likableObject.getObjectType()).let { likeRecord ->

                    likeRecordRepository.save(likeRecord).also {
                        updateLikeCount(likableObject, 1)
                    }
                }
            }
        }
    }

    override fun unlike(clientAccount: ClientAccount, likableObject: LikableObject) {

        mongoTemplate.remove(Query.query(where("id").`is`(likableObject.id(clientAccount))), LikeRecord::class.java).let {
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

    override fun getLikedObjects(clientAccount: ClientAccount, likableObjectType: KClass<out LikableObject>): List<LikeRecord> {
        return likeRecordRepository.findAllByLikedClientAccountAndObjectType(clientAccount.id.toString(), likableObjectType.toString())
    }
}