package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Question
import io.geekhub.service.questions.service.QuestionService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController<T : Question>(private val service: QuestionService<T>) {

    companion object {
        val logger = LoggerFactory.getLogger(QuestionController::class.java)!!
    }

    @PostMapping("/problems")
    fun createProblem(@RequestBody problem: T): T {

        logger.info("$problem")

        return service.saveQuestion(problem)
    }

    @GetMapping("/{id}")
    fun getQuestion(@PathVariable id: String): T {

        logger.info("Received id: $id")

        return service.getQuestion(id).orElseThrow({ RuntimeException("not found") })
    }


}