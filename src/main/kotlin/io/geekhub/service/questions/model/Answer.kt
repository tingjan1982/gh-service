package io.geekhub.service.questions.model

data class Answer<T>(val ans: T) {

    fun getAnswer(): T {
        return ans
    }
}