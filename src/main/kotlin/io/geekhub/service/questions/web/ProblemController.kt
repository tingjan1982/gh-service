package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.Problem
import io.geekhub.service.questions.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/problems")
class ProblemController(service: QuestionService<Problem>) : AbstractQuestionController<Problem>(service)