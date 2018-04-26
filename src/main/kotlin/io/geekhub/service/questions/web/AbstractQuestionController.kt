package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

abstract class AbstractQuestionController<T : Question<*>>(private val service: QuestionService<T>) {

    companion object {
        val logger = LoggerFactory.getLogger(AbstractQuestionController::class.java)!!
    }

    @PostMapping
    open fun createQuestion(@RequestBody question: T): T {
        logger.info("Received creation request for: $question")

        return service.saveQuestion(question)
    }

    @GetMapping("/{id}")
    open fun getQuestion(@PathVariable id: String): T {
        logger.info("Attempt to lookup by id: $id")

        return service.getQuestion(id).orElseThrow({ RuntimeException("not found") })
    }
}