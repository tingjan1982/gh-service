package io.geekhub.service.questions.service

interface SocialLikeService {

    fun likeQuestion(questionId: String, userId: String)
}