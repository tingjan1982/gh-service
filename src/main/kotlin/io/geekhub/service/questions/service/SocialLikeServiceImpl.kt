package io.geekhub.service.questions.service

import io.geekhub.service.user.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger

@Service
@Transactional
class SocialLikeServiceImpl(val questionService: QuestionService, val userService: UserService) : SocialLikeService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SocialLikeServiceImpl::class.java)

        val likeCounts: ConcurrentMap<String, AtomicInteger> = ConcurrentHashMap<String, AtomicInteger>()
    }

    @Secured("ROLE_USER")
    override fun likeQuestion(questionId: String, userId: String) {

        questionService.getQuestion(questionId)?.let { q ->
            val user = userService.getUser(userId)
            val qId = q.questionId.toString()
            user.likedQuestions[qId] = q

            val freshCount = AtomicInteger(0)
            var questionLikes = likeCounts.putIfAbsent(qId, freshCount)

            if (questionLikes == null) {
                questionLikes = freshCount
            }

            questionLikes.incrementAndGet()
        }
    }

    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    fun saveLikedQuestionsPeriodically() {
        logger.trace("Check for liked questions to update...")

        if (likeCounts.isNotEmpty()) {
            logger.debug("Found liked questions, processing {} records.", likeCounts.size)

            val copiedLikeCounts = likeCounts.toMap()
            likeCounts.clear()

//            copiedLikeCounts.forEach { qid, count ->
//                questionService.getQuestionAttribute(qid, TOTAL_LIKES_KEY)?.let {
//                    val updatedTotal = it.value.toInt() + count.get()
//                    it.value = updatedTotal.toString()
//
//                } ?: questionService.saveOrUpdateAttribute(qid, QuestionAttribute(key = TOTAL_LIKES_KEY, value = count.get().toString()))
//            }
        }
    }
}