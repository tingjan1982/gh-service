package io.geekhub.service.questions.web

import io.geekhub.service.questions.model.ShortAnswer
import io.geekhub.service.questions.service.QuestionService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/shortanswers")
class ShortAnswerController(service: QuestionService<ShortAnswer>) : AbstractQuestionController<ShortAnswer>(service)